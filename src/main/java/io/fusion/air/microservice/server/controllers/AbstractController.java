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

import io.fusion.air.microservice.adapters.security.ClaimsManager;
import io.fusion.air.microservice.domain.models.core.StandardResponse;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
import io.fusion.air.microservice.server.config.ServiceHelp;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpServletRequest;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Abstract Controller for the Service
 * All Other Controllers can extend from this Base Controller which sets the URL base
 * 
 * @author arafkarsh
 * @version 1.0
 * 
 */
public abstract class AbstractController {

	// Set Logger -> Lookup will automatically determine the class name.
	public static final Logger log = getLogger(lookup().lookupClass());
	
	@Autowired
	private ServiceConfiguration serviceConfig;
	private String serviceName;

	@Autowired
	private ClaimsManager claimsManager;

	/**
	 * Returns Claims Manager
	 * @return
	 */
	public ClaimsManager getClaims() {
		return claimsManager;
	}

	/**
	 * Returns the Service Name
	 * @return
	 */
	public final  String name() {
		if(serviceName == null) {
			if(serviceConfig == null) {
				log.debug("1|AC|TIME=|STATUS=Error|Class=|Msg=Error in Autowiring Service config!!!");
				serviceName = "|NoServiceName";
			} else {
				serviceName = "|" + serviceConfig.getServiceName() + "Service";
				log.debug("1|AC|TIME=|STATUS=INIT|Class=|Version="+ServiceHelp.VERSION);
			}
		}
		return serviceName;
	}

	/**
	 * Returns StandardResponse for Success
	 * @param _msg
	 * @return
	 */
	public final StandardResponse createSuccessResponse(String _msg) {
		return createSuccessResponse("200", _msg);
	}

	/**
	 * Returns StandardResponse for Success
	 * @param _statusCode
	 * @param _msg
	 * @return
	 */
	public final StandardResponse createSuccessResponse(String _statusCode, String _msg) {
		String prefix = (serviceConfig != null) ? serviceConfig.getServiceAPIErrorPrefix() : "99";
		StandardResponse stdResponse = new StandardResponse();
		stdResponse.initSuccess(prefix + _statusCode, _msg);
		return stdResponse;
	}
	
	/**
	 * Print the Request
	 * 
	 * @param request
	 * @return
	 */
	public final String printRequestURI(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		String[] req = request.getRequestURI().split("/");
		sb.append("Params Size = "+req.length+" : ");
		for(int x=0; x < req.length; x++) {
			sb.append(req[x]).append("|");
		}
 		sb.append("\n");
		log.info(sb.toString());
		return sb.toString();
	}
 }