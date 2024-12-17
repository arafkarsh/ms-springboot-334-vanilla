/**
 * (C) Copyright 2023 Araf Karsh Hamid
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

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
public class StreamException extends MessagingException {

    /**
     * Messaging Exception
     * @param msg
     */
    public StreamException(String msg) {
        super(msg);
    }

    /**
     * Messaging Exception
     * @param msg
     * @param e
     */
    public StreamException(String msg, Throwable e) {
        super(msg, e);
    }

    /**
     * Messaging Exception
     * @param e
     */
    public StreamException(Throwable e) {
        this("Message Error!", e);
    }
}
