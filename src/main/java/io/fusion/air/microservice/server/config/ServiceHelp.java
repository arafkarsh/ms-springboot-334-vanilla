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
package io.fusion.air.microservice.server.config;
// Java
import java.util.List;
import java.util.Map;
// Jakarta
import jakarta.annotation.PostConstruct;
// Spring
import org.springframework.stereotype.Component;
// Logging System
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import static java.lang.invoke.MethodHandles.lookup;

/**
 * 
 * @author arafkarsh
 * @version 1.0
 * 
 */
@Component
public class ServiceHelp {
	
	// Set Logger -> Lookup will automatically determine the class name.
	private static final Logger log = getLogger(lookup().lookupClass());
	
	private static int counter;
	
	// Autowired using the Constructor
	private ServiceConfiguration serviceConfig;
	
	public static final String NL = System.getProperty("line.separator");
	
	public static final String PADDING = "                 ";
	public static final String LINE = "                 --------------------------------------------";

	
	public static final String DL =
	 "-----------------------------------------------------------------------------------------------------";

	public static final String VERSION = "0.1.0";
	
	public static final String LOGO = "" +NL+NL
			+"888b     d888 d8b                           .d8888b.                            d8b" + NL
			+"8888b   d8888 Y8P                          d88P  Y88b                           Y8P " + NL
			+"88888b.d88888                              Y88b. " + NL
			+"888Y88888P888 888  .d8888b 888d888 .d88b.   \"Y888b.    .d88b.  888d888 888  888 888  .d8888b .d88b." + NL
			+"888 Y888P 888 888 d88P\"    888P\"  d88\"\"88b     \"Y88b. d8P  Y8b 888P\"   888  888 888 d88P\"   d8P  Y8b" + NL
			+"888  Y8P  888 888 888      888    888  888      \"888  88888888 888     Y88  88P 888 888     88888888" + NL
			+"888   \"   888 888 Y88b.    888    Y88..88P Y88b  d88P Y8b.     888      Y8bd8P  888 Y88b.   Y8b." + NL
			+"888       888 888  \"Y8888P 888     \"Y88P\"   \"Y8888P\"   \"Y8888  888       Y88P   888  \"Y8888P \"Y8888" + NL + NL
	+"=====================================================================================================" + NL
	+":: :: SIGMA Service. (MSVERSION)  :: Java Version (JAVAVERSION) :: SpringBoot Version (SPRINGBOOTVERSION) :: :: ::" + NL
	+"=====================================================================================================" + NL;


	/**
	 * Autowired using the Constructor
	 * @param serviceCfg
	 */
	public ServiceHelp(ServiceConfiguration serviceCfg) {
		serviceConfig = serviceCfg;
		counter++;
	}
	
	/**
	 * Returns the Restart Counter
	 * @return
	 */
	public static int getCounter() {
		return counter;
	}
	
	/**
	 * Print Properties
	 */
	@PostConstruct
	public void printProperties() {
		// Environment Variables
		for (Map.Entry<String, String> entry : serviceConfig.systemProperties().entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			log.debug("|System Property Key   = {}  | Value = {} ", key, value);
		}

		// Property Map (Application.Properties)
		for (Map.Entry<String, String> entry : serviceConfig.getAppPropertyMap().entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			log.debug("|System Property Key   = {}  | Value = {} ", key, value);
		}

		// Property List (Application.properties)
		List<String> properties = serviceConfig.getAppPropertyProductList();
		for(String p: properties) {
			log.info("|Service Property List = {} ", p);
		}
 	}
}
