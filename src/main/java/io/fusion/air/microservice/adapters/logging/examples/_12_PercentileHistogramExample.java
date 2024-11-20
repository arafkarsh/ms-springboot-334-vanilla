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
package io.fusion.air.microservice.adapters.logging.examples;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.stereotype.Component;

/**
 * ms-springboot-334-vanilla / _12_PercentileHistogramExample
 *
 * @author: Araf Karsh Hamid
 * @version: 0.1
 * @date: 2024-11-18T17:36
 */
@Component
public class _12_PercentileHistogramExample {

    // @Autowired not required - Constructor based Autowiring
    private final Timer timerWithPercentiles;
    private final DistributionSummary summaryWithHistogram;
    private final MeterRegistry meterRegistry;

    public _12_PercentileHistogramExample(MeterRegistry _meterRegistry) {
        meterRegistry = _meterRegistry;
        this.timerWithPercentiles = Timer.builder("fusion.air.example.12.percentileHistogram.timer")
                .publishPercentileHistogram()
                .publishPercentiles(0.5, 0.9, 0.95) // Track 50th, 90th, and 95th percentiles
                .description("Response time with percentiles and histogram support")
                .register(meterRegistry);;

        summaryWithHistogram = DistributionSummary.builder("fusion.air.example.12.percentileHistogram.ds")
                .publishPercentileHistogram()
                .publishPercentiles(0.75, 0.9, 0.99) // Track 75th, 90th, and 99th percentiles
                .description("Request size distribution with percentiles and histogram")
                .register(meterRegistry);
    }

    public void printStats() {
        // Print all registered meters and their measurements
        UtilsMeter.printStats(meterRegistry);
    }

    public static void main(String[] args) {
        // Use a SimpleMeterRegistry for demonstration
        MeterRegistry meterRegistry = new SimpleMeterRegistry();

        _12_PercentileHistogramExample ex = new _12_PercentileHistogramExample(meterRegistry);
        ex.printStats();
    }
}
