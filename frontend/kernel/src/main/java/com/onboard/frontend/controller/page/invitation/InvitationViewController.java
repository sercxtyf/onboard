package com.onboard.frontend.controller.page.invitation;

import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.onboard.frontend.controller.page.account.form.InvitationRegistrationForm;
import com.onboard.frontend.model.ResponseMap;
import com.onboard.frontend.model.User;
import com.onboard.frontend.model.dto.UserDTO;
import com.onboard.frontend.service.account.AccountService;
import com.onboard.frontend.service.net.NetService;
import com.onboard.frontend.service.web.SessionService;

@Controller
@RequestMapping("/{companyId}/invitation")
public class InvitationViewController {
    @Autowired
    private NetService netService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private SessionService sessionService;

    private static final String INVITATIONRESPONSE = "invitationResponse";
    private static final String EMAILISNULL = "emailIsNull";
    private static final String TOKENINVALID = "tokenInvalid";
    private static final String USEREXIST = "userExist";

    private String INVITATEURL = "/%d/invitation/%s";
    private String GETPROJECTBYCURRENTUSERURL = "/%d/users/%d";

    public static final Logger logger = LoggerFactory.getLogger(InvitationViewController.class);

    @RequestMapping(value = "/{token}", method = RequestMethod.GET)
    public String initRegistrationForm(@PathVariable("companyId") int companyId, @PathVariable("token") String token,
            @ModelAttribute("registrationCommand") InvitationRegistrationForm form, Model model) {

        Map<String, String> invitationMap = netService.getForObject(String.format(INVITATEURL, companyId, token),
                ResponseMap.class);

        String response = invitationMap.get(INVITATIONRESPONSE);
        String email = invitationMap.get("email");

        if (response.equals(EMAILISNULL)) {
            return "error/InvitationTokenInvalid";
        }

        if (response.equals(TOKENINVALID)) {
            return "error/InvitationTokenInvalid";
        }

        if (response.equals(USEREXIST)) {
            User currentUser = accountService.getUserByEmailOrUsername(email);
            sessionService.setCurrentUser(currentUser);
            return "redirect:/teams";
        }
        form.setEmail(email);
        String postRegistrationUrl = String.format(INVITATEURL, companyId, token);
        model.addAttribute("postRegistrationUrl", postRegistrationUrl);

        return "invitation/InvitationRegistration";
    }

    @RequestMapping(value = "/{token}", method = RequestMethod.POST)
    public String postRegistrationForm(@PathVariable("companyId") int companyId, @PathVariable("token") String token,
            @ModelAttribute("registrationCommand") @Valid InvitationRegistrationForm form, BindingResult result) {
        if (result.hasErrors()) {
            for (ObjectError error : result.getAllErrors()) {
                logger.info(error.getObjectName() + " " + error.getCode() + " " + error.getDefaultMessage());
            }

            return "invitation/InvitationRegistration";
        }
        UserDTO userDTO = netService.postForFormObject(String.format(INVITATEURL, companyId, token), form, UserDTO.class);
        sessionService.setCurrentUser(userDTO.toUser());
        Boolean currentHasProject = netService.getForObject(
                String.format(GETPROJECTBYCURRENTUSERURL, companyId, userDTO.getId()), Boolean.class);
        if (currentHasProject) {
            return "redirect:/teams/{companyId}";
        } else {
            return "redirect:/teams";
        }
    }
}
