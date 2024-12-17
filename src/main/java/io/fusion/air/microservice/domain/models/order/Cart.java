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
package io.fusion.air.microservice.domain.models.order;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * Cart item Request
 *
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
public class Cart {

    @NotBlank(message = "The Customer ID is required.")
    private String customerId;

    @NotBlank(message = "The Product ID is required.")
    private String productId;

    private String productName;

    private BigDecimal price;

    private BigDecimal quantity;

    public Cart() {
        // Nothing to instantiate
    }

    /**
     * Get Customer ID
     * @return
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Get Product ID
     * @return
     */
    public String getProductId() {
        return productId;
    }

    /**
     * Get Product Name
     * @return
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Get Price
     * @return
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Get Quantity
     * @return
     */
    public BigDecimal getQuantity() {
        return quantity;
    }
}
