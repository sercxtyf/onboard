package com.onboard.frontend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

import com.onboard.frontend.interceptors.LoginRequired;
import com.onboard.frontend.interceptors.RememberMeInterceptor;

@Component
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private RememberMeInterceptor rememberMeInterceptor;

    @Autowired
    private LoginRequired loginRequired;

    @Bean
    WebContentInterceptor initWebContentInterceptor() {
        WebContentInterceptor webContentInterceptor = new WebContentInterceptor();
        webContentInterceptor.setCacheSeconds(0);
        webContentInterceptor.setUseCacheControlHeader(true);
        return webContentInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rememberMeInterceptor);
        registry.addInterceptor(initWebContentInterceptor());
        registry.addInterceptor(loginRequired).excludePathPatterns("/api/account-exist/**")
                .excludePathPatterns("/api/account-forget/**").addPathPatterns("/api/**").addPathPatterns("/plugins/**")
                .addPathPatterns("/teams").addPathPatterns("/teams/**").addPathPatterns("/account")
                .addPathPatterns("/account/profile").addPathPatterns("/account/password").addPathPatterns("/account/sshkey");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/js/min/**").addResourceLocations("classpath:/static/js/min/")
                .setCachePeriod(31556926);
        registry.addResourceHandler("/static/css/min/**").addResourceLocations("classpath:/static/css/min/")
                .setCachePeriod(31556926);
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/").setCachePeriod(31556926);
        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/static/img/favicon.ico")
                .setCachePeriod(31556926);
        registry.addResourceHandler("/lib/**").addResourceLocations("classpath:/static").setCachePeriod(31556926);
    }

}
