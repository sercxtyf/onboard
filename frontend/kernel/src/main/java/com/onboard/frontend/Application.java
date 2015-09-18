package com.onboard.frontend;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import com.onboard.frontend.service.web.RequestFilter;
import com.onboard.frontend.websocket.RedisMessageReceiver;

/**
 * Created by XingLiang on 2015/4/23.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableWebSocket
public class Application {
    public static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(listenerAdapter, new PatternTopic("channel"));

        return container;

    }

    @Bean
    MessageListenerAdapter listenerAdapter(RedisMessageReceiver receiver) {

        return new MessageListenerAdapter(receiver, "receiveMessage");

    }

    @Bean
    RedisMessageReceiver receiver() {

        return new RedisMessageReceiver();

    }

    @Bean
    StringRedisTemplate template(RedisConnectionFactory connectionFactory) {

        return new StringRedisTemplate(connectionFactory);

    }

    @Bean
    public RequestContextFilter registrationRequestFilter() {

        RequestContextFilter requestContextFilter = new RequestFilter();

        return requestContextFilter;

    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return new OnboardCustomizer();
    }

    private static class OnboardCustomizer implements EmbeddedServletContainerCustomizer {

        public void customize(ConfigurableEmbeddedServletContainer container) {
            container.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, "/error/404"));
            container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error/404"));
            container.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500"));
            container.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN, "/error/403"));
        }

    }

    @Bean
    public Servlet getGitServlet() throws ServletException {
        ApiProxyServlet proxyServlet = new ApiProxyServlet();
        return proxyServlet;
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean() throws ServletException {
        return new ServletRegistrationBean(getGitServlet(), "/api/*");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
