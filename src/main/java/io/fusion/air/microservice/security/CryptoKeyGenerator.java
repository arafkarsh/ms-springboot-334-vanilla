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


import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

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

    public final static String PUBLIC = "PUBLIC";
    private final static String PRIVATE = "PRIVATE";

    private PublicKey publicKey;
    private PrivateKey privateKey;

    private File publicKeyFile;
    private File privateKeyFile;

    private boolean publicKeyFileExists = false;
    private boolean privateKeyFileExists = false;

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
    }

    /**
     * Set the Crypto Key File Names
     * @param _publicKeyFile
     * @param _privateKeyFile
     * @return
     */
    public CryptoKeyGenerator setKeyFiles(String _publicKeyFile, String _privateKeyFile) {
        publicKeyFile = new File(_publicKeyFile);
        privateKeyFile = new File(_privateKeyFile);
        return this;
    }

    /**
     * Checks if the Public / Private Key File Exists
     * @return
     */
    public CryptoKeyGenerator iFPublicPrivateKeyFileNotFound() {
        publicKeyFileExists = (publicKeyFile == null || !publicKeyFile.exists()) ? false : true;
        privateKeyFileExists = (privateKeyFile == null || !privateKeyFile.exists()) ? false : true;
        return this;
    }

    public CryptoKeyGenerator THEN() { return this; }

    /**
     * Generate the Public / Private Keys if the File doesnt exists
     */
    public CryptoKeyGenerator createRSAKeyFiles()  {
        if(!publicKeyFileExists) {
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
        publicKey = (PublicKey) kp.getPublic();
        privateKey = (PrivateKey) kp.getPrivate();
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
     * @param _key
     * @param _fileName
     * @param _description
     */
    public void writePEMFile(Key _key, String _fileName, String _description) {
        if(_key == null || _fileName == null) {
            return;
        }
        PemObject pemObject = new PemObject(_description, _key.getEncoded());
        PemWriter pemWriter = null;
        try {
            pemWriter = new PemWriter(new OutputStreamWriter(new FileOutputStream(_fileName)));
            pemWriter.writeObject(pemObject);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(pemWriter != null) {
                try {
                    pemWriter.close();
                } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Print All Keys
     */
    public void printAllKeys() {
        System.out.println(getPublicKeyPEMFormat());
        System.out.println(getPrivateKeyPEMFormat());
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
     * @param _key
     * @param _description
     * @return
     */
    private String convertKeyToText(Key _key, String _description){
        if(_key == null || _description == null) {
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        PemObject pemObject = new PemObject(_description, _key.getEncoded());
        PemWriter pemWriter = null;
        try {
            pemWriter = new PemWriter(new PrintWriter(stringWriter));
            pemWriter.writeObject(pemObject);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(pemWriter != null) {
                try {
                    pemWriter.close();
                } catch (Exception ignored) {}
            }
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
    public RSAPublicKey readPublicKey(File file) throws Exception {
        KeyFactory factory = KeyFactory.getInstance("RSA");

        try (FileReader keyReader = new FileReader(file);
             PemReader pemReader = new PemReader(keyReader)) {

            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
            return (RSAPublicKey) factory.generatePublic(pubKeySpec);
        }
    }

    /**
     * Returns the RSA Private Key from a PEM File
     * @param file
     * @return
     * @throws Exception
     */
    public RSAPrivateKey readPrivateKey(File file) throws Exception {
        KeyFactory factory = KeyFactory.getInstance("RSA");

        try (FileReader keyReader = new FileReader(file);
             PemReader pemReader = new PemReader(keyReader)) {

            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
            return (RSAPrivateKey) factory.generatePrivate(privKeySpec);
        }
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

        System.out.println("Private key format: " + priKey.getFormat());
        System.out.println(keys.getPrivateKeyPEMFormat());
        System.out.println("Public key format: " + pubKey.getFormat());
        System.out.println(keys.getPublicKeyPEMFormat());

        // System.out.println("<><><>\n");
        // System.out.println("Write to Files");
        // keys.writePEMFile(pubKey, "publicKey", "RSA PUBLIC KEY");
        // keys.writePEMFile(priKey, "privateKey", "RSA PRIVATE KEY");

        System.out.println("<><><>\n");

        System.out.println("Read Public PEM File ..... ");
        pubKey = keys.readPublicKey(new File("publicKey.pem"));
        System.out.println("Public  key format: " + pubKey.getFormat());
        System.out.println(keys.getPublicKeyPEMFormat());

        System.out.println("Read Private PEM File ..... ");
        priKey = keys.readPrivateKey(new File("privateKey.pem"));
        System.out.println("Private key format: " + priKey.getFormat());
        System.out.println(keys.getPrivateKeyPEMFormat());
    }
}
