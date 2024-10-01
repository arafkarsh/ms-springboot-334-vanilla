/**
 * (C) Copyright 2021 Araf Karsh Hamid 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fusion.air.microservice.server.controllers;

import io.fusion.air.microservice.adapters.security.AuthorizationRequired;
import io.fusion.air.microservice.domain.exceptions.*;
import io.fusion.air.microservice.domain.models.core.StandardResponse;
import io.fusion.air.microservice.adapters.security.AuthorizeRequestAspect;
import io.fusion.air.microservice.adapters.security.ValidateRefreshToken;
import io.fusion.air.microservice.security.CryptoKeyGenerator;
import io.fusion.air.microservice.security.JsonWebToken;
import io.fusion.air.microservice.security.TokenManager;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.http.HttpHeaders;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Refresh Token Controller for the Service
 * Generates the following
 * 1. Auth Token
 * 2. Refresh Token
 * 
 * @author arafkarsh
 * @version 1.0
 * 
 */
@Configuration
@RestController
// "/service-name/api/v1/tokens
@RequestMapping("${service.api.path}/tokens")
@RequestScope
@Tag(name = "System - Tokens", description = "Token (Auth Token, Refresh Tokens")
public class TokenController extends AbstractController {

	// Set Logger -> Lookup will automatically determine the class name.
	private static final Logger log = getLogger(lookup().lookupClass());

	@Autowired
	private ServiceConfiguration serviceConfig;
	private String serviceName;

	@Autowired
	private JsonWebToken jsonWebToken;

	@Autowired
	private CryptoKeyGenerator cryptoKeys;
	@Autowired
	private TokenManager tokenManager;

	@Value("${server.token.auth.expiry:300000}")
	private long tokenAuthExpiry;

	@Value("${server.token.refresh.expiry:1800000}")
	private long tokenRefreshExpiry;

	@AuthorizationRequired(role = "User")
	@Operation(summary = "Get the Public Key", security = { @SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Public Key Retrieved",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "404",
					description = "Public Key Retrieval Failed!",
					content = @Content)
	})
	@GetMapping("/publickey")
	@ResponseBody
	public ResponseEntity<StandardResponse> getPublicKey(HttpServletRequest request) throws Exception {
		log.debug(name()+"|Request to Generate Tokens... ");
		cryptoKeys.setKeyFiles(getCryptoPublicKeyFile(), getCryptoPrivateKeyFile())
				.readRSAKeyFiles();
		StandardResponse stdResponse = createSuccessResponse("Public Key Retrieved!");
		// Send the Token in the Body (This is NOT Required and ONLY for Testing Purpose)
		HashMap<String,String> data = new HashMap<String,String>();
		data.put("type", "Public-Key");
		data.put("format", cryptoKeys.getPublicKey().getFormat());
		data.put("key", cryptoKeys.getPublicKeyPEMFormat());
		stdResponse.setPayload(data);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Returns Crypto Public Key File
	 * @return
	 */
	private String getCryptoPublicKeyFile() {
		return (serviceConfig != null) ? serviceConfig.getCryptoPublicKeyFile() : "publicKey.pem";
	}

	/**
	 * Returns Crypto Private Key File
	 * @return
	 */
	private String getCryptoPrivateKeyFile() {
		return (serviceConfig != null) ? serviceConfig.getCryptoPrivateKeyFile() : "privateKey.pem";
	}

	/**
	 * Get Method Call To Generate Authorization Token if the Refresh Token is Valid.
	 * This controller should ONLY be used in an Authorization Microservice and this
	 * is NOT required for all microservices.
	 * Steps:
	 * 1. Checks if the Refresh (JWT) Token is Valid.
	 * 2. If valid then Retrieves Refresh Token
	 * 3. Retrieves all the Necessary info like Subject, Issuer, Claims etc.
	 * 4. Creates a new Pair of Auth and Refresh Token.
	 * 5. Returns the Tokens in the Header
	 * @return
	 */
	@ValidateRefreshToken(role = "User")
	@Operation(summary = "Generate Refresh Tokens", security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
            description = "Auth & Refresh Tokens Generated",
            content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
            description = "Auth & Refresh Token Generation Failed!",
            content = @Content)
    })
	@GetMapping("/refresh")
	@ResponseBody
	public ResponseEntity<StandardResponse> generate(HttpServletRequest request) throws Exception {
		log.debug(name()+"|Request to Generate Refresh Tokens... ");

		// Step 1:
		//  final String authToken = getToken(request.getHeader(AuthorizeRequestAspect.AUTH_TOKEN));
		final String refreshToken = getToken(request.getHeader(AuthorizeRequestAspect.REFRESH_TOKEN));
		String subject = jsonWebToken.getSubjectFromToken(refreshToken);

		// Claims authTokenClaims = jwtUtil.getAllClaims(authToken);
		Claims refreshTokenClaims = jsonWebToken.getAllClaims(refreshToken);
		HashMap<String, String> tokens = refreshTokens(subject, refreshTokenClaims, refreshTokenClaims);

		// Step 2: Generate Authorize Tokens
		HttpHeaders headers = new HttpHeaders();
		HashMap<String, String> allTokens = tokenManager.createAuthorizationToken(subject, headers);
		String txToken = tokenManager.createTXToken(subject, TokenManager.TX_USERS, headers);
		allTokens.putIfAbsent("TX-Token", txToken);
		StandardResponse stdResponse = createSuccessResponse("Auth & Refresh Tokens Generated!!!");
		// Send the Token in the Body (This is NOT Required and ONLY for Testing Purpose)
		stdResponse.setPayload(allTokens);
		return new ResponseEntity<StandardResponse>(stdResponse, headers, HttpStatus.OK );
	}

	/**
	 * Extract Token
	 * @param tokenKey
	 * @return
	 */
	private String getToken(String tokenKey) {
		if (tokenKey != null && tokenKey.startsWith("Bearer ")) {
			return tokenKey.substring(7);
		}
		String msg = "Access Denied: Unable to extract token from Header = ";
		throw new JWTTokenExtractionException(msg);
	}

	/**
	 * Refresh Tokens
	 * 1. Auth Token
	 * 2. Refresh Token
	 * @param subject
	 * @param authTokenClaims
	 * @param refreshTokenClaims
	 * @return
	 */
	private HashMap<String, String> refreshTokens(String subject,
												  Claims authTokenClaims, Claims refreshTokenClaims) {
		tokenAuthExpiry = (tokenAuthExpiry < 10) ? JsonWebToken.EXPIRE_IN_FIVE_MINS : tokenAuthExpiry;
		tokenRefreshExpiry = (tokenRefreshExpiry < 10) ? JsonWebToken.EXPIRE_IN_THIRTY_MINS : tokenRefreshExpiry;
		return jsonWebToken
				.init(serviceConfig.getTokenType())
				.setSubject(subject)
				.setIssuer(serviceConfig.getServiceOrg())
				.setTokenAuthExpiry(tokenAuthExpiry)
				.setTokenRefreshExpiry(tokenRefreshExpiry)
				.addAllTokenClaims(authTokenClaims)
				.addAllRefreshTokenClaims(refreshTokenClaims)
				.generateTokens();
	}
 }

