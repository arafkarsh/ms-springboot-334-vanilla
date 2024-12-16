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
 * ms-springboot-334-vanilla / MetricModel 
 *
 * @author: Araf Karsh Hamid
 * @version: 0.1
 * @date: 2024-11-21T11:40
 */
public class MetricModel {

    private final String metricPath;
    private final String metricEndPoint;
    private final String metricDescription;
    private final String[] metricTags;
    private final String metricName;

    /**
     * Create the Metric Model with relevant data
     * @param metricPath
     * @param metricEndPoint
     * @param metricDescription
     * @param metricTags
     */
    public MetricModel(String metricPath, String metricEndPoint, String metricDescription,
                       String[] metricTags, String metricName) {
        this.metricPath = metricPath;
        this.metricEndPoint = metricEndPoint;
        this.metricDescription = metricDescription;
        this.metricTags = metricTags;
        this.metricName = metricName;
    }

    /**
     * Returns Metric Path
     * @return
     */
    public String getMetricPath() {
        return metricPath;
    }

    /**
     * Returns Metric EndPoint
     * @return
     */
    public String getMetricEndPoint() {
        return metricEndPoint;
    }

    /**
     * Returns Metric Description
     * @return
     */
    public String getMetricDescription() {
        return metricDescription;
    }

    /**
     * Returns the Metric Tags
     * @return
     */
    public String[] getMetricTags() {
        return metricTags;
    }

    public String getMetricName() {
        return metricName;
    }
}
