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

import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.stereotype.Component;

/**
 * ms-springboot-334-vanilla / _7_FunctionCounterExample 
 *
 * @author: Araf Karsh Hamid
 * @version: 0.1
 * @date: 2024-11-19T10:24
 */
@Component
public class _7_FunctionCounterExample {

    private final ProcessingQueue processingQueue;

    public _7_FunctionCounterExample(MeterRegistry meterRegistry) {
        // Simulate a processing queue
        processingQueue = new ProcessingQueue();

        // Create and register a FunctionCounter
        FunctionCounter.builder("fusion.air.example.7.functionCounter", processingQueue, ProcessingQueue::getItemsProcessed)
                .description("Tracks the number of items processed from the queue")
                .tags("queue", "processing")
                .register(meterRegistry);
    }

    public static void main(String[] args) {
        // For testing purposes, use a simple in-memory MeterRegistry
        MeterRegistry meterRegistry = new SimpleMeterRegistry();

        // Create an instance of the example
        _7_FunctionCounterExample example = new _7_FunctionCounterExample(meterRegistry);

        // Simulate processing items
        example.processingQueue.processItem();
        example.processingQueue.processItem();
        example.processingQueue.processItem();

        // Access and print the FunctionCounter value
        double itemsProcessed = meterRegistry.get("fusion.air.example.7.functionCounter").functionCounter().count();
        System.out.println("Items processed: " + itemsProcessed);
    }
}

// Simulated queue for processing items
class ProcessingQueue {
    private long itemsProcessed = 0;

    public void processItem() {
        itemsProcessed++;
    }

    public long getItemsProcessed() {
        return itemsProcessed;
    }
}
