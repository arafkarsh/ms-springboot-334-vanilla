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
import io.fusion.air.microservice.adapters.logging.MetricsCounter;
import io.fusion.air.microservice.domain.entities.order.OrderEntity;
import io.fusion.air.microservice.domain.exceptions.BusinessServiceException;
import io.fusion.air.microservice.domain.exceptions.ControllerException;
import io.fusion.air.microservice.domain.exceptions.InputDataException;
import io.fusion.air.microservice.domain.models.core.StandardResponse;
import io.fusion.air.microservice.domain.models.order.PaymentDetails;
import io.fusion.air.microservice.domain.models.order.PaymentStatus;
import io.fusion.air.microservice.domain.models.order.PaymentType;
import io.fusion.air.microservice.domain.ports.services.OrderService;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
import io.fusion.air.microservice.server.controllers.AbstractController;
// Swagger Open API
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
// Spring
import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;
// Java
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Order Controller to Save the Order and Process the Payments.
 * This is to demonstrate certain concepts in Exception Handling ONLY.
 * Order, Product, Cart all must be part of 3 different Microservices.
 *
 * @author arafkarsh
 * @version 1.0
 * 
 */
@CrossOrigin
@Configuration
@Validated // This enables validation for method parameters
@RestController
// "/ms-vanilla/api/v1"
@RequestMapping("${service.api.path}/order")
@RequestScope
@MetricsCounter(name = "fusion.air.order")
@Tag(name = "Order API", description = "To Manage (Add/Update/Delete/Search) Order CRUD Operations")
public class OrderControllerImpl extends AbstractController {

	// Set Logger -> Lookup will automatically determine the class name.
	private static final Logger log = getLogger(lookup().lookupClass());

	// @Autowired not required - Constructor based Autowiring
	private final ServiceConfiguration serviceConfig;
	private String serviceName;

	// @Autowired not required - Constructor based Autowiring
	private final OrderService orderService;

	/**
	 * Constructor for Autowiring
	 * @param _serviceConfig
	 * @param _orderService
	 */
	public OrderControllerImpl(ServiceConfiguration _serviceConfig,  OrderService _orderService) {
		serviceConfig = _serviceConfig;
		orderService = _orderService;
	}

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
	@MetricsCounter(endpoint = "/all")
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
	@MetricsCounter(endpoint = "/customer")
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
	@MetricsCounter(endpoint = "/save")
	public ResponseEntity<StandardResponse> saveOrder(@Valid @RequestBody OrderEntity _order) {
		log.debug("|"+name()+"|Request to Save Order ... "+_order);
		OrderEntity order = orderService.save(_order);
		StandardResponse stdResponse = createSuccessResponse("Order Saved!");
		stdResponse.setPayload(order);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Process the Payment
	 * To Demonstrate Exception Handling.
	 * The Error Code for the Exceptions will be automatically determined by the Framework.
	 * Error Code Prefix will be Different for Each Microservice.
	 */
	@Operation(
			summary = "Process Process the Payment using Credit/Debit Cards, Paypal etc.",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Process the payment",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = ResponseEntity.class))
					),
					@ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
					@ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
					@ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
			},
			parameters = {
					@Parameter(
							name = "custom-header",
							in = ParameterIn.HEADER,
							description = "Custom Parameter in the HTTP Header",
							required = false,
							schema = @Schema(type = "string", defaultValue = "2072dc75-d126-4442-a006-1f657c8973c2")
					)
			}
	)
	@PostMapping("/processPayments")
	@MetricsCounter(endpoint = "/processPayments")
	public ResponseEntity<StandardResponse> processPayments(@RequestBody PaymentDetails _payDetails) {
		log.debug("|"+name()+"|Request to process Payments... "+_payDetails);
		if(_payDetails != null) {
			if(_payDetails.getCardDetails().getExpiryYear() < LocalDate.now().getYear()) {
				throw new BusinessServiceException("Invalid Card Expiry Year");
			}
			if(_payDetails.getCardDetails().getExpiryMonth() < 1 ||  _payDetails.getCardDetails().getExpiryMonth() >12) {
				throw new BusinessServiceException("Invalid Card Expiry Month");
			}
			if (_payDetails.getOrderValue() > 0) {
				StandardResponse stdResponse = createSuccessResponse("Processing Success!");
				PaymentStatus ps = new PaymentStatus(
						"fb908151-d249-4d30-a6a1-4705729394f4",
						LocalDateTime.now(),
						"Accepted",
						UUID.randomUUID().toString(),
						LocalDateTime.now(),
						PaymentType.CREDIT_CARD);
				stdResponse.setPayload(ps);
				return ResponseEntity.ok(stdResponse);
			}
			throw new InputDataException("Invalid Order Value");
		}
		throw new ControllerException("Invalid Order!!!");
	}

 }