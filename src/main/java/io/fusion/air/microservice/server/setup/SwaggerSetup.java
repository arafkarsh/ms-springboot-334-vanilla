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
package io.fusion.air.microservice.server.setup;
// Custom
import io.fusion.air.microservice.server.config.ServiceConfig;
import io.fusion.air.microservice.server.controllers.HealthController;
// Swagger Open API
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
// Spring
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
// Java
import java.util.ArrayList;
import java.util.List;

/**
 * ms-springboot-334-vanilla / SwaggerSetup
 *
 * Set up the Swagger Open API Documentation for the Service
 * Create all the necessary beans.
 *
 * @author: Araf Karsh Hamid
 * @version: 0.1
 * @date: 2024-12-21T5:28 PM
 */
@Component
public class SwaggerSetup {
    // Autowired using the Constructor Injection
    private final ServiceConfig serviceConfig;

    /**
     * Autowired using the Constructor Injection
     * @param serviceConfig
     */
    public SwaggerSetup(ServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    // =======================================================================================
    // Open API Documentation Code
    // =======================================================================================

    /**
     * Open API v3 Docs - All
     * Reference: https://springdoc.org/faq.html
     * @return
     */
    @Bean
    public GroupedOpenApi allPublicApi() {
        return GroupedOpenApi.builder()
                .group(serviceConfig.getServiceName()+"-service")
                .pathsToMatch(serviceConfig.getServiceApiPath()+"/**")
                .build();
    }

    /**
     * Open API v3 Docs - MicroService
     * Reference: https://springdoc.org/faq.html
     * @return
     */
    @Bean
    public GroupedOpenApi appPublicApi() {
        return GroupedOpenApi.builder()
                .group(serviceConfig.getServiceName()+"-service-core")
                .pathsToMatch(serviceConfig.getServiceApiPath()+"/**")
                .pathsToExclude(serviceConfig.getServiceApiPath()+"/service/**", serviceConfig.getServiceApiPath()+"/config/**")
                .build();
    }

    /**
     * Open API v3 Docs - Core Service
     * Reference: https://springdoc.org/faq.html
     * Change the Resource Mapping in HealthController
     *
     * @see HealthController
     */
    @Bean
    public GroupedOpenApi configPublicApi() {
        return GroupedOpenApi.builder()
                .group(serviceConfig.getServiceName()+"-service-config")
                .pathsToMatch(serviceConfig.getServiceApiPath()+"/config/**")
                .build();
    }

    /**
     * System Public APIs
     * @return
     */
    @Bean
    public GroupedOpenApi systemPublicApi() {
        return GroupedOpenApi.builder()
                .group(serviceConfig.getServiceName()+"-service-health")
                .pathsToMatch(serviceConfig.getServiceApiPath()+"/service/**")
                .build();
    }

    /**
     * Open API v3
     * Reference: https://springdoc.org/faq.html
     * @return
     */
    @Bean
    public OpenAPI buildOpenAPI() {
        return new OpenAPI()
                .servers(getServers())
                .info(new Info()
                        .title(serviceConfig.getServiceName()+" Service")
                        .description(serviceConfig.getServiceDetails())
                        .version(serviceConfig.getServerVersion())
                        .license(new License().name("License: "+serviceConfig.getServiceLicense())
                                .url(serviceConfig.getServiceLicenseURL()))
                )
                .externalDocs(new ExternalDocumentation()
                        .description(serviceConfig.getServiceName()+" Service Source Code")
                        .url(serviceConfig.getServiceApiRepository())
                )
                .components(new Components().addSecuritySchemes("bearer-key",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"))
                );
    }

    /**
     * Get the List of Servers for Open API Docs - Swagger
     * @return
     */
    private List<Server> getServers() {
        List<Server> serverList = new ArrayList<>();

        Server dev = new Server();
        dev.setUrl(serviceConfig.getServerHostDev());
        dev.setDescription(serviceConfig.getServerHostDevDesc());
        Server uat = new Server();
        uat.setUrl(serviceConfig.getServerHostUat());
        uat.setDescription(serviceConfig.getServerHostUatDesc());
        Server prod = new Server();
        prod.setUrl(serviceConfig.getServerHostProd());
        prod.setDescription(serviceConfig.getServerHostProdDesc());
        // Add all the servers
        serverList.add(dev);        // Development Server
        serverList.add(uat);        // UAT Server
        serverList.add(prod);       // Production Server
        // return ServerList
        return serverList;
    }
}
