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
package io.fusion.air.microservice.domain.statemachine.order;

import java.io.Serializable;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
public class OrderNotes implements Serializable {

    private String sourceState = "";
    private String targetState = "";
    private String transitionEvent = "";
    private String message = "";
    private String errorMessage = "";

    public OrderNotes() {}

    /**
     * Create Order Notes
     * @param _sourceState
     * @param _targetState
     * @param _transitionEvent
     * @param _message
     * @param _errorMessage
     */
    public OrderNotes(String _sourceState, String _targetState,
                      String _transitionEvent, String _message, String _errorMessage) {
        this.sourceState        = (_sourceState != null) ? _sourceState : "";
        this.targetState        = (_targetState != null) ? _targetState : "";
        this.transitionEvent    = (_transitionEvent != null) ? _transitionEvent : "";
        this.message            = (_message != null) ? _message : "";
        this.errorMessage       = (_errorMessage != null) ? _errorMessage : "";
    }

    /**
     * Returns Source State
     * @return
     */
    public String getSourceState() {
        return sourceState;
    }

    /**
     * Returns the Target State
     * @return
     */
    public String getTargetState() {
        return targetState;
    }

    /**
     * Returns the Transition Event
     * @return
     */
    public String getTransitionEvent() {
        return transitionEvent;
    }

    /**
     * Returns the Notes
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the Error Message
     * @return
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
