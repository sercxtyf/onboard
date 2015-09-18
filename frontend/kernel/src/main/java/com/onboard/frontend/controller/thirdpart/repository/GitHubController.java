package com.onboard.frontend.controller.thirdpart.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.onboard.frontend.model.User;
import com.onboard.frontend.service.thirdpart.ThirdpartService;
import com.onboard.frontend.service.web.SessionService;

@Controller
public class GitHubController {
    @Autowired
    private SessionService session;

    @Value("${data.github.client_id}")
    private String client_id;

    @Value("${data.github.scope}")
    private String scope;

    @Value("${data.github.state}")
    private String githubState;

    @Value("${data.host}")
    private String applicationHostUrl;

    private static final String GITHUBOAUTH = "https://github.com/login/oauth/authorize?client_id=%s&amp;"
            + "redirect_uri=%s/%d/projects/%d/users/%d/github/callback&amp;scope=%s&amp;state=%s";

    @Autowired
    private ThirdpartService thirdpartService;

    @RequestMapping(value = "/{companyId}/projects/{projectId}/github", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> githubAuthenticate(@PathVariable int companyId, @PathVariable int projectId) {
        User user = session.getCurrentUser();
        Map<String, String> callbackUrl = new HashMap<String, String>();
        String url = String.format(GITHUBOAUTH, client_id, applicationHostUrl, companyId, projectId, user.getId(), scope,
                githubState);
        callbackUrl.put("callbackUrl", url);
        return callbackUrl;
    }

    @RequestMapping(value = "/{companyId}/projects/{projectId}/users/{userId}/github/callback", method = RequestMethod.GET)
    public String getGitHubResponse(@PathVariable int companyId, @PathVariable int projectId, @PathVariable int userId,
            @RequestParam("code") String code, @RequestParam("state") String state, Model model) throws Exception {
        if (!githubState.equals(state)) {
            model.addAttribute("error", "Authenticate error!");
            return "redirect:/teams/{companyId}/projects/{projectId}/repository/prepare/create";
        }

        thirdpartService.thirdPartAuthenticateRepository(companyId, projectId, userId, code);

        return "redirect:/teams/{companyId}/projects/{projectId}/repository/prepare/choiceGitHubRepository";
    }
}
