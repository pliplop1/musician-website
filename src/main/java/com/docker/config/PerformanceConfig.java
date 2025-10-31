package com.docker.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Configuration de performance : compression, cache, headers de sécurité
 */
@Configuration
public class PerformanceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Assets statiques avec cache long terme (1 an)
        registry.addResourceHandler("/css/**", "/js/**", "/images/**", "/vue/**", "/uploaded-images/**", "/uploaded-videos/**", "/uploaded-tracks/**")
                .addResourceLocations("classpath:/static/css/", "classpath:/static/js/", "classpath:/static/images/",
                                     "classpath:/static/vue/", "file:uploaded-images/", "file:uploaded-videos/", "file:uploaded-tracks/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic());

        // Avatars avec cache moyen terme (7 jours)
        registry.addResourceHandler("/uploaded-avatars/**")
                .addResourceLocations("file:uploaded-avatars/")
                .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic());
    }

    /**
     * Filtre pour ajouter les headers de sécurité et performance
     */
    @Bean
    public FilterRegistrationBean<SecurityHeadersFilter> securityHeadersFilter() {
        FilterRegistrationBean<SecurityHeadersFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SecurityHeadersFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    public static class SecurityHeadersFilter implements Filter {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {

            HttpServletResponse httpResponse = (HttpServletResponse) response;
            jakarta.servlet.http.HttpServletRequest httpRequest = (jakarta.servlet.http.HttpServletRequest) request;

            // Sécurité
            httpResponse.setHeader("X-Frame-Options", "SAMEORIGIN");
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
            httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
            httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            httpResponse.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");

            // Désactiver le cache pour les API dynamiques (compteurs, likes, etc.)
            String requestURI = httpRequest.getRequestURI();
            if (requestURI.startsWith("/api/")) {
                httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                httpResponse.setHeader("Pragma", "no-cache");
                httpResponse.setHeader("Expires", "0");
            }

            // HSTS (HTTPS uniquement en production)
            // httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

            chain.doFilter(request, response);
        }
    }
}
