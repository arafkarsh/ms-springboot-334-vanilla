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
// Custom
import io.fusion.air.microservice.domain.exceptions.CryptoSecurityException;
// Crypto
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
// Java
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Arrays;

import io.fusion.air.microservice.utils.Std;
import org.slf4j.Logger;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date: 20220626
 */
public class SecureData {

    // Set Logger -> Lookup will automatically determine the class name.
    private static final Logger log = getLogger(lookup().lookupClass());

    private static final String DEFAULT_SECRET_KEY  = "<([SecretKey!!To??Encrypt##Data@12345%6790])>";

    private static final String DEFAULT_ALGORITHM   = Algorithms.AES_CBC_PKCS_5_PADDING;
    private static final String DEFAULT_MD_ALGO     = Algorithms.SHA_512;

    /**
     * Create Secret Key Specs with AES Algorithm
     * @param secret
     * @param mdAlgo
     * @return
     */
    private static SecretKeyData createSecretKeySpec(String secret, String mdAlgo) {
        return createSecretKeySpec( secret,  mdAlgo, Algorithms.AES);
    }

    /**
     * Create Secret Key Specs
     * @param secret
     * @param mdAlgo
     * @param encryptAlgo
     * @return
     */
    private static SecretKeyData createSecretKeySpec(String secret, String mdAlgo, String encryptAlgo) {
        SecretKeySpec secretKey = null;
        SecretKeyData secretKeyData = null;
        if (encryptAlgo == null)  { encryptAlgo = Algorithms.AES; }
        byte[] key = null;
        try {
            if(mdAlgo != null) {
                key = Arrays.copyOf(
                        MessageDigest.getInstance(mdAlgo).digest(secret.getBytes(StandardCharsets.UTF_8)), 16);
            } else {
                switch(encryptAlgo){
                    case Algorithms.AES:
                        key = Arrays.copyOf(secret.getBytes(StandardCharsets.UTF_8), 16);
                        break;
                    case Algorithms.DES:
                        key = Arrays.copyOf(secret.getBytes(StandardCharsets.UTF_8), 8);
                        break;
                    case Algorithms.TRIPLE_DES:
                        key = Arrays.copyOf(secret.getBytes(StandardCharsets.UTF_8), 24);
                        break;
                    default:
                        key = secret.getBytes(StandardCharsets.UTF_8);
                }
            }
            secretKey = new SecretKeySpec(key, encryptAlgo);
            secretKeyData = new SecretKeyData(secretKey, key, encryptAlgo);
        } catch (Exception e) {
            Std.println("Error: "+e);
            throw new CryptoSecurityException(e);
        }
        return secretKeyData;
    }

    /**
     * Encrypt Data using AES/CBC/PKCS5PADDING
     * @param data
     * @return
     */
    public static String encrypt(String data) {
        return encrypt(data, DEFAULT_SECRET_KEY);
    }

    /**
     * Encrypt the String using AES/CBC/PKCS5PADDING
     * @param data
     * @param secret
     * @return
     */
    public static String encrypt(String data, String secret) {
        return   encrypt(data, DEFAULT_ALGORITHM, secret, DEFAULT_MD_ALGO, Algorithms.AES);
    }

    /**
     * Encrypt the String using AES/CBC/PKCS5PADDING
     *
     * @param data
     * @param secret
     * @param algo (MD5, SHA-1, SHA-256, SHA-384, SHA-512)
     * @return
     */
    public static String encrypt(String data, String secret, String algo) {
        return   encrypt(data, DEFAULT_ALGORITHM, secret, algo, Algorithms.AES);
    }

    /**
     * Encrypt the String using AES/CBC/PKCS5PADDING
     * @param data
     * @param secret
     * @return
     */
    public static String encryptAES(String data, String secret) {
        return   encrypt(data, Algorithms.AES_CBC_PKCS_5_PADDING, secret, null, Algorithms.AES);
    }

    /**
     * Encrypt the String using TripleDES_CBC_PKCS5Padding
     * @param data
     * @param secret
     * @return
     */
    public static String encryptTripleDES(String data, String secret) {
        return   encrypt(data, Algorithms.TRIPLE_DES_CBC_PKCS_5_PADDING, secret, null, Algorithms.TRIPLE_DES);
    }

