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
package io.fusion.air.microservice.adapters.security.jwt;
// Custom
import io.fusion.air.microservice.adapters.security.core.UserRole;
import io.fusion.air.microservice.adapters.security.service.UserDetailsServiceImpl;
import io.fusion.air.microservice.domain.exceptions.*;
import io.fusion.air.microservice.security.jwt.client.JsonWebTokenValidator;
import io.fusion.air.microservice.security.jwt.core.TokenData;
import io.fusion.air.microservice.security.jwt.core.TokenDataFactory;
import static io.fusion.air.microservice.security.jwt.core.JsonWebTokenConstants.*;
// JWT
// Jakarta
import jakarta.servlet.http.HttpServletRequest;
// Aspect
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
// Log
import org.slf4j.Logger;
import org.slf4j.MDC;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;
// Spring
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * User Token Authorization
 *
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Component
public class UserTokenAuthorization {

    // Set Logger -> Lookup will automatically determine the class name.
    private static final Logger log = getLogger(lookup().lookupClass());

    // Autowired using the Constructor
    private final TokenDataFactory tokenFactory;

    // Autowired using the Constructor
    private final UserDetailsServiceImpl userDetailsService;

    // Autowired using the Constructor
    private final ClaimsManager claimsManager;

    /**
     * Autowired using the Constructor
     * @param userService
     * @param claims
     */
    public UserTokenAuthorization(TokenDataFactory tokenFactory, UserDetailsServiceImpl userService,
                                  ClaimsManager claims ) {
        this.tokenFactory = tokenFactory;
        this.userDetailsService = userService;
        this.claimsManager = claims;
    }

    /**
     * Validate the Request
     *
     * @param singleToken
     * @param tokenMode
     * @param joinPoint
     * @param tokenCtg
     * @return
     * @throws Throwable
     */
    public Object validateRequest(boolean singleToken, String tokenMode,
                                  ProceedingJoinPoint joinPoint, int tokenCtg) throws Throwable {
        // Get the request object
        long startTime = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        logTime(startTime, "Extracting & Validating Token", request.getRequestURI(), joinPoint);
        // Create Token Data from the TokenDataFactory
        final TokenData tokenData = tokenFactory.getTokenData( request.getHeader(AUTH_TOKEN), AUTH_TOKEN, joinPoint.toString());
        // Get the User (Subject) from the Token
        final String user = getUser(startTime, tokenData, joinPoint);
        log.debug("Validate Request: User Extracted... {} ", user);
        // If the User == NULL then ERROR is thrown from getUser() method itself
        // Validate the Token when User is NOT Null
        UserDetails userDetails = validateToken(startTime, user, tokenMode, tokenData, joinPoint, tokenCtg);
        // Create Authorize Token
        UsernamePasswordAuthenticationToken authorizeToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authorizeToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // Set the Security Context with current user as Authorized for the request,
        // So it passes the Spring Security Configurations successfully.
        SecurityContextHolder.getContext().setAuthentication(authorizeToken);
        logTime(startTime, SUCCESS, "User Authorized for the request",  joinPoint);
        // Check the Tx Token if It's NOT a SINGLE_TOKEN Request
        if(!singleToken ) {
            validateTxToken(startTime, user, request.getHeader(TX_TOKEN), joinPoint);
        }
        return joinPoint.proceed();
    }

    /**
     * Returns the user from the Token
     *
     * @param startTime
     * @param token
     * @param joinPoint
     * @return
     */
    private String getUser(long startTime, TokenData token, ProceedingJoinPoint joinPoint) {
        try {
            String user = JsonWebTokenValidator.getSubjectFromToken(token);
            // Store the user info for logging
            MDC.put("user", user);
            return user;
        } catch (Exception e) {
            logTime(startTime, ERROR, e.getMessage(), joinPoint);
            throw e;
        }
    }

