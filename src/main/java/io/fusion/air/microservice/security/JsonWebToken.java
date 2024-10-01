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

import java.security.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

import io.fusion.air.microservice.server.config.ServiceConfiguration;
import io.jsonwebtoken.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;


/**
 * 
 * @author arafkarsh
 *
 */
@Service
public final class JsonWebToken {

	// Set Logger -> Lookup will automatically determine the class name.
	//  private static final Logger log = getLogger(lookup().lookupClass());
	
	private static String TOKEN = "<([1234567890SecretKey!!To??Encrypt##Data@12345%6790])>";

	public static final long EXPIRE_IN_ONE_MINS 		= 1000 * 60;
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

	@Autowired
	private ServiceConfiguration serviceConfig;

	@Autowired
	private CryptoKeyGenerator cryptoKeys;

	private int tokenType;

	private Key signingKey;
	private Key validatorKey;

	private SignatureAlgorithm algorithm;
	public final static SignatureAlgorithm defaultAlgo = SignatureAlgorithm.HS512;

	private final Map<String, Object> claimsToken;
	private final Map<String, Object> claimsRefreshToken;
	private String issuer;
	private String subject;
	private long tokenAuthExpiry;
	private long tokenRefreshExpiry;

	/**
	 * Initialize the JWT with the Signature Algorithm based on Secret Key or Public / Private Key
	 */
	public JsonWebToken() {
		claimsToken 		= new HashMap<String, Object>();
		claimsRefreshToken 	= new HashMap<String, Object>();
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
	public JsonWebToken init(int _tokenType) {
		tokenType 			= _tokenType;
		// Set the Algo Symmetric (Secret) OR Asymmetric (Public/Private) based on the Configuration
		algorithm 			= (tokenType == SECRET_KEY) ? SignatureAlgorithm.HS512 : SignatureAlgorithm.RS256;

		System.out.println("Token Type = "+tokenType+" Algorithm = "+algorithm);
		// Create the Key based on Secret Key or Private Key
		createSigningKey();

		issuer				= (serviceConfig != null) ? serviceConfig.getServiceOrg() : "metarivu";
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
			case SECRET_KEY:
				signingKey = new SecretKeySpec(getTokenKeyBytes(), algorithm.getJcaName());
				validatorKey = signingKey;
				break;
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
				System.out.println("Public key format: " + getCryptoKeyGenerator().getPublicKey().getFormat());
				System.out.println(getCryptoKeyGenerator().getPublicKeyPEMFormat());
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
	 * @param _issuer
	 * @return
	 */
	public JsonWebToken setIssuer(String _issuer) {
		issuer = _issuer;
		return this;
	}

	/**
	 * Set the Subject
	 * @param _subject
	 * @return
	 */
	public JsonWebToken setSubject(String _subject)   {
		subject = _subject;
		return this;
	}

	/**
	 * Set the Token Expiry Time - MUST NOT BE GREATER THAN 30 MINS
	 * IF YES THEN SET EXPIRY TO 5 MINS
	 * @param _time
	 * @return
	 */
	public JsonWebToken setTokenAuthExpiry(long _time)   {
		tokenAuthExpiry = (_time > EXPIRE_IN_THIRTY_MINS) ? EXPIRE_IN_FIVE_MINS : _time;
		return this;
	}

	/**
	 * Set the Token Expiry Time
	 * @param _time
	 * @return
	 */
	public JsonWebToken setTokenRefreshExpiry(long _time)   {
		tokenRefreshExpiry = (_time < EXPIRE_IN_THIRTY_MINS) ? EXPIRE_IN_THIRTY_MINS : _time;;
		return this;
	}

	/**
	 * Add Default Claims
	 * @param _claims
	 * @return
	 */
	private Map<String, Object>  addDefaultClaims(Map<String, Object> _claims) {
		String aud = (serviceConfig != null) ? serviceConfig.getServiceName() : "general";
		_claims.putIfAbsent("aud", aud);
		_claims.putIfAbsent("jti", UUID.randomUUID().toString());
		_claims.putIfAbsent("rol", "User");
		return _claims;
	}

	/**
	 * Clear & Add All Claims for Token
	 * @param _claims
	 * @return
	 */
	public JsonWebToken addAllTokenClaims(Map<String, Object> _claims) {
		claimsToken.clear();
		claimsToken.putAll(_claims);
		String aud = (serviceConfig != null) ? serviceConfig.getServiceName() : "general";
		claimsToken.putIfAbsent("aud", aud);
		claimsToken.putIfAbsent("jti", UUID.randomUUID().toString());
		claimsToken.putIfAbsent("rol", "User");
		return this;
	}

	/**
	 * Clear & Add All Claims for Refresh Token
	 * @param _claims
	 * @return
	 */
	public JsonWebToken addAllRefreshTokenClaims(Map<String, Object> _claims) {
		claimsRefreshToken.clear();
		claimsRefreshToken.putAll(_claims);
		String aud = (serviceConfig != null) ? serviceConfig.getServiceName() : "general";
		claimsRefreshToken.putIfAbsent("aud", aud);
		claimsRefreshToken.putIfAbsent("jti", UUID.randomUUID().toString());
		claimsRefreshToken.putIfAbsent("rol", "User");
		return this;
	}

	/**
	 * Generate Authorize Bearer Token and Refresh Token
	 * Returns in a HashMap
	 * token = Authorization Token
	 * refresh = Refresh token to re-generate the Authorize Token
	 * API Usage
	 * HashMap<String,String> tokens = new JsonWebToken()
	 * 									.init()
	 * 									.setSubject("user")
	 * 									.setIssuer("company")
	 * 									.setTokenExpiry(JsonWebToken.EXPIRE_IN_FIVE_MINS)
	 * 									.setTokenRefreshExpiry(JsonWebToken.EXPIRE_IN_THIRTY_MINS)
	 * 									.addAllTokenClaims(Map<String,Object> claims)
	 * 									.addAllRefreshTokenClaims(Map<String,Object> claims)
	 * 									generateTokens()
	 * @return
	 */
	public HashMap<String,String>  generateTokens() {
		HashMap<String, String> tokens  = new HashMap<String, String>();
		String tokenAuth 	= generateToken(subject, issuer, tokenAuthExpiry, claimsToken);
		String tokenRefresh = generateToken(subject, issuer, tokenRefreshExpiry, claimsRefreshToken);
		tokens.put("token", tokenAuth);
		tokens.put("refresh", tokenRefresh);
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
	public HashMap<String,String>  generateTokens(String subject, String issuer, Map<String,Object> claimsToken, Map<String,Object> claimsRefreshToken) {
		claimsToken = addDefaultClaims(claimsToken);
		claimsRefreshToken = addDefaultClaims(claimsRefreshToken);
		HashMap<String, String> tokens  = new HashMap<String, String>();
		String tokenAuth 	= generateToken(subject, issuer, tokenAuthExpiry, claimsToken);
		String tokenRefresh = generateToken(subject, issuer, tokenRefreshExpiry, claimsRefreshToken);
		tokens.put("token", tokenAuth);
		tokens.put("refresh", tokenRefresh);
		return tokens;
	}

	/**
	 * Clear All Claims (Token and Refresh Token)
	 * @return
	 */
	public JsonWebToken clearAllClaims()  {
		claimsToken.clear();
		claimsRefreshToken.clear();
		return this;
	}

	/**
	 * Returns the Algorithm
	 * @return
	 */
	public SignatureAlgorithm getAlgorithm() {
		return algorithm;
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
	 * @param _userId
	 * @param _expiryTime
	 * @return
	 */
    public String generateToken(String _userId, long _expiryTime) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("aud", "general");
        claims.put("jti", UUID.randomUUID().toString());
        return generateToken(_userId,issuer,_expiryTime,claims);
    }

    /**
     * Generate Token with Claims
     *  
     * @param _userId
     * @param _expiryTime
     * @param _claims
     * @return
     */
    public String generateToken(String _userId, long _expiryTime, Map<String, Object> _claims) {
        return generateToken(_userId,issuer,_expiryTime, _claims);
    }
    
    /**
     * Generate Token with Claims
	 *
	 * @param _userId
	 * @param _issuer
	 * @param _expiryTime
	 * @param _claims
	 * @return
	 */
    public String generateToken(String _userId, String _issuer, long _expiryTime, Map<String, Object> _claims) {
		return generateToken( _userId,  _issuer,  _expiryTime, _claims, signingKey, algorithm);

    }

	/**
	 * Generate Token with Claims and with Either Secret Key or Private Key
	 *
	 * @param _userId
	 * @param _issuer
	 * @param _expiryTime
	 * @param _claims
	 * @param key
	 * @param algorithm
	 * @return
	 */
	public String generateToken(String _userId, String _issuer, long _expiryTime,
								Map<String, Object> _claims, Key key, SignatureAlgorithm algorithm) {
		long currentTime = System.currentTimeMillis();
		return Jwts.builder()
				.setSubject(_userId)
				.setIssuer(_issuer)
				.setClaims(_claims)
				.setIssuedAt(new Date(currentTime))
				.setExpiration(new Date(currentTime + _expiryTime))
				// Key Secret Key or Public/Private Key
				.signWith(key, algorithm)
				.compact();
	}

    /**
     * Validate User Id with Token
     * 
     * @param _userId
     * @param _token
     * @return
     */
    public boolean validateToken(String _userId, String _token) {
        return (!isTokenExpired(_token) &&
        		  getSubjectFromToken(_token).equals(_userId));
    }
    
    /**
     * Returns True if the Token is expired
     * 
     * @param _token
     * @return
     */
    public boolean isTokenExpired(String _token) {
        return getExpiryDateFromToken(_token).before(new Date());
    }
    
	/**
	 * Get the User / Subject from the Token
	 * 
	 * @param _token
	 * @return
	 */
    public String getSubjectFromToken(String _token) {
        return getClaimFromToken(_token, Claims::getSubject);
    }

    /**
     * Get the Expiry Date of the Token
     * 
     * @param _token
     * @return
     */
    public Date getExpiryDateFromToken(String _token) {
        return getClaimFromToken(_token, Claims::getExpiration);
    }
    
    /**
     * Token Should not be used before this Date.
     * 
     * @param _token
     * @return
     */
    public Date getNotBeforeDateFromToken(String _token) {
        return getClaimFromToken(_token, Claims::getNotBefore);
    }
    /**
     * Get the Token Issue Date
     * 
     * @param _token
     * @return
     */
    public Date getIssuedAtFromToken(String _token) {
        return getClaimFromToken(_token, Claims::getIssuedAt);
    }
    
    /**
     * Get the Issuer from the Token
     * 
     * @param _token
     * @return
     */
    public String getIssuerFromToken(String _token) {
        return getClaimFromToken(_token, Claims::getIssuer);
    }
    
    /**
     * Get the Audience from the Token
     * 
     * @param _token
     * @return
     */
    public String getAudienceFromToken(String _token) {
        return getClaimFromToken(_token, Claims::getAudience);
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
     * Return Payload as JSON String
     * 
     * @param _token
     * @return
     */
    public String getPayload(String _token) {
    	StringBuilder sb = new StringBuilder();
		Claims claims = getAllClaims(_token);
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
     * Get a Claim from the Token based on the Claim Type
     * 
     * @param <T>
     * @param _token
     * @param _claimsResolver
     * @return
     */
    public <T> T getClaimFromToken(String _token, 
    		Function<Claims, T> _claimsResolver) {
        return _claimsResolver.apply(getAllClaims(_token));
    }
    
    /**
     * Return All Claims for the Token
     * 
     * @param _token
     * @return
     */
    public Claims getAllClaims(String _token) {
    	/**
		return Jwts.parserBuilder()
				.setSigningKey(validatorKey)
				.requireIssuer(issuer)
				.build()
				.parseClaimsJws(_token)
				.getBody();
		 */
    	return (Claims) getJws(_token).getBody();
    }

	/**
	 * Returns Jws
	 * @param _token
	 * @return
	 */
	public Jws getJws(String _token) {
		return Jwts.parserBuilder()
				.setSigningKey(validatorKey)
				.requireIssuer(issuer)
				.build()
				.parseClaimsJws(_token);
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
		System.out.println("-------------- aaa.bbb.ccc -------------------");
		System.out.println(token);
		System.out.println("-------------- ----------- -------------------");
		System.out.println("Subject  = "+getSubjectFromToken(token));
		System.out.println("Audience = "+getAudienceFromToken(token));
		System.out.println("Issuer   = "+getIssuerFromToken(token));
		System.out.println("IssuedAt = "+getIssuedAtFromToken(token));
		System.out.println("Expiry   = "+getExpiryDateFromToken(token));
		System.out.println("Expired  = "+isTokenExpired(token));
		System.out.println("----------------------------------------------");
		Jws jws = getJws(token);

		System.out.println("Header     : " + jws.getHeader());
		System.out.println("Body       : " + jws.getBody());
		System.out.println("Signature  : " + jws.getSignature());
		if(showClaims) {
			Claims claims = getAllClaims(token);
			int x = 1;
			for (Entry<String, Object> o : claims.entrySet()) {
				System.out.println(x + "> " + o);
				x++;
			}
		}
		if(showPayload) {
			System.out.println("----------------------------------------------");
			System.out.println("Payload=" + getPayload(token));
			System.out.println("----------------------------------------------");
		}

    }

	/**
	 * Returns Expiry Time in Days:Hours:Mins
	 * @param _time
	 * @return
	 */
	public static String printExpiryTime(long _time) {
		String ms="0", hs="0", ds="0";
		long m = _time / (1000 * 60);
		long h = _time / (1000 * 60 * 60);
		long d = _time / (1000 * 60 * 60 * 24);
		if(m > 59) { m = m-(h*60); }
		if(h > 23) { h = h-(d*24);}
		ms = (m<10) ? ms + m : ""+m;
		hs = (h<10) ? hs + h : ""+h;
		ds = (d<10) ? ds + d : ""+d;
		return ds + ":" + hs + ":" + ms;
	}

	/**
	 * Returns Token Claims Set for Generating the Token
	 * @return
	 */
	public Map<String, Object> getClaimsToken() {
		return claimsToken;
	}

	/**
	 * Returns the Token Claims set for Generating the Refresh Token
	 * @return
	 */
	public Map<String, Object> getClaimsRefreshToken() {
		return claimsRefreshToken;
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
	public static void main(String[] args) throws Exception {
		System.out.println("===============================================================================");
		System.out.println("Generate Json Web Tokens Based on SECRET KEYS");
		System.out.println("===============================================================================");
		testJWTCreation(JsonWebToken.SECRET_KEY);
		System.out.println("===============================================================================");
		System.out.println("Generate Json Web Tokens Based on PUBLIC/PRIVATE KEYS");
		System.out.println("===============================================================================");
		testJWTCreation(JsonWebToken.PUBLIC_KEY);
		System.out.println("===============================================================================");
	}

	/**
	 * Test JWT Creation
	 * @param _tokenType
	 */
	protected static void testJWTCreation(int _tokenType) {
		// Default Algo Secret Key is HS512 = Hmac with SHA-512
		// for Public / Private Key is RS256
		JsonWebToken jsonWebToken = new JsonWebToken();

		long tokenAuthExpiry = JsonWebToken.EXPIRE_IN_FIVE_MINS;
		long tokenRefreshExpiry = JsonWebToken.EXPIRE_IN_THIRTY_MINS;

		String subject	 = "jane.doe";
		String issuer    = "metarivu.com";

		Map<String, Object> claims = new HashMap<>();
		claims.put("aud", "generic");
		claims.put("jti", UUID.randomUUID().toString());
		claims.put("rol", "User");
		claims.put("did", "Device ID");
		claims.put("iss", issuer);
		claims.put("sub", subject);

		HashMap<String,String> tokens = jsonWebToken
				.init(_tokenType)
				.setSubject(subject)
				.setIssuer(issuer)
				.setTokenAuthExpiry(tokenAuthExpiry)
				.setTokenRefreshExpiry(tokenRefreshExpiry)
				.addAllTokenClaims(claims)
				.addAllRefreshTokenClaims(claims)
				.generateTokens();

		String token = tokens.get("token");
		String refresh = tokens.get("refresh");
		System.out.println("Token Expiry in Days:or:Hours:or:Mins  "+JsonWebToken.printExpiryTime(tokenAuthExpiry));
		jsonWebToken.tokenStats(token, false, false);

		System.out.println("Refresh Token Expiry in Days:or:Hours:or:Mins "+JsonWebToken.printExpiryTime(tokenRefreshExpiry));
		jsonWebToken.tokenStats(refresh, false, false);

	}

	/**
	 protected static void test() {
	 System.out.println(printExpiryTime(JsonWebToken.EXPIRE_IN_FIVE_MINS));
	 System.out.println(printExpiryTime(JsonWebToken.EXPIRE_IN_THIRTY_MINS));
	 System.out.println(printExpiryTime(JsonWebToken.EXPIRE_IN_THREE_HOUR));
	 System.out.println(printExpiryTime(JsonWebToken.EXPIRE_IN_ONE_DAY));
	 System.out.println(printExpiryTime(JsonWebToken.EXPIRE_IN_THREE_HOUR
	 +JsonWebToken.EXPIRE_IN_TWENTY_MINS));
	 System.out.println(printExpiryTime(JsonWebToken.EXPIRE_IN_TWO_DAYS
	 +JsonWebToken.EXPIRE_IN_THREE_HOUR+JsonWebToken.EXPIRE_IN_THIRTY_MINS));
	 }*/
}