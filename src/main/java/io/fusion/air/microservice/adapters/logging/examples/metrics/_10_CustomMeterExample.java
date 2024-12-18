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
// Custom
import io.fusion.air.microservice.utils.Std;
// Micrometer
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
// Spring
import org.springframework.stereotype.Component;
// Java
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

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

    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failureCount = new AtomicInteger(0);

    public _10_CustomMeterExample(MeterRegistry meterRegistry) {
        // Build and register the custom meter
        meter =  Meter.builder("fusion.air.example.10.customMeter", Meter.Type.OTHER, () -> {
                    // Create measurements for success and failure counts
                    return Arrays.asList(
                            // new Measurement(successCount::get, Statistic.COUNT),
                            new Measurement(failureCount::get, Statistic.COUNT)
                    ).iterator();
                })
                .description("Tracks the total success and failure requests")
                // With Multiple Measurements adding tags will create a runtime error > Duplicate Tags
                .tags(Tags.of("ops", "fusion.request.processing"))
                .register(meterRegistry);
    }

    /**
     * Success Count
     */
    public void recordSuccess() {
        successCount.incrementAndGet();
    }

    /**
     * Failure Count
     */
    public void recordFailure() {
        failureCount.incrementAndGet();
    }

    /**
     * Test the Code
     * @param args
     */
    public static void main (String[] args) {
        // Use a SimpleMeterRegistry for demonstration
        MeterRegistry meterRegistry = new SimpleMeterRegistry();

        _10_CustomMeterExample meterEx = new _10_CustomMeterExample(meterRegistry);

        // Output initial value of the TimeGauge
        Std.println("Initial Meter value: " + meterEx.meter.toString() + " ms");
        // Simulate a change in the tracked object's total
        for(int x=0; x<10; x++) { meterEx.recordSuccess(); }
        for(int x=0; x<2; x++) { meterEx.recordFailure(); }

        // Output updated value of the TimeGauge
        Std.println("Updated Meter value: " + meterEx.meter.toString() + " ms");

        // Print all registered meters and their measurements
        UtilsMeter.printStats(meterRegistry);
    }
}
