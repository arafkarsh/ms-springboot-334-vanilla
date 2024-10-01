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
public class MandatoryDataRequiredException extends BusinessServiceException {


    /**
     * MandatoryDataRequired Service Exception
     * @param _msg
     */
    public MandatoryDataRequiredException(String _msg) {
        super(_msg);
    }

    /**
     * MandatoryDataRequired Service Exception
     * @param _msg
     * @param _e
     */
    public MandatoryDataRequiredException(String _msg, Throwable _e) {
        super(_msg, HttpStatus.BAD_REQUEST, _e);
    }

    /**
     * MandatoryDataRequired Service Exception
     * @param _e
     */
    public MandatoryDataRequiredException(Throwable _e) {
        super("Mandatory Data Required!", HttpStatus.BAD_REQUEST, _e);
    }
}
