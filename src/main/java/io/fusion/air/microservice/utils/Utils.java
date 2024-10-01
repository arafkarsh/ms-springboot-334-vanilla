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

package io.fusion.air.microservice.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import io.fusion.air.microservice.domain.models.core.StandardResponse;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
import org.slf4j.MDC;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 
 * @author arafkarsh
 *
 */
public final class Utils {

	private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

	/**
	 * Returns UUID Object from a Byte Array
	 * Reference: https://www.baeldung.com/java-uuid
	 * @param _byteArray
	 * @return
	 */
	public static UUID getUUID(byte[] _byteArray) {
		return  UUID.nameUUIDFromBytes(_byteArray);
	}

	/**
	 * Returns UUID Object from a UUID String
	 * Reference: https://www.baeldung.com/java-uuid
	 * A UUID represents a 128-bit value (36 Characters long)
	 * @param _uuid
	 * @return
	 */
	public static UUID getUUID(String _uuid) {
		return  UUID.fromString(_uuid);
	}

	/**
	 * Generate Random UUID String
	 * A UUID represents a 128-bit value (36 Characters long)
	 * @return
	 */
	public static String generateUUIDString() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Generate Type 1 UUID
	 * A UUID represents a 128-bit value (36 Characters long)
	 * UUID version 1 is based on the current timestamp, measured in units of 100 nanoseconds
	 * from October 15, 1582, concatenated with the MAC address of the device where the UUID
	 * is created.
	 * If privacy is a concern, UUID version 1 can alternatively be generated with a random
	 * 48-bit number instead of the MAC address.
	 *
	 * Reference: https://www.baeldung.com/java-uuid
	 * @return
	 */
	public static UUID generateType1UUID() {

		long most64SigBits = get64MostSignificantBitsForVersion1();
		long least64SigBits = get64LeastSignificantBitsForVersion1();

		return new UUID(most64SigBits, least64SigBits);
	}

	/**
	 * Generate Type 3 UUID
	 *
	 *  Reference: https://www.baeldung.com/java-uuid
	 * @param namespace
	 * @param name
	 * @return
	 */
	public static UUID generateType3UUID(String namespace, String name) {
		final byte[] nameSpaceBytes = bytesFromUUID(namespace);
		final byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
		final byte[] result = joinBytes(nameSpaceBytes, nameBytes);
		return UUID.nameUUIDFromBytes(result);
	}

	/**
	 * Generate Type 4 UUID
	 * A UUID represents a 128-bit value (36 Characters long)
	 */
	public static UUID generateType4UUID() {
		return UUID.randomUUID();
	}

	/**
	 * Generate Type 5 UUID
	 * @param namespace
	 * @param name
	 * @return
	 */
	public static UUID generateType5UUID(String namespace, String name) {
		final byte[] nameSpaceBytes = bytesFromUUID(namespace);
		final byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
		final byte[] result = joinBytes(nameSpaceBytes, nameBytes);
		return type5UUIDFromBytes(result);
	}

	/**
	 * Reference: https://www.baeldung.com/java-uuid
	 * @return
	 */
	public static long get64LeastSignificantBitsForVersion1() {
		Random random = new Random();
		long random63BitLong = random.nextLong() & 0x3FFFFFFFFFFFFFFFL;
		long variant3BitFlag = 0x8000000000000000L;
		return random63BitLong + variant3BitFlag;
	}

	/**
	 * Reference: https://www.baeldung.com/java-uuid
	 * @return
	 */
	public static long get64MostSignificantBitsForVersion1() {
		LocalDateTime start = LocalDateTime.of(1582, 10, 15, 0, 0, 0);
		Duration duration = Duration.between(start, LocalDateTime.now());
		long seconds = duration.getSeconds();
		long nanos = duration.getNano();
		long timeForUuidIn100Nanos = seconds * 10000000 + nanos * 100;
		long least12SignificatBitOfTime = (timeForUuidIn100Nanos & 0x000000000000FFFFL) >> 4;
		long version = 1 << 12;
		return (timeForUuidIn100Nanos & 0xFFFFFFFFFFFF0000L) + version + least12SignificatBitOfTime;
	}

