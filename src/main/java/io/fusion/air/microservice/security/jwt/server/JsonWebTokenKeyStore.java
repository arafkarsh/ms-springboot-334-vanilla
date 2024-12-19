/**
 * Copyright (c) 2024 Araf Karsh Hamid
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * <p>
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 * <p>
 * or (per the licensee's choosing)
 * <p>
 * under the terms of the Apache 2 License version 2.0
 * as published by the Apache Software Foundation.
 */
package io.fusion.air.microservice.security.jwt.server;

import java.security.Key;

/**
 * ms-springboot-334-vanilla / JsonWebTokenKeyStore 
 *
 * @author: Araf Karsh Hamid
 * @version: 0.1
 * @date: 2024-12-19T12:50 PM
 */
public class JsonWebTokenKeyStore {

    private final int tokenType;
    private final Key signingKey;
    private final Key validatorKey;
    private final Key validatorLocalKey;
    private final String issuer;

    /**
     * Json Web Token Key Store contains the following keys
     * - Signing Key
     * - Validator Key (KeyCloak or Local)
     * - Issuer
     *  - Token Type (Secret or Public)
     *
     * @param tokenType
     * @param signingKey
     * @param validatorKey
     * @param validatorLocalKey
     * @param issuer
     */
    public JsonWebTokenKeyStore(int tokenType, Key signingKey,
                                Key validatorKey, Key validatorLocalKey,
                                String issuer) {
        this.tokenType = tokenType;
        this.signingKey = signingKey;
        this.validatorKey = validatorKey;
        this.validatorLocalKey = validatorLocalKey;
        this.issuer = issuer;
    }

    /**
     * Token Type - Secret or Public Key
     * @return
     */
    public int getTokenType() {
        return tokenType;
    }

    /**
     * Returns the Signing Key
     * @return
     */
    public Key getSigningKey() {
        return signingKey;
    }

    /**
     * Returns the Validator Key
     * @return
     */
    public Key getValidatorKey() {
        return validatorKey;
    }

    /**
     * Returns the Validator Local Key
     * @return
     */
    public Key getValidatorLocalKey() {
        return validatorLocalKey;
    }

    /**
     * Returns the Issuer
     * @return
     */
    public String getIssuer() {
        return issuer;
    }
}
