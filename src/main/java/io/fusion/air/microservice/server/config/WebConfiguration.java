package io.fusion.air.microservice.server.config;

import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This configuration is required to support Springboot 2.7.x to 3.x migration
 * Trailing Slash Matching Configuration
 *
 * Now the “GET /api/v1/todos/name/” doesn’t match anymore by default and will result in an HTTP 404 error.
 *
 * We can enable the trailing slash matching for all the endpoints by defining a new configuration class that
 * implements WebMvcConfigurer or WebFluxConfigurer (in case it's a reactive service):
 *
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
    }
}