	/**
	 * Convert Bytes to Hex
	 * @param bytes
	 * @return
	 */
	private static String bytesToHex(byte[] bytes) {
		final char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			final int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	/**
	 * Convert Bytes from UUID String
	 * @param uuidHexString
	 * @return
	 */
	private static byte[] bytesFromUUID(String uuidHexString) {
		final String normalizedUUIDHexString = uuidHexString.replace("-", "");
		assert normalizedUUIDHexString.length() == 32;
		final byte[] bytes = new byte[16];
		for (int i = 0; i < 16; i++) {
			final byte b = hexToByte(normalizedUUIDHexString.substring(i * 2, i * 2 + 2));
			bytes[i] = b;
		}
		return bytes;
	}

	/**
	 * Hex to Bytes
	 * @param hexString
	 * @return
	 */
	public static byte hexToByte(String hexString) {
		final int firstDigit = Character.digit(hexString.charAt(0), 16);
		final int secondDigit = Character.digit(hexString.charAt(1), 16);
		return (byte) ((firstDigit << 4) + secondDigit);
	}

	/***
	 * Join Bytes
	 * @param byteArray1
	 * @param byteArray2
	 * @return
	 */
	public static byte[] joinBytes(byte[] byteArray1, byte[] byteArray2) {
		final int finalLength = byteArray1.length + byteArray2.length;
		final byte[] result = new byte[finalLength];
		System.arraycopy(byteArray1, 0, result, 0, byteArray1.length);
		System.arraycopy(byteArray2, 0, result, byteArray1.length, byteArray2.length);
		return result;
	}

	/**
	 *
	 * @param name
	 * @return
	 */
	public static UUID type5UUIDFromBytes(byte[] name) {
		final MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException exception) {
			throw new InternalError("SHA-1 not supported", exception);
		}
		final byte[] bytes = Arrays.copyOfRange(md.digest(name), 0, 16);
		bytes[6] &= 0x0f; /* clear version        */
		bytes[6] |= 0x50; /* set to version 5     */
		bytes[8] &= 0x3f; /* clear variant        */
		bytes[8] |= 0x80; /* set to IETF variant  */
		return constructType5UUID(bytes);
	}

	/**
	 *
	 * @param data
	 * @return
	 */
	private static UUID constructType5UUID(byte[] data) {
		long msb = 0;
		long lsb = 0;
		assert data.length == 16 : "data must be 16 bytes in length";
		for (int i = 0; i < 8; i++) {msb = (msb << 8) | (data[i] & 0xff);}
		for (int i = 8; i < 16; i++) {lsb = (lsb << 8) | (data[i] & 0xff);}
		return new UUID(msb, lsb);
	}

