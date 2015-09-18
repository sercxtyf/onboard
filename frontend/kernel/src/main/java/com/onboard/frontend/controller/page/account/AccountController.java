package com.onboard.frontend.controller.page.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.onboard.frontend.controller.page.account.form.UserUpdateForm;
import com.onboard.frontend.model.User;
import com.onboard.frontend.service.util.AvatarUtils;
import com.onboard.frontend.service.web.SessionService;

@Controller
@RequestMapping("/account")
public class AccountController {

    public static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private static final String UPDATE_COMMAND = "updateCommand";
    private static final String USER_EDIT_VIEW = "onboard/account/Account :: account";

    @Autowired
    private AvatarUtils avatarUtils;

    @Autowired
    private SessionService session;

    @ModelAttribute(UPDATE_COMMAND)
    public UserUpdateForm getUserUpdateForm() {
        return new UserUpdateForm();
    }

    /**
     * 更新用户界面
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String showUpdateUserPage(@ModelAttribute(UPDATE_COMMAND) UserUpdateForm form) {
        User user = session.getCurrentUser();
        form.setId(user.getId());
        form.setName(user.getName());
        form.setUsername(user.getUsername());
        form.setEmail(user.getEmail());
        form.setDescription(user.getDescription());
        form.setAvatar(avatarUtils.getAvatarById(user.getId().intValue(), "avatar110"));

        return USER_EDIT_VIEW;
    }
}
