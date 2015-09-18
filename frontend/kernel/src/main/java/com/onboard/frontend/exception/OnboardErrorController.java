package com.onboard.frontend.exception;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class OnboardErrorController {

    @RequestMapping(value = "/error/{statusCode}")
    public String handleError(@PathVariable int statusCode) {
        if (statusCode == 500) {
            return "error/500";
        } else if (statusCode == 403) {
            return "error/403";
        }
        return "error/404";
    }
}
