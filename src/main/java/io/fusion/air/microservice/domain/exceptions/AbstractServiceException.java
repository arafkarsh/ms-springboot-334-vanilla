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

    private static final String NO_INFO_AVAILABLE = "No-Info Available";

    /**
     * Abstract Service Exception
     * @param e
     */
    protected AbstractServiceException(String e) {
        super(e);
        errorMessage = (e != null) ? e : NO_INFO_AVAILABLE ;
        serviceException = this;
        httpStatus = HttpStatus.BAD_REQUEST;
    }

    /**
     * Abstract Service Exception
     * @param e
     */
    protected AbstractServiceException(Throwable e) {
        super(e);
        errorMessage = (e != null) ? e.getMessage() : NO_INFO_AVAILABLE ;
        serviceException = (e  != null) ? e : this;
        httpStatus = HttpStatus.BAD_REQUEST;
    }

    /**
     * Service Base Exception
     * @param msg
     * @param e
     */
    protected AbstractServiceException(String msg, Throwable e) {
        super(msg, e);
        errorMessage = (msg != null) ? msg : NO_INFO_AVAILABLE ;
        serviceException = (e  != null) ? e : this;
        httpStatus = HttpStatus.BAD_REQUEST;
    }

    /**
     * Service base Exception
     * @param msg
     * @param status
     * @param e
     */
    protected AbstractServiceException(String msg, HttpStatus status, Throwable e) {
        super(msg, e);
        errorMessage = (msg != null) ? msg : NO_INFO_AVAILABLE ;
        serviceException = (e  != null) ? e : this;
        httpStatus = status;
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
     * @param errCode
     */
    public void setErrorCode(String errCode) {
        this.errorCode = (errCode != null) ? errCode : "NA999";
    }
}