package com.bancodigital.corebanking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configuração para recursos estáticos
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600)
                .resourceChain(true);
                
        // Configuração específica para arquivos de mídia
        registry.addResourceHandler("/media/**")
                .addResourceLocations("classpath:/static/media/")
                .setCachePeriod(3600);
                
        // Configuração para permitir reprodução de vídeos
        registry.addResourceHandler("/**/*.mp4", "/**/*.webm", "/**/*.ogg")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }
}