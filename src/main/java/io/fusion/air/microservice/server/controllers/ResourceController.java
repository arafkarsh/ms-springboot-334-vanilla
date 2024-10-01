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

import io.fusion.air.microservice.domain.exceptions.ResourceNotFoundException;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Resource Controller for the Service
 * 
 * @author arafkarsh
 * @version 1.0
 */
@Configuration
@RestController
//  "/service-name/api/v1/"
@RequestMapping("${service.api.path}")
@RequestScope
@Tag(name = "System - Resources", description = "Static Files / Images / Videos etc")
public class ResourceController extends AbstractController {

	// Set Logger -> Lookup will automatically determine the class name.
	private static final Logger log = getLogger(lookup().lookupClass());

	@Autowired
	private ServiceConfiguration serviceConfig;
	private String serviceName;

	/**
	 * Get the Image
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Operation(summary = "Show the Image")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Image Retrieved",
					content = {@Content(mediaType = "application/image")}),
			@ApiResponse(responseCode = "404",
					description = "Error Retrieving the Image",
					content = @Content)
	})
	@GetMapping(value = "/images/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<InputStreamResource> getImageFiles(HttpServletRequest request, HttpServletResponse response,
											@PathVariable("imageName") String imageName) throws Exception {
		try {
			ClassPathResource imageFile = new ClassPathResource("static/images/" + imageName);
			return ResponseEntity
					.ok()
					.contentType(MediaType.IMAGE_JPEG)
					.body(new InputStreamResource(imageFile.getInputStream()));
		} catch (Exception ex) {
			return getErrorPage(ex);
		}
	}

	/**
	 * Get the Videos
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Operation(summary = "Show the Video")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "Video Retrieved",
					content = {@Content(mediaType = "application/html")}),
			@ApiResponse(responseCode = "404",
					description = "Error Retrieving the Video",
					content = @Content)
	})
	@GetMapping(value = "/video/{videoName}", produces = MediaType.ALL_VALUE)
	public ResponseEntity<InputStreamResource> getVideoFiles(HttpServletRequest request, HttpServletResponse response,
															@PathVariable("videoName") String videoName) throws Exception {
		try {
			ClassPathResource videoFile = new ClassPathResource("static/videos/" + videoName);
			return ResponseEntity
					.ok()
					.contentType(MediaType.ALL)
					.body(new InputStreamResource(videoFile.getInputStream()));
		} catch (Exception ex) {
			return getErrorPage(ex);
		}
	}

	/**
	 * Get the File
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Operation(summary = "Show the File")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",
					description = "File Retrieved",
					content = {@Content(mediaType = "application/html")}),
			@ApiResponse(responseCode = "404",
					description = "Error Retrieving the File",
					content = @Content)
	})
	@GetMapping(value = "/files/{fileName}", produces = MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<InputStreamResource> getHtmlFiles(HttpServletRequest request, HttpServletResponse response,
										@PathVariable("fileName") String fileName) throws Exception {
		try {
			ClassPathResource htmlFile = new ClassPathResource("static/files/" + fileName);
			return ResponseEntity
					.ok()
					.contentType(MediaType.valueOf(MediaType.TEXT_HTML_VALUE))
					.body(new InputStreamResource(htmlFile.getInputStream()));
		} catch (Exception ex) {
			return getErrorPage(ex);
		}
	}

	/**
	 * Returns Standard Error Page
 	 * @param e
	 * @return
	 * @throws Exception
	 */
	private ResponseEntity<InputStreamResource> getErrorPage(Exception e) throws Exception {
		try {
			ClassPathResource errorFile = new ClassPathResource("static/files/error.html");
			return ResponseEntity
					.ok()
					.contentType(MediaType.ALL)
					.body(new InputStreamResource(errorFile.getInputStream()));
		} catch (Exception ex) {
			throw new ResourceNotFoundException("File not found", e);
		}
	}
 }

