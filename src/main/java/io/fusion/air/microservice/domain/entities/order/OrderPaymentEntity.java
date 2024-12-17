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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */

@Entity
@Table(name = "order_payment_tx")
public class OrderPaymentEntity extends AbstractBaseEntityWithUUID {

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "status")
    private String status;

    public OrderPaymentEntity() {
        // Nothing to instantiate
    }


    public String getTransactionId() {
        return transactionId;
    }

    public String getStatus() {
        return status;
    }
}
