package ru.jetlabs.acquiringmockbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
//public class CorsConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:5173", "https://pay.lazyhat.ru",
//                        "http://localhost:5173/", "https://pay.lazyhat.ru/")
//                .allowedMethods("*")
//                .allowCredentials(true);
//    }
//}