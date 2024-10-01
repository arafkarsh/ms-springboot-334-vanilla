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

import io.fusion.air.microservice.server.config.ServiceConfiguration;
import io.fusion.air.microservice.utils.CPU;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
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

    @Autowired
    private ServiceConfiguration serviceConfig;

    @Override
    public void doFilter(ServletRequest _servletRequest, ServletResponse _servletResponse, FilterChain _filterChain)
            throws IOException, ServletException {
        String name= (serviceConfig != null) ? serviceConfig.getServiceName(): "NotDefined";
        MDC.put("Service", name);

        HttpServletRequest request = (HttpServletRequest) _servletRequest;
        HttpServletResponse response = (HttpServletResponse) _servletResponse;

        log.info("1|LF|TIME=|STATUS=INIT|CLASS={}", CPU.printCpuStats());

        _filterChain.doFilter(request, response);

    }
}
