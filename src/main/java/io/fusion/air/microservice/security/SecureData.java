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

import io.fusion.air.microservice.domain.exceptions.CryptoSecurityException;
import org.slf4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Arrays;
import java.util.UUID;

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

    private static final String DEFAULT_ALGORITHM   = Algorithms.AES_CBC_PKCS5Padding;
    private static final String DEFAULT_MD_ALGO     = Algorithms.SHA_512;

    /**
     * Create Secret Key Specs with AES Algorithm
     * @param _secret
     * @param _mdAlgo
     * @return
     */
    private static SecretKeyData createSecretKeySpec(String _secret, String _mdAlgo) {
        return createSecretKeySpec( _secret,  _mdAlgo, Algorithms.AES);
    }

    /**
     * Create Secret Key Specs
     * @param _secret
     * @param _mdAlgo
     * @param _encryptAlgo
     * @return
     */
    private static SecretKeyData createSecretKeySpec(String _secret, String _mdAlgo, String _encryptAlgo) {
        SecretKeySpec secretKey = null;
        SecretKeyData secretKeyData = null;
        if (_encryptAlgo == null)  { _encryptAlgo = Algorithms.AES; }
        byte[] key = null;
        try {
            if(_mdAlgo != null) {
                key = Arrays.copyOf(
                        MessageDigest.getInstance(_mdAlgo).digest(_secret.getBytes("UTF-8")), 16);
            } else {
                switch(_encryptAlgo){
                    case Algorithms.AES:
                        key = Arrays.copyOf(_secret.getBytes("UTF-8"), 16);
                        break;
                    case Algorithms.DES:
                        key = Arrays.copyOf(_secret.getBytes("UTF-8"), 8);
                        break;
                    case Algorithms.TripleDES:
                        key = Arrays.copyOf(_secret.getBytes("UTF-8"), 24);
                        break;
                    default:
                        key = _secret.getBytes("UTF-8");
                }
            }
            //  log.debug("Key={} length={}", key, key.length);
            secretKey = new SecretKeySpec(key, _encryptAlgo);
            secretKeyData = new SecretKeyData(secretKey, key, _encryptAlgo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CryptoSecurityException(e);
        }
        return secretKeyData;
    }

    /**
     * Encrypt Data using AES/CBC/PKCS5PADDING
     * @param _data
     * @return
     */
    public static String encrypt(String _data) {
        return encrypt(_data, DEFAULT_SECRET_KEY);
    }

    /**
     * Encrypt the String using AES/CBC/PKCS5PADDING
     * @param _data
     * @param _secret
     * @return
     */
    public static String encrypt(String _data, String _secret) {
        return   encrypt(_data, DEFAULT_ALGORITHM, _secret, DEFAULT_MD_ALGO, Algorithms.AES);
    }

    /**
     * Encrypt the String using AES/CBC/PKCS5PADDING
     *
     * @param _data
     * @param _secret
     * @param _algo (MD5, SHA-1, SHA-256, SHA-384, SHA-512)
     * @return
     */
    public static String encrypt(String _data, String _secret, String _algo) {
        return   encrypt(_data, DEFAULT_ALGORITHM, _secret, _algo, Algorithms.AES);
    }

    /**
     * Encrypt the String using AES/CBC/PKCS5PADDING
     * @param _data
     * @param _secret
     * @return
     */
    public static String encryptAES(String _data, String _secret) {
        return   encrypt(_data, Algorithms.AES_CBC_PKCS5Padding, _secret, null, Algorithms.AES);
    }

    /**
     * Encrypt the String using TripleDES_CBC_PKCS5Padding
     * @param _data
     * @param _secret
     * @return
     */
    public static String encryptTripleDES(String _data, String _secret) {
        return   encrypt(_data, Algorithms.TripleDES_CBC_PKCS5Padding, _secret, null, Algorithms.TripleDES);
    }

    /**
     * Encrypt the String using AES or TripleDES
     * @param _data
     * @param _cipher (AES/CBC/PKCS5PADDING, TripleDES/CBC/PKCS5Padding)
     * @param _secret
     * @param _algo (MD5, SHA-1, SHA-256, SHA-384, SHA-512)
     * @param _encryptAlgo (AES, DES, TripleDES))
     * @return
     */
    public static String encrypt(String _data, String _cipher, String _secret, String _algo, String _encryptAlgo) {
        if(_data == null) { return ""; }
        _cipher = (_cipher == null) ? DEFAULT_ALGORITHM: _cipher;
        // _algo = (_algo == null) ? DEFAULT_MD_ALGO : _algo;
        try {
            SecretKeyData secretKeyData = createSecretKeySpec(_secret, _algo, _encryptAlgo);
            if(secretKeyData != null) {
                Cipher cipher = Cipher.getInstance(_cipher);
                if(_cipher.contains("CBC")) {
                    IvParameterSpec ivSpec = new IvParameterSpec(secretKeyData.getKeyBytesForIVSpecs() );
                    cipher.init(Cipher.ENCRYPT_MODE, secretKeyData.getSecretKeySpec(),ivSpec);
                } else {
                    cipher.init(Cipher.ENCRYPT_MODE, secretKeyData.getSecretKeySpec());
                }
                return Base64.getEncoder().encodeToString(cipher.doFinal(_data.getBytes("UTF-8")));
            }
            log.info("SecretKeyData Generation Failed for Encryption.... ");
        } catch (Exception e) {
            log.info("Unable to Encrypt Data: " + e.toString());
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Decrypt Data using AES/CBC/PKCS5PADDING with SHA-512
     * @param _data
     * @return
     */
    public static String decrypt(String _data) {
        return decrypt(_data, DEFAULT_SECRET_KEY, DEFAULT_MD_ALGO);
    }

    /**
     * Decrypt the Data using AES/ECB/PKCS5PADDING with SHA-512
     * @param _data
     * @param _secret
     * @return
     */
    public static String decrypt(String _data, String _secret) {
        return decrypt(_data, DEFAULT_ALGORITHM, _secret,DEFAULT_MD_ALGO , Algorithms.AES);
    }

    /**
     * Decrypt the Data using AES/CBC/PKCS5PADDING with MD5 or SHA
     * @param _data
     * @param _secret
     * @param _algo (MD5, SHA-1, SHA-256, SHA-384, SHA-512)
     * @return
     */
    public static String decrypt(String _data, String _secret, String _algo) {
        return decrypt(_data, DEFAULT_ALGORITHM, _secret,_algo, Algorithms.AES);
    }

    /**
     * Decrypt AES with AES_CBC_PKCS5Padding
     * @param _data
     * @param _secret
     * @return
     */
    public static String decryptAES(String _data, String _secret) {
        return decrypt(_data, Algorithms.AES_CBC_PKCS5Padding, _secret, null , Algorithms.AES);
    }

    /**
     * Decrypt TripleDes with TripleDES_CBC_PKCS5Padding
     * @param _data
     * @param _secret
     * @return
     */
    public static String decryptTripleDES(String _data, String _secret) {
        return decrypt(_data, Algorithms.TripleDES_CBC_PKCS5Padding, _secret, null , Algorithms.TripleDES);
    }

    /**
     * Decrypt the Data using AES or TripleDES
     * @param _data
     * @param _cipher (AES/CBC/PKCS5PADDING, TripleDES/CBC/PKCS5Padding)
     * @param _secret
     * @param _algo (MD5, SHA-1, SHA-256, SHA-384, SHA-512)
     * @param _encryptAlgo (AES, DES, TripleDES))
     * @return
     */
    public static String decrypt(String _data, String _cipher, String _secret, String _algo, String _encryptAlgo) {
        if(_data == null) { return ""; }
        _cipher = (_cipher == null) ? DEFAULT_ALGORITHM : _cipher;
        //  _algo = (_algo == null) ? DEFAULT_MD_ALGO : _algo;
        try {
            SecretKeyData secretKeyData = createSecretKeySpec(_secret, _algo, _encryptAlgo);
            if(secretKeyData != null) {
                Cipher cipher = Cipher.getInstance(_cipher);
                if(_cipher.contains("CBC")) {
                    IvParameterSpec ivSpec = new IvParameterSpec(secretKeyData.getKeyBytesForIVSpecs());
                    cipher.init(Cipher.DECRYPT_MODE, secretKeyData.getSecretKeySpec(),ivSpec);
                } else {
                    cipher.init(Cipher.DECRYPT_MODE, secretKeyData.getSecretKeySpec());
                }
                return new String(cipher.doFinal(Base64.getDecoder().decode(_data)));
            }
            log.info("SecretKeyData Generation Failed for Decryption.... ");
        } catch (Exception e) {
            log.info("Unable to Decrypt the data: " + e.toString());
            e.printStackTrace();

        }
        return null;
    }

    /**
     * ONLY FOR TESTING PURPOSE
     * Code to Encrypt and Decrypt the Data
     * @param args
     */
    public static void main(String[] args) throws Exception{
        System.out.println("----------------------------------------------------------------------------------------");

        testEncryptAESWithMD();
        testEncryptAES();
        testEncryptAES2();
        testEncryyptTripleDES();
        testEncryyptTripleDES2();
        testEncryyptAESusingCBC();
        testEncryyptAESusingECB();

        for(int x=1; x<2; x++) {
            testEncryption(x);
        }
    }

    public static void testEncryptAESWithMD() {
        String rawData  = "0123456789";
        String secret   = "eHEZ92vvd7jMqit6lkWa1sp7z6FpdVHRfRX8gZlslkw=";
        String rdEncrypt = SecureData.encrypt(rawData, secret);
        String rdDecrypt = SecureData.decrypt(rdEncrypt, secret);
        printResult(1, rawData,  secret,  Algorithms.AES_CBC_PKCS5Padding,  Algorithms.SHA_512, Algorithms.AES, rdEncrypt,  rdDecrypt);
    }

    public static void testEncryptAES() {
        String rawData  = "0123456789";
        String secret   = "eHEZ92vvd7jMqit6lkWa1sp7z6FpdVHRfRX8gZlslkw=";
        String rdEncrypt = SecureData.encryptAES(rawData, secret);
        String rdDecrypt = SecureData.decryptAES(rdEncrypt, secret);
        printResult(2, rawData,  secret,  Algorithms.AES_CBC_PKCS5Padding,  "", Algorithms.AES, rdEncrypt,  rdDecrypt);
    }

    public static void testEncryptAES2() {
        String rawData  = "0123456789";
        String secret   = "as323";
        String rdEncrypt = SecureData.encryptAES(rawData, secret);
        String rdDecrypt = SecureData.decryptAES(rdEncrypt, secret);
        printResult(2, rawData,  secret,  Algorithms.AES_CBC_PKCS5Padding,  "", Algorithms.AES, rdEncrypt,  rdDecrypt);
    }

    public static void testEncryyptTripleDES() {
        String rawData  = "0123456789";
        String secret   = "eHEZ92vvd7jMqit6lkWa1sp7z6FpdVHRfRX8gZlslkw=";
        String rdEncrypt = SecureData.encryptTripleDES(rawData, secret);
        String rdDecrypt = SecureData.decryptTripleDES(rdEncrypt, secret);
        printResult(3, rawData,  secret,  Algorithms.TripleDES_CBC_PKCS5Padding,  "", Algorithms.TripleDES, rdEncrypt,  rdDecrypt);
    }

    public static void testEncryyptTripleDES2() {
        String rawData  = "0123456789";
        String secret   = "as323";
        String rdEncrypt = SecureData.encryptTripleDES(rawData, secret);
        String rdDecrypt = SecureData.decryptTripleDES(rdEncrypt, secret);
        printResult(3, rawData,  secret,  Algorithms.TripleDES_CBC_PKCS5Padding,  "", Algorithms.TripleDES, rdEncrypt,  rdDecrypt);
    }
    public static void testEncryyptAESusingCBC() {
        String rawData  = "0123456789";
        String secret   = "eHEZ92vvd7jMqit6lkWa1sp7z6FpdVHRfRX8gZlslkw=";
        String cipher   = Algorithms.AES_CBC_PKCS5Padding;
        String md_algo  = Algorithms.SHA_512;
        String en_algo  = Algorithms.AES;
        String rdEncrypt = SecureData.encrypt(rawData, cipher, secret, md_algo, en_algo);
        String rdDecrypt = SecureData.decrypt(rdEncrypt, cipher, secret, md_algo, en_algo);
        printResult(4, rawData,  secret,  cipher,  md_algo, en_algo, rdEncrypt,  rdDecrypt);
    }

    public static void testEncryyptAESusingECB() {
        String rawData  = "0123456789";
        String secret   = "eHEZ92vvd7jMqit6lkWa1sp7z6FpdVHRfRX8gZlslkw=";
        String cipher   = Algorithms.AES_ECB_PKCS5Padding;
        String md_algo  = Algorithms.SHA_512;
        String en_algo  = Algorithms.AES;
        String rdEncrypt = SecureData.encrypt(rawData, cipher, secret, md_algo, en_algo);
        String rdDecrypt = SecureData.decrypt(rdEncrypt, cipher, secret, md_algo, en_algo);
        printResult(5, rawData,  secret,  cipher,  md_algo, en_algo, rdEncrypt,  rdDecrypt);
    }

    /**
     * Test Encryption Decryption
     * @param _cnt
     */
    public static void testEncryption(int _cnt) {
        String rawData = "My Name is Lincoln Hawk from the Galaxy Andromeda!";
        String secret = "<([SecretKey!!To??Encrypt##Data@12345%6790])>-" + _cnt;
        String cipher = Algorithms.AES_ECB_PKCS5Padding;
        String md_algo = Algorithms.SHA_512;
        String en_algo = Algorithms.AES;
        String rdEncrypt = SecureData.encrypt(rawData, cipher, secret, md_algo, en_algo);
        String rdDecrypt = SecureData.decrypt(rdEncrypt, cipher, secret, md_algo, en_algo);
        printResult(6, rawData, secret, cipher, md_algo, en_algo, rdEncrypt, rdDecrypt);
    }

    /**
     * Print the Result with Default Algorithms
     * @param testNo
     * @param rawData
     * @param secret
     * @param rdEncrypt
     * @param rdDecrypt
     */
    public static void printResult(int testNo, String rawData, String secret, String rdEncrypt, String rdDecrypt) {
        printResult( testNo,  rawData,  secret,  DEFAULT_ALGORITHM,  DEFAULT_MD_ALGO, Algorithms.AES, rdEncrypt,  rdDecrypt);
    }

    /**
     * Print the Result
     * @param testNo
     * @param rawData
     * @param secret
     * @param cipher
     * @param md_algo
     * @param encryptAlgo
     * @param rdEncrypt
     * @param rdDecrypt
     */
    public static void printResult(int testNo, String rawData, String secret, String cipher, String md_algo,
                                   String encryptAlgo, String rdEncrypt, String rdDecrypt) {
        String uuid = UUID.randomUUID().toString();
        // System.out.println("UUID         : "+uuid+" | Length = "+uuid.length());
        System.out.println("MD Algorithm : "+md_algo);
        System.out.println("Cipher Suite : "+cipher);
        System.out.println("Encrypt Algo : "+encryptAlgo);
        System.out.println("Secret Key   : "+secret);
        // System.out.println("----------------------------------------------------------------------------------------");
        System.out.println("Plain String : "+rawData);
        System.out.println("Encrypted "+testNo+"  : "+rdEncrypt);
        System.out.println("Decrypted "+testNo+"  : "+rdDecrypt);
        System.out.println("========================================================================================");
    }
}