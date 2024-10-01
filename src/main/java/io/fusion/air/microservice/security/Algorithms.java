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

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date: 20220626
 */
public final class Algorithms {

    // Message Digest Algorithms Definitions
    public final static String	MD2				= "MD2";
    public final static String	MD5				= "MD5";
    public final static String	SHA_1			= "SHA-1";
    public final static String	SHA_224			= "SHA-224";
    public final static String	SHA_256			= "SHA-256";
    public final static String	SHA_384			= "SHA-386";
    public final static String	SHA_512			= "SHA-512";
    public final static String	SHA_512_224		= "SHA-512_224";
    public final static String	SHA_512_256		= "SHA-512_256";

    // Cipher Algorithms
    public final static String AES_CBC_NoPadding    = "AES/CBC/NoPadding";
    public final static String AES_CBC_PKCS5Padding = "AES/CBC/PKCS5Padding";
    public final static String AES_ECB_NoPadding    = "AES/ECB/NoPadding";
    public final static String AES_ECB_PKCS5Padding = "AES/ECB/PKCS5Padding";
    public final static String AES_GCM_NoPadding    = "AES/GCM/NoPadding";
    public final static String DES_CBC_NoPadding    = "DES/CBC/NoPadding";
    public final static String DES_CBC_PKCS5Padding = "DES/CBC/PKCS5Padding";
    public final static String DES_ECB_NoPadding    = "DES/ECB/NoPadding";
    public final static String DES_ECB_PKCS5Padding = "DES/ECB/PKCS5Padding";
    public final static String DESede_CBC_NoPadding = "DESede/CBC/NoPadding";
    public final static String DESede_CBC_PKCS5Padding = "DESede/CBC/PKCS5Padding";
    public final static String DESede_ECB_NoPadding = "DESede/ECB/NoPadding";
    public final static String DESede_ECB_PKCS5Padding = "DESede/ECB/PKCS5Padding";
    public final static String TripleDES_CBC_PKCS5Padding = "TripleDES/CBC/PKCS5Padding";
    public final static String RSA_ECB_PKCS1Padding = "RSA/ECB/PKCS1Padding";
    public final static String RSA_ECB_OAEPWithSHA_1AndMGF1Padding = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";
    public final static String RSA_ECB_OAEPWithSHA_256AndMGF1Padding = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    // Algorithm Parameters
    public static final String AES          = "AES";
    public static final String DES          = "DES";
    public static final String TripleDES    = "TripleDES";
    public static final String DESede       = "DESede";
    public static final String DiffieHellman = "DiffieHellman";
    public static final String DSA          = "DSA";

    private String DEFAULT_MD_ALGO            = SHA_512;
    private String CURRENT_MD_ALGO            = DEFAULT_MD_ALGO;

    /**
     * Create Algorithms
     */
    public Algorithms() {
        try {
            CURRENT_MD_ALGO = (String) System.getProperty("HASHALGO");
        } catch (Exception ignored) {}
        if(CURRENT_MD_ALGO == null) {
            CURRENT_MD_ALGO = DEFAULT_MD_ALGO;
            System.out.println("CURRENT_ALGO = "+CURRENT_MD_ALGO);
        }
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
    public String getDefaultMessageDigestAlgorithm() {
        return CURRENT_MD_ALGO;
    }

    /**
     * Returns the Default Message Digest Algo
     * @return
     */
    public String toString() {
        return CURRENT_MD_ALGO;
    }
}