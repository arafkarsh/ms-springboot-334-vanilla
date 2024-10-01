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
package io.fusion.air.microservice.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * HashData
 *
 * Compute the hash for String using Standard Algorithms like MD5, SHA-1, SHA-256, SHA-386, SHA-512
 *
 * String hashValue = HashData.createHash("Secret Code", HashAlgorithms.SHA_1);
 * OR
 * String hashValue = HashData.createHash("Secret Code", HashAlgorithms.SHA_1, "UTF-16");
 *
 * The above code will return the computed Hash value of "Secret Code" using SHA-1
 * (Secure Hash Algorithm).
 *
 * Hash Algorithms Available
 *
 * Message Digest			= MD5 		(128 bit)
 * Secure Hash Algorithm 	= SHA-1 	(160 bit)
 * Secure Hash Algorithm 	= SHA-256
 * Secure Hash Algorithm	= SHA-384
 * Secure Hash Algorithm	= SHA-512
 *
 * @author:  Araf Karsh Hamid
 * @version: 1.0
 * @seriel:	 20170627131239
 */

public final class HashData {

    /**
    // Algorithms Definitions
    public final static int	MD5				= 1;	// Default Algorithm
    public final static int	SHA_1			= 2;
    public final static int	SHA_256			= 3;
    public final static int	SHA_384			= 4;
    public final static int	SHA_512			= 5;

    // Algorithm lookup codes
    private final static String[] algos		= {"", "MD5", "SHA-1", "SHA-256", "SHA-384", "SHA-512" };
    private int DEFAULT_ALGO 				= 5;
    private int CURRENT_ALGO 	        	= DEFAULT_ALGO;
*/
    private final static Algorithms algo = new Algorithms();

    /**
     * Private Constructor used to make this as a Singleton instance.
     */

    private HashData() {
    }

    /**
     * Returns Algorithms Available
     * @return
     */
    public static Algorithms algo() {
        return algo;
    }
    /**
     * Returns the default Algorithm code int value. Following are the Algorithm options
     *
     * Algorithm options
     *
     * 1 MD5		(128 bit)
     * 2 SHA-1		(160 bit)
     * 3 SHA-256	(256 bit Strong hash - check US export controls)
     * 4 SHA-384	(384 bit Strong hash - check US export controls)
     * 5 SHA-512	(512 bit Strong hash - check US export controls)
     *
     * @return int algo_code
     */
    public static String getDefaultAlgorithm() {
        return algo.getDefaultMessageDigestAlgorithm();
    }

    /**
     * computeHash() method will create a hash of the input message. This method uses UTF-8 for message
     * encoding.
     *
     * Algorithm options
     *
     * 1 MD5		(128 bit)
     * 2 SHA-1		(160 bit)
     * 3 SHA-256	(256 bit Strong hash - check US export controls)
     * 4 SHA-384	(384 bit Strong hash - check US export controls)
     * 5 SHA-512	(512 bit Strong hash - check US export controls)
     *
     * @param _message
     * @return String (computed hash value)
     * @throws Exception
     */

    public final static String createHash(final String _message) throws Exception {
        return createHash(_message, algo.getDefaultMessageDigestAlgorithm(), "UTF-8");
    }

    /**
     * computeHash() method will create a hash of the input message. This method uses UTF-8 for message
     * encoding.
     *
     * Algorithm options
     *
     * 1 MD5		(128 bit)
     * 2 SHA-1		(160 bit)
     * 3 SHA-256	(256 bit Strong hash - check US export controls)
     * 4 SHA-384	(384 bit Strong hash - check US export controls)
     * 5 SHA-512	(512 bit Strong hash - check US export controls)
     *
     * @param _message, int _algo
     * @return String (computed hash value)
     * @throws Exception
     */

    public final static String createHash(final String _message, final String _algo) throws Exception {
        return createHash(_message, _algo, "UTF-8");
    }

    /**
     * computeHash() method will create a hash of the input message.
     *
     * Algorithm options
     *
     * 1 MD5		(128 bit)
     * 2 SHA-1		(160 bit)
     * 3 SHA-256	(256 bit Strong hash - check US export controls)
     * 4 SHA-384	(384 bit Strong hash - check US export controls)
     * 5 SHA-512	(512 bit Strong hash - check US export controls)
     *
     * String Encoding = UTF-8, UTF-16 etc
     *
     * @param _message, int _algo, String _encoding
     * @return String (computed hash)
     * @throws Exception
     */

