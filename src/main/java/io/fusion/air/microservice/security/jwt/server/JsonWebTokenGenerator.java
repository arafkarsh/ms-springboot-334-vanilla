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
// Custom
import io.fusion.air.microservice.security.jwt.core.JsonWebTokenKeyManager;
import io.fusion.air.microservice.server.config.ServiceConfig;

import static io.fusion.air.microservice.security.jwt.core.JsonWebTokenConstants.*;
// JWT
import io.jsonwebtoken.Jwts;
// Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
// Java
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * ms-springboot-334-vanilla / JsonWebTokenGenerator
 *
 * Generates JsonWebToken Based on Public Key or Secret Key
 *
 * @author: Araf Karsh Hamid
 * @version: 0.1
 * @date: 2024-12-19T12:36 PM
 */
@Component
public class JsonWebTokenGenerator {

    // Autowired using the Constructor
    private ServiceConfig serviceConfig;

    // Autowired using the Constructor
    private JsonWebTokenKeyManager keyManager;

    /**
     * JsonWebToken Generator
     * Generates Tokens based on Secret Key or Public Key
     *
     * Autowired using the Constructor
     * @param serviceConfig
     * @param keyManager
     */
    @Autowired
    public JsonWebTokenGenerator(ServiceConfig serviceConfig,
                                 JsonWebTokenKeyManager keyManager) {
        this.serviceConfig = serviceConfig;
        this.keyManager = keyManager;
    }

    /**
     * Add Default Claims
     * @param claims
     * @return
     */
    private Map<String, Object>  addDefaultClaims(Map<String, Object> claims) {
        String aud = (serviceConfig != null) ? serviceConfig.getServiceName() : "general";
        claims.putIfAbsent("aud", aud);
        claims.putIfAbsent("jti", UUID.randomUUID().toString());
        claims.putIfAbsent("rol", ROLE_USER);
        return claims;
    }

    /**
     * Generate Authorize Bearer Token and Refresh Token
     * Returns in a HashMap
     * token = Authorization Token
     * refresh = Refresh token to re-generate the Authorize Token
     * API Usage
     * HashMap<String,String> tokens = jwt.generateTokens(subject, issuer,
     *                                                      tokenExpiryTime, refreshTokenExpiryTime);
     *
     * @param subject
     * @param issuer
     * @param tokenExpiryTime
     * @param refreshTokenExpiryTime
     * @return
     */
    public Map<String,String> generateTokens(String subject, String issuer, long tokenExpiryTime, long refreshTokenExpiryTime) {
        Map<String, Object> claimsToken = addDefaultClaims(new HashMap<>());
        Map<String, Object> claimsRefreshToken = addDefaultClaims(new HashMap<>());
        return generateTokens( subject,  issuer,  tokenExpiryTime,  refreshTokenExpiryTime, claimsToken, claimsRefreshToken);
    }

    /**
     * Generate Authorize Bearer Token and Refresh Token
     * Returns in a HashMap
     * token = Authorization Token
     * refresh = Refresh token to re-generate the Authorize Token
     * API Usage
     * HashMap<String,String> tokens = jwt.generateTokens(subject, issuer, tokenExpiryTime,
     * 									                refreshTokenExpiryTime,  claimsToken,  claimsRefreshToken)
     *
     * @param subject
     * @param issuer
     * @param tokenExpiryTime
     * @param refreshTokenExpiryTime
     * @param claimsToken
     * @param claimsRefreshToken
     * @return
     */
    public Map<String,String>  generateTokens(String subject, String issuer, long tokenExpiryTime, long refreshTokenExpiryTime,
                                              Map<String,Object> claimsToken, Map<String,Object> claimsRefreshToken) {
        addDefaultClaims(claimsToken);
        addDefaultClaims(claimsRefreshToken);
        HashMap<String, String> tokens  = new HashMap<>();
        String tokenAuth 	= generateToken(subject, issuer, tokenExpiryTime, claimsToken);
        String tokenRefresh = generateToken(subject, issuer, refreshTokenExpiryTime, claimsRefreshToken);
        tokens.put(AUTH_TOKEN  , tokenAuth);
        tokens.put(REFRESH_TOKEN, tokenRefresh);
        try {
            tokens.put(EXPIRES_IN, "" + (tokenExpiryTime / 1000));
            tokens.put(REFRESH_EXPIRES_IN, "" + (refreshTokenExpiryTime / 1000));
        } catch (Exception e) {
            tokens.put(EXPIRES_IN, "" + tokenExpiryTime);
            tokens.put(REFRESH_EXPIRES_IN, "" + refreshTokenExpiryTime);
        }
        tokens.put("token_type", "Bearer");
        tokens.put("not-before-policy", "0");
        tokens.put("session_state", UUID.randomUUID().toString());
        tokens.put("scope", "");
        tokens.put("mode", "Local Auth");
        return tokens;
    }

    /**
     * Generate Token for the User
     *
     * @param userId
     * @param expiryTime
     * @return
     */
    public String generateToken(String userId, long expiryTime) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("aud", "general");
        claims.put("jti", UUID.randomUUID().toString());
        return generateToken(userId,keyManager.getIssuer(),expiryTime,claims);
    }

    /**
     * Generate Token with Claims
     *
     * @param userId
     * @param expiryTime
     * @param claims
     * @return
     */
    public String generateToken(String userId, long expiryTime, Map<String, Object> claims) {
        return generateToken(userId, keyManager.getIssuer(), expiryTime, claims);
    }

    /**
     * Generate Token with Claims
     *
     * @param userId
     * @param issuer
     * @param expiryTime
     * @param claims
     * @return
     */
    public String generateToken(String userId, String issuer, long expiryTime, Map<String, Object> claims) {
        return generateToken( userId,  issuer,  expiryTime, claims, keyManager.getKey());
    }

    /**
     * Generate Token with Claims and with Either Secret Key or Private Key
     *
     * @param userId
     * @param issuer
     * @param expiryTime
     * @param claims
     * @param key
     * @return
     */
    private String generateToken(String userId, String issuer, long expiryTime,
                                Map<String, Object> claims, Key key) {
        long currentTime = System.currentTimeMillis();
        return Jwts.builder()
                .claims(claims)
                .subject(userId)
                .issuer(issuer)
                .issuedAt(new Date(currentTime))
                .expiration(new Date(currentTime + expiryTime))
                // Key Secret Key or Public/Private Key
                .signWith(key)
                .compact();
    }

    /**
     * Validate the Token Expiry Time
     * @param tokenExpiry
     * @param auth
     * @return
     */
    public static final long tokenExpiryValidator(long tokenExpiry, boolean auth) {
        if (auth) {
            // For Auth Token
            return (tokenExpiry > EXPIRE_IN_TEN_MINS) ?  EXPIRE_IN_TEN_MINS : tokenExpiry;
        }
        // For Other Token like Refresh or Tx Token
        return (tokenExpiry > EXPIRE_IN_THIRTY_MINS) ? EXPIRE_IN_THIRTY_MINS  : tokenExpiry;
    }
}
