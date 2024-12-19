/**
 * Copyright (c) 2024 Araf Karsh Hamid
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * <p>
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 * <p>
 * or (per the licensee's choosing)
 * <p>
 * under the terms of the Apache 2 License version 2.0
 * as published by the Apache Software Foundation.
 */
package io.fusion.air.microservice.security.utils;

// Jasypt
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.jasypt.salt.RandomSaltGenerator;
import org.jasypt.salt.ZeroSaltGenerator;

import static java.lang.System.out;

/**
 * Text Encryptor for Encrypting Sensitive Data
 * This code requires Java 17+
 * Usage: java -cp libs/jasypt-1.9.3.jar src/main/java/io/fusion/air/microservice/security/Encrypt17.jav <text_to_encrypt> <encryption_key>
 *
 * @author: Araf Karsh Hamid
 * @version: 0.1
 * @date: 2024-12-14T11:27
 */
public class Encrypt17 {

    public static final String LINE = "-------------------------------------------------------";

    public static void main(String[] args) {
        out.println("Text Encryptor using Jasypt Encryption Library (v1.9.3)");
        out.println(LINE);
        doEncryptionAES(args);
    }

    /**
     * Do Encryption with Standard PBEWithMD5AndDES Algorithm
     * @param args
     */
    private static void doEncryption(String[] args) {
        if(!validateInputs(args)) {
            return;
        }
        var textToEncrypt = args[0];    // Input text to encrypt
        var masterPassword = args[1];  // Master password for encryption

        // Create and configure the encryptor
        var encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(masterPassword);
        // Set the Algo and Zero Salt
        String algo = "PBEWithMD5AndDES";
        encryptor.setAlgorithm(algo);
        encryptor.setSaltGenerator(new ZeroSaltGenerator()); // Fixed salt for consistent output
        out.println("Algorithm Used : "+algo);
        // Encrypt the text
        var encryptedText = encryptor.encrypt(textToEncrypt);
        out.println("Text to Encrypt: "+ textToEncrypt);
        out.println("Encrypted Text : "+ encryptedText);
        // Decrypt the text
        var decryptedText = encryptor.decrypt(encryptedText);
        out.println("Decrypted Text : "+ decryptedText);
        out.println(LINE);
    }

    /**
     * Do Encryption using AES Algorithm - PBEWithHmacSHA512AndAES_256
     * @param args
     */
    private static void doEncryptionAES(String[] args) {
        if(!validateInputs(args)) {
            return;
        }
        var textToEncrypt = args[0];    // Input text to encrypt
        var masterPassword = args[1];  // Master password for encryption

        // Create and configure the encryptor
        var encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(masterPassword);

        // Use an AES-based PBE algorithm - PBEWithHmacSHA512AndAES_256
        String algo = "PBEWithHmacSHA512AndAES_256";
        encryptor.setAlgorithm(algo);
        //  Add Random IV and Salt
        encryptor.setIvGenerator(new RandomIvGenerator());
        encryptor.setSaltGenerator(new RandomSaltGenerator());

        out.println("Algorithm Used : "+algo);
        // Encrypt the text
        var encryptedText = encryptor.encrypt(textToEncrypt);
        out.println("Text to Encrypt: "+ textToEncrypt);
        out.println("Encrypted Text : "+ encryptedText);
        // Decrypt the text
        var decryptedText = encryptor.decrypt(encryptedText);
        out.println("Decrypted Text : "+ decryptedText);
        out.println(LINE);
    }

    /**
     * Validate the Inputs
     * @param args
     * @return
     */
    private static boolean validateInputs(String[] args) {
        if (args.length != 2) {
            // "Usage: java -cp libs/jasypt-1.9.3.jar src/main/java/io/fusion/air/microservice/security/Encrypt17.java <text_to_encrypt> <encryption_key>");
            out.println("Usage: source encrypt text_to_encrypt encryption_key");
            return false;
        }
        return true;
    }
}
