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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
//Logging System
import org.slf4j.Logger;
// Java
import java.util.Map;
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

	// Autowired using the Constructor
	private ServiceConfiguration serviceConfig;

	// Autowired using the Constructor
	private JsonWebToken jsonWebToken;

	// Autowired using the Constructor
	private JsonWebTokenValidator jsonWebTokenValidator;

	// Autowired using the Constructor
	private JsonWebTokenKeyManager jsonWebTokenKeyManager;

	// Autowired using the Constructor
	private  MeterRegistry meterRegistry;

	// Autowired using the Constructor
	private ConfigurableEnvironment environment;

	/**
	 * Autowired using the Constructor
	 * @param serviceConfig
	 * @param jsonWebToken
	 * @param jsonWebTokenValidator
	 * @param jsonWebTokenKeyManager
	 * @param meterRegistry
	 * @param environment
	 */
	public ServiceEventListener(ServiceConfiguration serviceConfig, JsonWebToken jsonWebToken,
								JsonWebTokenValidator jsonWebTokenValidator, JsonWebTokenKeyManager jsonWebTokenKeyManager,
								MeterRegistry meterRegistry, ConfigurableEnvironment environment) {
		this.serviceConfig = serviceConfig;
		this.jsonWebToken = jsonWebToken;
		this.jsonWebTokenValidator = jsonWebTokenValidator;
		this.jsonWebTokenKeyManager = jsonWebTokenKeyManager;
		this.meterRegistry = meterRegistry;
		this.environment = environment;
	}

	/**
	 * Check the Dev Mode
	 * @return
	 */
	private boolean  getDevMode() {
		activeProfile = getActiveProfile();
		return (activeProfile != null && activeProfile.equalsIgnoreCase("prod")) ? false : true;
	}

	/**
	 * Get Active Profile
	 * @return
	 */
	private String getActiveProfile() {
		if (environment.getActiveProfiles().length == 0) {
			log.info("Spring Profile is missing, so defaulting to {}  Profile!", activeProfile);
			environment.addActiveProfile(activeProfile);
		}
		StringBuilder sb = new StringBuilder();
		for(String profile : environment.getActiveProfiles()) {
			sb.append(profile).append(" ");
		}
		String profile = sb.toString().trim().replace(" ", ", ");
		log.debug("Profiles = {} ", profile);
		return profile;
	}

	/**
	 * Register the Product API List for Micrometer
	 */
	private void registerAPICallsForMicroMeter() {
		int totalApis = 0;
		String apiClass = serviceConfig.getAppPropertyProduct();
		for(String apiName : serviceConfig.getAppPropertyProductList()) {
			String fullCounterName = apiClass + (apiName.isEmpty() ? "" : apiName.replace("/", "."));
			// Create and Register the counter
			Counter counter = Counter
					.builder(fullCounterName)
					.register(meterRegistry);
			totalApis++;
		}
		log.info("Total fusion.air.product APIs registered with MicroMeter = {} ", totalApis);
	}

	/**
	 * Shows Logo and Generate Test Tokens
	 * This method is automatically called by the SpringBoot Application when the Application
	 * is ready.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		log.info("Service is getting ready. Getting the CPU Stats ... ");
		String s = CPU.printCpuStats();
		log.info("{}", s);
		showLogo();
		// Register the APIs with Micrometer
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
		Map<String, String> tokens = tokenManager.createAuthorizationToken(subject, null);
		String token = tokens.get("token");
		String refresh = tokens.get("refresh");
		String jStats = JsonWebToken.printExpiryTime(tokenAuthExpiry);
		log.info("Auth Token Expiry in Days:Hours:Mins  {}   Tkn-1 <>", jStats);
		jsonWebToken.tokenStats(token, false, false);
		log.info("Auth Token.... END ................................... Tkn-1 <>");
		jStats = JsonWebToken.printExpiryTime(tokenRefreshExpiry);
		log.info("Refresh Token Expiry in Days:Hours:Mins  {} Tkn-2 <>", jStats);
		jsonWebToken.tokenStats(refresh, false, false);
		log.info("Refresh Token.... END ............................... Tkn-2 <>");
		jStats = JsonWebToken.printExpiryTime(tokenRefreshExpiry);
		log.info("Tx-Token Expiry in Days:Hours:Mins  {}    Tkn-3 <>", jStats);
		String txToken = tokenManager.createTXToken(subject, type, null);
		jsonWebToken.tokenStats(txToken, false, false);
		log.info("Tx Token.... END ....................................... Tkn-3 <>");
		jStats = JsonWebToken.printExpiryTime(tokenRefreshExpiry);
		log.info("Admin Token Expiry in Days:Hours:Mins  {} Tkn-4 <>", jStats);
		String admToken = tokenManager.adminToken(subject, issuer);
		jsonWebToken.tokenStats(admToken, false, false);
		log.info("Admin Token.... END ................................. Tkn-4 <>");

		log.info("Token Creation done... for Dev Testing........... ............ COMPLETE!!");
	}

	/**
	 * Shows the Service Logo and Version Details.
	 */
	public void showLogo() {
		String version="v0.1.0";
		String name="NoName";
		String javaVersion="21";
		String sbVersion="3.1.0";
		int buildNo = 0;
		String buildDt = "";
		String apiURL = "";
		String depModel = geDeploymentMode();

		if(serviceConfig != null) {
			version = serviceConfig.getServerVersion();
			name =serviceConfig.getServiceName();
			javaVersion = System.getProperty("java.version");
			sbVersion = SpringBootVersion.getVersion();
			buildNo = serviceConfig.getBuildNumber();
			buildDt = serviceConfig.getBuildDate();
			apiURL = serviceConfig.apiURL();
		}
		MDC.put("Service", name);
		String logo =ServiceHelp.LOGO
				.replace("SIGMA", name)
				.replace("MSVERSION", version)
				.replace("JAVAVERSION", javaVersion)
				.replace("SPRINGBOOTVERSION", sbVersion);
		log.info("{} Service is ready! ... .. {}"
				+ "Build No. = {} "
				+ " :: Build Date = {} "
				+ " :: Mode = {} "
				+ " :: Restart = {} {} {} ",
				name, logo, buildNo, buildDt, depModel, ServiceHelp.getCounter(), ServiceHelp.NL , ServiceHelp.DL);
		// if(getDevMode() ) {
			log.info("{} API URL : {} {} {} ", ServiceHelp.NL, apiURL, ServiceHelp.NL, ServiceHelp.DL);
		//}
	}
	private String geDeploymentMode() {
		return switch (getActiveProfile()) {
            case "staging" -> "Staging";
            case "prod" -> "Production";
			case "dev" -> "Development";
			default -> "Development";
        };
	}
}