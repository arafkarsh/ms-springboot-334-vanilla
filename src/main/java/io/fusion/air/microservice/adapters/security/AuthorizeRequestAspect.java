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
package io.fusion.air.microservice.adapters.security;

import io.fusion.air.microservice.domain.exceptions.*;
import io.fusion.air.microservice.security.JsonWebToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Component
@Aspect
public class AuthorizeRequestAspect {

    // Set Logger -> Lookup will automatically determine the class name.
    private static final Logger log = getLogger(lookup().lookupClass());

    public final static String AUTH             = "auth";
    public final static String AUTH_REFRESH     = "refresh";
    public final static String TX_USERS         = "tx-users";
    public final static String TX_SERVICE       = "tx-internal";
    public final static String TX_EXTERNAL      = "tx-external";

    public final static String REFRESH_TOKEN    = "Refresh-Token";
    public final static String AUTH_TOKEN       = "Authorization";
    public final static String SINGLE_TOKEN     = "Authorization";
    public final static String TX_TOKEN         = "TX-TOKEN";

    public final static int CONSUMERS           = 1;
    public final static int INTERNAL_SERVICES   = 2;
    public final static int EXTERNAL_SERVICES   = 3;

    @Autowired
    private JsonWebToken jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ClaimsManager claimsManager;

    /**
     * Validate REST Endpoint Annotated with @validateRefreshToken Annotation
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("@annotation(io.fusion.air.microservice.adapters.security.ValidateRefreshToken)")
    public Object validateRefreshRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        return validateRequest(false, REFRESH_TOKEN, joinPoint, CONSUMERS);
    }

    /**
     * Validate REST Endpoints Annotated with @SingleTokenAuthorizationRequired Annotation
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("@annotation(io.fusion.air.microservice.adapters.security.SingleTokenAuthorizationRequired)")
    public Object validateSingleTokenRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        return validateRequest(true, SINGLE_TOKEN, joinPoint, CONSUMERS);
    }

    /**
     * Validate REST Endpoints Annotated with @AuthorizationRequired Annotation
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("@annotation(io.fusion.air.microservice.adapters.security.AuthorizationRequired)")
    public Object validateAnnotatedRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        return validateRequest(false, AUTH_TOKEN, joinPoint, CONSUMERS);
    }

    /**
     * Secure All the Consumers REST Endpoints in the Secured Packaged using JWT
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "execution(* io.fusion.air.microservice.adapters.controllers.secured.*.*(..))")
    public Object validateAnyRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        return validateRequest(false, AUTH_TOKEN,joinPoint, CONSUMERS);
    }

    /**
     * Secure All the Internal REST Endpoints in the Secured Packaged using JWT
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "execution(* io.fusion.air.microservice.adapters.controllers.internal.*.*(..))")
    public Object validateInternalRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        return validateRequest(false, AUTH_TOKEN,joinPoint, INTERNAL_SERVICES);
    }

    /**
     * Secure All the External REST Endpoints in the Secured Packaged using JWT
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "execution(* io.fusion.air.microservice.adapters.controllers.external.*.*(..))")
    public Object validateExternalRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        return validateRequest(false, AUTH_TOKEN,joinPoint, EXTERNAL_SERVICES);
    }

    /**
     * Validate the Request
     *
     * @param singleToken
     * @param tokenKey
     * @param joinPoint
     * @param tokenCtg
     * @return
     * @throws Throwable
     */
    private Object validateRequest(boolean singleToken, String tokenKey, ProceedingJoinPoint joinPoint, int tokenCtg)
            throws Throwable {
        // Get the request object
        long startTime = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        logTime(startTime, "Validating", request.getRequestURI(), joinPoint);
        final String token = getToken(startTime, request.getHeader(tokenKey), joinPoint);
        final String user = getUser(startTime, token, joinPoint);
        System.out.println("Step 0: User Extracted... "+user);
        // System.out.println("Step 0: Security Context. "+SecurityContextHolder.getContext().getAuthentication());
        // System.out.println("Step 0: Principal....... "+SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        // Validate the Token when User is NOT Null
        if (user != null) {
            // System.out.println("Step 1: Extract Tokens...");
            // Validate Token
            UserDetails userDetails = validateToken(startTime, singleToken, user, tokenKey, token, joinPoint, tokenCtg);
            // Create Authorize Token
            UsernamePasswordAuthenticationToken authorizeToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authorizeToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // Set the Security Context with current user as Authorized for the request,
            // So it passes the Spring Security Configurations successfully.
            SecurityContextHolder.getContext().setAuthentication(authorizeToken);
            // System.out.println("Step 5: Security Context. "+SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            logTime(startTime, "SUCCESS", "User Authorized for the request",  joinPoint);
        }
        // If the User == NULL then ERROR is thrown from getUser() method itself
        // Check the Tx Token if It's NOT a SINGLE_TOKEN Request
        if(!singleToken ) {
            validateAndSetClaimsFromTxToken(startTime, user, tokenKey, request.getHeader(TX_TOKEN), joinPoint);
        }
        return joinPoint.proceed();
    }

