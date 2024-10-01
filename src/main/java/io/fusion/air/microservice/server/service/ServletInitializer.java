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

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import io.fusion.air.microservice.ServiceBootStrap;

/**
 * SpringBootServletInitializer is an interface to run SpringApplication 
 * from a traditional WAR deployment. It binds Servlet, Filter and 
 * ServletContextInitializer beans from the application context to the 
 * server
 * 
 * @author arafkarsh
 *
 */
public class ServletInitializer extends SpringBootServletInitializer {

	/**
	 * 
	 */
	@Override
	protected SpringApplicationBuilder configure( SpringApplicationBuilder application) {
		return application.sources(ServiceBootStrap.class);
	}

}
