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
import static io.fusion.air.microservice.utils.Std.println;
// Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// Java
import java.security.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
// Json Web Token
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;


/**
 * JSON Web Token Implementation
 * Supports Secret Key based and Public / Private Key based Token Generation & Validation.
 *
 * @author arafkarsh
 * @version 1.0
 * @date
 */
@Service
public final class JsonWebTokenNew {
	
	private static final String TOKEN = "<([1234567890SecretKey!!To??Encrypt##Data@12345%6790])>";

	public static final long EXPIRE_IN_ONE_MINS 		= 1000L * 60;
	public static final long EXPIRE_IN_FIVE_MINS 	= EXPIRE_IN_ONE_MINS * 5;
	public static final long EXPIRE_IN_TEN_MINS 		= EXPIRE_IN_ONE_MINS * 10;
	public static final long EXPIRE_IN_TWENTY_MINS 	= EXPIRE_IN_ONE_MINS * 20;
	public static final long EXPIRE_IN_THIRTY_MINS 	= EXPIRE_IN_ONE_MINS * 30;
	public static final long EXPIRE_IN_ONE_HOUR 		= EXPIRE_IN_ONE_MINS * 60;

	public static final long EXPIRE_IN_TWO_HOUR 		= EXPIRE_IN_ONE_HOUR * 2;
	public static final long EXPIRE_IN_THREE_HOUR 	= EXPIRE_IN_ONE_HOUR * 3;
	public static final long EXPIRE_IN_FIVE_HOUR 	= EXPIRE_IN_ONE_HOUR * 5;
	public static final long EXPIRE_IN_EIGHT_HOUR 	= EXPIRE_IN_ONE_HOUR * 8;
	public static final long EXPIRE_IN_ONE_DAY 		= EXPIRE_IN_ONE_HOUR * 24;

	public static final long EXPIRE_IN_TWO_DAYS 		= EXPIRE_IN_ONE_DAY * 2;
	public static final long EXPIRE_IN_ONE_WEEK 		= EXPIRE_IN_ONE_DAY * 7;
	public static final long EXPIRE_IN_TWO_WEEKS 	= EXPIRE_IN_ONE_DAY * 14;
	public static final long EXPIRE_IN_ONE_MONTH 	= EXPIRE_IN_ONE_DAY * 30;
	public static final long EXPIRE_IN_THREE_MONTHS	= EXPIRE_IN_ONE_DAY * 90;
	public static final long EXPIRE_IN_SIX_MONTHS 	= EXPIRE_IN_ONE_DAY * 180;
	public static final long EXPIRE_IN_ONE_YEAR 		= EXPIRE_IN_ONE_DAY * 365;

	public static final long EXPIRE_IN_TWO_YEARS 	= EXPIRE_IN_ONE_YEAR * 2;
	public static final long EXPIRE_IN_FIVE_YEARS 	= EXPIRE_IN_ONE_YEAR * 5;
	public static final long EXPIRE_IN_TEN_YEARS 	= EXPIRE_IN_ONE_YEAR * 10;

	public static final int SECRET_KEY 				= 1;
	public static final int PUBLIC_KEY				= 2;

	private static final String TOKEN_KEY = "token";
	private static final String REFRESH_KEY = "refresh";

	public static final String SINGLE_LINE = "----------------------------------------------";
	public static final String DOUBLE_LINE = "===============================================================================";


	// Autowired using the Constructor
	private ServiceConfiguration serviceConfig;

	// Autowired using the Constructor
	private CryptoKeyGenerator cryptoKeys;

	private int tokenType;

	private Key signingKey;
	private Key validatorKey;

	private String issuer;
	private String subject;
	private long tokenAuthExpiry;
	private long tokenRefreshExpiry;

	/**
	 * Initialize the JWT with the Signature Algorithm based on Secret Key or Public / Private Key
	 */
	public JsonWebTokenNew() {
	}

	/**
	 * Autowired using the Constructor
	 * @param serviceCfg
	 * @param keyGenerator
	 */
	@Autowired
	public JsonWebTokenNew(ServiceConfiguration serviceCfg, CryptoKeyGenerator keyGenerator) {
		serviceConfig = serviceCfg;
		cryptoKeys = keyGenerator;
	}

	/**
	 * Initialize the JsonWebToken with Token Type Secret Keys and other default claims
	 * settings.
	 * @return
	 */
	public JsonWebTokenNew init() {
		return init(SECRET_KEY);
	}