    public final static String createHash(final String _message, final String _algo, final String _encoding) throws Exception {

        if(_message == null) {							// Check Input Message
            throw new Exception("Invalid message for hashing");
        }

        String encoding = _encoding;
        if(encoding == null) {								// Check Input Encoding
            encoding = "UTF-8";
        }

        // Input validation over -------------------------------------------------------------------------------
        MessageDigest mesgDigest = null;
        try {
            mesgDigest = MessageDigest.getInstance(_algo); 	// Load the Algorithm
        } catch(NoSuchAlgorithmException e) { throw e; }
        try {
            mesgDigest.update(_message.getBytes(encoding)); 				// Updates the digest
        } catch(UnsupportedEncodingException e) { throw e; }

        byte raw[] = mesgDigest.digest(); 									// Hash Computation and reset

        return base64Encoder(raw); 											// Convert raw data in Base64

        // Hash computing over -------------------------------------------------------------------------------
    }


    /**
     * Base 64 Encoder
     *
     * @param raw
     * @return String
     */
    public static String base64Encoder(byte raw[]) {
        return Base64.getEncoder().encodeToString(raw);
    }

    /**
     * Base 64 Encoder
     *
     * @param s
     * @return String
     */
    public static String base64Encoder(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes());
    }

    /**
     * To Convert bytes to Hexa String
     *
     * @param raw
     * @return String
     */

    public static String hexEncoder(byte raw[]) {
        char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

        StringBuilder sb = new StringBuilder(raw.length * 2);
        for (byte b : raw) {
            sb.append(HEX_CHARS[(b & 0xF0) >> 4]);
            sb.append(HEX_CHARS[b & 0x0F]);
        }
        String hexValue = sb.toString();
        System.out.println("HEX="+hexValue);
        return hexValue;
    }

    /**
     * Use this method to test the code in the command line.
     */

    public static void main(String[] args) throws Exception {

        System.out.println("12345 = "+HashData.createHash("12345"));
        // Usage
        if(args.length == 0) {
            System.out.println("\n0 - Print all the Algorithms to create Hash.\n");
            System.out.println("1 - MD5   (128 bit)");
            System.out.println("2 - SHA_1 (160 bit)");
            System.out.println("3 - SHA_256");
            System.out.println("4 - SHA_384");
            System.out.println("5 - SHA_512\n");
            System.out.println("java HashingAlgorithms string algorithm_number (default is 5 = SHA_512, use 0 to print all Hash values)");

            System.out.println("\nTesting with default Password = MyC0mp13xPa$$w0rd -----------------------\n");

            long ll = System.currentTimeMillis();
            int xx = 9999999;
            System.out.println("TimeInMillis = "+ll);
            System.out.println("Hex Value    = "+Long.toHexString(ll).toUpperCase());
            System.out.println("Base64 Value = "+ HashData.base64Encoder(""+ll));
            System.out.println("Running No.  = "+xx);
            System.out.println("Hex Value    = "+Integer.toHexString(xx).toUpperCase());

            System.out.println("\n");

            args = new String[2];
            args[0]	=	new String("MyC0mp13xPa$$w0rd");
            args[1]	=	"0";

        }
        /**
         MD5     	Password = ( MyC0mp13xPa$$w0rd ) { FdI/f4FwX9G77bWsXvPdNA== }
         SHA-1     	Password = ( MyC0mp13xPa$$w0rd ) { 1efAOuOs0D6NOqispKWGOV4hPyk= }
         SHA-256     Password = ( MyC0mp13xPa$$w0rd ) { Q0RVvX7GKq2vShiNF8VPv9A770eGY/ZvluRpQ+sFcUg= }
         SHA-384     Password = ( MyC0mp13xPa$$w0rd ) { mXlHG4Nrw+e7Z/si20aBzm02Mpadj1AzZ9TBBg86UBjD9hvmytvCogWO5BleQJax }
         SHA-512     Password = ( MyC0mp13xPa$$w0rd ) { XR/g4iAU1mGrGAVj6XUNfsfptbg+9cenyCtb8z2NdIlJZfm1dpET61fnc34hYUa2FuEUZealj4UCla16+rpaXA== }
         */
        String passwordHash = "";
        String password 	= args[0];
        String algo 		= HashData.getDefaultAlgorithm();
        Algorithms algos    = HashData.algo();
        algo = args[1];

        // Print all the algorithms computed hash value of the input message if the algo code == ZERO
        /**
        if (algo == 0) {
            for(int x = 0; x< (algos.totalMessageDigestAlgorithms()-1); x++) {
                passwordHash = HashData.createHash(password, x+1);
                System.out.println(algos.algos(x+1)+"\tPassword = ( "+password+" )"+" { "+passwordHash+" }");
            }
        } else {
            passwordHash = HashData.createHash(password, algo);
            System.out.println("ALGOS="+algos.algos(algo)+" \tPassword = ( "+password+" )"+" { "+passwordHash+" }");
        }
         */
    }
}