    /**
     * Extract the Token fromm the Authorization Header
     * ------------------------------------------------------------------------------------------------------
     * Authorization: Bearer AAA.BBB.CCC
     * ------------------------------------------------------------------------------------------------------
     *
     * @param startTime
     * @param tokenKey
     * @param joinPoint
     * @return
     */
    private String getToken(long startTime, String tokenKey, ProceedingJoinPoint joinPoint) {
        if (tokenKey != null && tokenKey.startsWith("Bearer ")) {
            return tokenKey.substring(7);
        }
        String msg = "Access Denied: Unable to extract token from Header!";
        logTime(startTime, "ERROR", msg,  joinPoint);
        throw new JWTTokenExtractionException(msg);
    }

    /**
     * Returns the user from the Token
     *
     * @param startTime
     * @param token
     * @param joinPoint
     * @return
     */
    private String getUser(long startTime, String token, ProceedingJoinPoint joinPoint) {
        String user = null;
        String msg = null;
        try {
            user = jwtUtil.getSubjectFromToken(token);
            // Store the user info for logging
            MDC.put("user", user);
            return user;
        } catch (IllegalArgumentException e) {
            msg = "Access Denied: Unable to get Subject JWT Token Error: "+e.getMessage();
            throw new JWTTokenSubjectException(msg, e);
        } catch (ExpiredJwtException e) {
            msg = "Access Denied: JWT Token has expired Error: "+e.getMessage();
            throw new JWTTokenExpiredException(msg, e);
        } catch (NullPointerException e) {
            msg = "Access Denied: Invalid Token (Null Token) Error: "+e.getMessage();
            throw new JWTUnDefinedException(msg, e);
        } catch (Throwable e) {
            msg = "Access Denied: Error:  "+e.getMessage();
            throw new JWTUnDefinedException(msg, e);
        } finally {
            if(msg != null) {
                logTime(startTime, "ERROR", msg, joinPoint);
            }
        }
    }

