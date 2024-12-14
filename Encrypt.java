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
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.salt.ZeroSaltGenerator;
/**
 * Text Encryptor for Encrypting Sensitive Data
 * This code requires Java 23+
 * It's a preview feature in Java 23
 * Usage: java --enable-preview Encrypt.java <password_to_encrypt> <encryption_key>
 *
 * @author arafkarsh
 * @version 1.0
 *
 */
void main(String... args) {
	println("Text Encryptor using Jasypt Encryption Library (v1.9.3)");
    println("-------------------------------------------------------");
    var argsLength = args.length;
    if (argsLength != 2) {
        // println("Usage: java --enable-preview Encrypt.java <password_to_encrypt> <encryption_key>");
        println("Usage: source encrypt password_to_encrypt encryption_key");
        System.exit(1);
    }

    var textToEncrypt = args[0];    // Input text to encrypt
    var masterPassword = args[1];  // Master password for encryption

    // Create and configure the encryptor
    var encryptor = new StandardPBEStringEncryptor();
    encryptor.setPassword(masterPassword);
    encryptor.setAlgorithm("PBEWithMD5AndDES");
    encryptor.setSaltGenerator(new ZeroSaltGenerator()); // Fixed salt for consistent output

    println("Algorithm Used : PBEWithMD5AndDES");
    // Encrypt the text
    var encryptedText = encryptor.encrypt(textToEncrypt);
    println("Text to Encrypt: "+ textToEncrypt);
    println("Encrypted Text : "+ encryptedText);
    // Decrypt the text
    var decryptedText = encryptor.decrypt(encryptedText);
    println("Decrypted Text : "+ decryptedText);
    println("-------------------------------------------------------");
}