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
// Custom
import io.fusion.air.microservice.adapters.security.AuthorizationRequired;
import io.fusion.air.microservice.domain.exceptions.AbstractServiceException;
import io.fusion.air.microservice.domain.models.core.StandardResponse;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
// Swagger API
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
// Spring
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// Java
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.slf4j.Logger;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Config Controller for the Service
 * 
 * @author arafkarsh
 * @version 1.0
 */
@RestController
//  "/service-name/api/v1/config"
@RequestMapping("${service.api.path}"+ ServiceConfiguration.CONFIG_PATH)
@Tag(name = "System - Config", description = "Config (Environment, Secrets, ConfigMap.. etc)")
public class ConfigController extends AbstractController {

	// Set Logger -> Lookup will automatically determine the class name.
	private static final Logger log = getLogger(lookup().lookupClass());

	// Autowired using the Constructor
	private final ServiceConfiguration serviceConfig;
	private final String serviceName;

	/**
	 * Autowired using the Constructor
	 * @param serviceCfg
	 */
	public ConfigController(ServiceConfiguration serviceCfg) {
		serviceConfig = serviceCfg;
		serviceName = super.name();
	}

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
	public ResponseEntity<StandardResponse> getEnv(HttpServletRequest request) throws AbstractServiceException {
		log.info("{} |Request to Get Environment Vars Check.. ", serviceName);
		Map<String, String> sysProps = serviceConfig.systemProperties();
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
	public ResponseEntity<StandardResponse> getConfigMap(HttpServletRequest request) throws AbstractServiceException {
		StandardResponse stdResponse = createSuccessResponse("Config is Ready!");
		String json = serviceConfig.toJSONString();
		log.debug("{} |Request to Get ServiceConfiguration .1. {} ", serviceName, json);
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
    public ResponseEntity<StandardResponse> printLogs() {
		log.debug("{} |Request to Log Level.. ", serviceName);
    	log.trace("{} |This is TRACE level message", serviceName);
        log.debug("{} |This is a DEBUG level message", serviceName);
        log.info("{} |This is an INFO level message", serviceName);
        log.warn("{} |This is a WARN level message", serviceName);
        log.error("{} |This is an ERROR level message", serviceName);
		StandardResponse stdResponse = createSuccessResponse("Check the Log Files!");
		return ResponseEntity.ok(stdResponse);
    }
 }