    /**
     * Validate Token
     * - User
     * - Expiry Time
     *
     * @param startTime
     * @param singleToken
     * @param user
     * @param tokenKey
     * @param token
     * @param joinPoint
     * @param tokenCtg
     * @return
     */
    private UserDetails validateToken(long startTime, boolean singleToken, String user, String tokenKey,
                                      String token, ProceedingJoinPoint joinPoint, int tokenCtg) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user);
        String msg = null;
        try {
            // System.out.println("Step 2: Validate Token...");
            // Validate the Token
            if (jwtUtil.validateToken(userDetails.getUsername(), token)) {
                String role = jwtUtil.getUserRoleFromToken(token);
                // Set the Claims ONLY If it's a Single Token
                Claims claims = jwtUtil.getAllClaims(token);
                if(singleToken) {
                    claimsManager.setClaims(claims);
                    claimsManager.isClaimsInitialized();
                }
                // Validate the Token Type
                validateTokenType( startTime,  user,  tokenKey, claims,  tokenCtg,  joinPoint);
                // Verify that the user role name matches the role name defined by the protected resource
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                AuthorizationRequired annotation = null;
                String annotationRole = null;
                try {
                    if (tokenKey.equalsIgnoreCase(AUTH_TOKEN)) {
                        annotation = signature.getMethod().getAnnotation(AuthorizationRequired.class);
                        annotationRole = annotation.role();
                        // } else {
                        //    annotation = signature.getMethod().getAnnotation(ValidateRefreshToken.class);
                    }
                } catch (Exception ignored) {
                    // System.out.println("ROLE NOT FOUND: "+ignored.getMessage());
                }
                System.out.println("Step 3: Role Check @Role = "+annotationRole+" Claims Role = "+role);
                // If the Role in the Token is User and Required is Admin then Reject the request

                if(role.trim().equalsIgnoreCase(UserRole.User.toString())
                        && annotationRole != null
                        && annotationRole.equals(UserRole.Admin.toString())) {
                    throw new AuthorizationException("Invalid User Role!");
                }
                // System.out.println("Step 4: Return UserDetails");
                return userDetails;
            } else {
                msg = "Auth-Token: Unauthorized Access: Token Validation Failed!";
                throw new AuthorizationException(msg);
            }
        } catch(AuthorizationException e) {
            throw e;
        } catch(Throwable e) {
            msg = "Auth-Token: Unauthorized Access: Error: "+e.getMessage();
            throw new AuthorizationException(msg, e);
        } finally {
            // Error is Logged ONLY if msg != NULL
            if(msg != null) {
                logTime(startTime, "ERROR", msg, joinPoint);
            }
        }
    }

    /**
     * Validate the Token Type
     *
     * @param startTime
     * @param user
     * @param claims
     * @param tokenCtg
     * @param joinPoint
     */
    private void validateTokenType(long startTime, String user, String tokenKey, Claims claims, int tokenCtg, ProceedingJoinPoint joinPoint) {
        String msg = null;
        try {
            if (claims == null) {
                msg = "Invalid Token! No Claims available! " + user;
                throw new AuthorizationException(msg);
            }
            String tokenType = null;
            try {
                tokenType = (String) claims.get("type");
            } catch (Exception e) {
            }
            if (tokenType == null) {
                msg = "Invalid Token Type from Claims! " + user;
                throw new AuthorizationException(msg);
            }
            switch(tokenCtg) {
                case CONSUMERS:
                    if (tokenKey.equals(AUTH_TOKEN) && !tokenType.equals(AUTH)) {
                        msg = "Invalid Auth Token! ("+tokenType+")  For " + user;
                        throw new AuthorizationException(msg);
                    } else if (tokenKey.equals(REFRESH_TOKEN) && !tokenType.equals(AUTH_REFRESH)) {
                        msg = "Invalid Refresh Token! " + user;
                        throw new AuthorizationException(msg);
                    }
                    break;
                case INTERNAL_SERVICES:
                    if (tokenKey.equals(AUTH_TOKEN) && !tokenType.equals(TX_SERVICE )) {
                        msg = "Invalid Auth Token ("+tokenType+") For Internal Service! " + user;
                        throw new AuthorizationException(msg);
                    }
                    break;
                case EXTERNAL_SERVICES:
                    if (tokenKey.equals(AUTH_TOKEN) && !tokenType.equals(TX_EXTERNAL )) {
                        msg = "Invalid Auth Token ("+tokenType+") For External! " + user;
                        throw new AuthorizationException(msg);
                    }
                    break;
            }
        } finally {
            // Error is Logged ONLY if msg != NULL
            if(msg != null) {
                logTime(startTime, "ERROR", msg, joinPoint);
            }
        }
    }

    /**
     * Validate Tx Token and Set the Claims in the ClaimsManager
     *
     * @param startTime
     * @param user
     * @param tokenKey
     * @param joinPoint
     */
    private void validateAndSetClaimsFromTxToken(long startTime, String user, String tokenKey,
                                                 String tokenData, ProceedingJoinPoint joinPoint) {
        String token = null;
        if (tokenData != null && tokenData.startsWith("Bearer ")) {
            token = tokenData.substring(7);
        } else {
            String msg = "TX-Token: Access Denied: Unable to extract TX-Token from Header! "+user;
            logTime(startTime, "ERROR", msg, joinPoint);
            throw new JWTTokenExtractionException(msg);
        }
        String msg = null;
        try {
            Claims claims = null;
            if (jwtUtil.validateToken(user, token)) {
                claims = jwtUtil.getAllClaims(token);
                String tokenType = null;
                try {
                    tokenType = (String) claims.get("type");
                } catch (Exception e) {
                }
                if (tokenType == null) {
                    msg = "Invalid Token Type from Claims! " + user;
                    throw new AuthorizationException(msg);
                }
                if (!tokenType.equals(TX_USERS)) {
                    msg = "Invalid TX Token Type ("+tokenType+") ! " + user;
                    throw new AuthorizationException(msg);
                }
                claimsManager.setClaims(claims);
                claimsManager.isClaimsInitialized();
                logTime(startTime, "SUCCESS", "TX-Token: User TX Authorized for the request",  joinPoint);
            }  else {
                msg = "TX-Token: Unauthorized Access: Token Validation Failed!";
                throw new AuthorizationException(msg);
            }
        } catch(AuthorizationException e) {
            throw e;
        } catch(Throwable e) {
            msg = "TX-Token: Unauthorized Access: Error: "+e.getMessage();
            throw new AuthorizationException(msg, e);
        } finally {
            // Error is Logged ONLY if msg != NULL
            if(msg != null) {
                logTime(startTime, "ERROR", msg, joinPoint);
            }
        }
    }

    /**
     * Log Time
     * @param _startTime
     * @param _status
     * @param joinPoint
     */
    private void logTime(long _startTime, String _status, String _msg, ProceedingJoinPoint joinPoint) {
        long timeTaken=System.currentTimeMillis() - _startTime;
        log.info("2|JA|TIME={} ms|STATUS={}|CLASS={}|Msg={}", timeTaken, _status,joinPoint, _msg);
    }
 }