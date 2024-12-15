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
package io.fusion.air.microservice.adapters.service;
// Custom
import io.fusion.air.microservice.adapters.repository.OrderPagingRepository;
import io.fusion.air.microservice.adapters.repository.OrderRepository;
import io.fusion.air.microservice.domain.entities.order.OrderEntity;
import io.fusion.air.microservice.domain.exceptions.BusinessServiceException;
import io.fusion.air.microservice.domain.exceptions.DataNotFoundException;
import io.fusion.air.microservice.domain.exceptions.InputDataException;
import io.fusion.air.microservice.domain.ports.services.OrderService;
import io.fusion.air.microservice.domain.statemachine.order.OrderEvent;
import io.fusion.air.microservice.utils.Utils;
// Spring
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.RequestScope;
// Java
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Order Service
 * Order Processing is implemented with Spring State Machine
 * 1. Request for Credit
 * 2. Payment Processing
 * 3. Shipping the Product
 *
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Service
@RequestScope
public class OrderServiceImpl implements OrderService {

    // Set Logger -> Lookup will automatically determine the class name.
    private static final Logger log = getLogger(lookup().lookupClass());

    // Autowired using Constructor
    private final OrderPagingRepository orderPagingRepository;

    // Autowired using Constructor
    private final OrderRepository orderRepository;

    // Autowired using Constructor
    private final MeterRegistry meterRegistry;

    /**
     * Autowiring through the Constructor
     * @param orderPagingRepo
     * @param orderRepo
     * @param meterReg
     */
    public OrderServiceImpl(OrderPagingRepository orderPagingRepo, OrderRepository orderRepo, MeterRegistry meterReg) {
        orderPagingRepository = orderPagingRepo;
        orderRepository = orderRepo;
        meterRegistry = meterReg;
    }

    /**
     * ONLY FOR TESTING PURPOSE
     *
     * @return
     */
    @Override
    public List<OrderEntity> findAll() {
        return (List<OrderEntity>) orderRepository.findAll();
    }

    /**
     * Find Order by Customer ID
     *
     * @param customerId
     * @return
     */
    @Override
    public List<OrderEntity> findByCustomerId(String customerId) {
        return orderPagingRepository.findByCustomerId(customerId);
    }

    /**
     * Find by Order by Customer ID and Order ID
     *
     * @param customerId
     * @param orderId
     * @return
     */
    @Override
    public Optional<OrderEntity> findById(String customerId, String orderId) {
        Optional<OrderEntity> o = orderPagingRepository.findByCustomerIdAndOrderId(customerId, Utils.getUUID(orderId));
        if(o.isPresent()) {
            return o;
        }
        throw new DataNotFoundException("Order Not Found for OrderId "+orderId);
    }

    /**
     * Find By Order by Customer ID and Order ID
     *
     * @param customerId
     * @param orderId
     * @return
     */
    @Override
    public Optional<OrderEntity> findById(String customerId, UUID orderId) {
        Optional<OrderEntity> o = orderPagingRepository.findByCustomerIdAndOrderId(customerId, orderId);
        if(o.isPresent()) {
            return o;
        }
        throw new DataNotFoundException("Order Not Found for OrderId="+orderId);    }

    /**
     * Save Order
     *
     * @param order
     * @return
     */
    @Override
    @Transactional
    public OrderEntity save(OrderEntity order) {
        if(order == null) {
            throw new InputDataException("Invalid Order Data");
        }
        order.calculateTotalOrderValue();
        // Log Order Status
        meterRegistry.counter("orders.processed", "status", order.getOrderState().name()).increment();
        return orderRepository.save(order);
    }

    /**
     * Reset the Order State to Initialized
     * THIS METHOD IS ONLY FOR TESTING THE STATE MACHINE BY RESETTING THE ORDER BACK TO ITS INIT STATE.
     * @param customerId
     * @param orderId
     * @return
     */
    @Transactional
    public OrderEntity resetOrder(String customerId, String orderId) {
        Optional<OrderEntity> orderOpt = findById( customerId,  orderId);
        log.info("Reset Order ID = {} ", orderId);
        if(orderOpt.isPresent()) {
            OrderEntity order = orderOpt.get();
            order.resetOrderState();
            orderRepository.save(order);
            return order;
        }
        throw new DataNotFoundException("Order Not Found for "+orderId);
    }

    /**
     * Process Credit Approval for the Order
     *
     * @param customerId
     * @param orderId
     * @return
     */
    public OrderEntity processCreditApproval(String customerId, String orderId) {
        Optional<OrderEntity> orderOpt = findById( customerId,  orderId);
        log.info("[1] Process Order ID = {} ",orderId);
        return orderOpt.get();
    }

    /**
     * For Testing Purpose Only
     * Handle Event - Generic Method
     *
     * @param customerId
     * @param orderId
     * @param event
     * @return
     */
    public OrderEntity handleEvent(String customerId, String orderId, String event) {
        OrderEvent orderEvent = OrderEvent.fromString(event);
        return handleEvent(customerId, orderId, orderEvent);
    }

    /**
     * For Testing Purpose Only
     * Handle Event - Generic Method
     *
     * @param customerId
     * @param orderId
     * @param orderEvent
     * @return
     */
    public OrderEntity handleEvent(String customerId, String orderId, OrderEvent orderEvent) {
        if(orderEvent == null) {
            throw new BusinessServiceException("Invalid Event for OrderProcessing!");
        }
        Optional<OrderEntity> orderOpt = findById( customerId,  orderId);
        log.info("Handle Event {} For Order ID = {} ", orderEvent, orderId);
        log.info("(1) INCOMING EVENT == (OrderServiceImpl) === [ {} ] ======= >> OrderID = {} ", orderEvent.name(),orderId);
        return orderOpt.get();
    }

    /**
     * Process the Payment Request with External System
     * @param customerId
     * @param orderId
     * @return
     */
    public OrderEntity processPaymentRequest(String customerId, String orderId) {
        Optional<OrderEntity> orderOpt = findById( customerId,  orderId);
        log.info("Process Order ID = {} ", orderId);
        return orderOpt.get();
    }
}
