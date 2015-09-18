package com.onboard.frontend.controller.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.onboard.frontend.model.User;
import com.onboard.frontend.service.web.SessionService;

/**
 * Created by XingLiang on 2015/4/23.
 */
@RestController
public class CurrentUserAPIController {

    @Autowired
    private SessionService sessionService;

    @RequestMapping(value = "/api/currentUser", method = RequestMethod.GET)
    @ResponseBody
    public User getCurrentUser(HttpServletRequest httpServletRequest) {
        return sessionService.getCurrentUser();
    }
}
