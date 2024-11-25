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
package io.fusion.air.microservice.adapters.logging.examples.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

/**
 * ms-springboot-334-vanilla / _11_MeterFilterExample
 *
 * @author: Araf Karsh Hamid
 * @version: 0.1
 * @date: 2024-11-18T17:29
 */
public class _11_MeterFilterExample {

    public static void main (String[] args) {
        // Use a SimpleMeterRegistry for demonstration
        MeterRegistry meterRegistry = new SimpleMeterRegistry();
        // change the prefix to fusion instead of jvm and all the fusion metrics will be filtered
        meterRegistry.config().meterFilter(MeterFilter.denyNameStartsWith("jvm"));

        _1_CounterExample ex1Counter = new _1_CounterExample(meterRegistry);
        _2_GaugeMonitorExample ex2Gauge = new _2_GaugeMonitorExample(meterRegistry);

        // Print all registered meters and their measurements
        UtilsMeter.printStats(meterRegistry);
    }
 }
