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

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
public class SecurityException extends AbstractServiceException {

    /**
     * Security Exception Forbidden
     * @param _msg
     */
    public SecurityException(String _msg) {
        super(_msg);
    }

    /**
     * Security Exception Forbidden
     * @param _e
     */
    public SecurityException(Throwable _e) {
        super("Security Exception!", HttpStatus.FORBIDDEN, _e);
    }

    /**
     * Security Exception Forbidden
     * @param _msg
     * @param _e
     */
    public SecurityException(String _msg, Throwable _e) {
        super(_msg, HttpStatus.FORBIDDEN, _e);
    }

    /**
     * Security Exception Forbidden
     * @param _msg
     * @param badRequest
     * @param _e
     */
    public SecurityException(String _msg, HttpStatus badRequest, Throwable _e) {
        super(_msg, badRequest, _e);
    }
}
