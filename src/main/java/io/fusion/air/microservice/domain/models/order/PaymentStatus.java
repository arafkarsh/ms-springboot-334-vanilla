/**
 * (C) Copyright 2021 Araf Karsh Hamid 
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

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.fusion.air.microservice.utils.DateJsonSerializer;

/**
 * Payment Status
 * 
 * @author arafkarsh
 *
 */
public class PaymentStatus {
	
	private String transactionId;
	
	@JsonSerialize(using = DateJsonSerializer.class)
	private LocalDateTime transactionDate;
	
	private String payStatus;
	private String paymentReference;
	
	@JsonSerialize(using = DateJsonSerializer.class)
	private LocalDateTime paymentDate;
	private PaymentType paymentType;
	
	/**
	 * 
	 */
	public PaymentStatus() {
	}
	/**
	 * Payment Status
	 * 
	 * @param txId
	 * @param txDate
	 * @param payStatus
	 * @param payRef
	 * @param payDate
	 * @param payType
	 */
	public PaymentStatus(String txId, LocalDateTime txDate, String payStatus,
			String payRef, LocalDateTime payDate, PaymentType payType) {
		
		transactionId		= txId;
		transactionDate		= txDate;
		this.payStatus = payStatus;
		
		paymentReference	= payRef;
		paymentDate			= payDate;
		paymentType			= payType;
	}
	
	/**
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}
	/**
	 * @return the transactionDate
	 */
	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}
	/**
	 * @return the paymentStatus
	 */
	public String getPaymentStatus() {
		return payStatus;
	}
	/**
	 * @return the paymentReference
	 */
	public String getPaymentReference() {
		return paymentReference;
	}
	/**
	 * @return the paymentDate
	 */
	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}
	/**
	 * @return the paymentType
	 */
	public PaymentType getPaymentType() {
		return paymentType;
	}
	
	/**
	 * Returns Transaction ID | Payment Status | Payment Reference
	 */
	public String toString() {
		return transactionId + "|" + payStatus + "|" + paymentReference;
	}
	
	/**
	 * Returns the HashCode of the Tx ID
	 */
	public int hashCode() {
		return transactionId.hashCode();
	}

	/**
	 * Equals Payment Status based on Tx ID
	 * @param o
	 * @return
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PaymentStatus ps = (PaymentStatus) o;
		return ps.transactionId.equalsIgnoreCase(this.transactionId);
	}
}
