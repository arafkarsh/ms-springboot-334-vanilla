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
package io.fusion.air.microservice.security.crypto;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date: 20220626
 */
public final class Algorithms {

    // Message Digest Algorithms Definitions
    public static final  String	MD2				= "MD2";
    public static final  String	MD5				= "MD5";
    public static final String	SHA_1			= "SHA-1";
    public static final String	SHA_224			= "SHA-224";
    public static final String	SHA_256			= "SHA-256";
    public static final String	SHA_512			= "SHA-512";

    protected static final String[] ALGOS           = { MD2, MD5, SHA_1, SHA_224, SHA_256, SHA_512 };

    // Cipher Algorithms
    public static final String AES_CBC_NO_PADDING = "AES/CBC/NoPadding";
    public static final String AES_CBC_PKCS_5_PADDING = "AES/CBC/PKCS5Padding";
    public static final String AES_ECB_NO_PADDING = "AES/ECB/NoPadding";
    public static final String AES_ECB_PKCS_5_PADDING = "AES/ECB/PKCS5Padding";
    public static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
    public static final String DES_CBC_NO_PADDING = "DES/CBC/NoPadding";
    public static final String DES_CBC_PKCS_5_PADDING = "DES/CBC/PKCS5Padding";
    public static final String DES_ECB_NO_PADDING = "DES/ECB/NoPadding";
    public static final String DES_ECB_PKCS_5_PADDING = "DES/ECB/PKCS5Padding";
    public static final String DESEDE_CBC_NO_PADDING = "DESede/CBC/NoPadding";
    public static final String DESEDE_CBC_PKCS_5_PADDING = "DESede/CBC/PKCS5Padding";
    public static final String DESEDE_ECB_NO_PADDING = "DESede/ECB/NoPadding";
    public static final String DESEDE_ECB_PKCS_5_PADDING = "DESede/ECB/PKCS5Padding";
    public static final String TRIPLE_DES_CBC_PKCS_5_PADDING = "TripleDES/CBC/PKCS5Padding";
    public static final String RSA_ECB_PKCS_1_PADDING = "RSA/ECB/PKCS1Padding";
    public static final String RSA_ECB_OAEPWITH_SHA_1_AND_MGF_1_PADDING = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";
    public static final String RSA_ECB_OAEPWITH_SHA_256_AND_MGF_1_PADDING = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    // Algorithm Parameters
    public static final String AES          = "AES";
    public static final String DES          = "DES";
    public static final String TRIPLE_DES = "TripleDES";
    public static final String DESEDE = "DESede";
    public static final String DIFFIE_HELLMAN = "DiffieHellman";
    public static final String DSA          = "DSA";

    private static final String DEFAULT_MD_ALGO            = SHA_512;
    private final String currentMdAlgo;

    /**
     * Create Algorithms
     */
    public Algorithms() {
        String algo = System.getProperty("HASHALGO");
        currentMdAlgo = (algo == null) ? DEFAULT_MD_ALGO : algo ;
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
        return currentMdAlgo;
    }

    /**
     * Returns the Default Message Digest Algo
     * @return
     */
    public String toString() {
        return currentMdAlgo;
    }
}