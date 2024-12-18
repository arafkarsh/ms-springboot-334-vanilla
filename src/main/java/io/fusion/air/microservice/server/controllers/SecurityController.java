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
import io.fusion.air.microservice.server.config.ServiceConfiguration;
// Swagger Open API
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
// Encrypt / Decrypt
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.jasypt.salt.RandomSaltGenerator;
// Spring
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
// Java
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.slf4j.Logger;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Security Controller for the Service
 * 
 * @author arafkarsh
 * @version 1.0
 * 
 */
@RestController
// "/service-name/api/v1/service"
@RequestMapping("${service.api.path}"+ ServiceConfiguration.HEALTH_PATH)
@Tag(name = "System - Health", description = "Health (Liveness, Readiness, ReStart.. etc)")
public class SecurityController {

	// Set Logger -> Lookup will automatically determine the class name.
	private static final Logger log = getLogger(lookup().lookupClass());

	/**
	 * Get Method Call to Convert Plain Text to Encrypted Text of the App
	 * 
	 * @return
	 */
    @Operation(summary = "Security Utils of Order Service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
            description = "Text Encrypted OK",
            content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
            description = "Failed to Encrypt Text.",
            content = @Content)
    })
	@GetMapping("/security/{text}")
	public ResponseEntity<HashMap<String, String>> encryptText(@PathVariable("text") String text) throws SecurityException {
		log.info("{} |Request to Encrypt of Service... ", LocalDateTime.now());
		String masterPassword = System.getenv("JASYPT_ENCRYPTOR_PASSWORD");

		if(masterPassword != null) {
			// Create a StandardPBEStringEncryptor instance
			StandardPBEStringEncryptor textEncryptor = new StandardPBEStringEncryptor();
			// Set encryption configurations
			textEncryptor.setPassword(masterPassword); // Master password
			// Use an AES-based PBE algorithm - PBEWithHmacSHA512AndAES_256
			String algo = "PBEWithHmacSHA512AndAES_256";
			textEncryptor.setAlgorithm(algo);
			//  Add Random IV and Salt
			textEncryptor.setIvGenerator(new RandomIvGenerator());
			textEncryptor.setSaltGenerator(new RandomSaltGenerator());
			log.info("Algorithm Used: {} ",algo);
			String encryptedText = textEncryptor.encrypt(text); // String to encrypt
			log.info("Encrypted Text: ENC({})", encryptedText);
			// Decrypt the text
			String decryptedText = textEncryptor.decrypt(encryptedText);
			log.info("Decrypted Text: {} ", decryptedText);
			HashMap<String, String> data = new LinkedHashMap<>();
			data.put("algo", algo);
			data.put("text", text);
			data.put("encrypted", encryptedText);
			return ResponseEntity.ok(data);
		}
		throw new SecurityException("Set ENV Variable JASYPT_ENCRYPTOR_PASSWORD for Encryption Key.");
	}
 }

