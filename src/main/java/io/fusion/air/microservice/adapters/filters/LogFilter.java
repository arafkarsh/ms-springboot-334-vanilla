/**
 * (C) Copyright 2022 Araf Karsh Hamid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fusion.air.microservice.adapters.filters;
// Custom
import io.fusion.air.microservice.server.config.ServiceConfig;
import io.fusion.air.microservice.utils.CPU;
// Spring
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
// Java
import org.slf4j.Logger;
import org.slf4j.MDC;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Component
@Order(1)
public class LogFilter implements Filter {

    // Set Logger -> Lookup will automatically determine the class name.
    private static final Logger log = getLogger(lookup().lookupClass());

    // Autowired using Constructor
    private final ServiceConfig serviceConfig;

    /**
     * Autowired using Constructor
     * @param serviceCfg
     */
    public LogFilter(ServiceConfig serviceCfg) {
        serviceConfig = serviceCfg;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        String name= (serviceConfig != null) ? serviceConfig.getServiceName(): "NotDefined";
        MDC.put("Service", name);

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String s = CPU.printCpuStats();
        log.info("1|LF|TIME=|STATUS=INIT|CLASS={}", s);

        filterChain.doFilter(request, response);

    }
}
