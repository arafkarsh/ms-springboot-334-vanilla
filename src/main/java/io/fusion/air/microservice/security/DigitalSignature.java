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

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Signature;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */

@Service
public class DigitalSignature {

    private static final String path = "/Users/arafkarsh/ws/IntelliJ/book/ms-springboot-334-vanilla/";

    @Autowired
    private CryptoKeyGenerator cryptoKeyGenerator;

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
     * @throws Exception
     */
    public void signDocument(String documentName)  throws Exception {

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
        System.out.println("Create the Signature = " + fileName + ".signature");
        Files.write(Paths.get(fileName + ".signature"), digitalSignature);

        // Write the digital signature to a file in PEM format
        writePEMFile(digitalSignature, fileName+".pem", "SIGNATURE");
    }

    /**
     * Verify the Signature
     * @param documentName
     * @throws Exception
     */
    public  void verifySignature(String documentName) throws Exception {
        String fileName = documentName.split("\\.")[0];

        // Read the digital signature - Reading Binary Version of the Signature
        byte[] digitalSignature = Files.readAllBytes(Paths.get(fileName + ".signature"));

        // Create a signature instance and initialize it with the public key
        Signature signature = Signature.getInstance("SHA256withRSA");
        // Instantiate with the Public Key
        signature.initVerify(getCrypto().getPublicKey());

        // Read and update the document
        byte[] document = Files.readAllBytes(Paths.get(documentName));
        signature.update(document);

        // Verify the signature
        boolean isValid = signature.verify(digitalSignature);
        System.out.println("Verify the Signature = " + fileName + ".signature");
        System.out.println("Signature Verified   = Status = [" + isValid + "] "+ documentName );
    }

    /**
     * Write PEM File for the Signature
     * @param _fileName
     * @param _description
     */
    private void writePEMFile(byte[] _signature, String _fileName, String _description) {
        if(_signature == null || _fileName == null) {
            return;
        }
        PemObject pemObject = new PemObject(_description, _signature);
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
     * Testing the Digital Signature
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        DigitalSignature ds = new DigitalSignature();
        System.out.println("SIGN THE DOCUMENT >------------------------------------------------------");
        ds.signDocument(path + "x509.txt");

        System.out.println("VERIFY DOCUMENT   >------------------------------------------------------");
        ds.verifySignature(path + "x509.txt");
    }

}
