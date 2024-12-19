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

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
public class SecretKeyData {

    private final SecretKeySpec secretKeySpec;
    private final byte[] keyBytes;
    private final String encryptAlgo;

    /**
     * Create SecretKey Data
     * @param secretKey
     * @param kBytes
     * @param encAlgo
     */
    public SecretKeyData(SecretKeySpec secretKey, byte[] kBytes, String  encAlgo) {
        secretKeySpec   = secretKey;
        keyBytes        = kBytes;
        encryptAlgo     = encAlgo;
    }

    /**
     * Returns SecretKeySpec
     * @return
     */
    public SecretKeySpec getSecretKeySpec() {
        return secretKeySpec;
    }

    /**
     * Returns Byte[] of Keys
     * @return
     */
    public byte[] getKeyBytes() {
        return keyBytes;
    }

    /***
     *
     * @return
     */
    public byte[] getKeyBytesForIVSpecs() {
        return (encryptAlgo.equalsIgnoreCase(Algorithms.TRIPLE_DES))
                ? Arrays.copyOf(keyBytes, 8) : keyBytes;
    }

    /**
     * Get Secure Random IV Specs for  Encryption
     *
     * @param cipher
     * @return
     */
    public byte[] getKeyBytesEncryptRandomIVSpecs(Cipher cipher) {
        byte[] ivBytes = null; // To hold the IV bytes
        ivBytes = new byte[cipher.getBlockSize()]; // Get block size dynamically
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(ivBytes); // Generate secure random IV
        return ivBytes;
    }

    /**
     * Get Secure Random IV Specs for Decryption
     *
     * @param cipher
     * @return
     */
    public byte[] getKeyBytesDecryptRandomIVSpecs(Cipher cipher, String encryptedData) {
        byte[] combined = Base64.getDecoder().decode(encryptedData);
        int blockSize = cipher.getBlockSize();
        return Arrays.copyOfRange(combined, 0, blockSize);
    }
}
