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
package io.fusion.air.microservice.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ms-springboot-334-vanilla / RegexExample
 *
 * @author: Araf Karsh Hamid
 * @version: 0.1
 * @date: 2024-12-18T5:14 PM
 */
public class RegexExample {
    public static void main(String[] args) {
        Std.println("===============================================");
        // The regex pattern for zip code validation
        String regex = "^[0-9]{5}$";
        doTest(regex, "Zip Code:1");
        regex = "^[0-9]{1,5}$";
        doTest(regex, "Zip Code:2");
        regex = "^\\d{5}$";
        doTest(regex, "Zip Code:3");
    }

    public static void doTest(String regex, String name) {
        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Std.println("Testing the regex: " + regex);
        for (String code : getZipCodes()) {
            Matcher matcher = pattern.matcher(code);
            boolean isValid = matcher.matches();
            Std.println(name+": \"" + code + "\" is " + (isValid ? "VALID" : "INVALID"));
        }
        Std.println("----------------------------------------------");
    }

    /**
     * Return the Data for Testing Zip Code
     * @return
     */
    public static String[] getZipCodes() {
        return new String[] {
                "12345",      // Valid: 5 digits
                "123",        // Invalid: 3 digits
                "0",          // Invalid: 1 digit
                "123456",     // Invalid: More than 5 digits
                "abcde",      // Invalid: Non-numeric characters
                "12 34",      // Invalid: Spaces included
                "",           // Invalid: Empty string
                "00000",      // Valid: 5 digits
        };
    }
}
