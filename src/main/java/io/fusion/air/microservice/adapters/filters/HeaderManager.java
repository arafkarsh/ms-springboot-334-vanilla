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
package io.fusion.air.microservice.adapters.filters;

import io.fusion.air.microservice.adapters.security.AuthorizeRequestAspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Enumeration;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Component
@RequestScope
public class HeaderManager {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    /**
     * Adds Response Header
     * @param key
     * @param value
     */
    public void setResponseHeader(String key, String value) {
        if(key != null && value != null) {
            response.setHeader(key, value);
        }
    }

    /**
     * Returns HttpServletRequest
     * @return
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * Returns HttpServletResponse
     * @return
     */
    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * Transfers Headers from Request to Response
     */
    public void transferHeadersFromRequestToResponse() {
        HeaderManager.returnHeaders(request, response);
    }

    /**
     * Extract All Headers from Request
     * @return
     */
    public  HttpHeaders extractHeaders() {
        return HeaderManager.extractHeaders(request);
    }

    /**
     * Extract All the Tokens
     * @return
     */
    public HttpHeaders extractTokens()  {
        return HeaderManager.extractTokens(request);
    }

    /**
     * Returns Headers and Cookies
     *
     * @param request
     * @param response
     */
    public static void returnHeaders(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(token != null) {
            response.setHeader(HttpHeaders.AUTHORIZATION, token);
        }
        String refresh = request.getHeader(AuthorizeRequestAspect.REFRESH_TOKEN);
        if(refresh != null) {
            response.setHeader(AuthorizeRequestAspect.REFRESH_TOKEN, refresh);
        }
        String txToken = request.getHeader(AuthorizeRequestAspect.TX_TOKEN);
        if (txToken != null) {
            response.setHeader(AuthorizeRequestAspect.TX_TOKEN, txToken);
        }
    }

    /**
     * Returns Auth Token
     * @param request
     * @return
     */
    public static String getAuthToken(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(token != null) {
            token = token.replaceAll("Bearer", "");
        }
        return token;
    }

    /**
     * Returns Refresh Token
     * @param request
     * @return
     */
    public static String getRefreshToken(HttpServletRequest request) {
        String token = request.getHeader(AuthorizeRequestAspect.REFRESH_TOKEN);
        if(token != null) {
            token = token.replaceAll("Bearer", "");
        }
        return token;
    }

    /**
     * Returns Tx Token
     * @param request
     * @return
     */
    public static String getTxToken(HttpServletRequest request) {
        String token = request.getHeader(AuthorizeRequestAspect.TX_TOKEN);
        if(token != null) {
            token = token.replaceAll("Bearer", "");
        }
        return token;
    }

    /**
     * Extract Headers
     * @param request
     * @return
     */
    public static HttpHeaders extractHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> hNames = request.getHeaderNames();
        while(hNames.hasMoreElements()) {
            String name = hNames.nextElement();
            headers.addIfAbsent(name, request.getHeader(name));
            System.out.println(name + " = " + request.getHeader(name));
        }
        return headers;
    }

    /**
     * Extract Tokens
     * @param request
     * @return
     */
    public static HttpHeaders extractTokens(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", "application/json");
        headers.setContentType(MediaType.APPLICATION_JSON);
        if(request != null) {
            String token = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (token != null) {
                headers.addIfAbsent(HttpHeaders.AUTHORIZATION, token);
            }
            String refresh = request.getHeader(AuthorizeRequestAspect.REFRESH_TOKEN);
            if (refresh != null) {
                headers.addIfAbsent(AuthorizeRequestAspect.REFRESH_TOKEN, refresh);
            }
            String txToken = request.getHeader("TX-TOKEN");
            if (txToken != null) {
                headers.addIfAbsent("TX-TOKEN", txToken);
            }
        }
        return headers;
    }
}
