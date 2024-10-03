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
package io.fusion.air.microservice.domain.ports.services;
// Custom
import io.fusion.air.microservice.domain.entities.order.OrderEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Order Service
 *
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
public interface OrderService {

    /**
     * ONLY FOR TESTING PURPOSE
     * @return
     */
    public List<OrderEntity> findAll();

    /**
     * Find Order by Customer ID
     * @param customerId
     * @return
     */
    public List<OrderEntity> findByCustomerId(String customerId);

    /**
     * Find by Order by Customer ID and Order ID
     * @param customerId
     * @param orderId
     * @return
     */
    public Optional<OrderEntity> findById(String customerId, String orderId);

    /**
     * Find By Order by Customer ID and Order ID
     * @param customerId
     * @param orderId
     * @return
     */
    public Optional<OrderEntity> findById( String customerId, UUID orderId);


    /**
     * Save Order
     * @param order
     * @return
     */
    public OrderEntity save(OrderEntity order);


    /**
     * Reset the Order State to Initialized
     * THIS METHOD IS ONLY FOR TESTING THE STATE MACHINE BY RESETTING THE ORDER BACK TO ITS INIT STATE.
     * @param customerId
     * @param orderId
     * @return
     */
    public OrderEntity resetOrder(String customerId, String orderId);

    /**
     * Process Credit Approval for the Order
     *
     * @param customerId
     * @param orderId
     * @return
     */
    public OrderEntity processCreditApproval(String customerId, String orderId);

    /**
     * Process the Payment Request with External System
     * @param customerId
     * @param orderId
     * @return
     */
    public OrderEntity processPaymentRequest(String customerId, String orderId);

    /**
     * For Testing Purpose Only
     * Handle Event - Generic Method
     * @param customerId
     * @param orderId
     * @param event
     * @return
     */
    public OrderEntity handleEvent(String customerId, String orderId, String event);
}
