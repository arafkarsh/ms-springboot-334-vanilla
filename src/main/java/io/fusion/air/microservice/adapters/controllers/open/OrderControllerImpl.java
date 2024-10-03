/**
 * (C) Copyright 2023 Araf Karsh Hamid
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
//  Custom
import io.fusion.air.microservice.domain.entities.order.OrderEntity;
import io.fusion.air.microservice.domain.models.core.StandardResponse;
import io.fusion.air.microservice.domain.ports.services.OrderService;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
import io.fusion.air.microservice.server.controllers.AbstractController;
// Swagger Open API
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
// Spring
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;
// Java
import jakarta.validation.Valid;
import java.util.List;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Order for the Service
 *
 * @author arafkarsh
 * @version 1.0
 * 
 */
@CrossOrigin
@Configuration
@RestController
// "/ms-cache/api/v1"
@RequestMapping("${service.api.path}/order")
@RequestScope
@Tag(name = "Order API", description = "To Manage (Add/Update/Delete/Search) Order CRUD Operations")
public class OrderControllerImpl extends AbstractController {

	// Set Logger -> Lookup will automatically determine the class name.
	private static final Logger log = getLogger(lookup().lookupClass());
	
	@Autowired
	private ServiceConfiguration serviceConfig;
	private String serviceName;

	@Autowired
	private OrderService orderService;


	/**
	 * GET Method Call to ALL Orders
	 *
	 * @return
	 */
	@Operation(summary = "Get The Orders")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Order Retrieved!",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "400",
					description = "Invalid Order ID",
					content = @Content)
	})
	@GetMapping("/all")
	@ResponseBody
	public ResponseEntity<StandardResponse> fetchAllOrders() throws Exception {
		log.debug("|"+name()+"|Request to Get Order For the Customers ");
		List<OrderEntity> orders = orderService.findAll();
		StandardResponse stdResponse = createSuccessResponse("Order Retrieved. Orders =  "+orders.size());
		stdResponse.setPayload(orders);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * GET Method Call to Get Cart for the Customer
	 * 
	 * @return
	 */
    @Operation(summary = "Get The Order for the Customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
            description = "Order Retrieved!",
            content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
            description = "Invalid Order ID",
            content = @Content)
    })
	@GetMapping("/customer/{customerId}")
	@ResponseBody
	public ResponseEntity<StandardResponse> fetchOrder(@PathVariable("customerId") String customerId) throws Exception {
		log.debug("|"+name()+"|Request to Get Order For the Customer "+customerId);
		List<OrderEntity> orders = orderService.findByCustomerId(customerId);
		StandardResponse stdResponse = createSuccessResponse("Order Retrieved. Orders =  "+orders.size());
		stdResponse.setPayload(orders);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Save Order
	 */
	@Operation(summary = "Save Order")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Order Saved!",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "404",
					description = "Unable to Save Order",
					content = @Content)
	})
	@PostMapping("/save")
	public ResponseEntity<StandardResponse> saveOrder(@Valid @RequestBody OrderEntity _order) {
		log.debug("|"+name()+"|Request to Save Order ... "+_order);
		OrderEntity order = orderService.save(_order);
		StandardResponse stdResponse = createSuccessResponse("Order Saved!");
		stdResponse.setPayload(order);
		return ResponseEntity.ok(stdResponse);
	}

 }