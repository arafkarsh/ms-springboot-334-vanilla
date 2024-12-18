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
package io.fusion.air.microservice.adapters.controllers.secured;
// Custom
import io.fusion.air.microservice.adapters.logging.MetricsCounter;
import io.fusion.air.microservice.adapters.logging.MetricsPath;
import io.fusion.air.microservice.domain.models.core.StandardResponse;
import io.fusion.air.microservice.domain.models.order.PaymentDetails;
import io.fusion.air.microservice.domain.models.order.PaymentStatus;
import io.fusion.air.microservice.domain.models.order.PaymentType;
import io.fusion.air.microservice.server.controllers.AbstractController;
// Swagger
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
// Spring
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
// Java
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Payment Controller (Secured) for the Service
 *
 * All the calls in this package will be secured with JWT Token.
 * 
 * @author arafkarsh
 * @version 1.0
 * 
 */
@Validated // This enables validation for method parameters
@RestController
// "/ms-vanilla/api/v1"
@RequestMapping("${service.api.path}/payment")
@MetricsPath(name = "fusion.air.payment")
@Tag(name = "Secured Payments API", description = "")
public class PaymentControllerImpl extends AbstractController {

	// Set Logger -> Lookup will automatically determine the class name.
	private static final Logger log = getLogger(lookup().lookupClass());
	private String serviceName;

	/**
	 * Set the Service Name from Super
	 */
	public PaymentControllerImpl() {
		serviceName = super.name();
	}

	/**
	 * Get Method Call to Check the Health of the App
	 * 
	 * @return
	 */
    @Operation(summary = "Check the Payment status", security = { @SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
            @ApiResponse(responseCode = "200",
            description = "Payment Status Check",
            content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
            description = "Invalid Payment Reference No.",
            content = @Content)
    })
	@GetMapping("/status/{referenceNo}")
	@MetricsCounter(endpoint = "/status")
	public ResponseEntity<StandardResponse> getStatus(@PathVariable("referenceNo") String referenceNo,
														HttpServletRequest request)  {
		log.debug("| {} |Request to Payment Status of Service... ", serviceName);
		StandardResponse stdResponse = createSuccessResponse("Processing Success!");
		// Response Object
		stdResponse.setPayload(getResult(referenceNo, "Payment Status is good!"));
		// Return the Response
		return new ResponseEntity<>(stdResponse, HttpStatus.OK);
	}

	/**
	 * Process the Payments
	 */
    @Operation(summary = "Process Payments", security = { @SecurityRequirement(name = "bearer-key") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
            description = "Process the payment",
            content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
            description = "Unable to process the payment",
            content = @Content)
    })
    @PostMapping("/processPayments")
	@MetricsCounter(endpoint = "/processPayments")
	public ResponseEntity<StandardResponse> processPayments(@Valid @RequestBody PaymentDetails payDetails) {
		log.debug("| {} |Request to  process payments..... ", serviceName);
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

	/**
	 * Cancel the Payment
	 */
	@Operation(summary = "Cancel Payment", security = { @SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Payment Cancelled",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "404",
					description = "Unable to Cancel the Payment",
					content = @Content)
	})
	@DeleteMapping("/cancel/{referenceNo}")
	@MetricsCounter(endpoint = "/cancel")
	public ResponseEntity<StandardResponse> cancel(@PathVariable("referenceNo") String referenceNo) {
		log.debug("| {} |Request to  Cancel the payments..... ", serviceName);
		StandardResponse stdResponse = createSuccessResponse("Cancelled!");
		stdResponse.setPayload(getResult(referenceNo, "Payment cancelled!"));
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Update the Payment
	 */
	@Operation(summary = "Update Payment", security = { @SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Update the Payment",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "404",
					description = "Unable to Update the Payment",
					content = @Content)
	})
	@PutMapping("/update/{referenceNo}")
	@MetricsCounter(endpoint = "/update")
	public ResponseEntity<StandardResponse> updatePayment(@PathVariable("referenceNo") String referenceNo) {
		log.debug("| {} |Request to  Update the payments..... ", serviceName);
		StandardResponse stdResponse = createSuccessResponse("Updated!");
		stdResponse.setPayload(getResult(referenceNo, "Product updated"));
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Create the Result
	 * @param referenceNo
	 * @param obj
	 * @return
	 */
	private HashMap<String,Object> getResult(String referenceNo, Object obj) {
		HashMap<String,Object> status = new HashMap<>();
		status.put("ReferenceNo", referenceNo);
		status.put("Message", obj);
		return status;
	}
 }