    /**
     * Encrypt the String using AES or TripleDES
     * @param data
     * @param cipherAlgo (AES/CBC/PKCS5PADDING, TripleDES/CBC/PKCS5Padding)
     * @param secret
     * @param algo (MD5, SHA-1, SHA-256, SHA-384, SHA-512)
     * @param encryptAlgo (AES, DES, TripleDES))
     * @return
     */
    public static String encrypt(String data, String cipherAlgo, String secret, String algo, String encryptAlgo) {
        if(data == null) { return ""; }
        cipherAlgo = (cipherAlgo == null) ? DEFAULT_ALGORITHM: cipherAlgo;
        try {
            SecretKeyData secretKeyData = createSecretKeySpec(secret, algo, encryptAlgo);
            if(secretKeyData != null) {
                Cipher cipher = Cipher.getInstance(cipherAlgo);
                byte[] ivBytes = null; // To hold the IV bytes
                if(cipherAlgo.contains("CBC")) {
                    // Dynamically generate IV
                    ivBytes = new byte[cipher.getBlockSize()]; // Get block size dynamically
                    SecureRandom secureRandom = new SecureRandom();
                    secureRandom.nextBytes(ivBytes); // Generate secure random IV
                    IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
                    cipher.init(Cipher.ENCRYPT_MODE, secretKeyData.getSecretKeySpec(),ivSpec);
                } else {
                    cipher.init(Cipher.ENCRYPT_MODE, secretKeyData.getSecretKeySpec());
                }
                byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
                // Encode IV and ciphertext for storage or transmission
                if (ivBytes != null) {
                    byte[] combined = new byte[ivBytes.length + encryptedData.length];
                    System.arraycopy(ivBytes, 0, combined, 0, ivBytes.length);
                    System.arraycopy(encryptedData, 0, combined, ivBytes.length, encryptedData.length);
                    return Base64.getEncoder().encodeToString(combined);
                } else {
                    return Base64.getEncoder().encodeToString(encryptedData);
                }
            }
            log.info("SecretKeyData Generation Failed for Encryption.... ");
        } catch (Exception e) {
            log.info("Unable to Encrypt Data: {} ",  e.toString());
            Std.println("Error: "+e);
        }
        return "";
    }

    /**
     * Decrypt Data using AES/CBC/PKCS5PADDING with SHA-512
     * @param data
     * @return
     */
    public static String decrypt(String data) {
        return decrypt(data, DEFAULT_SECRET_KEY, DEFAULT_MD_ALGO);
    }

    /**
     * Decrypt the Data using AES/ECB/PKCS5PADDING with SHA-512
     * @param data
     * @param secret
     * @return
     */
    public static String decrypt(String data, String secret) {
        return decrypt(data, DEFAULT_ALGORITHM, secret,DEFAULT_MD_ALGO , Algorithms.AES);
    }

    /**
     * Decrypt the Data using AES/CBC/PKCS5PADDING with MD5 or SHA
     * @param data
     * @param secret
     * @param algo (MD5, SHA-1, SHA-256, SHA-384, SHA-512)
     * @return
     */
    public static String decrypt(String data, String secret, String algo) {
        return decrypt(data, DEFAULT_ALGORITHM, secret,algo, Algorithms.AES);
    }

    /**
     * Decrypt AES with AES_CBC_PKCS5Padding
     * @param data
     * @param secret
     * @return
     */
    public static String decryptAES(String data, String secret) {
        return decrypt(data, Algorithms.AES_CBC_PKCS_5_PADDING, secret, null , Algorithms.AES);
    }

    /**
     * Decrypt TripleDes with TripleDES_CBC_PKCS5Padding
     * @param data
     * @param secret
     * @return
     */
    public static String decryptTripleDES(String data, String secret) {
        return decrypt(data, Algorithms.TRIPLE_DES_CBC_PKCS_5_PADDING, secret, null , Algorithms.TRIPLE_DES);
    }

