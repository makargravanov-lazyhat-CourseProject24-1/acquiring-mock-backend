package ru.jetlabs.acquiringmockbackend.config;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.jetlabs.acquiringmockbackend.service.JWTValidator;

import java.io.IOException;
import java.util.List;

@Configuration
public class SecurityConfig {
    private final JWTValidator jwtValidator;

    public SecurityConfig(JWTValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<>();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("https://pay.lazyhat.ru");
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "OPTIONS", "PUT"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        corsBean.setFilter(new CorsFilter(source));
        corsBean.setOrder(1);
        return corsBean;
    }

    @Bean
    public FilterRegistrationBean<JWTFilter> jwtFilter() {
        FilterRegistrationBean<JWTFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JWTFilter(jwtValidator));
        registrationBean.addUrlPatterns("/acquiring-mock-backend/api/v1/secured/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }

    public class JWTFilter implements Filter {
        private final JWTValidator jwtValidator;

        public JWTFilter(JWTValidator jwtValidator) {
            this.jwtValidator = jwtValidator;
        }

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            Filter.super.init(filterConfig);
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            String jwt = null;
            if (httpRequest.getCookies() != null) {
                for (Cookie cookie : httpRequest.getCookies())
                    if ("jwt".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        break;
                    } else if ("loginWaitingJwt".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        break;
                    }
            }
            boolean condition = true;
            Long id = null;
            try {
                id = jwtValidator.getIdFromToken(jwtValidator.validate(jwt));
            } catch (NullPointerException e) {
                condition = false;
            }
            if (condition) {
                request.setAttribute("userIdFromFilter", id);
                chain.doFilter(request, response);
            } else {
                System.out.println("Unauthorized, jwt=" + jwt);
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            }
        }

        @Override
        public void destroy() {
            Filter.super.destroy();
        }
    }

}