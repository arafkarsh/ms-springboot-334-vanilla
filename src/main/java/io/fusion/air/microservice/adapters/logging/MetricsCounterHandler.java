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

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * ms-springboot-334-vanilla / MetricsCounterHandler 
 *
 * @author: Araf Karsh Hamid
 * @version: 0.1
 * @date: 2024-11-21T13:27
 */
@Component
public class MetricsCounterHandler {

    private static final String METRIC_NAME = "METRIC-NAME-NOT-DEFINED";
    private static final String METRIC_PATH = "METRICS.PATH.NOT.DEFINED.";
    private static final String METRIC_ENDPOINT = "METRICS.FUNCTION.NOT.DEFINED";

    /**
     * Get the Metric Meta-Data from the Annotations
     * @param joinPoint
     * @return
     */
    public MetricModel getMetricModel(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // Check for class-level annotation first
        Class<?> targetClass = signature.getDeclaringType();
        MetricsPath metricClass = targetClass.getAnnotation(MetricsPath.class);
        // Check for method-level annotation
        MetricsCounter metricFunction = signature.getMethod().getAnnotation(MetricsCounter.class);

        String metricName = METRIC_NAME;
        String name = METRIC_PATH;
        String endPoint = METRIC_ENDPOINT;
        String[] tags = null;
        // Extract Class Name and Method Name
        if (metricClass != null) {
            name = metricClass.name();
        }
        if (metricFunction != null) {
            if(metricFunction.name() != null && !metricFunction.name().trim().isEmpty()) {
                name = metricFunction.name();
            }
            endPoint = metricFunction.endpoint().replace("/", ".");  // Use method endpoint
            metricName = name + endPoint;
            tags = metricFunction.tags();
        } else {
            // No annotation, proceed without tracking
            return null;
        }
        return new MetricModel(name, endPoint, "", tags, metricName);
    }

    /**
     * Create a Counter If it doesn't Exist or Retrieve the Counter based on Metric Name and Tags
     * @param name
     * @param tags
     * @param meterRegistry
     * @return
     */
    public Counter getCounter(String name, String[] tags, MeterRegistry meterRegistry) {
        // Retrieve or create the counter
        Counter counter = null;
        if(tags != null) {
            counter = meterRegistry.find(name).tags(tags).counter();
            if (counter == null) {
                // Create Counter if Not Found
                counter = Counter.builder(name)
                        .tags(tags)
                        .register(meterRegistry);
            }
        } else {
            counter = meterRegistry.find(name).counter();
            if (counter == null) {
                // Create Counter if Not Found
                counter = Counter.builder(name)
                        .register(meterRegistry);
            }
        }
        return counter;
    }
}