	/**
	 * Initialize the JsonWebToken with Token Type (Secret or Public/Private Keys) and other default claims
	 * settings.
	 * @return
	 */
	public JsonWebTokenNew init(int tokenType) {
		this.tokenType = tokenType;
		// Set the Algo Symmetric (Secret) OR Asymmetric (Public/Private) based on the Configuration
		println("Token Type = "+ this.tokenType);
		// Create the Key based on Secret Key or Private Key
		createSigningKey();
		issuer				= (serviceConfig != null) ? serviceConfig.getServiceOrg() : "fusion.air";
		subject 			= "jane.doe";
		setTokenAuthExpiry((serviceConfig != null) ? serviceConfig.getTokenAuthExpiry() : EXPIRE_IN_FIVE_MINS );
		setTokenRefreshExpiry((serviceConfig != null) ? serviceConfig.getTokenRefreshExpiry() : EXPIRE_IN_THIRTY_MINS );
		return this;
	}

	/**
	 * Create the Key based on  Secret Key or Public / Private Key
	 *
	 * @return
	 */
	private void createSigningKey() {
		switch(tokenType) {
			case PUBLIC_KEY:
				getCryptoKeyGenerator()
				.setKeyFiles(getCryptoPublicKeyFile(), getCryptoPrivateKeyFile())
				.iFPublicPrivateKeyFileNotFound().THEN()
					.createRSAKeyFiles()
				.ELSE()
					.readRSAKeyFiles()
				.build();
				signingKey = getCryptoKeyGenerator().getPrivateKey();
				validatorKey = getCryptoKeyGenerator().getPublicKey();
				println("Public key format: " + getCryptoKeyGenerator().getPublicKey().getFormat());
				println(getCryptoKeyGenerator().getPublicKeyPEMFormat());
				break;
			case SECRET_KEY:
				// Fall through to default
			default:
				signingKey = new SecretKeySpec(getTokenKeyBytes(), "HmacSHA512");
				validatorKey = signingKey;
				break;
		}
	}

	/**
	 * Returns Crypto Public Key File
	 * @return
	 */
	private String getCryptoPublicKeyFile() {
		return (serviceConfig != null) ? serviceConfig.getCryptoPublicKeyFile() : "publicKey.pem";
	}

	/**
	 * Returns Crypto Private Key File
	 * @return
	 */
	private String getCryptoPrivateKeyFile() {
		return (serviceConfig != null) ? serviceConfig.getCryptoPrivateKeyFile() : "privateKey.pem";
	}

	/**
	 * Returns Token Key -
	 * In SpringBooT Context from ServiceConfiguration
	 * Else from Static TOKEN Key
	 * @return
	 */
	private String getTokenKey() {
		return (serviceConfig != null) ? serviceConfig.getTokenKey() : TOKEN;
	}

	/**
	 * Returns the Token Key in Bytes
	 * @return
	 */
	private byte[] getTokenKeyBytes() {
		return HashData.base64Encoder(getTokenKey()).getBytes();
	}

	/**
	 * Returns CryptoKeyGenerator
	 * @return
	 */
	private CryptoKeyGenerator getCryptoKeyGenerator() {
		if(cryptoKeys == null) {
			cryptoKeys = new CryptoKeyGenerator();
		}
		return cryptoKeys;
	}

	/**
	 * Set the Issuer
	 * @param issuer
	 * @return
	 */
	public JsonWebTokenNew setIssuer(String issuer) {
		this.issuer = issuer;
		return this;
	}

	/**
	 * Set the Subject
	 * @param subject
	 * @return
	 */
	public JsonWebTokenNew setSubject(String subject)   {
		this.subject = subject;
		return this;
	}

	/**
	 * Set the Token Expiry Time - MUST NOT BE GREATER THAN 30 MINS
	 * IF YES THEN SET EXPIRY TO 5 MINS
	 * @param time
	 * @return
	 */
	public JsonWebTokenNew setTokenAuthExpiry(long time)   {
		tokenAuthExpiry = (time > EXPIRE_IN_THIRTY_MINS) ? EXPIRE_IN_FIVE_MINS : time;
		return this;
	}

