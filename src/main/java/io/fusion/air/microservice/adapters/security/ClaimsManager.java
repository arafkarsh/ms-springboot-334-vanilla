/**
 * (C) Copyright 2022 Araf Karsh Hamid
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
package io.fusion.air.microservice.adapters.security;

import io.fusion.air.microservice.domain.exceptions.AuthorizationException;
import io.fusion.air.microservice.utils.Utils;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Claims Manager
 *
 * Claims Manager handles all the Claims Extracted from the JWT Token
 * Claims Manager is Scoped at the Request Level and is Thread Safe as the ProxyMode is set to TARGET_CLASS.
 *
 * Scoped Proxy: The proxyMode = ScopedProxyMode.TARGET_CLASS creates a CGLIB proxy of the actual target bean.
 * Method Invocation: When methods of the injected bean are invoked in the singleton, the proxy routes those
 * calls to the instance of the bean that is tied to the current HTTP request.
 * Thread Safety: Since a new instance of the bean is created for each HTTP request, this setup is inherently
 * thread-safe for the request-scoped bean. Different threads handling different HTTP requests will each get
 * their own unique instance.
 * This way, the singleton bean is interacting with a request-specific instance of the request-scoped bean,
 * thanks to the proxy, which makes it safe and thread-local to the current request.
 * Note: Although the request-scoped bean is thread-safe in this context, the singleton bean into which it is
 * injected still needs to be designed to be thread-safe if it maintains any mutable shared state.
 *
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Service
@RequestScope
public class ClaimsManager {

    // Set Logger -> Lookup will automatically determine the class name.
    private static final Logger log = getLogger(lookup().lookupClass());

    private Claims claims;
    private boolean claimsInitialized = false;

    /**
     *
     * @param claims
     */
    public void setClaims(Claims claims) {
        this.claims = claims;
        claimsInitialized = true;
        String c =  Utils.toJsonString(this.claims);
        log.debug(">>> CLAIMS = {}", c);
    }

    /**
     *
     * Returns the Claims
     * @return
     */
    public Claims getClaims() {
        return claims;
    }

    /**
     * Returns True if the Claims are Initialized
     * @return
     */
    public boolean isClaimsInitialized() {
        if(!claimsInitialized) {
            throw new AuthorizationException("Tx-Token: Un-Authorized Access by user - Claims Not Initialized!");
        }
        String subject = this.getSubject();
        if(subject == null) {
            throw new AuthorizationException("Tx-Token: Un-Authorized Access by user - Subject Not Found!");
        }
        return claimsInitialized;
    }

    /**
     * Returns true if the App Specific value is there
     * @return
     */
    public boolean validate() {
        // Custom Implementation
        return true;
    }

    /**
     * Returns Audience
     * @return
     */
    public String getAudience() {
        return (String) claims.get("aud");
    }

    /**
     * Returns Subject
     * @return
     */
    public String getSubject() {
        return (String) claims.get("sub");
    }

    /**
     * Returns the User Role
     * @return
     */
    public String getUserRole() {
        return (String) claims.get("rol");
    }

    // ====================================================================================================
    // Claims from KeyCloak Authentication
    // ====================================================================================================

    /**
     * Returns the User ID
     * @return
     */
    public String getUserID() {
        return (claims != null) ? (String) claims.get("preferred_username") : "";
    }

    /**
     * Returns the User Name
     * @return
     */
    public String getUserName() {
        return (claims != null) ? (String) claims.get("name") : "";
    }

    /**
     * Returns the Email Address
     * @return
     */
    public String getEmail() {
        return (claims != null) ? (String) claims.get("email") : "";
    }
}
