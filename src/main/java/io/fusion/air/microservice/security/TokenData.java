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

import java.security.Key;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
public class TokenData {

    private final String token;

    private final String issuer;

    private final int keyType;

    private final Key validatoryKey;

    public TokenData(String _token, String _issuer, int _keyType, Key _validatorKey) {
        token = _token;
        issuer = _issuer;
        keyType = _keyType;
        validatoryKey = _validatorKey;
    }

    /**
     * Returns the Token
     * @return
     */
    public String getToken() {
        return token;
    }

    /**
     * Returns the Issuer
     * @return
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Returns the Key Type
     * @return
     */
    public int getKeyType() {
        return keyType;
    }

    /**
     * Returns the Validator Key
     * @return
     */
    public Key getValidatoryKey() {
        return validatoryKey;
    }
}
