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
import org.jasypt.iv.RandomIvGenerator;
import org.jasypt.salt.RandomSaltGenerator;
import org.jasypt.salt.ZeroSaltGenerator;
/**
 * Text Decryptor for Decrypting Sensitive Data
 * This code requires Java 23+
 * It's a preview feature in Java 23
 * Usage: java --enable-preview java -cp libs/jasypt-1.9.3.jar Decrypt.java <encrypted_text>
 *
 * @author arafkarsh
 * @version 1.0
 *
 */
void main(String... args) {
    println("Text Decryptor using Jasypt Encryption Library (v1.9.3)");
    println("-------------------------------------------------------");
    var argsLength = args.length;
    if (argsLength != 1) {
        // "Usage: java --enable-preview java -cp libs/jasypt-1.9.3.jar Decrypt.java <encrypted_text>";
        println("Usage: source decrypt encrypted_text");
        System.exit(1);
    }
    var testToDecrypt = args[0];    // Input text to encrypt
    // Master password for Decryption
    var masterPassword = System.getenv("JASYPT_ENCRYPTOR_PASSWORD");
    if(masterPassword == null) {
        println("Encryption Key is missing in Env var > JASYPT_ENCRYPTOR_PASSWORD!");
        return;
    }

    // Create and configure the decryptor
    var decryptor = new StandardPBEStringEncryptor();
    decryptor.setPassword(masterPassword);
    // Use an AES-based PBE algorithm - PBEWithHmacSHA512AndAES_256
    String algo = "PBEWithHmacSHA512AndAES_256";
    decryptor.setAlgorithm(algo);
    println("Algorithm Used : "+algo);
    // Add Randomness to the Decryption
    decryptor.setIvGenerator(new RandomIvGenerator());
    decryptor.setSaltGenerator(new RandomSaltGenerator());
    // Decrypt Text
    println("Encrypted Text : "+ testToDecrypt);
    var decryptedText = decryptor.decrypt(testToDecrypt);
    println("Decrypted Text : "+ decryptedText);
    println("-------------------------------------------------------");

}