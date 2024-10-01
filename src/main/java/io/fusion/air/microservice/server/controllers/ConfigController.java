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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fusion.air.microservice.adapters.security.AuthorizationRequired;
import io.fusion.air.microservice.domain.models.core.StandardResponse;
import io.fusion.air.microservice.server.config.ConfigMap;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
import io.fusion.air.microservice.server.config.ServiceHelp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Config Controller for the Service
 * 
 * @author arafkarsh
 * @version 1.0
 */
@Configuration
@RestController
//  "/service-name/api/v1/config"
@RequestMapping("${service.api.path}"+ ServiceConfiguration.CONFIG_PATH)
@RequestScope
@Tag(name = "System - Config", description = "Config (Environment, Secrets, ConfigMap.. etc)")
public class ConfigController extends AbstractController {

	// Set Logger -> Lookup will automatically determine the class name.
	private static final Logger log = getLogger(lookup().lookupClass());
	
	private final String title = "<h1>Welcome to Health Service<h1/>"
					+ ServiceHelp.NL
					+"<h3>Copyright (c) COMPANY Pvt Ltd, 2022</h3>"
					+ ServiceHelp.NL
					;

	@Autowired
	private ServiceConfiguration serviceConfig;
	private String serviceName;

	/**
	 * Show Service Environment
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@AuthorizationRequired(role = "Admin")
	@Operation(summary = "Show the Environment Settings ", security = { @SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Show the environment Settings",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "404",
					description = "Service Env is not ready.",
					content = @Content)
	})
	@GetMapping("/env")
	@ResponseBody
	public ResponseEntity<StandardResponse> getEnv(HttpServletRequest request) throws Exception {
		log.info(name()+"|Request to Get Environment Vars Check.. ");
		HashMap<String, String> sysProps = serviceConfig.systemProperties();
		StandardResponse stdResponse = createSuccessResponse("System Properties Ready!");
		stdResponse.setPayload(sysProps);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Show Service Configurations
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Operation(summary = "Show the ConfigMap Settings ")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Show the ConfigMap Settings",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "404",
					description = "Service ConfigMap is not ready.",
					content = @Content)
	})
	@GetMapping("/map")
	@ResponseBody
	public ResponseEntity<StandardResponse> getConfigMap(HttpServletRequest request) throws Exception {
		StandardResponse stdResponse = createSuccessResponse("Config is Ready!");
		ObjectMapper om = new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.findAndRegisterModules();
		String json = serviceConfig.toJSONString();
		log.debug(name()+"|Request to Get ServiceConfiguration .1. "+json);
		stdResponse.setPayload(serviceConfig.getConfigMap());
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Check the Current Log Levels
	 * @return
	 */
	@AuthorizationRequired(role = "User")
	@Operation(summary = "Show Service Log Levels", security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
            description = "Service Log Level Check",
            content = {@Content(mediaType = "application/text")}),
            @ApiResponse(responseCode = "404",
            description = "Service is not ready.",
            content = @Content)
    })
	@GetMapping("/log")
    public ResponseEntity<StandardResponse> log() {
		log.debug(name()+"|Request to Log Level.. ");
    	log.trace(name()+"|This is TRACE level message");
        log.debug(name()+"|This is a DEBUG level message");
        log.info(name()+"|This is an INFO level message");
        log.warn(name()+"|This is a WARN level message");
        log.error(name()+"|This is an ERROR level message");
		StandardResponse stdResponse = createSuccessResponse("Check the Log Files!");
		return ResponseEntity.ok(stdResponse);
    }
 }

