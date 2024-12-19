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

/**
 * ms-springboot-334-vanilla / Std
 *
 * Used only for testing purpose and for command line utilities.
 *
 * -----------------------------------------------------------------------------
 * WARNING:
 * DO NOT USE THIS IN PRODUCTION CODE
 * -----------------------------------------------------------------------------
 *
 * @author: Araf Karsh Hamid
 * @version: 0.1
 * @date: 2024-12-18T9:12 PM
 */
public class Std {

    private Std() {}

    /**
     * Print New Line to Standard Out
     */
    public static final void println() {
        System.out.println();
    }
    /**
     * Print to Standard Out
     * @param l
     */
    public static final void println(long l) {
        System.out.println(l);
    }

    /**
     * Print to Standard Out
     * @param d
     */
    public static final void println ( double d) {
        System.out.println(d);
    }

    /**
     * Print to Standard Out
     * @param i
     */
    public static final void println(int i) {
        System.out.println(i);
    }

    /**
     * Print to Standard Out
     * @param s
     */
    public static final void println(short s) {
        System.out.println(s);
    }

    /**
     * Print to Standard Out
     * @param b
     */
    public static final void println(boolean b) {
        System.out.println(b);
    }

    /**
     * Print to Standard Out
     * @param f
     */
    public static final void println(float f) {
        System.out.println(f);
    }

    /**
     * Print to Standard Out
     * @param c
     */
    public static final void println(char c) {
        System.out.println(c);
    }

    /**
     * Print to Standard Out
     * @param c
     */
    public static final void println(char[] c) {
        System.out.println(c);
    }

    /**
     * Print to Standard Out
     * @param msg
     */
    public static final void println(String msg) {
        System.out.println(msg);
    }

    /**
     * Print to Standard Out
     * @param msg
     */
    public static final void println(Object msg) {
        System.out.println(msg.toString());
    }

    /**
     * Print Error
     * @param msg
     */
    public static final void printError(Object msg) {
        System.out.println("Error: "+msg);
    }

    /**
     * Print formated text to Standard out
     * @param format
     * @param args
     */
    public static final void printf(String format, Object... args) {
        System.out.printf(format, args);
    }
}
