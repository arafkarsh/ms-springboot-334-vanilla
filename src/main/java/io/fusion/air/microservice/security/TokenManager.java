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
package io.fusion.air.microservice.security;
// Custom
import io.fusion.air.microservice.adapters.filters.HeaderManager;
import io.fusion.air.microservice.adapters.security.AuthorizeRequestAspect;
import io.fusion.air.microservice.adapters.security.ClaimsManager;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
// Spring
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
// Java
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Service
@RequestScope
public class TokenManager {

    // Set Logger -> Lookup will automatically determine the class name.
    private static final Logger log = getLogger(lookup().lookupClass());

    public static final String AUTH         = "auth";
    public static final String AUTH_REFRESH = "refresh";
    public static final String TX_USERS     = "tx-users";
    public static final String TX_SERVICE   = "tx-internal";
    public static final String TX_EXTERNAL  = "tx-external";
    public static final String BEARER = "Bearer ";

    // Autowired using the Constructor
    private ServiceConfiguration serviceConfig;

    // Autowired using the Constructor
    private ClaimsManager claimsManager;

    // Autowired using the Constructor
    private HeaderManager headerManager;

    @Value("${server.token.auth.expiry:300000}")
    private long tokenAuthExpiry;

    @Value("${server.token.refresh.expiry:1800000}")
    private long tokenRefreshExpiry;

    public TokenManager() {}

    /**
     * Autowired using the Constructor
     * @param serviceCfg
     * @param cManager
     * @param hManager
     */
    @Autowired
    public TokenManager(ServiceConfiguration serviceCfg,
                        ClaimsManager cManager, HeaderManager hManager) {
        serviceConfig = serviceCfg;
        claimsManager = cManager;
        headerManager = hManager;
    }

    /**
     * For External Testing
     *
     * @param serviceCfg
     * @param tknExpiry
     * @param tknRefreshExpiry
     */
    public TokenManager(ServiceConfiguration serviceCfg, long tknExpiry, long tknRefreshExpiry) {
        serviceConfig = serviceCfg;
        tokenAuthExpiry = tknExpiry;
        tokenRefreshExpiry = tknRefreshExpiry;
    }

    /**
     * Returns Claims
     * @return
     */
    public ClaimsManager getClaims() {
        return claimsManager;
    }

    /**
     * Create TX-Token with Subject, Token Type and Add to Header
     *
     * @param subject
     * @param type
     * @param headers
     * @return
     */
    public String createTXToken(String subject, String type, HttpHeaders headers) {

        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("aud", "tx-services");
        claims.put("sub", subject);
        claims.put("type",type);
        claims.put("iss", serviceConfig.getServiceOrg());
        claims.put("rol", "User");
        claims.put("jti", UUID.randomUUID().toString());

        long txTokenExpiry = (tokenRefreshExpiry < 50) ? JsonWebTokenConstants.EXPIRE_IN_ONE_HOUR : tokenRefreshExpiry;
        String token = new JsonWebTokenNew()
                            .init(serviceConfig.getTokenType())
                            .generateToken(subject,  serviceConfig.getServiceOrg(),  txTokenExpiry,  claims);

        if(headers != null) {
            headerManager.setResponseHeader(AuthorizeRequestAspect.TX_TOKEN, BEARER + token);
        }
        return token;
    }

    /**
     * Create External Service Token
     *
     * @param serviceName
     * @param servicesAllowed
     * @param headers
     * @return
     */
    public Map<String, String> createExternalToken(String serviceId, String serviceName, String serviceOwner,
                                      String servicesAllowed, HttpHeaders headers) {
        String subject      = serviceName;
        Map<String, Object> claims = getServiceClaims(serviceId, serviceName, serviceOwner, servicesAllowed);
        claims.put("type",TX_EXTERNAL);

        long txTokenExpiry =  JsonWebTokenConstants.EXPIRE_IN_ONE_DAY;
        String token = new JsonWebToken()
                            .init(serviceConfig.getTokenType())
                            .generateToken( subject,  serviceConfig.getServiceOrg(),  txTokenExpiry,  claims);

        // Store Tokens
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put("authToken", BEARER + token);
        tokens.put("expiryTime", ""+txTokenExpiry);
        // Add Token to Headers
        if(headers != null) {
            headerManager.setResponseHeader("Authorization", BEARER + token);
        }
        return tokens;
    }

    /**
     * Create Internal Service Token
     *
     * @param serviceName
     * @param servicesAllowed
     * @return
     */
    public Map<String, String> createInternalToken(String serviceId, String serviceName, String serviceOwner,
                                      String servicesAllowed, HttpHeaders headers) {
        String subject      = serviceName;

        // Auth Token
        Map<String, Object> claims = getServiceClaims(serviceId, serviceName, serviceOwner, servicesAllowed);
        claims.put("type",TX_SERVICE);
        long txTokenExpiry =  JsonWebTokenConstants.EXPIRE_IN_ONE_DAY;
        String token = new JsonWebToken()
                            .init(serviceConfig.getTokenType())
                            .generateToken( subject,  serviceConfig.getServiceOrg(),  txTokenExpiry,  claims);

        // TX-Token
        claims.put("type",TX_USERS);
        String txToken = new JsonWebToken()
                            .init(serviceConfig.getTokenType())
                            .generateToken( subject,  serviceConfig.getServiceOrg(),  txTokenExpiry,  claims);

        // Store Tokens
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put("authToken", BEARER + token);
        tokens.put("txToken", BEARER + txToken);
        tokens.put("expiryTime", ""+txTokenExpiry);
        // Add Token to Headers
        if(headers != null) {
            headerManager.setResponseHeader("Authorization", BEARER + token);
            headerManager.setResponseHeader("TX-TOKEN", BEARER + txToken);
        }
        // Return Auth & TX Tokens
        return tokens;
    }

