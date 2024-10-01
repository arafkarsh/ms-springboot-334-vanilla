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
package io.fusion.air.microservice.server.models;

import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.fusion.air.microservice.utils.DateJsonSerializer;
import io.fusion.air.microservice.utils.Utils;

/**
 * 
 * @author arafkarsh
 *
 */
public class EchoData {
	
	private String word;
	private int day;
	
	@JsonSerialize(using = DateJsonSerializer.class)
	private LocalDateTime requestTime;

	/**
	 * Echo Constructor
	 */
	public EchoData() {
		this("Jane Doe");
	}
	
	/**
	 * Echo Constructor
	 * @param _wordData
	 */
	public EchoData(String _wordData) {
		this.word = _wordData;
		requestTime = LocalDateTime.now();
		day = requestTime.getDayOfYear();
	}

	/**
	 * @return the word
	 */
	public String getWord() {
		return word;
	}

	@Override
	public int hashCode() {
		return Objects.hash(word);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EchoData other = (EchoData) obj;
		return Objects.equals(word, other.word);
	}

	/**
	 * 
	 */
	public String toString() {
		return Utils.toJsonString(this);
	}

	/**
	 * @return the day
	 */
	public int getDay() {
		return day;
	}

	/**
	 * @return the requestTime
	 */
	public LocalDateTime getRequestTime() {
		return requestTime;
	}
	
	public static void main(String[] args) {
		System.out.println("Serialize "
		+Utils.toJsonString(new EchoData("John")));
	}

}
