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
package io.fusion.air.microservice.domain.exceptions;

import org.springframework.http.HttpStatus;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
public abstract class AbstractServiceException extends  RuntimeException {

    private final String errorMessage;
    private final Throwable serviceException;
    private final HttpStatus httpStatus;
    private String errorCode = "NA000";

    /**
     * Abstract Service Exception
     * @param _e
     */
    public AbstractServiceException(String _e) {
        super(_e);
        errorMessage = (_e != null) ? _e : "No-Info Available" ;
        serviceException = this;
        httpStatus = HttpStatus.BAD_REQUEST;
    }

    /**
     * Abstract Service Exception
     * @param _e
     */
    public AbstractServiceException(Throwable _e) {
        super(_e);
        errorMessage = (_e != null) ? _e.getMessage() : "No-Info Available" ;
        serviceException = (_e  != null) ? _e : this;
        httpStatus = HttpStatus.BAD_REQUEST;
    }

    /**
     * Service Base Exception
     * @param _msg
     * @param _e
     */
    public AbstractServiceException(String _msg, Throwable _e) {
        super(_msg, _e);
        errorMessage = (_msg != null) ? _msg : "No-Info Available" ;
        serviceException = (_e  != null) ? _e : this;
        httpStatus = HttpStatus.BAD_REQUEST;
    }

    /**
     * Service base Exception
     * @param _msg
     * @param _status
     * @param _e
     */
    public AbstractServiceException(String _msg, HttpStatus _status, Throwable _e) {
        super(_msg, _e);
        errorMessage = (_msg != null) ? _msg : "No-Info Available" ;
        serviceException = (_e  != null) ? _e : this;
        httpStatus = _status;
    }

    /**
     * Returns Exception Stack Trace as a String
     * @return
     */
    public String getStackTraceAsString() {
        StringWriter stringWriter = new StringWriter();
        serviceException.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    /**
     * Get the App Error Message
     * @return
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Returns the Exception
     * @return
     */
    public Throwable getServiceException() {
        return serviceException;
    }

    /**
     * Returns HttpStatus
     * @return
     */
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    /**
     * Get Service Error Code
     * @return
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     *  Set Service Error Code
     * @param _errorCode
     */
    public void setErrorCode(String _errorCode) {
        this.errorCode = (_errorCode != null) ? _errorCode : "NA999";
    }
}