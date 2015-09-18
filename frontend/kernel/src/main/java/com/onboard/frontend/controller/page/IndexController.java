package com.onboard.frontend.controller.page;

import com.onboard.frontend.service.web.SessionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by XingLiang on 2015/4/23.
 */
@Controller
public class IndexController {

    public static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private SessionService session;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        if (session.getCurrentUser() != null) {
            return "onboard/Onboard";
        }

        return "home/Index";
    }

    @RequestMapping(value = "/features", method = RequestMethod.GET)
    public String features() {
        return "home/Features";
    }

    @RequestMapping(value = "/tool", method = RequestMethod.GET)
    public String tool() {
        return "home/Tool";
    }

    @RequestMapping(value = "/help", method = RequestMethod.GET)
    public String help() {
        return "home/HelpCenter";
    }
}
