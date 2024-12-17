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

import io.fusion.air.microservice.domain.entities.core.AbstractBaseEntityWithUUID;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */

@Entity
@Table(name = "order_item_tx")
public class OrderItemEntity extends AbstractBaseEntityWithUUID {

    @Column(name = "productId")
    private String productId;

    @Column(name = "productName")
    private String productName;

    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "price")
    private BigDecimal price;

    public OrderItemEntity() {}

    /**
     * Create Order Item Entity
     *
     * @param productId
     * @param productName
     * @param quantity
     * @param price
     */
    public OrderItemEntity(String productId, String productName, BigDecimal quantity, BigDecimal price) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    /**
     * Returns Product ID
     * @return
     */
    public String getProductId() {
        return productId;
    }

    /**
     * Returns Product Name
     * @return
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Returns Quantity
     * @return
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Returns the Price
     * @return
     */
    public BigDecimal getPrice() {
        return price;
    }
}
