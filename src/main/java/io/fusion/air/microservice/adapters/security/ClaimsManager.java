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
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Service
@RequestScope
public class ClaimsManager {

    private Claims claims;
    private boolean claimsInitialized = false;

    /**
     *
     * @param _claims
     */
    public void setClaims(Claims _claims) {
        claims = _claims;
        claimsInitialized = true;
        System.out.println(">>> CLAIMS = "+ Utils.toJsonString(claims));
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
}
