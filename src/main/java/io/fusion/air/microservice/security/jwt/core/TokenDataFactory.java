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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Component
public class TokenDataFactory {

    public static final int LOCAL_KEY 				= 1;
    public static final int KEYCLOAK_KEY 			= 2;

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
     * Create Token Data with JWT and Validating Key
     * @param token
     * @return
     */
    public TokenData createTokenData(String token) {
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

}
