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

import io.fusion.air.microservice.domain.entities.order.CartEntity;
import io.fusion.air.microservice.domain.models.order.CartItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CartItem Service
 *
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
public interface CartService {


    /**
     * ONLY FOR TESTING PURPOSE
     * @return
     */
    public List<CartEntity> findAll();

    /**
     * Find CartItem by Customer ID
     * @param customerId
     * @return
     */
    public List<CartEntity> findByCustomerId(String customerId);

    /**
     * Find by Item by ID
     * @param itemId
     * @return
     */
    public Optional<CartEntity> findById(String itemId, String customerId);

    /**
     * Find By Item ID
     * @param itemId
     * @return
     */
    public Optional<CartEntity> findById(UUID itemId, String customerId);

    /**
     * Search for the Item By Price Greater Than or Equal To
     * @param price
     * @return
     */
    public List<CartEntity> fetchProductsByPriceGreaterThan(String customerId, BigDecimal price);

    /**
     * Returns Active Products Only
     * @return
     */
    public List<CartEntity> fetchActiveItems(String customerId);

    /**
     * Search for the Item By the Item Names Like 'name'
     * @param name
     * @return
     */
    public List<CartEntity> findByItemNameContains(String customerId, String name);

    /**
     * Save the CartItem Item
     * @param cartItem
     * @return
     */
    public CartEntity save(CartItem cartItem);

    /**
     * De Activate the CartItem item
     * @param cartItem
     * @return
     */
    public CartEntity deActivateCartItem(String customerId, UUID cartItem);

    /**
     * Activate the CartItem item
     * @param customerId
     * @param cartItem
     * @return
     */
    public CartEntity activateCartItem(String customerId, UUID cartItem);

    /**
     * Delete the CartItem item (Permanently Deletes the Item)
     * @param customerId
     * @param cartItem
     */
    public void deleteCartItem(String customerId, UUID cartItem);

}
