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

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.stereotype.Component;

/**
 * ms-springboot-334-vanilla / _10_CustomMeterExample
 *
 * @author: Araf Karsh Hamid
 * @version: 0.1
 * @date: 2024-11-18T12:47
 */
@Component
public class _10_CustomMeterExample {

    // @Autowired not required - Constructor based Autowiring
    private final Meter meter;
    private final TimedObject timedObject;

    public _10_CustomMeterExample(MeterRegistry meterRegistry) {
        timedObject = new TimedObject(30, 6000);
        // Build and register the custom meter
        /**
        meter = Meter.builder("fusion.air.example.10.customMeter", Meter.Type.GAUGE,
                        timedObject, TimedObject::getCustomValue)
                .description("A custom metric with flexible measurement type")
                .tags("customMeter", "exampleMeter")
                .register(meterRegistry);
         */
        meter = null;
    }

    public static void main (String[] args) {
        // Use a SimpleMeterRegistry for demonstration
        MeterRegistry meterRegistry = new SimpleMeterRegistry();

        _10_CustomMeterExample meterEx = new _10_CustomMeterExample(meterRegistry);

        // Output initial value of the TimeGauge
        System.out.println("Initial Meter value: " + meterEx.meter.toString() + " ms");
        // Simulate a change in the tracked object's total time
        meterEx.timedObject.setTotalTime(6000);
        // Output updated value of the TimeGauge
        System.out.println("Updated Meter value: " + meterEx.meter.toString() + " ms");

        // Print all registered meters and their measurements
        UtilsMeter.printStats(meterRegistry);
    }
}
