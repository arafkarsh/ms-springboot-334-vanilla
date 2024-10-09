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
import io.fusion.air.microservice.adapters.logging.MicroMeterCounter;
import io.fusion.air.microservice.adapters.security.AuthorizationRequired;
import io.fusion.air.microservice.domain.entities.order.ProductEntity;
import io.fusion.air.microservice.domain.exceptions.*;
import io.fusion.air.microservice.domain.models.core.StandardResponse;
import io.fusion.air.microservice.domain.models.order.PaymentDetails;
import io.fusion.air.microservice.domain.models.order.PaymentStatus;
import io.fusion.air.microservice.domain.models.order.PaymentType;
import io.fusion.air.microservice.domain.models.order.Product;
import io.fusion.air.microservice.domain.ports.services.ProductService;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
import io.fusion.air.microservice.server.controllers.AbstractController;
// Swagger
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
// Spring
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;
// Java
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Product Controller for the Service
 *
 * Only Selected Methods will be secured in this packaged - which are Annotated with
 * @AuthorizationRequired
 * @Operation(summary = "Cancel Product", security = { @SecurityRequirement(name = "bearer-key") })
 * 
 * @author arafkarsh
 * @version 1.0
 * 
 */
@CrossOrigin
@Configuration
@RestController
// "/ms-vanilla/api/v1"
@Validated // This enables validation for method parameters
@RequestMapping("${service.api.path}/product")
@RequestScope
@MicroMeterCounter(name = "fusion.air.product")
@Tag(name = "Product API", description = "Search Products, Create Products, Activate / DeActivate, Delete & Update Product")
public class ProductControllerImpl extends AbstractController {

	// Set Logger -> Lookup will automatically determine the class name.
	private static final Logger log = getLogger(lookup().lookupClass());
	
	// @Autowired not required - Constructor based Autowiring
	private final ServiceConfiguration serviceConfig;
	private String serviceName;
	// @Autowired not required - Constructor based Autowiring
	private final ProductService productServiceImpl;

	/**
	 * Constructor based Autowiring
	 * @param _serviceConfig
	 * @param _productServiceImpl
	 */
	public ProductControllerImpl(ServiceConfiguration _serviceConfig, ProductService _productServiceImpl) {
		serviceConfig = _serviceConfig;
		productServiceImpl = _productServiceImpl;
	}