    /**
     * Returns Service Claims
     * @param serviceId
     * @param serviceName
     * @param serviceOwner
     * @param servicesAllowed
     * @return
     */
    private Map<String, Object> getServiceClaims(String serviceId, String serviceName, String serviceOwner,
                                                 String servicesAllowed) {
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("aud", servicesAllowed);
        claims.put("sub", serviceName);
        claims.put("iss", serviceConfig.getServiceOrg());
        claims.put("rol", "Service");
        claims.put("serviceId", serviceId);
        claims.put("service", serviceName);
        claims.put("owner", serviceOwner);
        claims.put("jti", UUID.randomUUID().toString());

        return claims;
    }

    /**
     * Add Authorization Tokens
     *
     * @param subject
     * @param headers
     * @return
     */
    public Map<String, String>  createAuthorizationToken(String subject, HttpHeaders headers) {

        Map<String, Object> authClaims = new LinkedHashMap<>();
        authClaims.put("aud", "generic");
        authClaims.put("sub", subject);
        authClaims.put("type",AUTH);
        authClaims.put("iss", serviceConfig.getServiceOrg());
        authClaims.put("rol", "User");
        authClaims.put("jti", UUID.randomUUID().toString());

        Map<String, Object> refreshClaims = new LinkedHashMap<>();
        refreshClaims.put("aud", "generic");
        refreshClaims.put("sub", subject);
        refreshClaims.put("type",AUTH_REFRESH);
        refreshClaims.put("iss", serviceConfig.getServiceOrg());
        refreshClaims.put("rol", "User");
        refreshClaims.put("jti", UUID.randomUUID().toString());

        HashMap<String, String> tokens = refreshTokens(subject, authClaims, refreshClaims);
        String authToken = tokens.get("token");
        String refreshTkn = tokens.get(AUTH_REFRESH);
        if(headers != null) {
            headerManager.setResponseHeader(AuthorizeRequestAspect.AUTH_TOKEN, BEARER + authToken);
            headerManager.setResponseHeader(AuthorizeRequestAspect.REFRESH_TOKEN, BEARER + refreshTkn);
        }
        return tokens;
    }

    /**
     * Refresh Tokens
     * 1. Auth Token
     * 2. Refresh Token
     * @param subject
     * @param authTokenClaims
     * @param refreshTokenClaims
     * @return
     */
    private HashMap<String, String> refreshTokens(String subject,
                                                  Map<String, Object> authTokenClaims, Map<String, Object> refreshTokenClaims) {
        tokenAuthExpiry = (tokenAuthExpiry < 10) ? JsonWebTokenConstants.EXPIRE_IN_FIVE_MINS : tokenAuthExpiry;
        tokenRefreshExpiry = (tokenRefreshExpiry < 10) ? JsonWebTokenConstants.EXPIRE_IN_THIRTY_MINS : tokenRefreshExpiry;
        return  new JsonWebTokenNew()
                    .init(serviceConfig.getTokenType())
                    .setTokenAuthExpiry(tokenAuthExpiry)
                    .setTokenRefreshExpiry(tokenRefreshExpiry)
                    .generateTokens(subject, serviceConfig.getServiceOrg(), authTokenClaims, refreshTokenClaims);
    }

    /**
     * Create Admin Token
     * @param subject
     * @return
     */
    public String adminToken(String subject, String issuer) {
        Map<String, Object> claims = getClaims(subject,  issuer);
        claims.put("rol", "Admin");

        long txTokenExpiry = (tokenRefreshExpiry < 50) ? JsonWebTokenConstants.EXPIRE_IN_ONE_HOUR : tokenRefreshExpiry;
        String st = JsonWebToken.printExpiryTime(txTokenExpiry);
        log.info("Admin Token Expiry in Days:Hours:Mins  {}", st);
        return new JsonWebTokenNew()
                .init(serviceConfig.getTokenType())
                .generateToken( subject,  issuer,  txTokenExpiry,  claims);
    }

    /**
     * Create Claims
     * @param subject
     * @param issuer
     * @return
     */
    private Map<String, Object> getClaims(String subject, String issuer) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("aud", serviceConfig.getServiceName());
        claims.put("jti", UUID.randomUUID().toString());
        claims.put("sub", subject);
        claims.put("iss", issuer);
        claims.put("type",TokenManager.TX_USERS);
        claims.put("rol", "User");
        return claims;
    }

}
