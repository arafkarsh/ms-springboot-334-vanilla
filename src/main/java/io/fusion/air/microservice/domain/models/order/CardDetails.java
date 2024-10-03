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

/**
 * 
 * @author arafkarsh
 *
 */
public class CardDetails {
	
	private String cardNumber;
	private String holderName;
	private int expiryMonth;
	private int expiryYear;
	private int cardCode;
	
	private CardType cardType;
	
	/**
	 * 
	 */
	public CardDetails() {
	}
	
	/**
	 * @param cardNumber
	 * @param holderName
	 * @param expiryMonth
	 * @param expiryYear
	 * @param cardCode
	 * @param cardType
	 */
	public CardDetails(String cardNumber, String holderName, 
			int expiryMonth, int expiryYear, int cardCode,
			CardType cardType) {
		super();
		this.cardNumber = cardNumber;
		this.holderName = holderName;
		this.expiryMonth = expiryMonth;
		this.expiryYear = expiryYear;
		this.cardCode = cardCode;
		this.cardType = cardType;
	}

	/**
	 * @return the cardNumber
	 */
	public String getCardNumber() {
		return cardNumber;
	}
	/**
	 * @return the holderName
	 */
	public String getHolderName() {
		return holderName;
	}
	/**
	 * @return the expiryMonth
	 */
	public int getExpiryMonth() {
		return expiryMonth;
	}
	/**
	 * @return the expiryYear
	 */
	public int getExpiryYear() {
		return expiryYear;
	}
	/**
	 * @return the cardCode
	 */
	public int getCardCode() {
		return cardCode;
	}

	/**
	 * @return the cardType
	 */
	public CardType getCardType() {
		return cardType;
	}
}
