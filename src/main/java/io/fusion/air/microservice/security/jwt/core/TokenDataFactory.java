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
package io.fusion.air.microservice.security.jwt.core;
// Custom
import io.fusion.air.microservice.domain.exceptions.JWTTokenExtractionException;
import static io.fusion.air.microservice.security.jwt.core.JsonWebTokenConstants.*;
import static io.fusion.air.microservice.security.jwt.core.JsonWebTokenConstants.ERROR;
// Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
// Log
import org.slf4j.Logger;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Component
public class TokenDataFactory {

    // Set Logger -> Lookup will automatically determine the class name.
    private static final Logger log = getLogger(lookup().lookupClass());

    // Autowired using the Constructor
    private JsonWebTokenConfig jwtConfig;

    // Autowired using the Constructor
    private KeyCloakConfig keyCloakConfig;

    // Autowired using the Constructor
    private JsonWebTokenKeyManager keyManager;

    /**
     * Autowired using the Constructor
     * @param jwtCfg
     * @param kCloakCfg
     * @param kManager
     */
    @Autowired
    public TokenDataFactory(JsonWebTokenConfig jwtCfg, KeyCloakConfig kCloakCfg,
                             JsonWebTokenKeyManager kManager) {
        jwtConfig = jwtCfg;
        keyCloakConfig = kCloakCfg;
        keyManager = kManager;
    }

    /**
     * Extract the Token fromm the Authorization Header
     * ------------------------------------------------------------------------------------------------------
     * Authorization: Bearer AAA.BBB.CCC
     * ------------------------------------------------------------------------------------------------------
     *
     * @param jwToken
     * @param className
     * @return
     */
    public final TokenData getTokenData(String jwToken, String tokenType, String className) {
        if (jwToken != null && jwToken.startsWith(BEARER)) {
            String token =  jwToken.substring(7);
            if(token != null) {
                return createTokenData(token);
            }
        }
        String msg = "Access Denied: Unable to extract ["+tokenType+"] token from Header!";
        logTime( ERROR, msg,  className);
        throw new JWTTokenExtractionException(msg);
    }

    /**
     * Create Token Data with JWT and Validating Key
     * @param token
     * @return
     */
    public final TokenData createTokenData(String token) {
        if(keyCloakConfig.isKeyCloakEnabled()) {
            return createKeyCloakTokenData( token);
        } else {
            return createLocalTokenData( token);
        }
    }

    /**
     * Returns Local Key
     * @param token
     * @return
     */
    private TokenData createLocalTokenData(String token) {
        return new TokenData(token, jwtConfig.getTokenIssuer() , LOCAL_KEY,  keyManager.getValidatorLocalKey());
    }

    /**
     * Returns KeyCloak Key
     * @param token
     * @return
     */
    private TokenData createKeyCloakTokenData(String token) {
        return new TokenData(token, keyCloakConfig.getTokenIssuer() , KEYCLOAK_KEY,  keyManager.getValidatorKey());
    }

    /**
     * Returns true if the KeyCloak is enabled
     * @return
     */
    public boolean isKeyCloakEnabled() {
    	return keyCloakConfig.isKeyCloakEnabled();
    }

    /**
     * Log Time
     * @param status
     * @param msg
     * @param className
     */
    private void logTime(String status, String msg, String className) {
        log.info("2|TDF|TIME=0 ms|STATUS={}|CLASS={}|Msg={}", status,className, msg);
    }

}
