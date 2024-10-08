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

import io.fusion.air.microservice.adapters.repository.CartRepository;
import io.fusion.air.microservice.domain.entities.order.CartEntity;
import io.fusion.air.microservice.domain.exceptions.DataNotFoundException;
import io.fusion.air.microservice.domain.models.order.Cart;
import io.fusion.air.microservice.domain.ports.services.CartService;
import io.fusion.air.microservice.utils.Utils;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The @Transactional annotation is used in Spring to define the scope of a single logical transaction.
 * The database transactions happen within the boundaries of the method marked with this annotation.
 *
 * The annotation offers several options for customization:
 *
 * readOnly: This attribute is a hint for the persistence provider that the transaction will be read-only.
 * In our order, we use @Transactional(readOnly = true) because the method is just reading data from the
 * database and not modifying anything. This can allow the persistence provider and the database to apply
 * some optimizations.
 *
 * rollbackFor and noRollbackFor: These attributes allow you to define for which exceptions a rollback
 * should be performed or not.
 *
 * propagation: This attribute allows you to define how the transaction of the annotated method is related
 * to the surrounding transaction.
 *
 * isolation: This attribute allows you to define the isolation level for the transaction.
 *
 * timeout: This attribute allows you to define a timeout for the transaction.
 *
 * The @Transactional annotation in Spring is quite powerful and flexible. It's the basis for handling
 * transactions in a Spring application
 *
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private MeterRegistry meterRegistry;

    /**
     * ONLY FOR TESTING PURPOSE
     *
     * @return
     */
    @Override
    public List<CartEntity> findAll() {
        return cartRepository.findAll();
    }

    /**
     * Find Cart by Customer ID
     * @param customerId
     * @return
     */
    @Transactional(readOnly = true)
    public List<CartEntity> findByCustomerId(String customerId) {
        return cartRepository.findByCustomerId(customerId);
    }

    /**
     * Find by Item by ID
     *
     * @param itemId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CartEntity> findById(String itemId, String customerId) {
        return cartRepository.findByuuidAndCustomerId(Utils.getUUID(itemId), customerId);
    }

    /**
     * Find By Item ID
     *
     * @param itemId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CartEntity> findById(UUID itemId, String customerId) {
        return cartRepository.findByuuidAndCustomerId(itemId, customerId);
    }

    /**
     * Search for the Item By Price Greater Than or Equal To
     *
     * @param price
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<CartEntity> fetchProductsByPriceGreaterThan(String customerId, BigDecimal price) {
        return cartRepository.fetchProductsByPriceGreaterThan(customerId, price);
    }

    /**
     * Returns Active Products Only
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<CartEntity> fetchActiveItems(String customerId) {
        return cartRepository.fetchActiveItems(customerId);
    }

    /**
     * Search for the Item By the Item Names Like 'name'
     *
     * @param name
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<CartEntity> findByItemNameContains(String customerId, String name) {
        return cartRepository.findByCustomerIdAndProductNameContains(customerId, name);
    }

    /**
     * Save the Cart
     *
     * @param _cart
     * @return
     */
    @Override
    @Transactional
    public CartEntity save(Cart _cart) {
        CartEntity cart = new CartEntity(_cart);
        meterRegistry.counter("cart.saved", "status", "Cart Saved!").increment();
        return cartRepository.save(cart);
    }

    /**
     * De Activate the Cart item
     *
     * @param _customerId
     * @param _cartItem
     * @return
     */
    @Override
    public CartEntity deActivateCartItem(String _customerId, UUID _cartItem) {
        Optional<CartEntity> cartItem = findById(_cartItem,_customerId);
        if(cartItem.isPresent()) {
            cartItem.get().deActivate();
            cartRepository.saveAndFlush(cartItem.get());
            return cartItem.get();
        }
        throw new DataNotFoundException("Cart Item Not Found");
    }

    /**
     * Activate the Cart item
     * @param _customerId
     * @param _cartItem
     * @return
     */
    @Override
    public CartEntity activateCartItem(String _customerId, UUID _cartItem) {
        Optional<CartEntity> cartItem = findById(_cartItem,_customerId);
        if(cartItem.isPresent()) {
            cartItem.get().activate();
            cartRepository.saveAndFlush(cartItem.get());
            return cartItem.get();
        }
        throw new DataNotFoundException("Cart Item Not Found");    }

    /**
     * Delete the Cart item (Permanently Deletes the Item)
     * @param _customerId
     * @param _cartItem
     */
    @Override
    public void deleteCartItem(String _customerId, UUID _cartItem) {
        Optional<CartEntity> cartItem = findById(_cartItem,_customerId);
        if(cartItem.isPresent()) {
            cartRepository.delete(cartItem.get());
        }
    }
}
