package com.woozooha.adonistrack.test.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.woozooha.adonistrack.filter.AdonistrackFilter;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Application.class, args);
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String name : beanDefinitionNames) {
            Object bean = context.getBean(name);
            // System.err.println(name + ": " + bean.getClass().getName());
        }
    }

    @Bean
    public FilterRegistrationBean<AdonistrackFilter> profileFilter() {
        FilterRegistrationBean<AdonistrackFilter> registrationBean = new FilterRegistrationBean<AdonistrackFilter>();

        registrationBean.setFilter(new AdonistrackFilter());
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }

}
