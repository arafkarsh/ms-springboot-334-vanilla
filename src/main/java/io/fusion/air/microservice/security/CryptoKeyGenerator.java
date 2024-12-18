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

// Java Security
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
// Custom
import io.fusion.air.microservice.domain.exceptions.SecurityException;
import io.fusion.air.microservice.utils.Utils;
// Security
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.springframework.stereotype.Component;

/**
 * Generates Public / Private Keys
 *
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Component
public class CryptoKeyGenerator {

    public static final String PUBLIC = "PUBLIC";
    public static final String PRIVATE = "PRIVATE";

    private PublicKey publicKey;
    private PrivateKey privateKey;

    private File publicKeyFile;
    private File privateKeyFile;

    private boolean publicKeyFileExists;
    private boolean privateKeyFileExists;

    /**
     * Instantiate Generate Crypto Keys
     * CryptoKeyGenerator cryptoKeys = new CryptoKeyGenerator()
     *                                  .setKeyFiles(publicKeyFileName, privateKeyFileName)
     *                                  .iFPublicPrivateKeyFileNotFound().THEN()
     *                                      .createRSAKeyFiles()
     *                                  .ELSE()
     *                                      .readRSAKeyFiles()
     *                                  .build();
     *
     */
    public CryptoKeyGenerator() {
        publicKeyFileExists = false;
        privateKeyFileExists = false;
    }

    /**
     * Set the Public Key from KeyCloak
     * @param key
     */
    public void setPublicKeyFromKeyCloak(Key key) {
        if(key != null) {
            publicKey = (PublicKey) key;
        }
    }

    /**
     * Set the Crypto Key File Names
     * @param pubKeyFile
     * @param priKeyFile
     * @return
     */
    public CryptoKeyGenerator setKeyFiles(String pubKeyFile, String priKeyFile) {
        publicKeyFile = new File(pubKeyFile);
        privateKeyFile = new File(priKeyFile);
        return this;
    }

    /**
     * Checks if the Public / Private Key File Exists
     * @return
     */
    public CryptoKeyGenerator iFPublicPrivateKeyFileNotFound() {
        publicKeyFileExists = (publicKeyFile == null || !publicKeyFile.exists());
        privateKeyFileExists = (privateKeyFile == null || !privateKeyFile.exists()) ;
        return this;
    }

    public CryptoKeyGenerator THEN() { return this; }

    /**
     * Generate the Public / Private Keys if the File doesnt exists
     */
    public CryptoKeyGenerator createRSAKeyFiles()  {
        if(!isPublicKeyFileExists()) {
            try {
                generateRSAKeyPair();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            writeKeyFiles();
        }
        return this;
    }

    /**
     * Else Read the Files to load the Public / Private Keys
     * @return
     */
    public CryptoKeyGenerator ELSE()   {
        return this;
    }

    /**
     * Read The Files
     * @return
     */
    public CryptoKeyGenerator readRSAKeyFiles() {
        // File Exists - Then Read the PEM File
        try {
            publicKey = readPublicKey(publicKeyFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            privateKey = readPrivateKey(privateKeyFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public CryptoKeyGenerator build()  {
        return this;
    }

    /**
     * Generate RSA Key Pair
     * @throws NoSuchAlgorithmException
     */
    public CryptoKeyGenerator generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        keyGenerator.initialize(2048);
        KeyPair kp = keyGenerator.genKeyPair();
        publicKey =  kp.getPublic();
        privateKey = kp.getPrivate();
        return this;
    }

    /**
     *
     * @return
     */
    private CryptoKeyGenerator writeKeyFiles() {
        writePEMFile(publicKey, publicKeyFile.getName(), "RSA PUBLIC KEY");
        writePEMFile(privateKey, privateKeyFile.getName(), "RSA PRIVATE KEY");
        return this;
    }

    /**
     * Write Key to PEM File
     *
     * @param key
     * @param fileName
     * @param description
     */
    public void writePEMFile(Key key, String fileName, String description) {
        if(key == null || fileName == null) {
            return;
        }
        PemObject pemObject = new PemObject(description, key.getEncoded());
        try (PemWriter pemWriter = new PemWriter(new OutputStreamWriter(new FileOutputStream(fileName)))) {
            pemWriter.writeObject(pemObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Print All Keys
     */
    public void printAllKeys() {
        Utils.println(getPublicKeyPEMFormat());
        Utils.println(getPrivateKeyPEMFormat());
    }

    /**
     * Return the Public Keys in PEM Format
     */
    public String getPublicKeyPEMFormat() {
        return convertKeyToText(getPublicKey(), "RSA PUBLIC KEY");
    }

    /**
     * Return the Private Keys in PEM Format
     */
    public String getPrivateKeyPEMFormat() {
       return convertKeyToText(getPrivateKey(), "RSA PRIVATE KEY");
    }

    /**
     * Convert KEY to PEM Format
     * Public Key = X.509
     * Private Key = PKCS#8
     *
     * @param key
     * @param description
     * @return
     */
    public String convertKeyToText(Key key, String description){
        if(key == null || description == null) {
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        PemObject pemObject = new PemObject(description, key.getEncoded());
        try (PemWriter pemWriter = new PemWriter(new PrintWriter(stringWriter));) {
            pemWriter.writeObject(pemObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }

    /**
     * Returns Public Key
     * @return
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Returns Private key
     * @return
     */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    /**
     * Returns RSA Public Key from a PEM File
     * @param file
     * @return
     * @throws Exception
     */
    public RSAPublicKey readPublicKey(File file) throws SecurityException {
        try (var keyReader = new FileReader(file);
             var pemReader = new PemReader(keyReader)) {

            var pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            var pubKeySpec = new X509EncodedKeySpec(content);

            KeyFactory factory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) factory.generatePublic(pubKeySpec);
        } catch (Exception e) {
            throw new SecurityException("Unable to read public key",e);
        }
    }

    /**
     * Returns the RSA Private Key from a PEM File
     * @param file
     * @return
     * @throws Exception
     */
    public RSAPrivateKey readPrivateKey(File file) throws SecurityException {
        try (var keyReader = new FileReader(file);
             var pemReader = new PemReader(keyReader)) {

            var pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            var privKeySpec = new PKCS8EncodedKeySpec(content);

            KeyFactory factory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) factory.generatePrivate(privKeySpec);
        } catch (Exception e) {
            throw new SecurityException("Unable to read private key",e);
        }
    }

    /**
     * Returns True if the Public Key File Exists
     * @return
     */
    public boolean isPublicKeyFileExists() {
        return publicKeyFileExists;
    }

    /**
     * Returns True if the Private Key File Exists
     * @return
     */
    public boolean isPrivateKeyFileExists() {
        return privateKeyFileExists;
    }

    /**
     * For Testing Purpose ONLY
     * Generate the Public / Private Key Pairs
     * @param args
     */
    public static void main(String[] args) throws Exception {
        CryptoKeyGenerator keys = new CryptoKeyGenerator();
        keys.generateRSAKeyPair();
        Key pubKey = keys.getPublicKey();
        Key priKey = keys.getPrivateKey();

        Utils.println("Private key format: " + priKey.getFormat());
        Utils.println(keys.getPrivateKeyPEMFormat());
        Utils.println("Public key format: " + pubKey.getFormat());
        Utils.println(keys.getPublicKeyPEMFormat());

        // Utils.println("Write to Files");
        // keys.writePEMFile(pubKey, "publicKey", "RSA PUBLIC KEY");
        // keys.writePEMFile(priKey, "privateKey", "RSA PRIVATE KEY");

        Utils.println("Read Public PEM File ..... ");
        pubKey = keys.readPublicKey(new File("publicKey.pem"));
        Utils.println("Public  key format: " + pubKey.getFormat());
        Utils.println(keys.getPublicKeyPEMFormat());

        Utils.println("Read Private PEM File ..... ");
        priKey = keys.readPrivateKey(new File("privateKey.pem"));
        Utils.println("Private key format: " + priKey.getFormat());
        Utils.println(keys.getPrivateKeyPEMFormat());
    }
}