    /**
     * Decrypt the Data using AES or TripleDES
     * @param encryptedData
     * @param cipherAlgo (AES/CBC/PKCS5PADDING, TripleDES/CBC/PKCS5Padding)
     * @param secret
     * @param digestAlgo (MD5, SHA-1, SHA-256, SHA-384, SHA-512)
     * @param encryptAlgo (AES, DES, TripleDES))
     * @return
     */
    public static String decrypt(String encryptedData, String cipherAlgo, String secret, String digestAlgo, String encryptAlgo) {
        if(encryptedData == null) { return ""; }
        cipherAlgo = (cipherAlgo == null) ? DEFAULT_ALGORITHM : cipherAlgo;
        try {
            SecretKeyData secretKeyData = createSecretKeySpec(secret, digestAlgo, encryptAlgo);
            if(secretKeyData != null) {
                Cipher cipher = Cipher.getInstance(cipherAlgo);
                byte[] combined = Base64.getDecoder().decode(encryptedData);
                if(cipherAlgo.contains("CBC")) {
                    // Dynamically generate IV
                    int blockSize = cipher.getBlockSize();
                    byte[] ivBytes = Arrays.copyOfRange(combined, 0, blockSize);
                    byte[] cipherBytes = Arrays.copyOfRange(combined, blockSize, combined.length);
                    IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
                    cipher.init(Cipher.DECRYPT_MODE, secretKeyData.getSecretKeySpec(), ivSpec);
                    return new String(cipher.doFinal(cipherBytes), StandardCharsets.UTF_8);
                } else {
                    cipher.init(Cipher.DECRYPT_MODE, secretKeyData.getSecretKeySpec());
                }
                return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
            }
            log.info("SecretKeyData Generation Failed for Decryption.... ");
        } catch (Exception e) {
            log.info("Unable to Decrypt the data: {} ", e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public static final String SINGLE_LINE = "----------------------------------------------------------------------------------------";

    /**
     * ONLY FOR TESTING PURPOSE
     * Code to Encrypt and Decrypt the Data
     * @param args
     */
    public static void main(String[] args) {
        Std.println(SINGLE_LINE);

        testEncryptAESWithMD();
        testEncryptAES();
        testEncryptAES2();
        testEncryptTripleDES();
        testEncryptTripleDES2();
        testEncryptAESusingCBC();
        testEncryptAESusingECB();

        Std.println(SINGLE_LINE);
        for(int x=1; x<2; x++) {
             testEncryptionECB(x);
        }
        Std.println(SINGLE_LINE);

    }

    public static final String RAW_DATA = "0123456789";
    public static final String ENC_KEY = "eHEZ92vvd7jMqit6lkWa1sp7z6FpdVHRfRX8gZlslkw=";

    public static void testEncryptAESWithMD() {
        String rdEncrypt = SecureData.encrypt(RAW_DATA, ENC_KEY);
        String rdDecrypt = SecureData.decrypt(rdEncrypt, ENC_KEY);
        printResult(1, RAW_DATA, ENC_KEY,  Algorithms.AES_CBC_PKCS_5_PADDING,  Algorithms.SHA_512, Algorithms.AES, rdEncrypt,  rdDecrypt);
    }

    public static void testEncryptAES() {
        String rdEncrypt = SecureData.encryptAES(RAW_DATA, ENC_KEY);
        String rdDecrypt = SecureData.decryptAES(rdEncrypt, ENC_KEY);
        printResult(2, RAW_DATA, ENC_KEY,  Algorithms.AES_CBC_PKCS_5_PADDING,  "", Algorithms.AES, rdEncrypt,  rdDecrypt);
    }

    public static void testEncryptAES2() {
        String encKey   = "as323";
        String rdEncrypt = SecureData.encryptAES(RAW_DATA, encKey);
        String rdDecrypt = SecureData.decryptAES(rdEncrypt, encKey);
        printResult(2, RAW_DATA,  encKey,  Algorithms.AES_CBC_PKCS_5_PADDING,  "", Algorithms.AES, rdEncrypt,  rdDecrypt);
    }

    public static void testEncryptTripleDES() {
        String rdEncrypt = SecureData.encryptTripleDES(RAW_DATA, ENC_KEY);
        String rdDecrypt = SecureData.decryptTripleDES(rdEncrypt, ENC_KEY);
        printResult(3, RAW_DATA, ENC_KEY,  Algorithms.TRIPLE_DES_CBC_PKCS_5_PADDING,  "", Algorithms.TRIPLE_DES, rdEncrypt,  rdDecrypt);
    }

    public static void testEncryptTripleDES2() {
        String encKey   = "as323";
        String rdEncrypt = SecureData.encryptTripleDES(RAW_DATA, encKey);
        String rdDecrypt = SecureData.decryptTripleDES(rdEncrypt, encKey);
        printResult(3, RAW_DATA,  encKey,  Algorithms.TRIPLE_DES_CBC_PKCS_5_PADDING,  "", Algorithms.TRIPLE_DES, rdEncrypt,  rdDecrypt);
    }
    public static void testEncryptAESusingCBC() {
        String cipher   = Algorithms.AES_CBC_PKCS_5_PADDING;
        String mdAlgo  = Algorithms.SHA_512;
        String enAlgo  = Algorithms.AES;
        String rdEncrypt = SecureData.encrypt(RAW_DATA, cipher, ENC_KEY, mdAlgo, enAlgo);
        String rdDecrypt = SecureData.decrypt(rdEncrypt, cipher, ENC_KEY, mdAlgo, enAlgo);
        printResult(4, RAW_DATA, ENC_KEY,  cipher,  mdAlgo, enAlgo, rdEncrypt,  rdDecrypt);
    }

    public static void testEncryptAESusingECB() {
        String cipher   = Algorithms.AES_ECB_PKCS_5_PADDING;
        String mdAlgo  = Algorithms.SHA_512;
        String enAlgo  = Algorithms.AES;
        String rdEncrypt = SecureData.encrypt(RAW_DATA, cipher, ENC_KEY, mdAlgo, enAlgo);
        String rdDecrypt = SecureData.decrypt(rdEncrypt, cipher, ENC_KEY, mdAlgo, enAlgo);
        printResult(5, RAW_DATA, ENC_KEY,  cipher,  mdAlgo, enAlgo, rdEncrypt,  rdDecrypt);
    }

    /**
     * Test Encryption Decryption
     * @param cnt
     */
    public static void testEncryptionECB(int cnt) {
        String rawData = "My Name is Lincoln Hawk from the Galaxy Andromeda!";
        String encKey = "<([SecretKey!!To??Encrypt##Data@12345%6790])>-" + cnt;
        String cipher = Algorithms.AES_ECB_PKCS_5_PADDING;
        String mdAlgo = Algorithms.SHA_512;
        String enAlgo = Algorithms.AES;
        String rdEncrypt = SecureData.encrypt(rawData, cipher, encKey, mdAlgo, enAlgo);
        String rdDecrypt = SecureData.decrypt(rdEncrypt, cipher, encKey, mdAlgo, enAlgo);
        printResult(6, rawData, encKey, cipher, mdAlgo, enAlgo, rdEncrypt, rdDecrypt);
    }

    /**
     * Print the Result with Default Algorithms
     * @param testNo
     * @param rawData
     * @param encKey
     * @param rdEncrypt
     * @param rdDecrypt
     */
    public static void printResult(int testNo, String rawData, String encKey, String rdEncrypt, String rdDecrypt) {
        printResult( testNo,  rawData,  encKey,  DEFAULT_ALGORITHM,  DEFAULT_MD_ALGO, Algorithms.AES, rdEncrypt,  rdDecrypt);
    }

    /**
     * Print the Result
     * @param testNo
     * @param rawData
     * @param encKey
     * @param cipher
     * @param mdAlgo
     * @param encryptAlgo
     * @param rdEncrypt
     * @param rdDecrypt
     */
    public static void printResult(int testNo, String rawData, String encKey, String cipher, String mdAlgo,
                                   String encryptAlgo, String rdEncrypt, String rdDecrypt) {
        Std.println("MD Algorithm : "+mdAlgo);
        Std.println("Cipher Suite : "+cipher);
        Std.println("Encrypt Algo : "+encryptAlgo);
        Std.println("Enc Key   : "+encKey);
        Std.println("Plain String : "+rawData);
        Std.println("Encrypted "+testNo+"  : "+rdEncrypt);
        Std.println("Decrypted "+testNo+"  : "+rdDecrypt);
        Std.println("========================================================================================");
    }
}