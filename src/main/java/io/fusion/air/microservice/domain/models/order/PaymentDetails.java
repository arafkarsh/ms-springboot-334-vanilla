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
import io.fusion.air.microservice.utils.Utils;

/**
 * Payment Details
 * 
 * @author arafkarsh
 *
 */
public class PaymentDetails {

	private String transactionId;
	
	@JsonSerialize(using = DateJsonSerializer.class)
	private LocalDateTime transactionDate;
	
	private double orderValue;
	private PaymentType paymentType;
	
	private CardDetails cardDetails;
	
	/**
	 * 
	 */
	public PaymentDetails() {
	}
	
	/**
	 * Payment Details
	 * 
	 * @param _txId
	 * @param _txDate
	 * @param _orderValue
	 * @param _payType
	 */
	public PaymentDetails(String _txId, LocalDateTime _txDate,
			double _orderValue, PaymentType _payType) {
		this( _txId,  _txDate, _orderValue,  _payType,  null); 
	}
	
	/**
	 * Payment Details with Card Details
	 * 
	 * @param _txId
	 * @param _txDate
	 * @param _orderValue
	 * @param _payType
	 * @param _card
	 */
	public PaymentDetails(String _txId, LocalDateTime _txDate,
			double _orderValue, PaymentType _payType, CardDetails _card) {
		
		transactionId		= _txId;
		transactionDate		= _txDate;
		orderValue			= _orderValue;
		paymentType			= _payType;
		cardDetails			= _card;
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
	 * @return the orderValue
	 */
	public double getOrderValue() {
		return orderValue;
	}
	
	/**
	 * @return the paymentType
	 */
	public PaymentType getPaymentType() {
		return paymentType;
	}
	
	public String toString() {
		return Utils.toJsonString(this);
	}
	
	/**
	 * @return the cardDetails
	 */
	public CardDetails getCardDetails() {
		return cardDetails;
	}
}
