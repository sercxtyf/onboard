package com.onboard.frontend.service.thirdpart;

import java.util.Map;

public interface ThirdpartService {
    String GITHUBCALLBACK = "/%d/projects/%d/users/%d/github/callback?code=%s";

    Map<String, String> thirdPartAuthenticateRepository(int companyId, int projectId, int userId, String code);

}
