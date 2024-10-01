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

import io.fusion.air.microservice.adapters.filters.HeaderManager;
import io.fusion.air.microservice.adapters.security.AuthorizeRequestAspect;
import io.fusion.air.microservice.adapters.security.ClaimsManager;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Service
@RequestScope
public class TokenManager {

    public final static String AUTH         = "auth";
    public final static String AUTH_REFRESH = "refresh";
    public final static String TX_USERS     = "tx-users";
    public final static String TX_SERVICE   = "tx-internal";
    public final static String TX_EXTERNAL  = "tx-external";

    @Autowired
    private ServiceConfiguration serviceConfig;

    @Autowired
    private ClaimsManager claimsManager;

    @Autowired
    private HeaderManager headerManager;

    @Value("${server.token.auth.expiry:300000}")
    private long tokenAuthExpiry;

    @Value("${server.token.refresh.expiry:1800000}")
    private long tokenRefreshExpiry;

    public TokenManager() {}

    /**
     * For External Testing
     * @param _serviceConfig
     */
    public TokenManager(ServiceConfiguration _serviceConfig, long tknExpiry, long tknRefreshExpiry) {
        this.serviceConfig = _serviceConfig;
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
     * @param _subject
     * @param _type
     * @param headers
     * @return
     */
    public String createTXToken(String _subject, String _type, HttpHeaders headers) {

        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("aud", "tx-services");
        claims.put("sub", _subject);
        claims.put("type",_type);
        claims.put("iss", serviceConfig.getServiceOrg());
        claims.put("rol", "User");
        claims.put("jti", UUID.randomUUID().toString());

        long txTokenExpiry = (tokenRefreshExpiry < 50) ? JsonWebToken.EXPIRE_IN_ONE_HOUR : tokenRefreshExpiry;
        String token = new JsonWebToken()
                            .init(serviceConfig.getTokenType())
                            .setIssuer(serviceConfig.getServiceOrg())
                            .generateToken(_subject,  serviceConfig.getServiceOrg(),  txTokenExpiry,  claims);

        if(headers != null) {
            // headers.add("TX-TOKEN", "Bearer " + token);
            headerManager.setResponseHeader(AuthorizeRequestAspect.TX_TOKEN, "Bearer " + token);
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
    public HashMap<String, String> createExternalToken(String serviceId, String serviceName, String serviceOwner,
                                      String servicesAllowed, HttpHeaders headers) {
        String subject      = serviceName;
        Map<String, Object> claims = getServiceClaims(serviceId, serviceName, serviceOwner, servicesAllowed);
        claims.put("type",TX_EXTERNAL);

        long txTokenExpiry =  JsonWebToken.EXPIRE_IN_ONE_DAY;
        String token = new JsonWebToken()
                            .init(serviceConfig.getTokenType())
                            .setIssuer(serviceConfig.getServiceOrg())
                            .generateToken( subject,  serviceConfig.getServiceOrg(),  txTokenExpiry,  claims);

        // Store Tokens
        HashMap<String, String> tokens = new HashMap<String, String>();
        tokens.put("authToken", "Bearer " + token);
        tokens.put("expiryTime", ""+txTokenExpiry);
        // Add Token to Headers
        if(headers != null) {
            // headers.add("Authorization", "Bearer " + token);
            headerManager.setResponseHeader("Authorization", "Bearer " + token);
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
    public HashMap<String, String> createInternalToken(String serviceId, String serviceName, String serviceOwner,
                                      String servicesAllowed, HttpHeaders headers) {
        String subject      = serviceName;

        // Auth Token
        Map<String, Object> claims = getServiceClaims(serviceId, serviceName, serviceOwner, servicesAllowed);
        claims.put("type",TX_SERVICE);
        long txTokenExpiry =  JsonWebToken.EXPIRE_IN_ONE_DAY;
        String token = new JsonWebToken()
                            .init(serviceConfig.getTokenType())
                            .setIssuer(serviceConfig.getServiceOrg())
                            .generateToken( subject,  serviceConfig.getServiceOrg(),  txTokenExpiry,  claims);

        // TX-Token
        claims.put("type",TX_USERS);
        String txToken = new JsonWebToken()
                            .init(serviceConfig.getTokenType())
                            .setIssuer(serviceConfig.getServiceOrg())
                            .generateToken( subject,  serviceConfig.getServiceOrg(),  txTokenExpiry,  claims);

        // Store Tokens
        HashMap<String, String> tokens = new HashMap<String, String>();
        tokens.put("authToken", "Bearer " + token);
        tokens.put("txToken", "Bearer " + txToken);
        tokens.put("expiryTime", ""+txTokenExpiry);
        // Add Token to Headers
        if(headers != null) {
            // headers.add("Authorization", "Bearer " + token);
            headerManager.setResponseHeader("Authorization", "Bearer " + token);
            headerManager.setResponseHeader("TX-TOKEN", "Bearer " + txToken);
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
    public HashMap<String, String>  createAuthorizationToken(String subject, HttpHeaders headers) {

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
        String refreshTkn = tokens.get("refresh");
        if(headers != null) {
            headerManager.setResponseHeader(AuthorizeRequestAspect.AUTH_TOKEN, "Bearer " + authToken);
            headerManager.setResponseHeader(AuthorizeRequestAspect.REFRESH_TOKEN, "Bearer " + refreshTkn);
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
        tokenAuthExpiry = (tokenAuthExpiry < 10) ? JsonWebToken.EXPIRE_IN_FIVE_MINS : tokenAuthExpiry;
        tokenRefreshExpiry = (tokenRefreshExpiry < 10) ? JsonWebToken.EXPIRE_IN_THIRTY_MINS : tokenRefreshExpiry;
        return  new JsonWebToken()
                    .init(serviceConfig.getTokenType())
                    .setTokenAuthExpiry(tokenAuthExpiry)
                    .setTokenRefreshExpiry(tokenRefreshExpiry)
                    .generateTokens(subject, serviceConfig.getServiceOrg(), authTokenClaims, refreshTokenClaims);
    }

}
