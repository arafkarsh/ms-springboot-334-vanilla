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
package io.fusion.air.microservice.security.jwt.server;
// Custom
import io.fusion.air.microservice.adapters.filters.HeaderManager;
import io.fusion.air.microservice.adapters.security.ClaimsManager;
import io.fusion.air.microservice.security.jwt.client.JsonWebTokenValidator;
import io.fusion.air.microservice.security.jwt.core.JsonWebTokenConfig;
import io.fusion.air.microservice.security.jwt.core.TokenData;
import io.fusion.air.microservice.security.jwt.core.TokenDataFactory;

import static io.fusion.air.microservice.security.jwt.core.JsonWebTokenConstants.*;
// Spring
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
// Java
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Service
public class TokenManager {

    // Set Logger -> Lookup will automatically determine the class name.
    private static final Logger log = getLogger(lookup().lookupClass());

    // Autowired using the Constructor
    private JsonWebTokenConfig jwtConfig;

    // Autowired using the Constructor
    private ClaimsManager claimsManager;

    // Autowired using the Constructor
    private HeaderManager headerManager;

    // Autowired using the Constructor
    private JsonWebTokenGenerator tokenGenerator;

    // Autowired
    private TokenDataFactory tokenDataFactory;

    /**
     * Autowired using the Constructor
     * Generates Json Web Token based on Secret Key or Public Key
     *
     * @param tknConfig
     * @param cManager
     * @param hManager
     * @param tokenGenerator
     */
    @Autowired
    public TokenManager(JsonWebTokenConfig tknConfig, ClaimsManager cManager,
                        HeaderManager hManager, JsonWebTokenGenerator tokenGenerator,
                        TokenDataFactory tokenDataFactory) {
        this.jwtConfig = tknConfig;
        this.claimsManager = cManager;
        this.headerManager = hManager;
        this.tokenGenerator = tokenGenerator;
        this.tokenDataFactory = tokenDataFactory;
    }

    /**
     * Returns Claims
     * @return
     */
    public ClaimsManager getClaims() {
        return claimsManager;
    }

    /**
     * Create TX-Token with Subject, Token Type and Add to Header
     *
     * @param subject
     * @param type
     * @param headers
     * @return
     */
    public String createTXToken(String subject, String type, HttpHeaders headers) {

        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("aud", "tx-services");
        claims.put("sub", subject);
        claims.put("type",type);
        claims.put("iss", jwtConfig.getServiceOrg());
        claims.put("rol", ROLE_USER);
        claims.put("jti", UUID.randomUUID().toString());

        String token = tokenGenerator
                .generateToken(subject,  jwtConfig.getServiceOrg(),  jwtConfig.getRefreshTokenExpiry(),  claims);

        if(headers != null) {
            headerManager.setResponseHeader(TX_TOKEN, BEARER + token);
        }
        return token;
    }

    /**
     * Create External Service Token
     *
     * @param serviceName
     * @param servicesAllowed
     * @param headers
     * @return
     */
    public Map<String, String> createExternalToken(String serviceId, String serviceName, String serviceOwner,
                                      String servicesAllowed, HttpHeaders headers) {
        String subject      = serviceName;
        Map<String, Object> claims = getServiceClaims(serviceId, serviceName, serviceOwner, servicesAllowed);
        claims.put("type",TX_EXTERNAL);

        long txTokenExpiry =  EXPIRE_IN_ONE_DAY;
        String token = tokenGenerator
                            .generateToken( subject,  jwtConfig.getServiceOrg(),  txTokenExpiry,  claims);

        // Store Tx External Tokens
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put("authToken", BEARER + token);
        tokens.put("expiryTime", ""+txTokenExpiry);
        // Add Token to Headers
        if(headers != null) {
            headerManager.setResponseHeader("Authorization", BEARER + token);
        }
        return tokens;
    }

    /**
     * Create Internal Service Token
     *
     * @param serviceName
     * @param servicesAllowed
     * @return
     */
    public Map<String, String> createInternalToken(String serviceId, String serviceName, String serviceOwner,
                                      String servicesAllowed, HttpHeaders headers) {
        String subject      = serviceName;

        // Auth Token
        Map<String, Object> claims = getServiceClaims(serviceId, serviceName, serviceOwner, servicesAllowed);
        claims.put("type",TX_SERVICE);
        long txTokenExpiry =  EXPIRE_IN_ONE_DAY;
        String token = tokenGenerator
                            .generateToken( subject,  jwtConfig.getServiceOrg(),  txTokenExpiry,  claims);

        // TX-Token
        claims.put("type",TX_USERS);
        String txToken = tokenGenerator
                            .generateToken( subject,  jwtConfig.getServiceOrg(),  txTokenExpiry,  claims);

        // Store Tokens
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put("authToken", BEARER + token);
        tokens.put("txToken", BEARER + txToken);
        tokens.put("expiryTime", ""+txTokenExpiry);
        // Add Token to Headers
        if(headers != null) {
            headerManager.setResponseHeader(AUTH_TOKEN , BEARER + token);
            headerManager.setResponseHeader(TX_TOKEN , BEARER + txToken);
        }
        // Return Auth & TX Tokens
        return tokens;
    }

