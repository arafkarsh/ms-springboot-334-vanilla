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
package io.fusion.air.microservice.domain.entities.order;
// Custom

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.fusion.air.microservice.domain.entities.core.AbstractBaseEntityWithUUID;
import io.fusion.air.microservice.domain.statemachine.order.OrderEvent;
import io.fusion.air.microservice.domain.statemachine.order.OrderNotes;
import io.fusion.air.microservice.domain.statemachine.order.OrderState;
import io.fusion.air.microservice.utils.Utils;

import jakarta.persistence.*;

/**
 * To Keep Track of Order States and its Transitions based on Order Event
 *
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */

@Entity
@Table(name = "order_state_history_tx")
public class OrderStateHistoryEntity extends AbstractBaseEntityWithUUID implements Comparable<OrderStateHistoryEntity> {

    @Column(name = "sourceState")
    @Enumerated(EnumType.STRING)
    private OrderState sourceState;

    @Column(name = "targetState")
    @Enumerated(EnumType.STRING)
    private OrderState targetState;

    @Column(name = "transitionEvent")
    @Enumerated(EnumType.STRING)
    private OrderEvent transitionEvent;

    @Column(name= "orderVersion", columnDefinition = "int default 0")
    private Integer orderVersion;

    @Column(name = "notes")
    private String notes;

    public OrderStateHistoryEntity() {}

    /**
     * Create Order State History
     * @param _source
     * @param _target
     * @param _event
     * @param _version
     * @param _notes
     */
    public OrderStateHistoryEntity(OrderState _source, OrderState _target, OrderEvent _event,
                                   int _version, String _notes) {
        this.sourceState = _source;
        this.targetState = _target;
        this.transitionEvent = _event;
        this.orderVersion = _version;
        this.notes = _notes;
    }

    /**
     * Returns Source State
     * @return
     */
    public OrderState getSourceState() {
        return sourceState;
    }

    /**
     * Returns Target State
     * @return
     */
    public OrderState getTargetState() {
        return targetState;
    }

    /**
     * Returns Transition Event
     * @return
     */
    public OrderEvent getTransitionEvent() {
        return transitionEvent;
    }

    /**
     * Get the Order Version
     * @return
     */
    public int getOrderVersion() {
        return orderVersion;
    }

    /**
     * Returns Notes
     * @return
     */
    @JsonIgnore
    public String getNotesString() {
        return notes;
    }

    /**
     * Returns Object Error
     * @return
     */
    public OrderNotes getNotes() {
        if(notes != null && notes.trim().length() > 0) {
            try {
                return Utils.fromJsonToObject(notes, OrderNotes.class);
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * Compare based on Order Version For Sorting in Ascending order.
     */
    @Override
    @JsonIgnore
    public int compareTo(OrderStateHistoryEntity o) {
        return this.orderVersion - o.orderVersion;
    }
}
