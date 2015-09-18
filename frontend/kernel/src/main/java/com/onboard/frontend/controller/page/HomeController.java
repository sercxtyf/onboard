package com.onboard.frontend.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by XingLiang on 2015/4/23.
 */
@Controller
public class HomeController {

    /**
     * 选择company
     * 
     */
    @RequestMapping(value = { "/teams", "/teams/**", "/account/**" }, method = RequestMethod.GET)
    public String home() {
        return "onboard/Onboard";
    }

}
