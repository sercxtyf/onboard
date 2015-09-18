package com.onboard.frontend.controller.page.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.onboard.frontend.exception.ResourceNotFoundException;
import com.onboard.frontend.service.account.AccountService;

@Controller
public class ConfirmationController {

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/account/{uid}/confirmation/{token}", method = RequestMethod.GET)
    public String confirmUser(@PathVariable("uid") int uid, @PathVariable("token") String token) {
        boolean result = accountService.confirmRegisteredUser(uid, token);
        if (!result) {
            throw new ResourceNotFoundException();
        }
        return "redirect:/account/signin";
    }
}
