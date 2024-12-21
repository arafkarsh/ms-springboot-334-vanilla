/**
 * (C) Copyright 2022 Araf Karsh Hamid
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
package io.fusion.air.microservice;

// Custom
import io.fusion.air.microservice.server.config.ServiceConfig;
import io.fusion.air.microservice.utils.Utils;
// Spring Framework
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
// FasterXML
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
// Java
import java.util.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;
// Spring Cache
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Micro Service - Spring Boot Application
 * API URL : http://localhost:9334/service/api/v1/swagger-ui.html
 *
 * @author arafkarsh
 */
@EnableScheduling
@ServletComponentScan
@ComponentScan(basePackages="io.fusion.air.microservice")
@RestController
@EnableCaching
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@SpringBootApplication(scanBasePackages = { "io.fusion.air.microservice" })
// @EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class ServiceBootStrap {

	// Set Logger -> Lookup will automatically determine the class name.
	private static final Logger log = getLogger(lookup().lookupClass());

	// All CAPS Words will be replaced using data from application.properties
	private  static final String SERVICE_TITLE = "<h1>Welcome to MICRO service</h1>"
			+"<h3>Copyright (c) COMPANY, 2022</h3>"
			+"<h5>Build No: BN :: Build Date: BD :: </h5>";

	private static ConfigurableApplicationContext context;

	// Autowired using Constructor Injection
	private final ServiceConfig serviceConfig;

	@Value("${spring.profiles.default:dev}")
	private static String activeProfile;

	/**
	 * Autowired using the Constructor Injection
	 * Microservice Bootstrap
	 * @param serviceConfig
	 */
	public ServiceBootStrap(ServiceConfig serviceConfig) {
		this.serviceConfig = serviceConfig;
	}
	
	/**
	 * Start the Microservice
	 * API URL : http://localhost:9334/ms-vanilla/api/v1/swagger-ui/index.html
	 * @param args
	 */
	public static void main(String[] args) {
		start(args);			// Start the Server
	}

	/**
	 * Start the Server
	 * @param args
	 */
	public static void start(String[] args) {
		log.info("Booting MicroService ..... ..");
		try {
			context = SpringApplication.run(ServiceBootStrap.class, args);
			// Set a default profile if no other profile is specified
			ConfigurableEnvironment environment = context.getEnvironment();
			if (environment.getActiveProfiles().length == 0) {
				log.info("Profile is missing, so defaulting to {} Profile!", activeProfile);
				environment.addActiveProfile(activeProfile);
			}
			log.info("Booting Microservice... Startup completed!");
		} catch (Exception e) {
			log.debug(Utils.getStackTraceAsString(e));
		}
	}

	/**
	 * Restart the Server
	 */
	public static void restart() {
		log.info("Restarting Service.... .. ");
		ApplicationArguments args = context.getBean(ApplicationArguments.class);
		Thread thread = new Thread(() -> {
			context.close();
			start(args.getSourceArgs());
		});
		log.info("Restarting Service.... Completed");

		thread.setDaemon(false);
		thread.start();
	}

	/**
	 * Load the Configuration
	 */
	@PostConstruct
	public void configure() {
		log.debug("For Future Usage..");
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("host*");
			}
		};
	}

	/**
	 * Micro Service - Home Page
	 * @return
	 */
	@GetMapping("/root")
	public String home(HttpServletRequest request) {
		String result = printRequestURI(request);
		log.info("Request to Home Page of Service...{} ",  result);
		return (serviceConfig == null) ? SERVICE_TITLE :
				SERVICE_TITLE.replace("MICRO", serviceConfig.getServiceName())
						.replace("COMPANY", serviceConfig.getServiceOrg())
						.replace("BN", "" + serviceConfig.getBuildNumber())
						.replace("BD", serviceConfig.getBuildDate());
	}

	/**
	 * Print the Request
	 *
	 * @param request
	 * @return
	 */
	public static String printRequestURI(final HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append("URI: ").append(request.getRequestURI());
		String[] req = request.getRequestURI().split("/");
		sb.append("Params Size = "+req.length+" : ");
		for(int x=0; x < req.length; x++) {
			sb.append(req[x]).append("|");
		}
		sb.append("\n");
		String result = sb.toString();
		log.info("HttpServletRequest: [ {} ]", result);
		return result;
	}

	/**
	 * CommandLineRunner Prints all the Beans defined ...
	 * @param ctx
	 * @return
	 */
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			log.debug("Inspect the beans provided by Spring Boot:");
			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				log.debug(beanName);
			}
		};
	}

	@Bean
	public ForwardedHeaderFilter forwardedHeaderFilter() {
		return new ForwardedHeaderFilter();
	}
	/**
	 * Returns the REST Template
	 * @return
	 */
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	/**
	 * Returns the Object Mapper
	 * @return
	 */

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.findAndRegisterModules();
	}
}