    /**
     * Validate Token
     * - User
     * - Expiry Time
     *
     * @param startTime
     * @param user
     * @param tokenMode
     * @param tokenData
     * @param joinPoint
     * @param tokenCtg
     * @return
     */
    private UserDetails validateToken(long startTime, String user, String tokenMode,
                                      TokenData tokenData, ProceedingJoinPoint joinPoint, int tokenCtg) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user);
        String msg = null;
        try {
            // Validate the Token with the User details and Token Expiry
            if (JsonWebTokenValidator.validateToken(userDetails.getUsername(), tokenData)) {
                // Validate the Token Type
                String tokenType = JsonWebTokenValidator.getTokenType(tokenData);
                validateAuthTokenType( startTime,  user,  tokenType, tokenMode,  tokenCtg,  joinPoint);
                // Verify that the user role name matches the role name defined by the protected resource
                String role = JsonWebTokenValidator.getUserRoleFromToken(tokenData);
                verifyTheUserRole( role,  tokenMode,  joinPoint);
                return userDetails;
            } else {
                msg = "Auth-Token: Unauthorized Access: Token Validation Failed!";
                throw new AuthorizationException(msg);
            }
        } catch(AuthorizationException e) {
            msg = e.getMessage();
            throw e;
        } catch(Exception e) {
            msg = "Auth-Token: Unauthorized Access: Error: "+e.getMessage();
            throw new AuthorizationException(msg, e);
        } finally {
            // Error is Logged ONLY if msg != NULL
            logTime(startTime, ERROR, msg, joinPoint);
        }
    }

    /**
     * Validate the Token Type
     *
     * @param startTime
     * @param user
     * @param tokenCtg
     * @param joinPoint
     */
    private void validateAuthTokenType(long startTime, String user, String tokenType, String tokenMode,
                                        int tokenCtg, ProceedingJoinPoint joinPoint) {
        String msg = null;
        try {
            switch(tokenCtg) {
                case CONSUMERS:
                    if (tokenMode.equals(REFRESH_TOKEN_MODE) && !tokenType.equals(AUTH_REFRESH)) {
                        msg = "Invalid Refresh Token!  (" + tokenType + ")  " + user;
                        throw new AuthorizationException(msg);
                    }
                    if ( !tokenType.equals(AUTH)) {
                        msg = "Invalid Auth Token! (" + tokenType + ")  " + user;
                        throw new AuthorizationException(msg);
                    }
                    break;
                case INTERNAL_SERVICES:
                    if (tokenMode.equals(SECURE_PKG_MODE) && !tokenType.equals(TX_SERVICE )) {
                        msg = "Invalid Auth Token ("+tokenType+") For Internal Service! " + user;
                        throw new AuthorizationException(msg);
                    }
                    break;
                case EXTERNAL_SERVICES:
                    if (tokenMode.equals(SECURE_PKG_MODE) && !tokenType.equals(TX_EXTERNAL )) {
                        msg = "Invalid Auth Token ("+tokenType+") For External! " + user;
                        throw new AuthorizationException(msg);
                    }
                    break;
                default:
                    throw new AuthorizationException("Invalid Token Category.");
            }
        } finally {
            // Error is Logged ONLY if msg != NULL
            logTime(startTime, ERROR, msg, joinPoint);
        }
    }

    /**
     * Verify the User Role Matches the Claim
     * @param role
     * @param tokenMode
     * @param joinPoint
     */
    private void verifyTheUserRole(String role, String tokenMode, ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String annotationRole = null;
        try {
            if (tokenMode.equalsIgnoreCase(MULTI_TOKEN_MODE)) {
                AuthorizationRequired annotation =  signature.getMethod().getAnnotation(AuthorizationRequired.class);
                annotationRole = annotation.role();
            } else if(tokenMode.equalsIgnoreCase(SINGLE_TOKEN_MODE)) {
                SingleTokenAuthorizationRequired annotation =  signature.getMethod().getAnnotation(SingleTokenAuthorizationRequired.class);
                annotationRole = annotation.role();
            } else {
                // Default Role in Secure Package Mode
                annotationRole = ROLE_USER;
            }
        } catch (Exception e) {
            log.error("Authorization Failed: Role Not Found!");
            throw new AuthorizationException("Unauthorized Access: Role Not Found!", e);
        }
        log.debug("Required Role = {},  User (Claims) Role = {} ", annotationRole, role);
        // If the Role in the Token is User and Required is Admin then Reject the request
        if(role.trim().equalsIgnoreCase(UserRole.USER.toString()) && annotationRole != null
                && annotationRole.equals(UserRole.ADMIN.toString())) {
            throw new AuthorizationException("Unauthorized Access: Invalid User Role!");
        }
    }

    /**
     * Validate Tx Token and Set the Claims in the ClaimsManager
     *
     * @param startTime
     * @param user
     * @param joinPoint
     */
    private void validateTxToken(long startTime, String user, String token, ProceedingJoinPoint joinPoint) {
        final TokenData tokenData = tokenFactory.getTokenData(token, TX_TOKEN, joinPoint.toString());
        try {
            if (JsonWebTokenValidator.isTokenExpired(tokenData)) {
                String errorMsg = "TX-Token: Unauthorized Access: Token Expired!";
                logTime(startTime, ERROR, errorMsg, joinPoint);
                throw new AuthorizationException(errorMsg);
            }
            validateTxTokenType( user);
            logTime(startTime, SUCCESS, "TX-Token: User TX Authorized for the request",  joinPoint);
        } catch(Exception e) {
            logTime(startTime, ERROR, e.getMessage(), joinPoint);
            throw e;
        }
    }

    /**
     * Validates Token  Type
     * @param user
     * @return
     */
    private String validateTxTokenType(String user) {
        String tokenType = claimsManager.getTokenType();
        if (tokenType == null) {
            throw new AuthorizationException("Invalid Tx Token Type  (NULL) from Claims! for user: " + user);
        }
        if (!tokenType.equals(TX_USERS)) {
            throw new AuthorizationException("Invalid TX Token Type ("+tokenType+") ! " + user);
        }
        return tokenType;
    }

    /**
     * Log Time
     * @param startTime
     * @param status
     * @param joinPoint
     */
    private void logTime(long startTime, String status, String msg, ProceedingJoinPoint joinPoint) {
        if(msg != null) {
            long timeTaken = System.currentTimeMillis() - startTime;
            switch(status) {
                case ERROR:
                    log.error("2|JA|TIME={} ms|STATUS={}|CLASS={}|Msg={}", timeTaken, status, joinPoint, msg);
                    break;
                case SUCCESS:
                    // fall thru
                default:
                    log.info("2|JA|TIME={} ms|STATUS={}|CLASS={}|Msg={}", timeTaken, status, joinPoint, msg);
                    break;
            }
        }
    }
 }