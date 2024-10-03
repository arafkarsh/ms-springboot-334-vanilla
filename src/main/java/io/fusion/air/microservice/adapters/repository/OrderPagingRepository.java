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
package io.fusion.air.microservice.adapters.repository;

import io.fusion.air.microservice.domain.entities.order.OrderEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Repository
public interface OrderPagingRepository extends PagingAndSortingRepository<OrderEntity, UUID> {


    /**
     * Find Order by Customer ID
     *
     * @param customerId
     * @return
     */
    public List<OrderEntity> findByCustomerId(String customerId);

    /**
     * Find by Order ID
     * @param orderId
     * @return
     */
    @Query("SELECT order FROM OrderEntity order WHERE order.uuid = :orderId ")
    public Optional<OrderEntity> findByOrderId(@Param("orderId") UUID orderId);

    /**
     * Find by Customer ID and Order ID
     * @param customerId
     * @param orderId
     * @return
     */
    @Query("SELECT order FROM OrderEntity order WHERE order.customerId = :customerId AND order.uuid = :orderId ")
    public Optional<OrderEntity> findByCustomerIdAndOrderId(
            @Param("customerId") String customerId,
            @Param("orderId") UUID orderId);


}
