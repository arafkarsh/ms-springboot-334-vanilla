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


import io.fusion.air.microservice.domain.exceptions.InputDataException;
import io.fusion.air.microservice.domain.models.core.StandardResponse;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
import io.fusion.air.microservice.server.config.ServiceHelp;
import io.fusion.air.microservice.server.models.EchoData;
import io.fusion.air.microservice.server.models.EchoResponseData;
import io.fusion.air.microservice.server.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

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
@Tag(name = "System - Echo", description = "Echo Tests (Get/Post) Request Scope, Session Scope, App Scope... ")
public class EchoController extends AbstractController {

	// Set Logger -> Lookup will automatically determine the class name.
	private static final Logger log = getLogger(lookup().lookupClass());

	@Autowired
	private EchoService echoService;

	@Autowired
	private EchoSessionService echoSessionService;

	@Autowired
	private EchoAppService echoAppService;

	@Autowired
	private MyService1 service1;

	@Autowired
	private MyService2 service2;

	@Autowired
	private MyService3 service3;
	
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
	@Operation(summary = "Get Echo back")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Echo OK",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "404",
					description = "Echo Not OK.",
					content = @Content)
	})
	@GetMapping("/echo")
	@ResponseBody
	public ResponseEntity<StandardResponse> getHealth(HttpServletRequest request) throws Exception {
		log.debug(name()+"|Request to Health of Service... ");
		StandardResponse stdResponse = createSuccessResponse("Service is OK!");
		HashMap<String, Object> payload = new LinkedHashMap<String, Object>();
		payload.put("requestScope", echoService.getEchoData());
		payload.put("sessionScope", echoSessionService.getEchoData());
		payload.put("appScope", echoAppService.getEchoData());

		service1.printData();
		service2.printData();
		service3.printData();

		stdResponse.setPayload(payload);
		return ResponseEntity.ok(stdResponse);
	}

    /**
     * Remote Echo Test
     * @param echoData
     * @return
     */
    @Operation(summary = "Service Echo World")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
            		description = "Service Echo World",
            		content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "400",
					description = "Service unable to deserialize!",
					content = @Content),
			@ApiResponse(responseCode = "404",
            		description = "Service unable to do Echo World!",
            		content = @Content)
    })
    @PostMapping("/echo/world")
    public ResponseEntity<StandardResponse> remoteEchoWorld(@RequestBody EchoData echoData) {
		log.debug(name()+"|Request for Echo ... "+echoData);
    	if(echoData == null) {
			throw new InputDataException("Empty EchoData");
		}
		EchoResponseData erd = new EchoResponseData(echoData.getWord()+" - New World");
    	echoService.setEchoData(erd);
    	echoSessionService.setEchoData(erd);
    	echoAppService.setEchoData(erd);

    	service1.printData();
		service2.printData();
		service3.printData();

		StandardResponse stdResponse = createSuccessResponse("Echo is Good!");
    	stdResponse.setPayload(erd);
		return ResponseEntity.ok(stdResponse);
    }

	/**
	 * Remote Echo Test
	 * @param echoData
	 * @return
	 */
	@Operation(summary = "Service Echo")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Service Echo",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "400",
					description = "Service unable to deserialize!",
					content = @Content),
			@ApiResponse(responseCode = "404",
					description = "Service unable to do Echo!",
					content = @Content)
	})
	@PostMapping("/echo")
	public ResponseEntity<StandardResponse> remoteEcho(@RequestBody EchoData echoData) {
		log.debug(name()+"|Request for Echo ... "+echoData);
		if(echoData == null) {
			throw new InputDataException("Empty EchoData");
		}
		EchoResponseData erd = new EchoResponseData(echoData.getWord());
		echoService.setEchoData(erd);
		echoSessionService.setEchoData(erd);
		echoAppService.setEchoData(erd);

		service1.printData();
		service2.printData();
		service3.printData();

		StandardResponse stdResponse = createSuccessResponse("Echo is Good!");
		stdResponse.setPayload(erd);
		return ResponseEntity.ok(stdResponse);
	}

 }