	/**
	 * Set the Token Expiry Time
	 * @param time
	 * @return
	 */
	public JsonWebTokenNew setTokenRefreshExpiry(long time)   {
		tokenRefreshExpiry = (time < EXPIRE_IN_THIRTY_MINS) ? EXPIRE_IN_THIRTY_MINS : time;
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
	 * 									.setTokenExpiry(JsonWebToken.EXPIRE_IN_FIVE_MINS)
	 * 									.setTokenRefreshExpiry(JsonWebToken.EXPIRE_IN_THIRTY_MINS)
	 * 									.addAllTokenClaims(Map<String,Object> claims)
	 * 									.addAllRefreshTokenClaims(Map<String,Object> claims)
	 * 									generateTokens()
	 * @return
	 */
	public Map<String,String>  generateTokens() {
		HashMap<String, String> tokens  = new HashMap<>();
		String tokenAuth 	= generateToken(subject, issuer, tokenAuthExpiry, addDefaultClaims(new HashMap<>()));
		String tokenRefresh = generateToken(subject, issuer, tokenRefreshExpiry, addDefaultClaims(new HashMap<>()));
		tokens.put(TOKEN_KEY, tokenAuth);
		tokens.put(REFRESH_KEY, tokenRefresh);
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
	 * 									generateTokens(subject, issuer, tokenExpiryTime, refreshTokenExpiryTime);
	 * @param subject
	 * @param issuer
	 * @return
	 */
	public Map<String,String>  generateTokens(String subject, String issuer,
												  long tokenExpiryTime, long refreshTokenExpiryTime) {
		Map<String, Object> claimsToken = addDefaultClaims(new HashMap<>());
		Map<String, Object> claimsRefreshToken = addDefaultClaims(new HashMap<>());
		HashMap<String, String> tokens  = new HashMap<>();
		String tokenAuth 	= generateToken(subject, issuer, tokenExpiryTime, claimsToken);
		String tokenRefresh = generateToken(subject, issuer, refreshTokenExpiryTime, claimsRefreshToken);
		tokens.put(TOKEN_KEY, tokenAuth);
		tokens.put(REFRESH_KEY, tokenRefresh);
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
		tokens.put(TOKEN_KEY, tokenAuth);
		tokens.put(REFRESH_KEY, tokenRefresh);
		return tokens;
	}

	/**
	 * Returns the Key
	 * @return
	 */
	public Key getKey() {
		return signingKey;
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
        return generateToken(userId, issuer, expiryTime, claims);
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
        return generateToken(userId, issuer, expiryTime, claims);
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
		return generateToken( userId,  issuer,  expiryTime, claims, signingKey);

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
					.verifyWith( (PublicKey) validatorKey )
					.requireIssuer(issuer)
					.build()
					.parseSignedClaims(token)
			: Jwts.parser()
					.verifyWith( (SecretKey) validatorKey )
					.requireIssuer(issuer)
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
			println(SINGLE_LINE);
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
	 * Get the Subject Set for Generating Token / Refresher Token
	 * @return
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Only for Testing from Command Line
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)   {
		println(DOUBLE_LINE);
		println("Generate Json Web Tokens Based on SECRET KEYS");
		println(DOUBLE_LINE);
		testJWTCreation(JsonWebTokenNew.SECRET_KEY);
		println(DOUBLE_LINE);
		println("Generate Json Web Tokens Based on PUBLIC/PRIVATE KEYS");
		println(DOUBLE_LINE);
		testJWTCreation(JsonWebTokenNew.PUBLIC_KEY);
		println(DOUBLE_LINE);
	}

	/**
	 * Test JWT Creation
	 * @param tokenType
	 */
	protected static void testJWTCreation(int tokenType) {
		// Default Algo Secret Key is HS512 = Hmac with SHA-512
		// for Public / Private Key is RS256
		JsonWebTokenNew jsonWebToken = new JsonWebTokenNew();

		long tokenAuthExpiry = JsonWebTokenNew.EXPIRE_IN_FIVE_MINS;
		long tokenRefreshExpiry = JsonWebTokenNew.EXPIRE_IN_THIRTY_MINS;

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

		String token = tokens.get(TOKEN_KEY);
		String refresh = tokens.get(REFRESH_KEY);
		println("Token Expiry in Days:or:Hours:or:Mins  "+ JsonWebTokenNew.printExpiryTime(tokenAuthExpiry));
		jsonWebToken.tokenStats(token, false, false);

		println("Refresh Token Expiry in Days:or:Hours:or:Mins "+ JsonWebTokenNew.printExpiryTime(tokenRefreshExpiry));
		jsonWebToken.tokenStats(refresh, false, false);

	}

	/**
	 protected static void test() {
	 println(printExpiryTime(JsonWebToken.EXPIRE_IN_FIVE_MINS));
	 println(printExpiryTime(JsonWebToken.EXPIRE_IN_THIRTY_MINS));
	 println(printExpiryTime(JsonWebToken.EXPIRE_IN_THREE_HOUR));
	 println(printExpiryTime(JsonWebToken.EXPIRE_IN_ONE_DAY));
	 println(printExpiryTime(JsonWebToken.EXPIRE_IN_THREE_HOUR
	 +JsonWebToken.EXPIRE_IN_TWENTY_MINS));
	 println(printExpiryTime(JsonWebToken.EXPIRE_IN_TWO_DAYS
	 +JsonWebToken.EXPIRE_IN_THREE_HOUR+JsonWebToken.EXPIRE_IN_THIRTY_MINS));
	 }*/
}