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
package io.fusion.air.microservice.adapters.aop;
// Custom
import io.fusion.air.microservice.domain.exceptions.*;
import io.fusion.air.microservice.domain.exceptions.SecurityException;
import io.fusion.air.microservice.domain.models.core.StandardResponse;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
import io.fusion.air.microservice.utils.Utils;
// Spring
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
// Java
import org.slf4j.Logger;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import jakarta.persistence.*;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Exception Handler to handle All Exceptions at a centralized location using AOP
 *
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@ControllerAdvice
@Order(2)
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    // Set Logger -> Lookup will automatically determine the class name.
    private static final Logger log = getLogger(lookup().lookupClass());

    // ServiceConfiguration
    // @Autowired not required - Constructor based Autowiring
    private final ServiceConfiguration serviceConfig;

    /**
     * Constructor for Autowiring
     * @param _serviceConfig
     */
    public ExceptionHandlerAdvice(ServiceConfiguration _serviceConfig) {
        serviceConfig = _serviceConfig;
    }

    /**
     * Handle All Exceptions
     * @param ex
     * @param body
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body,
            HttpHeaders headers, HttpStatusCode status,
            WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            // Purpose: If the status is an internal server error (500), this block sets the exception
            // as a request attribute (jakarta.servlet.error.exception). This is useful for internal
            // logging or forwarding error details to a logging framework or view.
	        // request.setAttribute(): This sets an attribute on the WebRequest with the key
            // "jakarta.servlet.error.exception" and the value of the exception (ex). The 0 is the
            // scope, indicating that the attribute is available only for the current request.
            request.setAttribute("jakarta.servlet.error.exception", ex, 0);
        }
        return createErrorResponse(ex, headers, (HttpStatus) status, request);
    }

    /**
     * Build Error Response Entity
     * @param _ex
     * @param _status
     * @param _request
     * @return
     */
    private ResponseEntity<Object> createErrorResponse(Exception _ex,
                                                       HttpStatus _status,
                                                       WebRequest _request) {
        return createErrorResponse(_ex, _ex.getMessage(), "599",null, _status, _request);
    }

    /**
     * Build Error Response Entity
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    private ResponseEntity<Object> createErrorResponse(Exception ex,
                                                       HttpHeaders headers,
                                                       HttpStatus status,
                                                       WebRequest request) {
        return createErrorResponse(ex, ex.getMessage(), "599", headers,  status, request);
    }

    /**
     * Build Error Response Entity
     * @param ase
     * @param errorCode
     * @param request
     * @return
     */
    private ResponseEntity<Object> createErrorResponse(AbstractServiceException ase,
                                                       String errorCode,
                                                       WebRequest request) {
        return createErrorResponse(ase, ase.getMessage(), errorCode, null, ase.getHttpStatus(), request);
    }
    /**
     * Build Error Response Entity
     * @param ase
     * @param errorCode
     * @param headers
     * @param request
     * @return
     */
    private ResponseEntity<Object> createErrorResponse(AbstractServiceException ase,
                                                       String errorCode,
                                                       HttpHeaders headers,
                                                       WebRequest request) {
        return createErrorResponse(ase, ase.getMessage(), errorCode, headers, ase.getHttpStatus(), request);
    }

    /**
     * Unable to Save Persistence Exceptions
     * @param pEx
     * @param request
     * @return
     */
    private ResponseEntity<Object> createErrorResponse(PersistenceException pEx,
                                                       String message,
                                                       String errorCode,
                                                       WebRequest request) {
        return createErrorResponse(pEx, message, errorCode, null, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Build Standard Error Response
     * @param exception
     * @param message
     * @param errorCode
     * @param headers
     * @param httpStatus
     * @param request
     * @return
     */
    private ResponseEntity<Object> createErrorResponse(Throwable exception,
                                                       String message,
                                                       String errorCode,
                                                       HttpHeaders headers,
                                                       HttpStatus httpStatus,
                                                       WebRequest request) {

        String errorPrefix = (serviceConfig != null) ? serviceConfig.getServiceAPIErrorPrefix() : "AKH";           // Microservice Prefix
        String appErrorCode = errorPrefix+errorCode;                                                                       // Error Code
        if(exception instanceof AbstractServiceException ase) {
            ase.setErrorCode(appErrorCode);                                                                                     // Set the Error Code
        }
        logException(appErrorCode,  exception);                                                                               // Log Exception
        StandardResponse stdResponse = Utils.createErrorResponse(
                null, errorPrefix, errorCode, httpStatus,  message);                                // Std Response

        return (headers != null)
                ?  new ResponseEntity<>(stdResponse, headers, httpStatus)                                         // HTTP Response
                : new ResponseEntity<>(stdResponse, httpStatus);
    }

    // ================================================================================================================
    // SERVER EXCEPTIONS: ERROR CODES 430 - 439
    // ================================================================================================================
    /**
     * Handle Runtime Exception
     * @param runEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Object> runtimeException(RuntimeException runEx, WebRequest request) {
        return createErrorResponse(runEx, runEx.getMessage(), "590", null, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Handle Any Exception
     * @param runEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = Throwable.class)
    public ResponseEntity<Object> throwable(Throwable runEx, WebRequest request) {
        return createErrorResponse(runEx, runEx.getMessage(), "599", null, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    // ================================================================================================================
    // STANDARD EXCEPTIONS: ERROR CODES 400 - 409
    // ================================================================================================================
    /**
     * Access Denied Exception
     * @param adEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<Object> accessDeniedException(AccessDeniedException adEx,  WebRequest request) {
        return createErrorResponse(adEx, adEx.getMessage(), "403", null, HttpStatus.FORBIDDEN, request);
    }

    /**
     * Request Rejected Exception
     * @param adEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = RequestRejectedException.class)
    public ResponseEntity<Object> requestRejectedException(RequestRejectedException adEx, WebRequest request) {
        return createErrorResponse(adEx, adEx.getMessage(), "403", null, HttpStatus.FORBIDDEN, request);
    }


    /**v
     * Exception if the Resource NOT Available!
     * @param rnfEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = ResourceException.class)
    public ResponseEntity<Object> resourceException(ResourceException rnfEx,  WebRequest request) {
        return createErrorResponse(rnfEx,  "404", request);
    }

    /**v
     * Exception if the Resource IS NOT FOUND!
     * @param rnfEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<Object> resourceNotFoundException(ResourceNotFoundException rnfEx,  WebRequest request) {
        return createErrorResponse(rnfEx,  "404", request);
    }

    // ================================================================================================================
    // SECURITY EXCEPTIONS: ERROR CODES 410 - 429
    // ================================================================================================================
    /**
     * Authorization Exception
     * @param adEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = SecurityException.class)
    public ResponseEntity<Object> securityException(SecurityException adEx, WebRequest request) {
        return createErrorResponse(adEx,  "411",  request);
    }

    /**
     * Authorization Exception
     * @param adEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = AuthorizationException.class)
    public ResponseEntity<Object> authorizationException(AuthorizationException adEx,  WebRequest request) {
        return createErrorResponse(adEx,  "413", request);
    }

    /**
     * JWT Token Extraction Exception
     * @param adEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = JWTTokenExtractionException.class)
    public ResponseEntity<Object> JWTTokenExtractionException(JWTTokenExtractionException adEx,  WebRequest request) {
        return createErrorResponse(adEx, "414",  request);
    }

    /**
     * JWT Token Expired Exception
     * @param adEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = JWTTokenExpiredException.class)
    public ResponseEntity<Object> JWTTokenExpiredException(JWTTokenExpiredException adEx,  WebRequest request) {
        return createErrorResponse(adEx, "415", request);
    }

    /**
     * JWT Token Subject Exception
     * @param adEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = JWTTokenSubjectException.class)
    public ResponseEntity<Object> JWTTokenSubjectException(JWTTokenSubjectException adEx,  WebRequest request) {
        return createErrorResponse(adEx,"416",  request);
    }

    /**
     * JWT Invalid Signature Exception
     * @param adEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = JWTInvalidSignatureException.class)
    public ResponseEntity<Object> JWTInvalidSignatureException(JWTInvalidSignatureException adEx,  WebRequest request) {
        return createErrorResponse(adEx,  "417",  request);
    }

    /**
     * JWT UnDefined Exception
     * @param adEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = JWTUnDefinedException.class)
    public ResponseEntity<Object> JWTUnDefinedException(JWTUnDefinedException adEx,  WebRequest request) {
        return createErrorResponse(adEx,  "429",  request);
    }

    // ================================================================================================================
    // MESSAGING EXCEPTIONS: ERROR CODES 430 - 439
    // ================================================================================================================
    /**
     * Messaging Exception
     * @param msgEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = MessagingException.class)
    public ResponseEntity<Object> handleMessagingException(MessagingException msgEx,  WebRequest request) {
        return createErrorResponse(msgEx,  "430", request);
    }

    // ================================================================================================================
    // DATABASE EXCEPTIONS: ERROR CODES 440 - 459
    // ================================================================================================================
    /**
     * Database Exception
     * @param dbEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = DatabaseException.class)
    public ResponseEntity<Object> handleDatabaseException(DatabaseException dbEx,  WebRequest request) {
        return createErrorResponse(dbEx,  "440", request);
    }

    /**
     * Unable to Query Due to Persistence Exception
     * @param pEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = NoResultException.class)
    public ResponseEntity<Object> handlePersistenceException(NoResultException pEx, WebRequest request) {
        return createErrorResponse(pEx,  "No Result Found!", "441", request);
    }

    /**
     * Unable to Query Due to Persistence Exception
     * @param pEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = NonUniqueResultException.class)
    public ResponseEntity<Object> handlePersistenceException(NonUniqueResultException pEx, WebRequest request) {
        return createErrorResponse(pEx, "Duplicate Data!", "442", request);
    }

    /**
     * Data Not Found Exception
     * @param dnfEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = DataNotFoundException.class)
    public ResponseEntity<Object> handleDataNotFoundException(DataNotFoundException dnfEx,  WebRequest request) {
        return createErrorResponse(dnfEx,  "444", request);
    }

    /**
     * Unable to Query Due to Persistence Exception
     * @param pEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<Object> handlePersistenceException(EntityNotFoundException pEx, WebRequest request) {
        return createErrorResponse(pEx,  "Entity Not Found!", "446", request);
    }

    /**
     * Unable to Query Due to Persistence Exception
     * @param pEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = EntityExistsException.class)
    public ResponseEntity<Object> handlePersistenceException(EntityExistsException pEx, WebRequest request) {
        return createErrorResponse(pEx,  "Duplicate Entity Found!","447", request);
    }

    /**
     * Duplicate Data Exception
     * @param ddEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = DuplicateDataException.class)
    public ResponseEntity<Object> handleDuplicateDataException(DuplicateDataException ddEx,  WebRequest request) {
        return createErrorResponse(ddEx,  "448", request);
    }

    /**
     * Unable to Query Due to Persistence Exception
     * @param pEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = QueryTimeoutException.class)
    public ResponseEntity<Object> handlePersistenceException(QueryTimeoutException pEx, WebRequest request) {
        return createErrorResponse(pEx,  "Query Timed out!", "449", request);
    }

    /**
     * Unable to Save Exception
     * @param utEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = UnableToSaveException.class)
    public ResponseEntity<Object> handleUnableToSaveException(UnableToSaveException utEx,  WebRequest request) {
        return createErrorResponse(utEx,  "452", request);
    }

    /**
     * Unable to Save Due to Persistence Exception
     * @param pEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = TransactionRequiredException.class)
    public ResponseEntity<Object> handlePersistenceException(TransactionRequiredException pEx, WebRequest request) {
        return createErrorResponse(pEx,  "Tx Required", "453", request);
    }

    /**
     * Unable to Save Due to Persistence Exception
     * @param pEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = RollbackException.class)
    public ResponseEntity<Object> handlePersistenceException(RollbackException pEx, WebRequest request) {
        return createErrorResponse(pEx,  "Rollback Error!", "454", request);
    }

    /**
     * Unable to Save Due to Persistence Exception
     * @param pEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = LockTimeoutException.class)
    public ResponseEntity<Object> handlePersistenceException(LockTimeoutException pEx, WebRequest request) {
        return createErrorResponse(pEx, "Lock Timed out!", "455", request);
    }

    /**
     * Unable to Save Due to Dirty Read/Write
     * @param utEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = OptimisticLockException.class)
    public ResponseEntity<Object> handleOptimisticLockException(OptimisticLockException utEx,  WebRequest request) {
        return createErrorResponse(utEx,  "Version Mismatch (Optimistic Lock)!", "456", request);
    }

    /**
     * Unable to Save Due to Dirty Read/Write
     * @param utEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = PessimisticLockException.class)
    public ResponseEntity<Object> handleOptimisticLockException(PessimisticLockException utEx,  WebRequest request) {
        return createErrorResponse(utEx, "Version Mismatch (Pessimistic Lock)!", "457", request);
    }

    /**
     * Unable to Save Due to  Persistence Exception
     * @param pEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = PersistenceException.class)
    public ResponseEntity<Object> handlePersistenceException(PersistenceException pEx, WebRequest request) {
        return createErrorResponse(pEx,  "Persistence Error: "+pEx.getMessage(), "458", request);
    }

    /**
     * Unable to Save Exception to SQL Exception
     * @param sqlEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = SQLException.class)
    public ResponseEntity<Object> handleSQLException(SQLException sqlEx,  WebRequest request) {
        return createErrorResponse(sqlEx, sqlEx.getMessage(), "459", null, HttpStatus.BAD_REQUEST, request);
    }

    // ================================================================================================================
    // BUSINESS EXCEPTIONS: ERROR CODES 460 - 489
    // ================================================================================================================
    /**
     * Business Exception
     * @param buEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = BusinessServiceException.class)
    public ResponseEntity<Object> handleBusinessServiceException(BusinessServiceException buEx, WebRequest request) {
        return createErrorResponse(buEx,  "460", request);
    }

    /**
     * InputDataException
     * @param idEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = InputDataException.class)
    public ResponseEntity<Object> handleInputDataException(InputDataException idEx,  WebRequest request) {
        return createErrorResponse(idEx, "461", request);
    }

    /**
     * Mandatory Data Required Exception
     * @param mdrEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = MandatoryDataRequiredException.class)
    public ResponseEntity<Object> handleMandatoryDataRequiredException(MandatoryDataRequiredException mdrEx,  WebRequest request) {
        return createErrorResponse(mdrEx,  "463", request);
    }

    /**
     * Rate Limit Exceeded Exception
     * @param mdrEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = LimitExceededException.class)
    public ResponseEntity<Object> handleLimitExceededException(LimitExceededException mdrEx, WebRequest request) {
        return createErrorResponse(mdrEx,  "464", request);
    }

    // ================================================================================================================
    // CONTROLLER EXCEPTIONS: ERROR CODES 490 - 499
    // ================================================================================================================
    /**
     * Controller Exception
     * @param coEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = ControllerException.class)
    public ResponseEntity<Object> handleControllerException(ControllerException coEx,  WebRequest request) {
        return createErrorResponse(coEx,  "490", request);
    }

    // ================================================================================================================
    // GENERIC Exception Handling
    // ================================================================================================================
    /**
     * Handle any unspecified Exceptions
     * @param runEx
     * @param request
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception runEx, WebRequest request) {
        return createErrorResponse(runEx, runEx.getMessage(), "500", null, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    // ================================================================================================================
    // LOG EXCEPTIONS
    // ================================================================================================================
    /**
     *
     * @param status
     * @param e
     */
    private void logException(String status, Throwable e) {
        log.trace(getStackTraceAsString(e));
        log.info("2|EH|TIME=00|STATUS=Error: {}|CLASS={}|",status, e.toString());
    }

    /**
     * Get the Stack Trace
     * @param e
     * @return
     */
    private String getStackTraceAsString(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
