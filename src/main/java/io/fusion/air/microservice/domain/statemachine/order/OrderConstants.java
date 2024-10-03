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

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
public class OrderConstants {

    // Message Header for the Events used in the State Machine
    public static final String ORDER_ID_HEADER = "ORDER_ID";
    public static final String ORDER_HEADER = "ORDER";
    public static final String ERROR_SOURCE = "ERROR_SOURCE_STATE";
    public static final String ERROR_MSG = "ERROR_MSG";
    public static final String ERROR_OBJECT = "ERROR_OBJECT";
}
