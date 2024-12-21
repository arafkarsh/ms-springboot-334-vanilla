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
package io.fusion.air.microservice.adapters.aop;
// Custom
import io.fusion.air.microservice.adapters.security.jwt.UserTokenAuthorization;
import static io.fusion.air.microservice.security.jwt.core.JsonWebTokenConstants.*;
// Aspect
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
// Spring
import org.springframework.stereotype.Component;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Component
@Aspect
public class AuthorizeRequestAspect {

    // Autowired using the Constructor
    private final UserTokenAuthorization userTokenAuthorization;

    /**
     * Autowired using the Constructor
     * @param userTokenAuthorization
     */
    public AuthorizeRequestAspect(UserTokenAuthorization userTokenAuthorization) {
        this.userTokenAuthorization = userTokenAuthorization;
    }

    /**
     * Validate REST Endpoint Annotated with @validateRefreshToken Annotation
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("@annotation(io.fusion.air.microservice.adapters.security.jwt.ValidateRefreshToken)")
    public Object validateRefreshRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        return validateRequest(false, REFRESH_TOKEN_MODE, joinPoint, CONSUMERS);
    }

    /**
     * Validate REST Endpoints Annotated with @SingleTokenAuthorizationRequired Annotation
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("@annotation(io.fusion.air.microservice.adapters.security.jwt.SingleTokenAuthorizationRequired)")
    public Object validateSingleTokenRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        return validateRequest(true, SINGLE_TOKEN_MODE, joinPoint, CONSUMERS);
    }

    /**
     * Validate REST Endpoints Annotated with @AuthorizationRequired Annotation
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("@annotation(io.fusion.air.microservice.adapters.security.jwt.AuthorizationRequired)")
    public Object validateAnnotatedRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        return validateRequest(false, MULTI_TOKEN_MODE, joinPoint, CONSUMERS);
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
        return validateRequest(false, SECURE_PKG_MODE,joinPoint, CONSUMERS);
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
        return validateRequest(false, SECURE_PKG_MODE,joinPoint, INTERNAL_SERVICES);
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
        return validateRequest(false, SECURE_PKG_MODE, joinPoint, EXTERNAL_SERVICES);
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
    private Object validateRequest(boolean singleToken, String tokenMode, ProceedingJoinPoint joinPoint, int tokenCtg)
            throws Throwable {
        return userTokenAuthorization.validateRequest(singleToken,tokenMode,  joinPoint,tokenCtg );
    }
 }