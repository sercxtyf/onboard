package com.onboard.frontend.service.web;

import com.onboard.frontend.model.Company;
import com.onboard.frontend.model.Project;
import com.onboard.frontend.model.User;

/**
 * Created by XingLiang on 2015/4/23.
 */
public interface SessionService {

    String CURRENT_USER = "currentUser";
    String CURRENT_COMPANY = "currentCompany";
    String CURRENT_PROJECT = "currentProject";
    String TEMP_USER = "tempUser";
    String CURRENT_THIRD_PART_USER_ID = "userId";
    String NEXT_URL = "nextUrl";

    User getCurrentUser();

    Company getCurrentCompany();

    Project getCurrentProject();

    int getCurrentThirdpartUserId();

    String getNextUrl();

    void setCurrentUser(User user);

    void setCurrentCompany(Company company);

    void setCurrentProject(Project project);

    void removeUserInformation();

    void setCurrentThirdpartUserId(Integer id);

    void setCurrentNextUrl(String url);

}
