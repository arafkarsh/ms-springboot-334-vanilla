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

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
public enum OrderEvent {

    AUTO_TRANSITION_EVENT,
    CREDIT_CHECKING_EVENT,

    CREDIT_APPROVED_EVENT,

    CREDIT_DECLINED_EVENT,

    PAYMENT_INIT_EVENT,

    PAYMENT_APPROVED_EVENT,

    PAYMENT_DECLINED_EVENT,

    PACKAGE_FORK_EVENT,
    PACKAGE_INIT_EVENT,
    PACKAGE_EVENT, // NO EFFECT
    ORDER_SEND_BILL_EVENT,

    ORDER_READY_TO_SHIP_EVENT,

    ORDER_SHIPPED_EVENT,

    ORDER_IN_TRANSIT_EVENT,

    SEND_FOR_DELIVERY_EVENT,

    ORDER_CANCELLED_EVENT,

    ORDER_DELIVERED_EVENT,

    ORDER_RETURNED_EVENT,

    FAILURE_EVENT
    ;

    // Lookup table
    private static final Map<String, OrderEvent> lookup = new HashMap<>();

    // Populate the lookup table on loading time
    static {
        for (OrderEvent os : OrderEvent.values()) {
            lookup.put(os.name().toLowerCase(), os);
        }
    }

    public static OrderEvent fromString(String event) {
        OrderEvent foundState = lookup.get(event.trim().toLowerCase());
        if (foundState == null) {
            throw new IllegalArgumentException("No OrderEvent with text " + event + " found");
        }
        return foundState;
    }

}
