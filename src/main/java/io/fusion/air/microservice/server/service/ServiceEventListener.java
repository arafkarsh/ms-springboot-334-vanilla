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
package io.fusion.air.microservice.server.service;
 
import io.fusion.air.microservice.security.JsonWebToken;
import io.fusion.air.microservice.security.TokenManager;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
import io.fusion.air.microservice.server.config.ServiceHelp;

import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import io.fusion.air.microservice.utils.CPU;

//Logging System
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * 
 * @author arafkarsh
 * @version 1.0
 */
@Configuration
public class ServiceEventListener {

	// Set Logger -> Lookup will automatically determine the class name.
	private static final Logger log = getLogger(lookup().lookupClass());

	@Autowired
	private ServiceConfiguration serviceConfig;

	@Autowired
	JsonWebToken jsonWebToken;

	@Value("${server.token.test}")
	private boolean serverTokenTest;

	// server.token.auth.expiry=300000
	@Value("${server.token.auth.expiry:300000}")
	private long tokenAuthExpiry;

	// server.token.refresh.expiry=1800000
	@Value("${server.token.refresh.expiry:1800000}")
	private long tokenRefreshExpiry;

	@Value("${server.dev.mode:true}")
	private boolean devMode;

	/**
	 * Shows Logo and Generate Test Tokens
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		log.info("Service is getting ready. Getting the CPU Stats ... ");
		log.info(CPU.printCpuStats());
		showLogo();
		// Initialize the Token
		jsonWebToken.init(serviceConfig.getTokenType());
		if(serverTokenTest && devMode) {
			log.debug("Generate Test Tokens = {} ", serverTokenTest);
			generateTestToken();
		}
	}

	/**
	 * WARNING:
	 * These tokens can be generated only in an Auth Service. All the services need not generate these tokens
	 * unless for the developers to test it out. In a real world scenario, disable (Comment out the function
	 * generateTestToken()) this feature for production environment.
	 * THIS IS ONLY FOR TESTING PURPOSES.
	 *
	 * Generate Tokens for Testing Purpose Only
	 * Token 			= Expires in 5 Mins
	 * Refresh Token 	= Expires in 30 Mins
	 * This shld be disabled in Production Environment
	 * serverTestToken=false
	 */
	private void generateTestToken() {
		log.info("Token Type = {}", serviceConfig.getTokenType());
		tokenAuthExpiry = (tokenAuthExpiry < 10) ? JsonWebToken.EXPIRE_IN_FIVE_MINS : tokenAuthExpiry;
		tokenRefreshExpiry = (tokenRefreshExpiry < 10) ? JsonWebToken.EXPIRE_IN_THIRTY_MINS : tokenRefreshExpiry;

		String subject	 = "jane.doe";
		String issuer    = serviceConfig.getServiceOrg();
		String type 	 = TokenManager.TX_USERS;

		TokenManager tokenManager = new TokenManager(serviceConfig, tokenAuthExpiry, tokenRefreshExpiry);

		// Step 4: Generate Authorize Tokens
		HashMap<String, String> tokens = tokenManager.createAuthorizationToken(subject, null);

		String token = tokens.get("token");
		String refresh = tokens.get("refresh");
		log.debug("\nToken Expiry in Days:Hours:Mins  {} ", JsonWebToken.printExpiryTime(tokenAuthExpiry));
		jsonWebToken.tokenStats(token, false, false);

		log.debug("\nRefresh Token Expiry in Days:Hours:Mins  {}", JsonWebToken.printExpiryTime(tokenRefreshExpiry));
		jsonWebToken.tokenStats(refresh, false, false);

		log.debug("\nTx-Token Expiry in Days:Hours:Mins  {}", JsonWebToken.printExpiryTime(tokenRefreshExpiry));
		String txToken = tokenManager.createTXToken(subject, type, null);
		jsonWebToken.tokenStats(txToken, false, false);

		String admToken = adminToken(subject);
		jsonWebToken.tokenStats(admToken, false, false);
	}

	/**
	 * Create Admin Token
	 * @param subject
	 * @return
	 */
	private String adminToken(String subject) {
		String issuer    = serviceConfig.getServiceOrg();
		Map<String, Object> claims = getClaims( subject,  issuer);
		claims.put("rol", "Admin");

		long txTokenExpiry = (tokenRefreshExpiry < 50) ? JsonWebToken.EXPIRE_IN_ONE_HOUR : tokenRefreshExpiry;;
		log.info("\nAdmin Token Expiry in Days:Hours:Mins  {}", JsonWebToken.printExpiryTime(txTokenExpiry));
		return jsonWebToken
				.init(serviceConfig.getTokenType())
				.setSubject(subject)
				.setIssuer(serviceConfig.getServiceOrg())
				.generateToken( subject,  serviceConfig.getServiceOrg(),  txTokenExpiry,  claims);
	}

	/**
	 * Create Claims
	 * @param subject
	 * @param issuer
	 * @return
	 */
	private Map<String, Object> getClaims(String subject, String issuer) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("aud", serviceConfig.getServiceName());
		claims.put("jti", UUID.randomUUID().toString());
		claims.put("sub", subject);
		claims.put("iss", issuer);
		claims.put("type",TokenManager.TX_USERS);
		claims.put("rol", "User");
		return claims;
	}

	/**
	 * Shows the Service Logo and Version Details.
	 */
	public void showLogo() {
		String version="v0.1.0", name="NoName";
		if(serviceConfig != null) {
			version = serviceConfig.getServerVersion();
			name =serviceConfig.getServiceName();
		}
		MDC.put("Service", name);
		String logo =ServiceHelp.LOGO.replaceAll("SIGMA", name).replaceAll("VERSION", version);
		log.info(name+" Service is ready! ... .."
				+ logo
				+ "Build No. = "+serviceConfig.getBuildNumber()
				+ " :: Build Date = "+serviceConfig.getBuildDate()
				+ " :: Mode = "+geDeploymentMode()
				+ " :: Restart = "+ServiceHelp.getCounter()
				+ ServiceHelp.NL + ServiceHelp.DL);
		if(devMode) {
			log.info(ServiceHelp.NL + "API URL : " + serviceConfig.apiURL()
					+ ServiceHelp.NL + ServiceHelp.DL
			);
		}
	}
	private String geDeploymentMode() {
		return (devMode) ? "Staging" : "Production";
	}
}