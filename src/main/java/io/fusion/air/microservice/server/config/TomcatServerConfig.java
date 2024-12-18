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
package io.fusion.air.microservice.server.config;

import org.apache.coyote.ProtocolHandler;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

/**
 * ms-springboot-334-vanilla / TomcatServerConfig
 * Configuring the Virtual Threads
 * ------------------------------------------------------------------------------------------
 * This Spring Bean is a TomcatProtocolHandlerCustomizer that customizes the Tomcat server’s
 * protocol handler by setting a custom executor. Specifically, it sets the executor to a virtual
 * thread executor, which leverages the virtual threads introduced in Java 19+ (in preview) and
 * fully supported in Java 21.
 *
 * Key Features
 * 	•	TomcatProtocolHandlerCustomizer<?>: This is a generic interface that allows customization
 * 	    of Tomcat’s protocol handler.
 * 	•	Executors.newVirtualThreadPerTaskExecutor(): This creates a virtual thread executor, where
 * 	    each task is run on a lightweight virtual thread. Virtual threads provide a scalable and efficient
 * 	    alternative to traditional platform threads, making them suitable for high-concurrency applications.
 *
 * This bean allows Tomcat to use virtual threads for handling incoming HTTP requests, improving
 * concurrency and scalability for I/O-bound applications.
 *
 * When to Use Virtual Threads in Tomcat
 *
 * Virtual threads are highly efficient for I/O-bound workloads (e.g., handling many HTTP requests),
 * as they reduce memory overhead compared to traditional threads. Use this customization if:
 * 	•	Your application is I/O-bound (e.g., many database or network calls).
 * 	•	You are running on Java 19+ with virtual threads enabled.
 *
 * @author: Araf Karsh Hamid
 * @version: 0.1
 * @date: 2024-10-08T07:22
 */
@Configuration
public class TomcatServerConfig {

        @Bean
        public TomcatProtocolHandlerCustomizer<ProtocolHandler> protocolHandlerVirtualThreadExecutorCustomizer() {
            return protocolHandler ->
                protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        }
}
