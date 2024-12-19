/**
 * (C) Copyright 2022 Araf Karsh Hamid
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
package io.fusion.air.microservice.utils;
// Java
import java.io.IOException;
import java.time.LocalDateTime;
// Faster XML
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
public class DateJsonSerializer extends StdSerializer<LocalDateTime> {
	private static final long serialVersionUID = -7035242849275956037L;

	/**
	 * Constructor
	 */
	public DateJsonSerializer() {
		super(LocalDateTime.class);
	}

	/**
	 * Serialize the date
	 * @param value
	 * @param gen
	 * @param provider
	 * @throws IOException
	 */
	@Override
	public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider)
			throws IOException {
		gen.writeString(value.toString());		
	}
}
