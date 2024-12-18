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

package io.fusion.air.microservice.security;
// JWT
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
import static io.fusion.air.microservice.utils.Utils.println;

/**
 *  JSON Web Token Validator
 *
 * @author arafkarsh
 *
 */
@Service
public final class JsonWebTokenValidator {

	public static final String DOUBLE_LINE = "===============================================================================";

	/**
	 * Initialize the JWT with the Signature Algorithm based on Secret Key or Public / Private Key
	 */
	public JsonWebTokenValidator() {
		// Nothing to instantiate
	}

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
	public boolean validateToken(String userId, TokenData token) {
		return (!isTokenExpired(token) &&
				getSubjectFromToken(token).equals(userId));
	}

	/**
	 * Returns True if the Token is expired
	 *
	 * @param token
	 * @return
	 */
	public boolean isTokenExpired(TokenData token) {
		return getExpiryDateFromToken(token).before(new Date());
	}

	/**
	 * Get the User / Subject from the Token
	 *
	 * @param token
	 * @return
	 */
	public String getSubjectFromToken(TokenData token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	/**
	 * Get the Expiry Date of the Token
	 *
	 * @param token
	 * @return
	 */
	public Date getExpiryDateFromToken(TokenData token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	/**
	 * Token Should not be used before this Date.
	 *
	 * @param token
	 * @return
	 */
	public Date getNotBeforeDateFromToken(TokenData token) {
		return getClaimFromToken(token, Claims::getNotBefore);
	}
	/**
	 * Get the Token Issue Date
	 *
	 * @param token
	 * @return
	 */
	public Date getIssuedAtFromToken(TokenData token) {
		return getClaimFromToken(token, Claims::getIssuedAt);
	}

	/**
	 * Get the Issuer from the Token
	 *
	 * @param token
	 * @return
	 */
	public String getIssuerFromToken(TokenData token) {
		return getClaimFromToken(token, Claims::getIssuer);
	}

	/**
	 * Get the Audience from the Token
	 *
	 * @param token
	 * @return
	 */
	public String getAudienceFromToken(TokenData token) {
		return getClaimFromToken(token, Claims::getAudience)
				.stream()
				.map(String::valueOf) // Convert each element to a string (if needed)
				.collect(Collectors.joining(", "));
	}

	public String getUserRoleFromToken(TokenData token) {
		Claims claims = getAllClaims(token);
		String role = (String) claims.get("rol");
		return (role == null) ? "Public" : role;
	}

	/**
	 * Get the Cloak User from the Token
	 * @param token
	 * @return
	 */
	public String getCloakPreferredUser(TokenData token) {
		Claims claims = getAllClaims(token);
		String subject = (String) claims.get("sub");
		String puser = (String) claims.get("preferred_username");
		return (puser == null) ? subject: puser;
	}

	public <T> T getClaimFromToken(TokenData token,
								   Function<Claims, T> claimsResolver) {
		return claimsResolver.apply(getAllClaims(token));
	}

	public Claims getAllClaims(TokenData token) {
		return getJws(token).getPayload();
	}

	public Jws<Claims> getJws(TokenData token) {
		return (token.getKeyType()  == JsonWebTokenConstants.PUBLIC_KEY) ?
				Jwts.parser()
						.verifyWith( (PublicKey) token.getValidatoryKey() )
						.requireIssuer(token.getIssuer())
						.build()
						.parseSignedClaims(token.getToken())
				: Jwts.parser()
				.verifyWith( (SecretKey) token.getValidatoryKey() )
				.requireIssuer(token.getIssuer())
				.build()
				.parseSignedClaims(token.getToken());
		/**
		return Jwts.parserBuilder()
				.setSigningKey(_token.getValidatoryKey())
				.requireIssuer(_token.getIssuer())
				.build()
				.parseClaimsJws(_token.getToken());
		 */
	}

	/**
	 * Return Payload as JSON String
	 *
	 * @param token
	 * @return
	 */
	public String getPayload(TokenData token) {
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
	public void tokenStats(TokenData token) {
		tokenStats(token, true, true);
	}

    /**
     * Print Token Stats
	 * @param token
	 * @param showClaims
	 */
	public void tokenStats(TokenData token,  boolean showClaims) {
		tokenStats(token, showClaims, false);
	}

	/**
	 * Print Token Stats
	 * @param token
	 * @param showClaims
	 * @param showPayload
	 */
    public void tokenStats(TokenData token, boolean showClaims, boolean showPayload) {
		println("-------------- aaa.bbb.ccc ------------------- 1 -");
		println(token);
		println("-------------- ----------- ------------------- 2 -");
		println("Subject  = "+getSubjectFromToken(token));
		println("Audience = "+getAudienceFromToken(token));
		println("Issuer   = "+getIssuerFromToken(token));
		println("IssuedAt = "+getIssuedAtFromToken(token));
		println("Expiry   = "+getExpiryDateFromToken(token));
		println("Expired  = "+isTokenExpired(token));
		println("---------------------------------------------- 3 -");
		Jws<Claims> jws = getJws(token);
		println("Header       : " + jws.getHeader());
		println("Body         : " + jws.getPayload());
		println("Content      : " + jws.toString());
		if(showClaims) {
			Claims claims = getAllClaims(token);
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

	/**
	 * Only for Testing from Command Line
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {
		println(DOUBLE_LINE);
		println("Generate Json Web Tokens Based on SECRET KEYS");
		println(DOUBLE_LINE);
		// testJWTCreation(JsonWebTokenValidator.SECRET_KEY);
		println(DOUBLE_LINE);
		println("Generate Json Web Tokens Based on PUBLIC/PRIVATE KEYS");
		println(DOUBLE_LINE);
		// testJWTCreation(JsonWebTokenValidator.PUBLIC_KEY);
		println(DOUBLE_LINE);
	}
}
