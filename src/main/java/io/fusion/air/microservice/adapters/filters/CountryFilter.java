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
import io.fusion.air.microservice.utils.Utils;
// Jakarta
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// Java
import java.io.IOException;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;
import org.slf4j.Logger;
import org.slf4j.MDC;
// Spring
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;

/**
 * Servlet Filter with WebFilter Example
 *
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */

/**
 * if you use the @WebFilter annotation with urlPatterns, this also allows you to specify the scope of the filter.
 * However, this is more Servlet standard-based rather than Spring-specific:
 */
@WebFilter(urlPatterns = "/ms-vanilla/api/v1/country/*")
@Order(30)
public class CountryFilter implements Filter {
    // Set Logger -> Lookup will automatically determine the class name.
    private static final Logger log = getLogger(lookup().lookupClass());

    @Override
    public void doFilter(ServletRequest _servletRequest, ServletResponse _servletResponse, FilterChain _filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) _servletRequest;
        HttpServletResponse response = (HttpServletResponse) _servletResponse;

        HttpHeaders headers = Utils.createSecureCookieHeaders("CNT-RID", MDC.get("ReqId"), 300);
        response.addHeader("Set-Cookie", headers.getFirst("Set-Cookie"));

        System.out.println("<[4]>>> Country Filter Called => "+ headers.getFirst("Set-Cookie"));

        _filterChain.doFilter(request, response);
    }
}

