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
package io.fusion.air.microservice.adapters.logging;

/**
 * ms-springboot-334-vanilla / TimedObject 
 *
 * @author: Araf Karsh Hamid
 * @version: 0.1
 * @date: 2024-11-18T12:24
 */
public class TimedObject {

    private int count = 1;
    private long totalTime = 100;

    /**
     * Create Timed Object with default values
     */
    public TimedObject() {
    }

    /**
     * Creates Timed Object
     * @param _count
     * @param _totalTime
     */
    public TimedObject(int _count, long _totalTime) {
        count = _count;
        totalTime = _totalTime;
    }

    /**
     * Get Count
     * @return
     */
    public int getCount() {
        return count;
    }

    public void setCount(int _count) {
        count = _count;
    }

    /**
     * Get Total Time
     * @return
     */
    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long _totalTime) {
        totalTime = _totalTime;
    }

    /**
     * For Custom Metrics
     * @return
     */
    public long getCustomValue() {
        return totalTime;
    }
}
