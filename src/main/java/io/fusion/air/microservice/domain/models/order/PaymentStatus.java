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
	
	private String paymentStatus;
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
	 * @param _txId
	 * @param _txDate
	 * @param _payStatus
	 * @param _payRef
	 * @param _payDate
	 * @param _payType
	 */
	public PaymentStatus(String _txId, LocalDateTime _txDate, String _payStatus,
			String _payRef, LocalDateTime _payDate, PaymentType _payType) {
		
		transactionId		= _txId;
		transactionDate		= _txDate;
		paymentStatus		= _payStatus;
		
		paymentReference	= _payRef;
		paymentDate			= _payDate;
		paymentType			= _payType;
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
		return paymentStatus;
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
		return transactionId + "|" + paymentStatus + "|" + paymentReference;
	}
	
	/**
	 * Returns the HashCode of the Tx ID
	 */
	public int hashCode() {
		return transactionId.hashCode();
	}
}