	/***
	 * 
	 * @param _object
	 * @return
	 */
	public static String toJsonString(Object _object) {
		if(_object == null) {
			return "";
		}
		try {
			return new ObjectMapper()
					.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
					.findAndRegisterModules()
					.writeValueAsString(_object);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * String Utilities
	 * 
	 * @author arafkarsh
	 *
	 */
	public static class Strings {

		/**
		 * Returns True if the String is NULL or Empty
		 * 
		 * @param input
		 * @return
		 */
	    public static boolean isBlank(String input) {
	        return input == null || input.trim().isEmpty();
	    }
	    
	    public static Stream<String> blankStrings() {
	        return Stream.of(null, "", "  ");
	    }
	}
	
	/**
	 * Number Utility
	 * 
	 * @author arafkarsh
	 *
	 */
	public static class Numbers {
		
		/**
		 * Returns True if the Number is an Odd Number
		 * @param number
		 * @return
		 */
	    public static boolean isOdd(int number) {
	        return number % 2 != 0;
	    }
	}

	/**
	 * Create Cookie
	 * @param request
	 * @param _key
	 * @param _value
	 * @return
	 */
	public static Cookie createCookie(HttpServletRequest request, String _key, String _value) {
		Cookie c = new Cookie(_key, _value);
		// c.setDomain(serviceConfig.getServerHost());
		c.setSecure(true);
		c.setHttpOnly(true);
		// c.setMaxAge((int)JsonWebToken.EXPIRE_IN_EIGHT_HOUR);
		// c.setPath(request.getRequestURI());
		return c;
	}

	/**
	 * Create Cookie
	 * @param request
	 * @param _key
	 * @param _value
	 * @param _age
	 * @return
	 */
	public static Cookie createCookie(HttpServletRequest request, String _key, String _value, int _age) {
		Cookie c = new Cookie(_key, _value);
		// c.setDomain(serviceConfig.getServerHost());
		c.setSecure(true);
		c.setHttpOnly(true);
		c.setMaxAge(_age);
		// c.setPath(request.getRequestURI());
		return c;
	}

	/**
	 * Create Standard Error Response
	 * @param _inputErrors
	 * @param servicePrefix
	 * @param _httpStatus
	 * @param _errorCode
	 * @param _message
	 * @return
	 */
	public static StandardResponse createErrorResponse(Object _inputErrors, String servicePrefix,
							 String _errorCode, HttpStatus _httpStatus, String _message) {

		// Initialize Standard Error Response
		StandardResponse stdResponse = new StandardResponse();
		stdResponse.initFailure(servicePrefix + _errorCode, _message);
		LinkedHashMap<String, Object> payload = new LinkedHashMap<String,Object>();

		// Add Input Errors If Available
		if(_inputErrors != null) {
			payload.put("input", _inputErrors);
		}

		// Add Error Details
		LinkedHashMap<String,Object> errorData = new LinkedHashMap<String,Object>();
		errorData.put("code", _httpStatus.value());
		errorData.put("mesg", _httpStatus.name());
		errorData.put("srv", MDC.get("Service"));
		errorData.put("reqId", MDC.get("ReqId"));
		errorData.put("http", MDC.get("Protocol"));
		errorData.put("path", MDC.get("URI"));
		payload.put("errors", errorData);
		stdResponse.setPayload(payload);

		return stdResponse;
	}

	/**
	 * Creates Curl Command
	 *  curl -X 'POST' \
	 *   'http://localhost:9090/ms-cache/api/v1/service/echo' \
	 *   -H 'accept: application/json' \
	 *   -H 'Content-Type: application/json' \
	 *   -d '{
	 *   "word": "John Doe",
	 *   "day": 0,
	 *   "requestTime": "2022-08-14T09:06:25.717Z"
	 * }'
	 *
	 * @param httpVerb
	 * @param url
	 * @param headers
	 * @param request
	 * @return
	 */
	public static String createCurlCommand(String httpVerb, String url, HttpHeaders headers, Object request) {
		StringBuilder sb = new StringBuilder();
		sb.append("curl -X '").append(httpVerb).append("' ").append("");
		sb.append(" '").append(url).append("' ").append("");
		if(headers != null) {
			for (String key : headers.keySet()) {
				List<String> val = headers.get(key);
				if (val != null && val.size() > 0) {
					sb.append(" -H ").append("'").append(key).append(": ");
					int x = 0;
					for (String v : val) {
						if (x > 0) sb.append(", ");
						sb.append(v);
						x++;
					}
					sb.append("'  ");
				}
			}
		}
		if(request != null) {
			sb.append(" -d '").append(Utils.toJsonString(request)).append("'").append("");
		}
		return sb.toString();
	}

	/**
	 * For Testing ONLY
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		System.out.println("Utils.toJsonString() = "+Utils.toJsonString(new ServiceConfiguration("localhost", 9090)));
	}
}
