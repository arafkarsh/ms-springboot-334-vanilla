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
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * ms-springboot-334-vanilla / MetricsAspect
 *
 * @author: Araf Karsh Hamid
 * @version: 0.1
 * @date: 2024-10-08T12:05
 */
@Aspect
@Component
public class MetricsAspect {

    private final MeterRegistry meterRegistry;

    public MetricsAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    // @Around("execution(* *(..)) && @within(io.fusion.air.microservice.adapters.logging.MetricsCounter) || @annotation(io.fusion.air.microservice.adapters.logging.MetricsCounter)")
    @Around("@annotation(io.fusion.air.microservice.adapters.logging.MetricsCounter)")
    public Object trackCounter(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // Check for class-level annotation first
        Class<?> targetClass = signature.getDeclaringType();
        MetricsCounter counterClass = targetClass.getAnnotation(MetricsCounter.class);
        // Check for method-level annotation
        MetricsCounter counterMethod = signature.getMethod().getAnnotation(MetricsCounter.class);

        String metricName = "METRIC-NAME-NOT-DEFINED";
        String name = "CLASS.NAME.NOT.DEFINED";
        String endPoint = "ENDPOINT.NOT.DEFINED";
        String[] tags = null;
        // Extract Class Name and Method Name
        if (counterClass != null && counterClass.name() != null) {
            name = counterClass.name();
        }
        if (counterMethod != null) {
            if(counterMethod.endpoint() != null) {
                endPoint = counterMethod.endpoint();  // Use method endpoint
            }
            endPoint = endPoint.replaceAll("/", ".");
            metricName = name + endPoint;
            tags = counterMethod.tags();
        } else {
            // No annotation, proceed without tracking
            return joinPoint.proceed();
        }
        // Get Counter and Increment the Counter
        getCounter(metricName, tags).increment();

        return joinPoint.proceed(); // Proceed with the method execution
    }

    private Counter getCounter(String name, String[] tags) {
        // Retrieve or create the counter
        Counter counter = null;
        if(tags != null) {
            counter = meterRegistry.find(name).tags(tags).counter();
            if (counter == null) {
                // System.out.println("<><> >> Adding Metrics: "+fullCounterName+" <> Tags # = "+tags.length);
                counter = Counter.builder(name)
                        .tags(tags)
                        .register(meterRegistry);
            }
        } else {
            counter = meterRegistry.find(name).counter();
            if (counter == null) {
                // System.out.println("<><> >> Adding Metrics: "+fullCounterName+" <> Tags # = "+tags.length);
                counter = Counter.builder(name)
                        .register(meterRegistry);
            }
        }
        return counter;
    }
}
