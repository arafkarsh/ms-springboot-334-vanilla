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

import jakarta.servlet.http.HttpServletRequest;

import io.fusion.air.microservice.adapters.security.AuthorizationRequired;
import io.fusion.air.microservice.domain.exceptions.InputDataException;
import io.fusion.air.microservice.domain.models.core.StandardResponse;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
import io.fusion.air.microservice.server.config.ServiceHelp;
import io.fusion.air.microservice.server.models.EchoResponseData;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

// Logging System
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import static java.lang.invoke.MethodHandles.lookup;

import io.fusion.air.microservice.ServiceBootStrap;
import io.fusion.air.microservice.server.models.EchoData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

/**
 * Health Controller for the Service
 * 
 * @author arafkarsh
 * @version 1.0
 * 
 */
@Configuration
@RestController
// "/service-name/api/v1/service"
@RequestMapping("${service.api.path}"+ ServiceConfiguration.HEALTH_PATH)
@RequestScope
@Tag(name = "System - Health", description = "Health (Liveness, Readiness, ReStart.. etc)")
public class HealthController extends AbstractController {

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
	 * Get Method Call to Check the Health of the App
	 * 
	 * @return
	 */
    @Operation(summary = "Health Check of the Service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
            description = "Health Check OK",
            content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
            description = "Service is in bad health.",
            content = @Content)
    })
	@GetMapping("/live")
	@ResponseBody
	public ResponseEntity<StandardResponse> getHealth(HttpServletRequest request) throws Exception {
		log.debug(name()+"|Request to Health of Service... ");
		StandardResponse stdResponse = createSuccessResponse("Service is OK!");
		return ResponseEntity.ok(stdResponse);
	}
    
    @Operation(summary = "Service Readiness Check")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
            description = "Service Readiness Check",
            content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
            description = "Service is not ready.",
            content = @Content)
    })
	@GetMapping("/ready")
	@ResponseBody
	public ResponseEntity<StandardResponse> isReady(HttpServletRequest request) throws Exception {
		log.debug(name()+"|Request to Ready Check.. ");
		StandardResponse stdResponse = createSuccessResponse("Service is Ready!");
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Restart the Service
	 */
	@AuthorizationRequired(role = "Admin")
	@Operation(summary = "Service ReStart", security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
            description = "Service ReStart",
            content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
            description = "Service is not ready.",
            content = @Content)
    })
    @PostMapping("/restart")
    public void restart() {
		log.info(name()+"|Server Restart Request Received ....");
		if(serviceConfig != null && serviceConfig.isServerRestart()) {
    		log.info(name()+"|Restarting the service........");
    		ServiceBootStrap.restart();
    	}
    }
    
	/**
	 * Basic Testing
	 * 
	 * @param request
	 * @return
	 */
	@AuthorizationRequired(role = "User")
	@Operation(summary = "Service Home", security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
            description = "Service Home",
            content = {@Content(mediaType = "application/text")}),
            @ApiResponse(responseCode = "404",
            description = "Service is not ready.",
            content = @Content)
    })
	@GetMapping("/home")
	@ResponseBody
	public String apiHome(HttpServletRequest request) {
		log.info("|Request to /home/ path... ");
		StringBuilder sb = new StringBuilder();
		sb.append(title);
		sb.append("<br>");
		sb.append(printRequestURI(request));
		return sb.toString();
	}
 }

