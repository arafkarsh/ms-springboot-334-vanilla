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
import io.swagger.v3.oas.annotations.tags.Tag;
// Jakarta
import jakarta.validation.Valid;
// Spring
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
// Log
import org.slf4j.Logger;
import java.time.LocalDateTime;
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
public class PaymentOpenControllerImpl extends AbstractController {

	// Set Logger -> Lookup will automatically determine the class name.
	private static final Logger log = getLogger(lookup().lookupClass());
	private String serviceName;

	/**
	 * Set the Service Name from Super
	 */
	public PaymentOpenControllerImpl() {
		serviceName = super.name();
	}

	/**
	 * Process the Payments
	 * For testing the Field Validations of Payment details Data Model
	 */
    @Operation(summary = "Process Payments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
            description = "Process the payment - For Testing Field Validations!",
            content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
            description = "Unable to process the payment",
            content = @Content)
    })
    @PostMapping("/processPayments/open")
	@MetricsCounter(endpoint = "/processPayments/open")
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
 }