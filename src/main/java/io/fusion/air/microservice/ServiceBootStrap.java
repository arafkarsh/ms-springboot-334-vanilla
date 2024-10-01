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

import jakarta.annotation.PostConstruct;
// import javax.servlet.MultipartConfigElement;
// import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.http.HttpServletRequest;

import io.fusion.air.microservice.adapters.aop.ExceptionHandlerAdvice;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
import io.fusion.air.microservice.server.controllers.HealthController;

import org.slf4j.Logger;

// Spring Framework
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.util.unit.DataSize;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

// Open API Imports
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

// Cache
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * Micro Service - Spring Boot Application
 * API URL : http://localhost:9090/service/api/v1/swagger-ui.html
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
	private final String title = "<h1>Welcome to MICRO service<h1/>"
			+"<h3>Copyright (c) COMPANY, 2022</h3>"
			+"<h5>Build No: BN :: Build Date: BD :: </h5>";

	private static ConfigurableApplicationContext context;

	@Autowired
	private ServiceConfiguration serviceConfig;

	// Get the Service Name from the properties file
	@Value("${service.name:NameNotDefined}")
	private String serviceName = "Unknown";
	
	/**
	 * Start the Microservice
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		// Start the Server
		start(args);
		// API URL : http://localhost:9090/service/api/v1/swagger-ui.html
	}

	/**
	 * Start the Server
	 * @param args
	 */
	public static void start(String[] args) {
		log.info("Booting Service ..... ..");
		try {
			context = SpringApplication.run(ServiceBootStrap.class, args);
			log.info("Booting Service ..... ...Startup completed!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Restart the Server
	 */
	public static void restart() {
		log.info("Restarting Service ..... .. 1");
		ApplicationArguments args = context.getBean(ApplicationArguments.class);
		log.info("Restarting Service ..... .. 2");

		Thread thread = new Thread(() -> {
			context.close();
			start(args.getSourceArgs());
		});
		log.info("Restarting Service ..... .. 3");

		thread.setDaemon(false);
		thread.start();
	}

	/**
	 * Load the Configuration
	 */
	@PostConstruct
	public void configure() {
	}

	@Bean
	public WebMvcConfigurer corsConfigurer()
	{
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*");
			}
		};
	}

	/**
	 * Micro Service - Home Page
	 * @return
	 */
	@GetMapping("/root")
	public String home(HttpServletRequest request) {
		log.info("Request to Home Page of Service... "+printRequestURI(request));
		return (serviceConfig == null) ? this.title :
				this.title.replaceAll("MICRO", serviceConfig.getServiceName())
						.replaceAll("COMPANY", serviceConfig.getServiceOrg())
						.replaceAll("BN", "" + serviceConfig.getBuildNumber())
						.replaceAll("BD", serviceConfig.getBuildDate());
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
		log.info("HttpServletRequest: ["+sb.toString()+"]");
		return sb.toString();
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

	/**
	 * Open API v3 Docs - All
	 * Reference: https://springdoc.org/faq.html
	 * @return
	 */
	@Bean
	public GroupedOpenApi allPublicApi() {
		return GroupedOpenApi.builder()
				.group(serviceConfig.getServiceName()+"-service")
				.pathsToMatch(serviceConfig.getServiceApiPath()+"/**")
				.build();
	}

	/**
	 * Open API v3 Docs - MicroService
	 * Reference: https://springdoc.org/faq.html
	 * @return
	 */
	@Bean
	public GroupedOpenApi appPublicApi() {
		return GroupedOpenApi.builder()
				.group(serviceConfig.getServiceName()+"-service-core")
				.pathsToMatch(serviceConfig.getServiceApiPath()+"/**")
				.pathsToExclude(serviceConfig.getServiceApiPath()+"/service/**", serviceConfig.getServiceApiPath()+"/config/**")
				.build();
	}

	/**
	 * Open API v3 Docs - Core Service
	 * Reference: https://springdoc.org/faq.html
	 * Change the Resource Mapping in HealthController
	 *
	 * @see HealthController
	 */
	@Bean
	public GroupedOpenApi configPublicApi() {
		// System.out.println;
		return GroupedOpenApi.builder()
				.group(serviceConfig.getServiceName()+"-service-config")
				.pathsToMatch(serviceConfig.getServiceApiPath()+"/config/**")
				.build();
	}

	@Bean
	public GroupedOpenApi systemPublicApi() {
		return GroupedOpenApi.builder()
				.group(serviceConfig.getServiceName()+"-service-health")
				.pathsToMatch(serviceConfig.getServiceApiPath()+"/service/**")
				.build();
	}

	/**
	 * Open API v3
	 * Reference: https://springdoc.org/faq.html
	 * @return
	 */
	@Bean
	public OpenAPI buildOpenAPI() {
		return new OpenAPI()
				.servers(getServers())
				.info(new Info()
						.title(serviceConfig.getServiceName()+" Service")
						.description(serviceConfig.getServiceDetails())
						.version(serviceConfig.getServerVersion())
						.license(new License().name("License: "+serviceConfig.getServiceLicense())
								.url(serviceConfig.getServiceUrl()))
				)
				.externalDocs(new ExternalDocumentation()
						.description(serviceConfig.getServiceName()+" Service Source Code")
						.url(serviceConfig.getServiceApiRepository())
				)
				.components(new Components().addSecuritySchemes("bearer-key",
						new SecurityScheme()
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT"))
				);
	}

	/**
	 * Get the List of Servers for Open API Docs - Swagger
	 * @return
	 */
	private List<Server> getServers() {
		List<Server> serverList = new ArrayList<Server>();

		Server dev = new Server();
		dev.setUrl(serviceConfig.getServerHostDev());
		dev.setDescription(serviceConfig.getServerHostDevDesc());
		Server uat = new Server();
		uat.setUrl(serviceConfig.getServerHostUat());
		uat.setDescription(serviceConfig.getServerHostUatDesc());
		Server prod = new Server();
		prod.setUrl(serviceConfig.getServerHostProd());
		prod.setDescription(serviceConfig.getServerHostProdDesc());

		serverList.add(dev);
		serverList.add(uat);
		serverList.add(prod);

		return serverList;
	}

	@Bean
	ForwardedHeaderFilter forwardedHeaderFilter() {
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

	/**
	 * All file upload till 512 MB
	 * returns MultipartConfigElement
	 * @return
	 */
	/**
	 * Deprecated from SpringBoot 3.1
	@Bean
	public MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize(DataSize.ofBytes(500000000L));
		return factory.createMultipartConfig();
	}
	 */

	@Primary
	@Bean
	public ExceptionHandlerAdvice serviceExceptionAdvisor(){
		return new ExceptionHandlerAdvice();
	}
}
