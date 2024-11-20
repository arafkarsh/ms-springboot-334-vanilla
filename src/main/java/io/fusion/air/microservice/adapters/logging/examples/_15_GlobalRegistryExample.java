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

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

/**
 * ms-springboot-334-vanilla / _15_GlobalRegistryExample 
 *
 * @author: Araf Karsh Hamid
 * @version: 0.1
 * @date: 2024-11-20T16:15
 */
public class _15_GlobalRegistryExample {

    public static void main(String[] args) {
        // Access the global registry via the Metrics class
        MeterRegistry globalRegistry = Metrics.globalRegistry;

        // Create and add a SimpleMeterRegistry
        SimpleMeterRegistry simpleRegistry = new SimpleMeterRegistry();
        Metrics.globalRegistry.add(simpleRegistry);  // OR Metrics.addRegistry(simpleRegistry);

        // Create and add a PrometheusMeterRegistry
        PrometheusMeterRegistry prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        Metrics.globalRegistry.add(prometheusRegistry);  // OR Metrics.addRegistry(prometheusRegistry);

        // Register a metric with the global registry
        Metrics.counter("fusion.air.example.15.GlobalRegistry", "type", "test").increment();

        // Print the metrics in SimpleMeterRegistry
        System.out.println("Metrics in SimpleMeterRegistry:");
        simpleRegistry.getMeters().forEach(meter -> System.out.println(meter.getId()));

        // Print the metrics in PrometheusMeterRegistry
        System.out.println("\nMetrics in PrometheusMeterRegistry:");
        System.out.println(prometheusRegistry.scrape());

    }
}
