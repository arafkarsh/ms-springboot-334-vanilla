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
package io.fusion.air.microservice.adapters.controllers.open;
// Custom
import io.fusion.air.microservice.domain.entities.order.CountryEntity;
import io.fusion.air.microservice.domain.entities.order.CountryGeoEntity;
import io.fusion.air.microservice.domain.models.core.StandardResponse;
import io.fusion.air.microservice.domain.ports.services.CountryService;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
import io.fusion.air.microservice.server.controllers.AbstractController;
// Swagger
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
// Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;
// Java
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Country Controller for the Service
 *
 * @author arafkarsh
 * @version 1.0
 * 
 */
@CrossOrigin
@Configuration
@RestController
// "/ms-vanilla/api/v1"
@RequestMapping("${service.api.path}/country")
@RequestScope
@Tag(name = "Country API", description = "Spring Examples with Pagination")
public class CountryControllerImpl extends AbstractController {

	// Set Logger -> Lookup will automatically determine the class name.
	private static final Logger log = getLogger(lookup().lookupClass());
	
	@Autowired
	private ServiceConfiguration serviceConfig;
	private String serviceName;

	@Autowired
	private CountryService countryService;

	/**
	 * GET Method Call to Get All the Geo Countries with Page and Size
	 * 
	 * @return
	 */
    @Operation(summary = "Get All Geo Countries by Page Number and Page Size")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
            description = "Geo Countries Retrieved!",
            content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
            description = "Invalid Page and Size!.",
            content = @Content)
    })
	@GetMapping("/geo/page/{page}/size/{size}")
	@ResponseBody
	public ResponseEntity<StandardResponse> fetchCountriesByPageAndSize(@PathVariable String page,
													@PathVariable("page") int _page,
													@PathVariable("size") int _size) throws Exception {
		log.debug("|"+name()+"|Request to Get All Countries by page no "+_page+" & Size = "+_size);

		Page<CountryGeoEntity> countries = countryService.getAllGeoCountries(_page, _size);
		StandardResponse stdResponse = createSuccessResponse("Data Fetch Success!");
		stdResponse.setPayload(countries);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * GET Method Call to Get All the Geo Countries
	 *
	 * @return
	 */
	@Operation(summary = "Get All the Geo Countries")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "List All the Geo Countries",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "400",
					description = "No Data Found",
					content = @Content)
	})
	@GetMapping("/geo/all/")
	@ResponseBody
	public ResponseEntity<StandardResponse> fetchAllGeoCountries(HttpServletRequest request,
														   HttpServletResponse response) throws Exception {
		log.debug("|"+name()+"|Request to get All Countries ... ");
		Page<CountryGeoEntity> countries = countryService.getAllGeoCountries();
		StandardResponse stdResponse = createSuccessResponse("Data Fetch Success!");
		stdResponse.setPayload(countries);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * GET Method Call to Get All the Countries
	 *
	 * @return
	 */
	@Operation(summary = "Get All the Countries")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "List All the Countries",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "400",
					description = "No Data Found",
					content = @Content)
	})
	@GetMapping("/all/")
	@ResponseBody
	public ResponseEntity<StandardResponse> fetchAlCountries(HttpServletRequest request,
														   HttpServletResponse response) throws Exception {
		log.debug("|"+name()+"|Request to get All Countries ... ");
		List<CountryEntity> countries = countryService.getAllCountries();
		StandardResponse stdResponse = createSuccessResponse("Data Fetch Success!");
		stdResponse.setPayload(countries);
		return ResponseEntity.ok(stdResponse);
	}

 }