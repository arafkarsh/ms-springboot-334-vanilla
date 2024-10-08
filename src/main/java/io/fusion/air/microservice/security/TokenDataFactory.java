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
package io.fusion.air.microservice.security;

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

    @Autowired
    private JsonWebTokenConfig jwtConfig;

    @Autowired
    private KeyCloakConfig keyCloakConfig;

    @Autowired
    private JsonWebToken jwt;

    @Autowired
    private JsonWebTokenKeyManager keyManager;

    public TokenData createTokenData(String _token) {
        if(keyCloakConfig.isKeyCloakEnabled()) {
            return createKeyCloakTokenData( _token);
            // return new TokenData(_token, keyCloakConfig.getTokenIssuer(), KEYCLOAK_KEY, jwt.getValidatorKey());
        } else {
            return createLocalTokenData( _token);
            // return new TokenData(_token, jwtConfig.getTokenIssuer(), LOCAL_KEY, jwt.getValidatorLocalKey());
        }
    }

    public TokenData createLocalTokenData(String _token) {
        return new TokenData(_token, jwtConfig.getTokenIssuer() , LOCAL_KEY,  jwt.getValidatorLocalKey());
    }

    public TokenData createKeyCloakTokenData(String _token) {
        return new TokenData(_token, keyCloakConfig.getTokenIssuer() , KEYCLOAK_KEY,  jwt.getValidatorKey());
    }

    public boolean isKeyCloakEnabled() {
    	return keyCloakConfig.isKeyCloakEnabled();
    }

}
