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

// Bouncy Castel
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
// Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// Java
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */

@Service
public class DigitalSignature {

    private static final String EXTENSION = ".signature";

    // Autowired using the Constructor
    private CryptoKeyGenerator cryptoKeyGenerator;

    public DigitalSignature() {
    }

    /**
     * Autowired using the Constructor
     * @param keyGenerator
     */
    @Autowired
    public DigitalSignature(CryptoKeyGenerator keyGenerator) {
        cryptoKeyGenerator = keyGenerator;
    }

    /**
     * Generate the Public and Private Keys
     * If the Key exists - Read the Keys from PEM File
     */
    public void generateAndLoadKeys() {
        cryptoKeyGenerator = new CryptoKeyGenerator()
            .setKeyFiles("publicKey-ds.pem", "privateKey-ds.pem")
            .iFPublicPrivateKeyFileNotFound().THEN()
                .createRSAKeyFiles()
            .ELSE()
                .readRSAKeyFiles()
            .build();
    }

    /**
     * Get Crypto Generator Instance, If not created then create one
     * @return
     */
    public CryptoKeyGenerator getCrypto() {
        if(cryptoKeyGenerator == null) {
            generateAndLoadKeys();
        }
        return cryptoKeyGenerator;
    }

    /**
     * Sign the Document
     *
     * @param documentName
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws SignatureException
     */
    public void signDocument(String documentName) throws
            NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException {

        String fileName = documentName.split("\\.")[0];
        // Create a signature instance
        System.out.println("Creating Signature Instance for the Algo = SHA256withRSA");
        Signature signature = Signature.getInstance("SHA256withRSA");
        // Initialize the Signature with Private Key Previously Generated
        signature.initSign(getCrypto().getPrivateKey());

        // Read and sign the document
        System.out.println("Sign the Document    = " + documentName);
        byte[] document = Files.readAllBytes(Paths.get( documentName));
        // Set the Doc in the Signature
        signature.update(document);
        // Sign the Document
        byte[] digitalSignature = signature.sign();

        // Write the digital signature to a file
        System.out.println("Create the Signature = " + fileName + EXTENSION);
        Files.write(Paths.get(fileName + EXTENSION), digitalSignature);

        // Write the digital signature to a file in PEM format
        writePEMFile(digitalSignature, fileName+".pem", "SIGNATURE");
    }

    /**
     * Verify the Signature
     *
     * @param documentName
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws SignatureException
     */
    public  void verifySignature(String documentName) throws
    NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException  {
        String fileName = documentName.split("\\.")[0];

        // Read the digital signature - Reading Binary Version of the Signature
        byte[] digitalSignature = Files.readAllBytes(Paths.get(fileName + EXTENSION));

        // Create a signature instance and initialize it with the public key
        Signature signature = Signature.getInstance("SHA256withRSA");
        // Instantiate with the Public Key
        signature.initVerify(getCrypto().getPublicKey());

        // Read and update the document
        byte[] document = Files.readAllBytes(Paths.get(documentName));
        signature.update(document);

        // Verify the signature
        boolean isValid = signature.verify(digitalSignature);
        System.out.println("Verify the Signature = " + fileName + EXTENSION);
        System.out.println("Signature Verified   = Status = [" + isValid + "] "+ documentName );
    }

    /**
     * Write PEM File for the Signature
     * @param fileName
     * @param description
     */
    private void writePEMFile(byte[] signature, String fileName, String description) {
        if(signature == null || fileName == null) {
            return;
        }
        PemObject pemObject = new PemObject(description, signature);
        try (PemWriter pemWriter  = new PemWriter(new OutputStreamWriter(new FileOutputStream(fileName))) )  {
            pemWriter.writeObject(pemObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Testing the Digital Signature
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String path = "/Users/arafkarsh/ws/IntelliJ/book/ms-springboot-334-vanilla/";
        DigitalSignature ds = new DigitalSignature();
        System.out.println("SIGN THE DOCUMENT >------------------------------------------------------");
        ds.signDocument(path + "x509.txt");

        System.out.println("VERIFY DOCUMENT   >------------------------------------------------------");
        ds.verifySignature(path + "x509.txt");
    }

}
