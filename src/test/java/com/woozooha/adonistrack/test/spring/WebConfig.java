package com.woozooha.adonistrack.test.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        AdonistrackInterceptor adonistrackInterceptor = new AdonistrackInterceptor();
        registry.addInterceptor(adonistrackInterceptor).addPathPatterns("/**/*");
    }

}
