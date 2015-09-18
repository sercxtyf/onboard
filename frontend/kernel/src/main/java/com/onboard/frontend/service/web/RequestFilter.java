package com.onboard.frontend.service.web;

import com.onboard.frontend.service.web.impl.GlobalServiceImpl;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.RequestContextFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by XingLiang on 2015/4/28.
 */
@Component
public class RequestFilter extends RequestContextFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        GlobalServiceImpl.setUpHttpSession(request);
        super.doFilterInternal(request, response, filterChain);
    }
}
