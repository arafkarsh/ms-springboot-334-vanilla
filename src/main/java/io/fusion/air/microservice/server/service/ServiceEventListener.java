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

// Custom
import io.fusion.air.microservice.security.*;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
import io.fusion.air.microservice.server.config.ServiceHelp;
import io.fusion.air.microservice.utils.CPU;
// Spring
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
//Logging System
import org.slf4j.Logger;
// Java
import java.util.HashMap;
import org.slf4j.MDC;
import static org.slf4j.LoggerFactory.getLogger;
import static java.lang.invoke.MethodHandles.lookup;

/**
 * Service Event Listener
 * This Service is called (by the Spring Framework) when the Application is Ready.
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
	private JsonWebToken jsonWebToken;

	@Autowired
	private JsonWebTokenValidator jsonWebTokenValidator;

	@Autowired
	private JsonWebTokenKeyManager jsonWebTokenKeyManager;

	@Autowired
	private  MeterRegistry meterRegistry;

	@Value("${server.token.test}")
	private boolean serverTokenTest;

	// server.token.auth.expiry=300000
	@Value("${server.token.auth.expiry:300000}")
	private long tokenAuthExpiry;

	// server.token.refresh.expiry=1800000
	@Value("${server.token.refresh.expiry:1800000}")
	private long tokenRefreshExpiry;

	@Value("${spring.profiles.default:dev}")
	private String activeProfile;

	@Autowired
	private ConfigurableEnvironment environment;

	/**
	 * Check the Dev Mode
	 * @return
	 */
	private boolean  getDevMode() {
		// System.out.println("<><><><1> Profile = "+devMode);
		activeProfile = getActiveProfile();
		return (activeProfile != null && activeProfile.equalsIgnoreCase("prod")) ? false : true;
	}

	/**
	 * Get Active Profile
	 * @return
	 */
	private String getActiveProfile() {
		// System.out.println("Total Profiles = "+environment.getActiveProfiles().length);
		// System.out.println("Checking Active Profiles.... ");
		if (environment.getActiveProfiles().length == 0) {
			log.info("Spring Profile is missing, so defaulting to "+ activeProfile +" Profile!");
			environment.addActiveProfile(activeProfile);
		}
		// System.out.println("Total Profiles = "+environment.getActiveProfiles().length);
		StringBuilder sb = new StringBuilder();
		for(String profile : environment.getActiveProfiles()) {
			sb.append(profile).append(" ");
		}
		String profile = sb.toString().trim().replaceAll(" ", ", ");
		log.debug("Profiles = "+profile);
		return profile;
	}

	/**
	 * Register the Product API List for Micrometer
	 */
	private void registerAPICallsForMicroMeter() {
		int totalApis = 0;
		String apiClass = serviceConfig.getAppPropertyProduct();
		for(String apiName : serviceConfig.getAppPropertyProductList()) {
			String fullCounterName = apiClass + (apiName.isEmpty() ? "" : apiName.replaceAll("/", "."));
			// Create and Register the counter
			Counter counter = Counter
					.builder(fullCounterName)
					.register(meterRegistry);
			totalApis++;
		}
		log.info("Total fusion.air.product APIs registered with MicroMeter = "+totalApis);
	}

	/**
	 * Shows Logo and Generate Test Tokens
	 * This method is automatically called by the SpringBoot Application when the Application
	 * is ready.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		log.info("Service is getting ready. Getting the CPU Stats ... ");
		log.info(CPU.printCpuStats());
		showLogo();
		registerAPICallsForMicroMeter();
		// Initialize the Key Manager
		jsonWebTokenKeyManager.init(serviceConfig.getTokenType());
		jsonWebTokenKeyManager.setKeyCloakPublicKey();
		// Initialize the Token
		jsonWebToken.init(serviceConfig.getTokenType());
		if(serverTokenTest && getDevMode() ) {
			log.info("Generate Test Tokens = {} ", serverTokenTest);
			generateTestToken();		// ONLY FOR DEVELOPER TESTING
		}
		// Initialize the KeyCloak Public Key
		jsonWebToken.setKeyCloakPublicKey();
	}



	/**
	 * ----------------------------------------------------------------------------------------------------------
	 * WARNING:
	 * ----------------------------------------------------------------------------------------------------------
	 * These tokens MUST be generated only in an Auth Service. All the services need not generate these tokens
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
		// Step 1: Set Expiry Time
		tokenAuthExpiry = (tokenAuthExpiry < 10) ? JsonWebTokenConstants.EXPIRE_IN_FIVE_MINS : tokenAuthExpiry;
		tokenRefreshExpiry = (tokenRefreshExpiry < 10) ? JsonWebTokenConstants.EXPIRE_IN_THIRTY_MINS : tokenRefreshExpiry;

		// Step 2: Set Subject and Issuer
		String subject	 = "jane.doe";
		String issuer    = serviceConfig.getServiceOrg();
		String type 	 = TokenManager.TX_USERS;

		// Step 3: Initialize TokenManager
		TokenManager tokenManager = new TokenManager(serviceConfig, tokenAuthExpiry, tokenRefreshExpiry);

		// Step 4: Generate Authorize Tokens
		HashMap<String, String> tokens = tokenManager.createAuthorizationToken(subject, null);
		String token = tokens.get("token");
		String refresh = tokens.get("refresh");

		log.info("Auth Token Expiry in Days:Hours:Mins  {}   Tkn-1 <>", JsonWebToken.printExpiryTime(tokenAuthExpiry));
		jsonWebToken.tokenStats(token, false, false);
		log.info("Auth Token.... END ................................... Tkn-1 <>");

		log.info("Refresh Token Expiry in Days:Hours:Mins  {} Tkn-2 <>", JsonWebToken.printExpiryTime(tokenRefreshExpiry));
		jsonWebToken.tokenStats(refresh, false, false);
		log.info("Refresh Token.... END ............................... Tkn-2 <>");

		log.info("Tx-Token Expiry in Days:Hours:Mins  {}    Tkn-3 <>", JsonWebToken.printExpiryTime(tokenRefreshExpiry));
		String txToken = tokenManager.createTXToken(subject, type, null);
		jsonWebToken.tokenStats(txToken, false, false);
		log.info("Tx Token.... END ....................................... Tkn-3 <>");

		log.info("Admin Token Expiry in Days:Hours:Mins  {} Tkn-4 <>", JsonWebToken.printExpiryTime(tokenRefreshExpiry));
		String admToken = tokenManager.adminToken(subject, issuer);
		jsonWebToken.tokenStats(admToken, false, false);
		log.info("Admin Token.... END ................................. Tkn-4 <>");

		log.info("Token Creation done... for Dev Testing........... ............ COMPLETE!!");
	}

	/**
	 * Shows the Service Logo and Version Details.
	 */
	public void showLogo() {
		String version="v0.1.0", name="NoName", javaVersion="21", sbVersion="3.1.0";

		if(serviceConfig != null) {
			version = serviceConfig.getServerVersion();
			name =serviceConfig.getServiceName();
			javaVersion = System.getProperty("java.version");
			sbVersion = SpringBootVersion.getVersion();
		}
		MDC.put("Service", name);
		String logo =ServiceHelp.LOGO
				.replaceAll("SIGMA", name)
				.replaceAll("MSVERSION", version)
				.replaceAll("JAVAVERSION", javaVersion)
				.replaceAll("SPRINGBOOTVERSION", sbVersion);
		log.info(name+" Service is ready! ... .."
				+ logo
				+ "Build No. = "+serviceConfig.getBuildNumber()
				+ " :: Build Date = "+serviceConfig.getBuildDate()
				+ " :: Mode = "+geDeploymentMode()
				+ " :: Restart = "+ServiceHelp.getCounter()
				+ ServiceHelp.NL + ServiceHelp.DL);
		// if(getDevMode() ) {
			log.info(ServiceHelp.NL + "API URL : " + serviceConfig.apiURL() + ServiceHelp.NL + ServiceHelp.DL
			);
		//}
	}
	private String geDeploymentMode() {
		return switch (getActiveProfile()) {
            case "dev" -> "Development";
            case "staging" -> "Staging";
            case "prod" -> "Production";
            default -> "Development";
        };
	}
}