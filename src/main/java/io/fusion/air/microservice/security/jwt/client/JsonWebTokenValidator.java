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

package io.fusion.air.microservice.security.jwt.client;
// JWT
import io.fusion.air.microservice.security.jwt.core.TokenData;
import io.fusion.air.microservice.security.jwt.core.JsonWebTokenConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
// Spring
import org.springframework.stereotype.Service;
// Java
import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
// Custom
import static io.fusion.air.microservice.utils.Std.println;

/**
 *  JSON Web Token Validator
 *
 * @author arafkarsh
 *
 */
@Service
public final class JsonWebTokenValidator {

	public static final String DOUBLE_LINE = "===============================================================================";

	private JsonWebTokenValidator() {}

	// =============================================================================================
	// Token Data
	// =============================================================================================

	/**
	 * Validate User Id with Token
	 *
	 * @param userId
	 * @param token
	 * @return
	 */
	public static boolean validateToken(String userId, TokenData token) {
		return (!isTokenExpired(token) &&
				getSubjectFromToken(token).equals(userId));
	}

	/**
	 * Returns True if the Token is expired
	 *
	 * @param token
	 * @return
	 */
	public static boolean isTokenExpired(TokenData token) {
		return getExpiryDateFromToken(token).before(new Date());
	}

	/**
	 * Get the User / Subject from the Token
	 *
	 * @param token
	 * @return
	 */
	public static String getSubjectFromToken(TokenData token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	/**
	 * Get the Expiry Date of the Token
	 *
	 * @param token
	 * @return
	 */
	public static Date getExpiryDateFromToken(TokenData token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	/**
	 * Token Should not be used before this Date.
	 *
	 * @param token
	 * @return
	 */
	public static Date getNotBeforeDateFromToken(TokenData token) {
		return getClaimFromToken(token, Claims::getNotBefore);
	}
	/**
	 * Get the Token Issue Date
	 *
	 * @param token
	 * @return
	 */
	public static Date getIssuedAtFromToken(TokenData token) {
		return getClaimFromToken(token, Claims::getIssuedAt);
	}

	/**
	 * Get the Issuer from the Token
	 *
	 * @param token
	 * @return
	 */
	public static String getIssuerFromToken(TokenData token) {
		return getClaimFromToken(token, Claims::getIssuer);
	}

	/**
	 * Get the Audience from the Token
	 * @param token
	 * @return
	 */
	public static String getAudienceFromToken(TokenData token) {
		return getClaimFromToken(token, Claims::getAudience)
				.stream()
				.map(String::valueOf) // Convert each element to a string (if needed)
				.collect(Collectors.joining(", "));
	}

	/**
	 * Get the User Role from the Token
	 * @param token
	 * @return
	 */
	public static String getUserRoleFromToken(TokenData token) {
		Claims claims = getAllClaims(token);
		String role = (String) claims.get("rol");
		return (role == null) ? "Public" : role;
	}

	/**
	 * Get the Cloak User from the Token
	 * @param token
	 * @return
	 */
	public static String getCloakPreferredUser(TokenData token) {
		Claims claims = getAllClaims(token);
		String subject = (String) claims.get("sub");
		String puser = (String) claims.get("preferred_username");
		return (puser == null) ? subject: puser;
	}

	/**
	 * Get Claims from the Token
	 * @param token
	 * @param claimsResolver
	 * @return
	 * @param <T>
	 */
	public static <T> T getClaimFromToken(TokenData token,
								   Function<Claims, T> claimsResolver) {
		return claimsResolver.apply(getAllClaims(token));
	}

	/**
	 * Get All Claims
	 * @param token
	 * @return
	 */
	public static Claims getAllClaims(TokenData token) {
		return getJws(token).getPayload();
	}

	/**
	 * Get Jws Claims
	 * @param token
	 * @return
	 */
	public static Jws<Claims> getJws(TokenData token) {
		return (token.getKeyType()  == JsonWebTokenConstants.PUBLIC_KEY) ?
				// if Token Validated using - Public Key
				Jwts.parser()
						.verifyWith( (PublicKey) token.getValidatoryKey() )
						.requireIssuer(token.getIssuer())
						.build()
						.parseSignedClaims(token.getToken())
				// Else Token Validated using - Secret Key
				: Jwts.parser()
				.verifyWith( (SecretKey) token.getValidatoryKey() )
				.requireIssuer(token.getIssuer())
				.build()
				.parseSignedClaims(token.getToken());
	}

	/**
	 * Return Payload as JSON String
	 * @param token
	 * @return
	 */
	public static String getPayload(TokenData token) {
		StringBuilder sb = new StringBuilder();
		Claims claims = getAllClaims(token);
		int x=1;
		int size=claims.size();
		sb.append("{");
		for(Entry<String, Object> claim : claims.entrySet()) {
			if(claim != null) {
				sb.append("\""+claim.getKey()+"\": \"").append(claim.getValue());
				sb.append("\"");
				if(x<size) {
					sb.append(",");
				}
			}
			x++;
		}
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Print Token Stats
	 * @param token
	 */
	public static void tokenStats(TokenData token) {
		tokenStats(token, true, true);
	}

    /**
     * Print Token Stats
	 * @param token
	 * @param showClaims
	 */
	public static void tokenStats(TokenData token,  boolean showClaims) {
		tokenStats(token, showClaims, false);
	}

	/**
	 * Print Token Stats
	 * @param token
	 * @param showClaims
	 * @param showPayload
	 */
    public static void tokenStats(TokenData token, boolean showClaims, boolean showPayload) {
		Claims claims = getAllClaims(token);
		println("-------------- aaa.bbb.ccc ------------------- 1 -");
		println("Bearer "+token.getToken());
		println("-------------- ----------- ------------------- 2 -");
		println("Subject  = "+getSubjectFromToken(token));
		println("Audience = "+getAudienceFromToken(token));
		println("Issuer   = "+getIssuerFromToken(token));
		println("Type     = "+claims.get("type"));
		println("Role     = "+claims.get("rol"));
		println("IssuedAt = "+getIssuedAtFromToken(token));
		println("Expiry   = "+getExpiryDateFromToken(token));
		println("Expired  = "+isTokenExpired(token));
		println("---------------------------------------------- 3 -");
		Jws<Claims> jws = getJws(token);
		println("Header       : " + jws.getHeader());
		println("Body         : " + jws.getPayload());
		println("Content      : " + jws.toString());
		if(showClaims) {
			int x = 1;
			for (Entry<String, Object> o : claims.entrySet()) {
				println(x + "> " + o);
				x++;
			}
		}
		println("---------------------------------------------- 4 -");
		if(showPayload) {
			println("Payload=" + getPayload(token));
			println("---------------------------------------------- 5 -");
		}

    }

	/**
	 * Returns Expiry Time in Days:Hours:Mins
	 * @param time
	 * @return
	 */
	public static String printExpiryTime(long time) {
		String ms="0";
		String hs="0";
		String ds="0";
		long m = time / (1000 * 60);
		long h = time / (1000 * 60 * 60);
		long d = time / (1000 * 60 * 60 * 24);
		if(m > 59) { m = m-(h*60); }
		if(h > 23) { h = h-(d*24);}
		ms = (m<10) ? ms + m : ""+m;
		hs = (h<10) ? hs + h : ""+h;
		ds = (d<10) ? ds + d : ""+d;
		return ds + ":" + hs + ":" + ms;
	}
}
