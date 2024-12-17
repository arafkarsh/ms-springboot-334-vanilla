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

// Custom
import io.fusion.air.microservice.server.config.ServiceConfiguration;
import static io.fusion.air.microservice.utils.Utils.println;
// JWT
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
// Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// Java
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * JSON Web Token Implementation
 * Supports Secret Key based and Public / Private Key based Token Generation & Validation.
 *
 * @author arafkarsh
 * @version 1.0
 * @date
 */
@Service
public final class JsonWebToken {

	public static final int SECRET_KEY 				= 1;
	public static final int PUBLIC_KEY				= 2;
	public static final int LOCAL_KEY 				= 1;

	public static final String EXPIRES_IN = "expires_in";
	public static final String REFRESH_EXPIRES_IN = "refresh_expires_in";
	public static final String SINGLE_LINE = "----------------------------------------------";
	public static final String DOUBLE_LINE = "===============================================================================";

	// Autowired using the Constructor
	private ServiceConfiguration serviceConfig;

	// Autowired using the Constructor
	private JsonWebTokenKeyManager keyManager;

	private int tokenType;

	private Key signingKey;
	private Key validatorKey;
	private Key validatorLocalKey;

	private String issuer;

	private long tokenAuthExpiry;
	private long tokenRefreshExpiry;

	/**
	 * Initialize the JWT with the Signature Algorithm based on Secret Key or Public / Private Key
	 */
	public JsonWebToken() {
	}

	/**
	 * Initialize the JWT with the Signature Algorithm based on Secret Key or Public / Private Key
	 * Autowired using the Constructor
	 * @param serviceCfg
	 * @param kManager
	 */
	@Autowired
	public JsonWebToken(ServiceConfiguration serviceCfg, JsonWebTokenKeyManager kManager) {
		serviceConfig = serviceCfg;
		keyManager = kManager;
	}

	/**
	 * Initialize the JsonWebToken with Token Type Secret Keys and other default claims
	 * settings.
	 * @return
	 */
	public JsonWebToken init() {
		return init(SECRET_KEY);
	}

	/**
	 * Initialize the JsonWebToken with Token Type (Secret or Public/Private Keys) and other default claims
	 * settings.
	 * @return
	 */
	public JsonWebToken init(int tknType) {
		tokenType 			= tknType;
		if(keyManager == null) {
			println("Key Manager is Not Auto-Initialized. Manually Initializing now...");
			keyManager = new JsonWebTokenKeyManager();
		}
		keyManager.init(tknType);
		return this;
	}

	/**
	 * This is set when the Applications Boots Up from the Servlet Event Listener
	 * Servlet Event Listener ensures that the public key is downloaded from the KeyCloak Server
	 * Set the Validator Key as KeyCloak Public Key if the Public Key downloaded from KeyCloak.
	 */
	public void setKeyCloakPublicKey() {
		keyManager.setKeyCloakPublicKey();
	}

	/**
	 * Set the Token Expiry Time - MUST NOT BE GREATER THAN 30 MINS
	 * IF YES THEN SET EXPIRY TO 5 MINS
	 * @param time
	 * @return
	 */
	public JsonWebToken setTokenAuthExpiry(long time)   {
		tokenAuthExpiry = (time > JsonWebTokenConstants.EXPIRE_IN_THIRTY_MINS) ? JsonWebTokenConstants.EXPIRE_IN_FIVE_MINS : time;
		return this;
	}

	/**
	 * Set the Token Expiry Time
	 * @param time
	 * @return
	 */
	public JsonWebToken setTokenRefreshExpiry(long time)   {
		tokenRefreshExpiry = (time < JsonWebTokenConstants.EXPIRE_IN_THIRTY_MINS) ? JsonWebTokenConstants.EXPIRE_IN_THIRTY_MINS : time;
		return this;
	}

	/**
	 * Add Default Claims
	 * @param claims
	 * @return
	 */
	private Map<String, Object>  addDefaultClaims(Map<String, Object> claims) {
		String aud = (serviceConfig != null) ? serviceConfig.getServiceName() : "general";
		claims.putIfAbsent("aud", aud);
		claims.putIfAbsent("jti", UUID.randomUUID().toString());
		claims.putIfAbsent("rol", "User");
		return claims;
	}

