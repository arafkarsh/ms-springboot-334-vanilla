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
package io.fusion.air.microservice.adapters.filters;
// Custom
// Jakarta
import io.fusion.air.microservice.adapters.security.jwt.ClaimsManager;
import io.fusion.air.microservice.domain.exceptions.AuthorizationException;
import io.fusion.air.microservice.domain.models.core.StandardResponse;
import io.fusion.air.microservice.security.jwt.client.JsonWebTokenValidator;
import io.fusion.air.microservice.security.jwt.core.TokenData;
import io.fusion.air.microservice.security.jwt.core.TokenDataFactory;
import io.fusion.air.microservice.server.config.ServiceConfig;
import io.fusion.air.microservice.utils.Utils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
// Spring
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
// Java
import org.slf4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;

import static io.fusion.air.microservice.security.jwt.core.JsonWebTokenConstants.AUTH_TOKEN;
import static io.fusion.air.microservice.security.jwt.core.JsonWebTokenConstants.TX_TOKEN;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * ms-springboot-334-vanilla / JwtFilter 
 *
 * @author: Araf Karsh Hamid
 * @version: 0.1
 * @date: 2024-12-20T11:06 PM
 */
// "/ms-vanilla/api/v1"
// @WebFilter(urlPatterns = "/ms-vanilla/api/v1/*", dispatcherTypes = {DispatcherType.REQUEST})
@Component
@Order(2)
public class JwtFilter implements Filter {
    // Set Logger -> Lookup will automatically determine the class name.
    private static final Logger log = getLogger(lookup().lookupClass());

    // Autowired using the Constructor
    private ServiceConfig serviceConfig;

    // Autowired using the Constructor
    private ClaimsManager claimsManager;

    // Autowired using the Constructor
    private TokenDataFactory tokenDataFactory;

    /**
     * Autowired using the Constructor
     * @param claimsManager
     */
    public JwtFilter(ClaimsManager claimsManager, TokenDataFactory tokenDataFactory,
                     ServiceConfig serviceConfig) {
        this.claimsManager = claimsManager;
        this.tokenDataFactory = tokenDataFactory;
        this.serviceConfig = serviceConfig;
    }

    /**
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        log.info("JWT Filter in Action... ");
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        // Extract Tokens if Available
        String authToken = extractToken( httpRequest,  AUTH_TOKEN);
        String txToken = extractToken( httpRequest,  TX_TOKEN);
        String errorMsg = "";
        try {
            // IF ONLY Auth Token Available
            if (authToken != null && txToken == null) {
                setClaims(authToken, AUTH_TOKEN);
            }
            // IF Tx Token is VALID
            if (txToken != null) {
                setClaims(txToken, TX_TOKEN);
            }
            // Continue the filter chain
            filterChain.doFilter(httpRequest, httpResponse);
        } catch(Exception e) {
            errorMsg = e.getMessage();
            throwError(httpResponse);
        } finally {
            log.info("|JwtFilter={}", errorMsg);
        }
    }

    /**
     * Set the Claims from the Extracted token
     * @param token
     * @param name
     */
    private void setClaims(String token, String name) {
        log.info("JWT Filter in Action...  Set {} Claims ", name);
        try {
            TokenData tokenData = tokenDataFactory.createTokenData(token);
            Claims claims = JsonWebTokenValidator.getAllClaims(tokenData);
            claimsManager.setClaims(claims);
        } catch (Exception e) {
            throw new AuthorizationException("Issue Extracting the "+name+" Token : "+e.getMessage());
        }
    }

    /**
     * Extract the Token from the HTTP Headers
     * @param request
     * @param tokenType
     * @return
     */
    private String extractToken(HttpServletRequest request, String tokenType) {
        log.debug("JWT Filter in Action... Extracting the Tokens... {} ", tokenType);
        String header = request.getHeader(tokenType);
        return (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;
    }

    /**
     * Throw Error if JWT Token Validation Fails.
     * @param httpResponse
     * @throws IOException
     */
    private void throwError( HttpServletResponse httpResponse) throws IOException{
        if (!httpResponse.isCommitted()) {
            String errorPrefix = serviceConfig.getServiceApiErrorPrefix();
            StandardResponse error = Utils.createErrorResponse(
                    null,errorPrefix, "403", HttpStatus.FORBIDDEN,
                    "The request was rejected by JwtFilter!");
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.setContentType("application/json");
            String json = Utils.toJsonString(error);
            PrintWriter out = httpResponse.getWriter();
            out.write(json);
            out.flush();
            MDC.clear();
        }
    }
}
