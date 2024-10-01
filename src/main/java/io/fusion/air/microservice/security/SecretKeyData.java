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

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

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
     * @param _secretKey
     * @param _keyBytes
     * @param _encryptAlgo
     */
    public SecretKeyData(SecretKeySpec _secretKey, byte[] _keyBytes, String  _encryptAlgo) {
        secretKeySpec   = _secretKey;
        keyBytes        = _keyBytes;
        encryptAlgo     = _encryptAlgo;
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
        return (encryptAlgo == Algorithms.TripleDES) ? Arrays.copyOf(keyBytes, 8) : keyBytes;
    }
}