	/**
	 * Create the Product
	 */
	@Operation(summary = "Create Product")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Create the Product",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "404",
					description = "Unable to Create the Product",
					content = @Content)
	})
	@PostMapping("/create")
	@MicroMeterCounter(endpoint = "/create")
	public ResponseEntity<StandardResponse> createProduct(@Valid @RequestBody Product _product) {
		log.debug("|"+name()+"|Request to Create Product... "+_product);
		ProductEntity prodEntity = productServiceImpl.createProduct(_product);
		StandardResponse stdResponse = createSuccessResponse("Product Created");
		stdResponse.setPayload(prodEntity);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * GET Method Call to Check the Product Status
	 * 
	 * @return
	 */
    @Operation(summary = "Get the Product By Product UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
            description = "Product Retrieved for status check",
            content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
            description = "Invalid Product ID.",
            content = @Content)
    })
	@GetMapping("/status/{productId}")
	@MicroMeterCounter(endpoint = "/status")
	@ResponseBody
	public ResponseEntity<StandardResponse> getProductStatus(@PathVariable("productId") UUID _productId,
														HttpServletRequest request,
														HttpServletResponse response) throws Exception {
		log.debug("|"+name()+"|Request to Get Product Status.. "+_productId);
		ProductEntity product = productServiceImpl.getProductById(_productId);
		StandardResponse stdResponse = createSuccessResponse("Data Fetch Success!");
		stdResponse.setPayload(product);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * GET Method Call to Get All the Products
	 *
	 * @return
	 */
	@Operation(summary = "Get All the Products")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "List All the Product",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "400",
					description = "No Products Found!",
					content = @Content)
	})
	@GetMapping("/all/")
	@MicroMeterCounter(endpoint = "/all")
	@ResponseBody
	public ResponseEntity<StandardResponse> getAllProducts(HttpServletRequest request,
														   HttpServletResponse response) throws Exception {
		return getAllProducts();
	}

	@Operation(summary = "Get All the Products (Secured)", security = { @SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "List All the Product",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "400",
					description = "No Products Found!",
					content = @Content)
	})
	@GetMapping("/all/secured/")
	@MicroMeterCounter(endpoint = "/all/secured")
	@ResponseBody
	public ResponseEntity<StandardResponse> getAllProductsSecured(HttpServletRequest request,
														   HttpServletResponse response) throws Exception {
		return getAllProducts();
	}

	/**
	 * Get All the Products
	 * @return
	 */
	private ResponseEntity<StandardResponse> getAllProducts() {
		log.debug("|"+name()+"|Request to get All Products ... ");
		List<ProductEntity> productList = productServiceImpl.getAllProduct();
		StandardResponse stdResponse = null;
		log.info("Products List = "+productList.size());
		if(productList == null || productList.isEmpty()) {
			productList = createFallBackProducts();
			stdResponse = createSuccessResponse("201","Fallback Data!");
		} else {
			stdResponse = createSuccessResponse("Data Fetch Success!");
		}
		stdResponse.setPayload(productList);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Search the Product by Product Name
	 */
	@Operation(summary = "Search Product By Product Name")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Product(s) Found!",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "400",
					description = "Unable to Find the Product(s)!",
					content = @Content)
	})
	@GetMapping("/search/product/{productName}")
	@MicroMeterCounter(endpoint = "/search/product")
	public ResponseEntity<StandardResponse> searchProductsByName(
			@PathVariable("productName")
			@NotBlank(message = "The Product Name is  required.")
			@Size(min = 3, max = 32, message = "The length of Product Name must be between 3 and 32 characters.")
			String _productName) {
		log.debug("|"+name()+"|Request to Search the Product By Name ... "+_productName);
		List<ProductEntity> products = productServiceImpl.fetchProductsByName(_productName);
		StandardResponse stdResponse = createSuccessResponse("Products Found For Search Term = "+_productName);
		stdResponse.setPayload(products);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Search the Product by Product price
	 */
	@Operation(summary = "Search Product By Product Price Greater Than or Equal To")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Product(s) Found!",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "400",
					description = "Unable to Find the Product(s)!",
					content = @Content)
	})
	@GetMapping("/search/price/{price}")
	@MicroMeterCounter(endpoint = "/search/price")
	public ResponseEntity<StandardResponse> searchProductsByPrice(@PathVariable("price") BigDecimal _price) {
		log.debug("|"+name()+"|Request to Search the Product By Price... "+_price);
		List<ProductEntity> products = productServiceImpl.fetchProductsByPriceGreaterThan(_price);
		StandardResponse stdResponse = createSuccessResponse("Products Found for Price >= "+_price);
		stdResponse.setPayload(products);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Search Active Products
	 */
	@Operation(summary = "Search Active Products")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Product(s) Found!",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "400",
					description = "Unable to Find the Product(s)!",
					content = @Content)
	})
	@GetMapping("/search/active/")
	@MicroMeterCounter(endpoint = "/search/active")
	public ResponseEntity<StandardResponse> searchActiveProducts() {
		log.debug("|"+name()+"|Request to Search the Active Products ... ");
		List<ProductEntity> products = productServiceImpl.fetchActiveProducts();
		StandardResponse stdResponse = createSuccessResponse("Active Products Found = "+products.size());
		stdResponse.setPayload(products);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * De-Activate the Product
	 */
	@AuthorizationRequired(role = "user")
	@Operation(summary = "De-Activate Product")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Product De-Activated",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "400",
					description = "Unable to De-Activate the Product",
					content = @Content)
	})
	@PutMapping("/deactivate/{productId}")
	@MicroMeterCounter(endpoint = "/deactivate")
	public ResponseEntity<StandardResponse> deActivateProduct(@PathVariable("productId") UUID _productId) {
		log.debug("|"+name()+"|Request to De-Activate the Product... "+_productId);
		ProductEntity product = productServiceImpl.deActivateProduct(_productId);
		StandardResponse stdResponse = createSuccessResponse("Product De-Activated");
		stdResponse.setPayload(product);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Activate the Product
	 */
	@AuthorizationRequired(role = "user")
	@Operation(summary = "Activate Product")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Product Activated",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "400",
					description = "Unable to Activate the Product",
					content = @Content)
	})
	@PutMapping("/activate/{productId}")
	@MicroMeterCounter(endpoint = "/activate")
	public ResponseEntity<StandardResponse> activateProduct(@PathVariable("productId") UUID _productId) {
		log.debug("|"+name()+"|Request to Activate the Product... "+_productId);
		ProductEntity product = productServiceImpl.activateProduct(_productId);
		StandardResponse stdResponse = createSuccessResponse("Product Activated");
		stdResponse.setPayload(product);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Update the Product Details
	 * This API Can be tested for Optimistic Lock Exceptions as the Entity is a Versioned Entity
	 */
	@Operation(summary = "Update the Product")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Product Updated",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "400",
					description = "Unable to Update the Product",
					content = @Content)
	})
	@PutMapping("/update/")
	@MicroMeterCounter(endpoint = "/update")
	public ResponseEntity<StandardResponse> updateProduct(@Valid @RequestBody ProductEntity _product) {
		log.debug("|"+name()+"|Request to Update Product Details... "+_product);
		ProductEntity prodEntity = productServiceImpl.updateProduct(_product);
		StandardResponse stdResponse = createSuccessResponse("Product Updated!");
		stdResponse.setPayload(prodEntity);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Update the Product Price
	 */
	@Operation(summary = "Update the Product Price")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Product Price Updated",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "400",
					description = "Unable to Update the Product Price",
					content = @Content)
	})
	@PutMapping("/update/price")
	@MicroMeterCounter(endpoint = "/update/price")
	public ResponseEntity<StandardResponse> updatePrice(@Valid @RequestBody ProductEntity _product) {
		log.debug("|"+name()+"|Request to Update Product Price... ["+_product);
		ProductEntity prodEntity = productServiceImpl.updatePrice(_product);
		StandardResponse stdResponse = createSuccessResponse("Product Price Updated");
		stdResponse.setPayload(prodEntity);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Update the Product Details
	 */
	@Operation(summary = "Update the Product Details")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Product Details Updated",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "400",
					description = "Unable to Update the Product Details",
					content = @Content)
	})
	@PutMapping("/update/details")
	@MicroMeterCounter(endpoint = "/update/details")
	public ResponseEntity<StandardResponse> updateProductDetails(@Valid @RequestBody ProductEntity _product) {
		log.debug("|"+name()+"|Request to Update Product Details... "+_product);
		ProductEntity prodEntity = productServiceImpl.updateProductDetails(_product);
		StandardResponse stdResponse = createSuccessResponse("Product Details Updated");
		stdResponse.setPayload(prodEntity);
		return ResponseEntity.ok(stdResponse);
	}

	/**
	 * Delete the Product
	 */
	@AuthorizationRequired(role = "User")
	@Operation(summary = "Delete Product", security = { @SecurityRequirement(name = "bearer-key") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Product Deleted",
					content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "400",
					description = "Unable to Delete the Product",
					content = @Content)
	})
	@DeleteMapping("/{productId}")
	@MicroMeterCounter(endpoint = "/delete")
	public ResponseEntity<StandardResponse> deleteProduct(@PathVariable("productId") UUID _productId) {
		log.debug("|"+name()+"|Request to Delete Product... "+_productId);
		productServiceImpl.deleteProduct(_productId);
		StandardResponse stdResponse = createSuccessResponse("Product Deleted!");
		return ResponseEntity.ok(stdResponse);
	}


	/**
	 * Create Fall Back Product for Testing Purpose ONLY
	 * @return
	 */
	private List<ProductEntity> createFallBackProducts() {
		List<ProductEntity> productList = new ArrayList<ProductEntity>();
		productList.add(new ProductEntity("iPhone 10", "iPhone 10, 64 GB", new BigDecimal(60000), "12345"));
		productList.add(new ProductEntity("iPhone 11", "iPhone 11, 128 GB", new BigDecimal(70000), "12345"));
		productList.add(new ProductEntity("Samsung Galaxy s20", "Samsung Galaxy s20, 256 GB", new BigDecimal(80000), "12345"));

		try {
			productServiceImpl.createProductsEntity(productList);
			productList = productServiceImpl.getAllProduct();
		} catch (Exception ignored) { ignored.printStackTrace();}
		return productList;
	}

	/**
	 * Process the Product
	 * To Demonstrate Exception Handling.
	 * The Error Code for the Exceptions will be automatically determined by the Framework.
	 * Error Code will be Different for Each Microservice.
	 */
	@Operation(
			summary = "Process Product",
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
	@PostMapping("/processProducts")
	@MicroMeterCounter(endpoint = "/processProducts")
	public ResponseEntity<StandardResponse> processProduct(@RequestBody PaymentDetails _payDetails) {
		log.debug("|"+name()+"|Request to process Product... "+_payDetails);
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
// throw new DuplicateDataException("Invalid Order Value");
// throw new InputDataException("Invalid Order Value");
// throw new BusinessServiceException("Invalid Order Value");
// throw new ResourceNotFoundException("Invalid Order Value");
// throw new RuntimeException("Invalid Order Value");