	/**
	 * Generate Authorize Bearer Token and Refresh Token
	 * Returns in a HashMap
	 * token = Authorization Token
	 * refresh = Refresh token to re-generate the Authorize Token
	 * API Usage
	 * HashMap<String,String> tokens = new JsonWebToken()
	 * 									.init()
	 * 									generateTokens(subject, issuer, tokenExpiryTime, refreshTokenExpiryTime);
	 * @param subject
	 * @param issuer
	 * @return
	 */
	public Map<String,String>  generateTokens(String subject, String issuer, long tokenExpiryTime, long refreshTokenExpiryTime) {
		Map<String, Object> claimsToken = addDefaultClaims(new HashMap<>());
		Map<String, Object> claimsRefreshToken = addDefaultClaims(new HashMap<>());
		HashMap<String, String> tokens  = new HashMap<>();
		String tokenAuth 	= generateToken(subject, issuer, tokenExpiryTime, claimsToken);
		String tokenRefresh = generateToken(subject, issuer, refreshTokenExpiryTime, claimsRefreshToken);
		tokens.put("access_token", tokenAuth);
		tokens.put("refresh_token", tokenRefresh);
		tokens.put(EXPIRES_IN, ""+tokenExpiryTime);
		tokens.put(REFRESH_EXPIRES_IN, ""+refreshTokenExpiryTime);
		tokens.put("token_type", "Bearer");
		tokens.put("not-before-policy", "0");
		tokens.put("session_state", UUID.randomUUID().toString());
		tokens.put("scope", "");
		tokens.put("mode", "Local Auth");
		return tokens;
	}

	/**
	 * Generate Authorize Bearer Token and Refresh Token
	 * Returns in a HashMap
	 * token = Authorization Token
	 * refresh = Refresh token to re-generate the Authorize Token
	 * API Usage
	 * HashMap<String,String> tokens = new JsonWebToken()
	 * 									.init()
	 * 									.setTokenExpiry(JsonWebToken.EXPIRE_IN_FIVE_MINS)
	 * 									.setTokenRefreshExpiry(JsonWebToken.EXPIRE_IN_THIRTY_MINS)
	 * 									generateTokens(Map<String,Object> claimsToken, Map<String,Object> claimsRefreshToken)
	 * @param claimsToken
	 * @param claimsRefreshToken
	 * @return
	 */
	public Map<String,String>  generateTokens(String subject, String issuer,
												  Map<String,Object> claimsToken, Map<String,Object> claimsRefreshToken) {
		addDefaultClaims(claimsToken);
		addDefaultClaims(claimsRefreshToken);
		HashMap<String, String> tokens  = new HashMap<>();
		String tokenAuth 	= generateToken(subject, issuer, tokenAuthExpiry, claimsToken);
		String tokenRefresh = generateToken(subject, issuer, tokenRefreshExpiry, claimsRefreshToken);
		tokens.put("token", tokenAuth);
		tokens.put("refresh", tokenRefresh);
		try {
			tokens.put(EXPIRES_IN, "" + (tokenAuthExpiry / 1000));
			tokens.put(REFRESH_EXPIRES_IN, "" + (tokenRefreshExpiry / 1000));
		} catch (Exception e) {
			tokens.put(EXPIRES_IN, "" + tokenAuthExpiry);
			tokens.put(REFRESH_EXPIRES_IN, "" + tokenRefreshExpiry);
		}
		tokens.put("token_type", "Bearer");
		tokens.put("not-before-policy", "0");
		tokens.put("session_state", UUID.randomUUID().toString());
		tokens.put("scope", "");
		tokens.put("mode", "Local Auth");
		return tokens;
	}

	/**
	 * Returns the Key
	 * @return
	 */
	public Key getKey() {
		return signingKey;
	}

	public Key getValidatorKey() {
		return validatorKey;
	}

	public Key getValidatorLocalKey() {
		return validatorLocalKey;
	}

