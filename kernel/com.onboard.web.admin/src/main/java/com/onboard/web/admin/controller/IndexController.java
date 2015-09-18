package com.onboard.web.admin.controller;

import com.onboard.service.web.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController {

    public static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private SessionService session;

    @RequestMapping(value = {"/", "/dashboard", "/application", "/feedback"}, method = RequestMethod.GET)
    public String indexHtml() {
        logger.debug("session = {}", session.getCurrentUser().getName());
        return "Index";
    }

}
