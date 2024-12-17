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

import com.fasterxml.jackson.annotation.JsonIgnore;
// Custom
import io.fusion.air.microservice.domain.entities.core.AbstractBaseEntityWithUUID;
import io.fusion.air.microservice.domain.statemachine.order.OrderResult;
import io.fusion.air.microservice.domain.statemachine.order.OrderState;
// Java
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Order Entity
 *
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Entity
@Table(name = "order_tx")
public class OrderEntity extends AbstractBaseEntityWithUUID {

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "currency")
    private String currency;

    @Column(name = "totalOrderValue")
    private BigDecimal totalOrderValue;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    @Embedded
    private ShippingAddress shippingAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id", referencedColumnName = "uuid")
    private OrderPaymentEntity payment;

    @Column(name = "orderStatus")
    @Enumerated(EnumType.STRING)
    private OrderState orderState;

    @Column(name = "result")
    @Enumerated(EnumType.STRING)
    private OrderResult result;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderStateHistoryEntity> orderHistory = new ArrayList<>();

    private OrderEntity() {
    }

    /**
     * Calculate the Order Value
     * @return
     */
    @JsonIgnore
    public BigDecimal calculateTotalOrderValue() {
        /**
        if(orderItems != null && orderItems.size() > 0) {
            for(OrderItemEntity i : orderItems) {
                totalOrderValue = totalOrderValue.add(i.getPrice());
            }
        }
         */
        if(getOrderItems() != null) {
            totalOrderValue = getOrderItems().stream()
                    .map(OrderItemEntity::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return getTotalOrderValue();
    }

    /**
     * Sets the Order State
     * @param state
     */
    public void setState(OrderState state) {
        orderState = state;
    }

    /**
     * Set the Order Result
     * @param result
     */
    public void setOrderResult(OrderResult result) {
        this.result = result;
    }

    /**
     * Returns the Order ID
     * @return
     */
    public String getOrderId() {
        return super.getIdAsString();
    }

    /**
     * Get the Customer ID
     * @return
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Get the Currency
     * @return
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Get the Total Order Value
     * @return
     */
    public BigDecimal getTotalOrderValue() {
        return totalOrderValue;
    }

    /**
     * Get the Order Items
     * @return
     */
    public List<OrderItemEntity> getOrderItems() {
        return orderItems;
    }

    /**
     * Get the shipping Address
     * @return
     */
    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    /**
     * Get the Payment details
     * @return
     */
    public OrderPaymentEntity getPayment() {
        return payment;
    }

    /**
     * Returns the Status of the Order
     * @return
     */
    public OrderState getOrderState() {
        return orderState;
    }

    /**
     * Returns the Result of the Current Order Processing State
     * @return
     */
    public OrderResult getResult() {
        return result;
    }

    /**
     * Returns the Order State Transition History
     * @return
     */
    public List<OrderStateHistoryEntity> getOrderHistory() {
         Collections.sort(orderHistory);
        return orderHistory;
    }

    /**
     * Add Order History
     * @param history
     */
    public void addOrderStateHistory(OrderStateHistoryEntity history) {
        orderHistory.add(history);
    }

    /**
     * ONLY TO DEMO/TEST VARIOUS DOMAIN EVENTS
     */
    public void resetOrderState() {
        initializeOrder();
        orderHistory.clear();
    }

    /**
     * Initialize the Order
     * @return
     */
    @JsonIgnore
    protected void initializeOrder() {
        orderState = OrderState.ORDER_INITIALIZED;
        result = OrderResult.IN_PROGRESS;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final OrderEntity order;

        private Builder() {
            order = new OrderEntity();
            order.initializeOrder();
        }

        public Builder addCustomerId(String customerId) {
            order.customerId = customerId;
            return this;
        }

        public Builder addOrderItems(List<OrderItemEntity> orderItems) {
            order.orderItems = orderItems;
            order.calculateTotalOrderValue();
            return this;
        }

        public Builder addOrderItem(OrderItemEntity orderItem) {
            order.orderItems.add(orderItem);
            order.calculateTotalOrderValue();
            return this;
        }

        public Builder addShippingAddress(ShippingAddress shippingAddress) {
            order.shippingAddress = shippingAddress;
            return this;
        }

        public Builder addPayment(OrderPaymentEntity payment) {
            order.payment = payment;
            return this;
        }

        public OrderEntity build() {
            return order;
        }
    }
}
