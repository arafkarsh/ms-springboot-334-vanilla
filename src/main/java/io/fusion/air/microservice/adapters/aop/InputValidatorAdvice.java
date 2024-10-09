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
import io.fusion.air.microservice.domain.models.core.StandardResponse;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
import io.fusion.air.microservice.utils.Utils;
// Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
// Java
import jakarta.validation.ConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Input Validator Advice
 *
 * This (AOP) Advice will validate all the inputs at a central location.
 *
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@ControllerAdvice
@Order(1)
public class InputValidatorAdvice {

    // Set Logger -> Lookup will automatically determine the class name.
    private static final Logger log = getLogger(lookup().lookupClass());

    // ServiceConfiguration
    @Autowired
    private ServiceConfiguration serviceConfig;

    /**
     * Validating Complex Object Rules
     * @param _ex
     * @param _request
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException _ex, WebRequest _request) {
        List<String> errors = _ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " | " + error.getDefaultMessage())
                .collect(Collectors.toList());
        return prepareErrorResponse( "462",  "Errors: Invalid Method Arguments",  errors );
    }

    /**
     * Validating Method Parameters
     * @param _ex
     * @param _request
     * @return
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException _ex,  WebRequest _request) {
        List<String> errors = _ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());
        return prepareErrorResponse( "463",  "Errors: Input Constraint Violations",  errors );
    }

    /**
     * Prepares the Error Message
     * @param errorCode
     * @param errorMsg
     * @param errors
     * @return
     */
    private ResponseEntity<Object> prepareErrorResponse(String errorCode, String errorMsg, List<String> errors ) {
        String errorPrefix = (serviceConfig != null) ? serviceConfig.getServiceAPIErrorPrefix() : "AKH";
        long startTime = System.currentTimeMillis();
        String status = "STATUS=ERROR: "+errorMsg;
        Collections.sort(errors);
        StandardResponse stdResponse = Utils.createErrorResponse(errors, errorPrefix, errorCode, HttpStatus.BAD_REQUEST, errorMsg);
        logTime(startTime, status);
        return new ResponseEntity<>(stdResponse, null, HttpStatus.BAD_REQUEST);
    }

    /**
     * Log Time with Input Validation Errors
     * @param _startTime
     * @param _status
     */
    private void logTime(long _startTime, String _status) {
        long timeTaken=System.currentTimeMillis() - _startTime;
        log.info("2|IV|TIME={} ms|{}|CLASS=|", timeTaken, _status);
    }
}
