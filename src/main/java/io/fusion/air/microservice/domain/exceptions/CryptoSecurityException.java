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
package io.fusion.air.microservice.domain.exceptions;

import org.springframework.http.HttpStatus;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
public class CryptoSecurityException extends SecurityException {

    private static final String CRYPTO = "Crypto Exception: ";
    /**
     * CryptoSecurity Exception INTERNAL_SERVER_ERROR,
     * @param msg
     */
    public CryptoSecurityException(String msg) {
        super(CRYPTO+msg);
    }

    /**
     * CryptoSecurity Exception INTERNAL_SERVER_ERROR,
     * @param e
     */
    public CryptoSecurityException(Throwable e) {
        super(CRYPTO, HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    /**
     * CryptoSecurity Exception INTERNAL_SERVER_ERROR,
     * @param msg
     * @param e
     */
    public CryptoSecurityException(String msg, Throwable e) {
        super(CRYPTO+msg, HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
}
