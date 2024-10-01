/**
 * (C) Copyright 2021 Araf Karsh Hamid
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
package io.fusion.air.microservice.adapters.security;

import io.fusion.air.microservice.server.config.ServiceConfiguration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {
    @Autowired
    private ServiceConfiguration serviceConfig;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Forces All Request to be Secured (HTTPS)
        // http.requiresChannel().anyRequest().requiresSecure();
        String apiPath = serviceConfig.getApiDocPath();
        http.authorizeRequests()
                .requestMatchers(apiPath + "/**")
                .permitAll()
                .and()
                // This configures exception handling, specifically specifying that when a user tries to access a page
                // they're not authorized to view, they're redirected to "/403" (typically an "Access Denied" page).
                .exceptionHandling().accessDeniedPage("/403");
        // Enable CSRF Protection
        // This line configures the Cross-Site Request Forgery (CSRF) protection, using a Cookie-based CSRF token
        // repository. This means that CSRF tokens will be stored in cookies. The withHttpOnlyFalse() method makes
        // these cookies accessible to client-side scripting, which is typically necessary for applications that use
        // a JavaScript-based frontend.
        /**
         http
            .csrf()
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            // Add the above Only for testing in Swagger
            .and()
            .addFilterAfter(new CsrfTokenResponseHeaderBindingFilter(), CsrfFilter.class);
         */
        // Disable for Local Testing
        http.csrf(CsrfConfigurer::disable);

        // X-Frame-Options is a security header that is intended to protect your website against "clickjacking" attacks.
        // Clickjacking is a malicious technique of tricking web users into revealing confidential information or taking
        // control of their interaction with the website, by loading your website in an iframe of another website and
        // then overlaying it with additional content.
        http.headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.deny())
                .contentSecurityPolicy("frame-ancestors 'none'"));
        String hostName = serviceConfig.getServerHost();
        // Content Security Policy
        // The last part sets the Content Security Policy (CSP). This is a security measure that helps prevent a range
        // of attacks, including Cross-Site Scripting (XSS) and data injection attacks. It does this by specifying which
        // domains the browser should consider to be valid sources of executable scripts. In this case, scripts
        // (script-src) and objects (object-src) are only allowed from the same origin ('self') or from a subdomain of
        // the specified host name.
        http.headers()
                .contentSecurityPolicy(
                        "default-src 'self'; "
                                +"script-src 'self' *."+hostName+"; "
                                +"object-src 'self' *."+hostName+"; "
                                +"img-src 'self'; media-src 'self'; frame-src 'self'; font-src 'self'; connect-src 'self'");
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**");
    }


    /**
     * Handles Malicious URI Path (handles special characters and other things
     * @return
     */
    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowedHttpMethods(Arrays.asList("GET","POST", "PUT", "DELETE"));
        return firewall;
    }

    /**
     * ONLY For Local Testing with Custom CSRF Headers in Swagger APi Docs
     */
    private static class CsrfTokenResponseHeaderBindingFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
            response.setHeader("X-CSRF-HEADER", token.getHeaderName());
            response.setHeader("X-CSRF-PARAM", token.getParameterName());
            response.setHeader("X-CSRF-TOKEN", token.getToken());
            filterChain.doFilter(request, response);
        }
    }
}