    /**
     * Generate Token for the User
     *  
	 * @param userId
	 * @param expiryTime
	 * @return
	 */
    public String generateToken(String userId, long expiryTime) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("aud", "general");
        claims.put("jti", UUID.randomUUID().toString());
		return generateToken(userId,keyManager.getIssuer(),expiryTime,claims);
    }

    /**
     * Generate Token with Claims
     *  
     * @param userId
     * @param expiryTime
     * @param claims
     * @return
     */
    public String generateToken(String userId, long expiryTime, Map<String, Object> claims) {
        return generateToken(userId, keyManager.getIssuer(), expiryTime, claims);
    }
    
    /**
     * Generate Token with Claims
	 *
	 * @param userId
	 * @param issuer
	 * @param expiryTime
	 * @param claims
	 * @return
	 */
    public String generateToken(String userId, String issuer, long expiryTime, Map<String, Object> claims) {
		return generateToken( userId,  issuer,  expiryTime, claims, keyManager.getKey());
	}

	/**
	 * Generate Token with Claims and with Either Secret Key or Private Key
	 *
	 * @param userId
	 * @param issuer
	 * @param expiryTime
	 * @param claims
	 * @param key
	 * @return
	 */
	public String generateToken(String userId, String issuer, long expiryTime,
								Map<String, Object> claims, Key key) {
		long currentTime = System.currentTimeMillis();
		return Jwts.builder()
				.claims(claims)
				.subject(userId)
				.issuer(issuer)
				.issuedAt(new Date(currentTime))
				.expiration(new Date(currentTime + expiryTime))
				// Key Secret Key or Public/Private Key
				.signWith(key)
				.compact();
	}

    /**
     * Validate User Id with Token
     * 
     * @param userId
     * @param token
     * @return
     */
    public boolean validateToken(String userId, String token) {
        return (!isTokenExpired(token) &&
        		  getSubjectFromToken(token).equals(userId));
    }
    
    /**
     * Returns True if the Token is expired
     * 
     * @param token
     * @return
     */
    public boolean isTokenExpired(String token) {
        return getExpiryDateFromToken(token).before(new Date());
    }
    
	/**
	 * Get the User / Subject from the Token
	 * 
	 * @param token
	 * @return
	 */
    public String getSubjectFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Get the Expiry Date of the Token
     * 
     * @param token
     * @return
     */
    public Date getExpiryDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    /**
     * Token Should not be used before this Date.
     * 
     * @param token
     * @return
     */
    public Date getNotBeforeDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getNotBefore);
    }
    /**
     * Get the Token Issue Date
     * 
     * @param token
     * @return
     */
    public Date getIssuedAtFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }
    
    /**
     * Get the Issuer from the Token
     * 
     * @param token
     * @return
     */
    public String getIssuerFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuer);
    }
    
    /**
     * Get the Audience from the Token
     * 
     * @param token
     * @return
     */
    public String getAudienceFromToken(String token) {
		return getClaimFromToken(token, Claims::getAudience)
				.stream()
				.map(String::valueOf) // Convert each element to a string (if needed)
				.collect(Collectors.joining(", "));
    }

    /**
     * Get a Claim from the Token based on the Claim Type
     * 
     * @param <T>
     * @param token
     * @param claimsResolver
     * @return
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(getAllClaims(token));
    }

	/**
	 * Returns the User Role
	 * @param token
	 * @return
	 */
	public String getUserRoleFromToken(String token) {
		Claims claims = getAllClaims(token);
		String role = (String) claims.get("rol");
		return (role == null) ? "Public" : role;
	}
    
    /**
     * Return All Claims for the Token
     * 
     * @param token
     * @return
     */
    public Claims getAllClaims(String token) {
		return getJws(token).getPayload();
    }

	/**
	 * Returns Jws
	 * @param token
	 * @return
	 */
	public Jws<Claims> getJws(String token) {
		return (tokenType  == PUBLIC_KEY) ?
				Jwts.parser()
					.verifyWith( (PublicKey) keyManager.getValidatorKey() )
					.requireIssuer(keyManager.getIssuer())
					.build()
					.parseSignedClaims(token)
			: Jwts.parser()
					.verifyWith( (SecretKey) keyManager.getValidatorKey() )
					.requireIssuer(keyManager.getIssuer())
					.build()
					.parseSignedClaims(token);
	}

	/**
	 * Returns Jws
	 * @param token
	 * @param keyType
	 * @return
	 */
	public Jws<Claims> getJws(String token,  int keyType) {
		Key key = (keyType == LOCAL_KEY) ? getValidatorLocalKey() : getValidatorKey();
		return (tokenType  == PUBLIC_KEY) ?
				Jwts.parser()
						.verifyWith( (PublicKey) key )
						.requireIssuer(keyManager.getIssuer())
						.build()
						.parseSignedClaims(token)
				: Jwts.parser()
				.verifyWith( (SecretKey) key )
				.requireIssuer(keyManager.getIssuer())
				.build()
				.parseSignedClaims(token);
	}

	/**
	 * Return Payload as JSON String
	 *
	 * @param token
	 * @return
	 */
	public String getPayload(String token) {
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
	public void tokenStats(String token) {
		tokenStats(token, true, true);
	}

    /**
     * Print Token Stats
	 * @param token
	 * @param showClaims
	 */
	public void tokenStats(String token,  boolean showClaims) {
		tokenStats(token, showClaims, false);
	}

	/**
	 * Print Token Stats
	 * @param token
	 * @param showClaims
	 * @param showPayload
	 */
    public void tokenStats(String token, boolean showClaims, boolean showPayload) {
		println("-------------- aaa.bbb.ccc -------------------");
		println(token);
		println("-------------- ----------- -------------------");
		println("Subject  = "+getSubjectFromToken(token));
		println("Audience = "+getAudienceFromToken(token));
		println("Issuer   = "+getIssuerFromToken(token));
		println("IssuedAt = "+getIssuedAtFromToken(token));
		println("Expiry   = "+getExpiryDateFromToken(token));
		println("Expired  = "+isTokenExpired(token));
		println(SINGLE_LINE);
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
		println(SINGLE_LINE);
		if(showPayload) {
			println("Payload=" + getPayload(token));
			println(SINGLE_LINE);
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
	 * Get the Issuer Set for Generating Token / Refresher Token
	 * @return
	 */
	public String getIssuer() {
		return issuer;
	}

	/**
	 * Only for Testing from Command Line
	 *
	 * @param args
	 */
	public static void main(String[] args)  {
		println(DOUBLE_LINE);
		println("Generate Json Web Tokens Based on SECRET KEYS");
		println(DOUBLE_LINE);
		testJWTCreation(JsonWebToken.SECRET_KEY);
		println(DOUBLE_LINE);
		println("Generate Json Web Tokens Based on PUBLIC/PRIVATE KEYS");
		println(DOUBLE_LINE);
		testJWTCreation(JsonWebToken.PUBLIC_KEY);
		println(DOUBLE_LINE);
	}

	/**
	 * Test JWT Creation
	 * @param tokenType
	 */
	protected static void testJWTCreation(int tokenType) {
		// Default Algo Secret Key is HS512 = Hmac with SHA-512
		// for Public / Private Key is RS256
		JsonWebToken jsonWebToken = new JsonWebToken();

		long tokenAuthExpiry = JsonWebTokenConstants.EXPIRE_IN_FIVE_MINS;
		long tokenRefreshExpiry = JsonWebTokenConstants.EXPIRE_IN_THIRTY_MINS;

		String subject	 = "jane.doe";
		String issuer    = "fusion.air";

		Map<String, Object> claims = new HashMap<>();
		claims.put("aud", "generic");
		claims.put("jti", UUID.randomUUID().toString());
		claims.put("rol", "User");
		claims.put("did", "Device ID");
		claims.put("iss", issuer);
		claims.put("sub", subject);

		Map<String,String> tokens = jsonWebToken
				.init(tokenType)
				.generateTokens(subject, issuer, tokenAuthExpiry, tokenRefreshExpiry);

		String token = tokens.get("token");
		String refresh = tokens.get("refresh");
		println("Token Expiry in Days:or:Hours:or:Mins  "+ JsonWebToken.printExpiryTime(tokenAuthExpiry));
		jsonWebToken.tokenStats(token, false, false);

		println("Refresh Token Expiry in Days:or:Hours:or:Mins "+ JsonWebToken.printExpiryTime(tokenRefreshExpiry));
		jsonWebToken.tokenStats(refresh, false, false);

	}
}