    /**
     * Returns Service Claims
     * @param serviceId
     * @param serviceName
     * @param serviceOwner
     * @param servicesAllowed
     * @return
     */
    private Map<String, Object> getServiceClaims(String serviceId, String serviceName, String serviceOwner,
                                                 String servicesAllowed) {
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("aud", servicesAllowed);
        claims.put("sub", serviceName);
        claims.put("iss", jwtConfig.getServiceOrg());
        claims.put("rol", "Service");
        claims.put("serviceId", serviceId);
        claims.put("service", serviceName);
        claims.put("owner", serviceOwner);
        claims.put("jti", UUID.randomUUID().toString());

        return claims;
    }

    /**
     * Add Authorization Tokens
     *
     * @param subject
     * @param headers
     * @return
     */
    public Map<String, String>  createAuthorizationToken(String subject, HttpHeaders headers) {

        Map<String, Object> authClaims = new LinkedHashMap<>();
        authClaims.put("aud", "generic");
        authClaims.put("sub", subject);
        authClaims.put("type",AUTH);
        authClaims.put("iss", jwtConfig.getServiceOrg());
        authClaims.put("rol", ROLE_USER);
        authClaims.put("jti", UUID.randomUUID().toString());

        Map<String, Object> refreshClaims = new LinkedHashMap<>();
        refreshClaims.put("aud", "generic");
        refreshClaims.put("sub", subject);
        refreshClaims.put("type",AUTH_REFRESH);
        refreshClaims.put("iss", jwtConfig.getServiceOrg());
        refreshClaims.put("rol", ROLE_USER);
        refreshClaims.put("jti", UUID.randomUUID().toString());

        Map<String, String> tokens = refreshTokens(subject, authClaims, refreshClaims);
        setHeaders( headers, tokens);
        return tokens;
    }

    /**
     * Create Authorization Tokens based on existing Claims (Refresh Tokens)
     * @param subject
     * @param headers
     * @param claims
     * @return
     */
    public Map<String, String>  createAuthorizationToken(String subject, HttpHeaders headers, Claims claims) {
        Map<String, Object> newClaims = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            newClaims.put(entry.getKey(), entry.getValue());
        }
        Map<String, String> tokens = refreshTokens(subject, newClaims, newClaims);
        setHeaders( headers, tokens);
        return tokens;
    }

    /**
     * Set the Headers with Tokens
     * @param headers
     * @param tokens
     */
    private void setHeaders(HttpHeaders headers, Map<String, String> tokens) {
        String authToken = tokens.get("token");
        String refreshTkn = tokens.get(AUTH_REFRESH);
        setHeaders( headers,  authToken,  refreshTkn);
    }

    /**
     * Set the Headers with Tokens
     * @param headers
     * @param authToken
     * @param refreshToken
     */
    private void setHeaders(HttpHeaders headers, String authToken, String refreshToken) {
        if(headers != null) {
            headerManager.setResponseHeader(AUTH_TOKEN, BEARER + authToken);
            headerManager.setResponseHeader(REFRESH_TOKEN, BEARER + refreshToken);
        }
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
    private Map<String, String> refreshTokens(String subject,
                                                  Map<String, Object> authTokenClaims, Map<String, Object> refreshTokenClaims) {
        return tokenGenerator
                    .generateTokens(
                        subject,                                        // Token Subject
                        jwtConfig.getServiceOrg(),                  // Organization Name
                        jwtConfig.getAuthTokenExpiry(),           // Auth Token Expiry Time
                        jwtConfig.getRefreshTokenExpiry(),        // Refresh Token Expiry Time
                        authTokenClaims,                              // Auth Token Claims
                        refreshTokenClaims);                          // Refresh Token Claims
    }

    /**
     * Create Admin Token
     * @param subject
     * @return
     */
    public String adminToken(String subject, String issuer) {
        Map<String, Object> claims = getClaims(subject,  issuer);
        claims.put("rol", ROLE_ADMIN);

        long txTokenExpiry = jwtConfig.getRefreshTokenExpiry();
        String st = printExpiryTime(txTokenExpiry);
        log.info("Admin Token Expiry in Days:Hours:Mins  {}", st);
        return tokenGenerator.generateToken( subject,  issuer,  txTokenExpiry,  claims);
    }

    /**
     * Create Claims
     * @param subject
     * @param issuer
     * @return
     */
    private Map<String, Object> getClaims(String subject, String issuer) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("aud", jwtConfig.getServiceName());
        claims.put("jti", UUID.randomUUID().toString());
        claims.put("sub", subject);
        claims.put("iss", issuer);
        claims.put("type",TX_USERS);
        claims.put("rol", ROLE_USER);
        return claims;
    }

    /**
     * Returns the Token Data based on the Input Token
     * Token Data Contains the JWT Token and the Validator Key
     * @param token
     * @return
     */
    public TokenData createTokenData(String token) {
        return tokenDataFactory.createTokenData(token);
    }

    /**
     * Print Token Details
     * @param token
     * @param showClaims
     * @param showPayload
     */
    public void printTokenStats(String token, boolean showClaims, boolean showPayload) {
        TokenData data = tokenDataFactory.createTokenData(token);
        JsonWebTokenValidator.tokenStats(data, showClaims, showPayload);
    }

    /**
     * Return the Token Expiry Time
     * @param expiryTime
     * @return
     */
    public String printExpiryTime(long expiryTime) {
        return JsonWebTokenValidator.printExpiryTime(expiryTime);
    }

    /**
     * Return JsonWebTokenConfig
     * @return
     */
    public JsonWebTokenConfig getJsonWebTokenConfig() {
        return jwtConfig;
    }
}
