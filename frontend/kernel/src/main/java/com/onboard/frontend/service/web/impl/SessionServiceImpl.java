package com.onboard.frontend.service.web.impl;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onboard.frontend.model.Company;
import com.onboard.frontend.model.Project;
import com.onboard.frontend.model.User;
import com.onboard.frontend.service.web.GlobalService;
import com.onboard.frontend.service.web.SessionService;

/**
 * Implementation of UserSession based on ThreadLocal in Eleven Framework.
 * 
 * @author yewei
 * 
 */
@Service
public class SessionServiceImpl implements SessionService {

    public static final Logger logger = LoggerFactory.getLogger(SessionServiceImpl.class);

    @Autowired
    private GlobalService globalService;

    public User getCurrentUser() {
        HttpSession session = globalService.getSession();
        if (session == null) {
            return null;
        }
        Object obj = session.getAttribute(CURRENT_USER);
        if (obj != null) {
            User user = (User) obj;

            return user;
        }
        return null;
    }

    public Company getCurrentCompany() {
        HttpSession session = globalService.getSession();
        Object obj = session.getAttribute(CURRENT_COMPANY);
        if (obj != null) {
            Company company = (Company) obj;

            return company;
        }
        return null;
    }

    public Project getCurrentProject() {
        HttpSession session = globalService.getSession();
        Object obj = session.getAttribute(CURRENT_PROJECT);
        if (obj != null) {
            return (Project) obj;
        }
        return null;
    }

    public int getCurrentThirdpartUserId() {
        HttpSession session = globalService.getSession();
        Object id = session.getAttribute(CURRENT_THIRD_PART_USER_ID);
        if (id != null) {
            return (Integer) id;
        }
        return -1;
    }

    public String getNextUrl() {
        HttpSession session = globalService.getSession();
        Object nextUrl = session.getAttribute(NEXT_URL);
        if (nextUrl != null) {
            return (String) nextUrl;
        }

        return null;
    }

    public void setCurrentUser(User user) {
        HttpSession httpSession = globalService.getSession();
        httpSession.setAttribute(CURRENT_USER, user);
    }

    public void setCurrentCompany(Company company) {
        HttpSession httpSession = globalService.getSession();
        httpSession.setAttribute(CURRENT_COMPANY, company);
    }

    public void setCurrentProject(Project project) {
        HttpSession httpSession = globalService.getSession();
        httpSession.setAttribute(CURRENT_PROJECT, project);
    }

    public void removeUserInformation() {
        HttpSession httpSession = globalService.getSession();
        httpSession.removeAttribute(CURRENT_USER);
        httpSession.removeAttribute(CURRENT_COMPANY);
        httpSession.removeAttribute(CURRENT_PROJECT);
    }

    public void setCurrentThirdpartUserId(Integer id) {
        HttpSession httpSession = globalService.getSession();
        httpSession.setAttribute(CURRENT_THIRD_PART_USER_ID, id);
    }

    public void setCurrentNextUrl(String url) {
        HttpSession httpSession = globalService.getSession();
        httpSession.setAttribute(NEXT_URL, url);

    }
}
