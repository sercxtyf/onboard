/*******************************************************************************
 * Copyright [2015] [Onboard team of SERC, Peking University]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.onboard.service.web.impl;

import javax.annotation.PostConstruct;

import org.elevenframework.web.GlobalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.onboard.domain.mapper.UserMapper;
import com.onboard.domain.model.Company;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.User;
import com.onboard.service.web.SessionService;

/**
 * Implementation of UserSession based on ThreadLocal in Eleven Framework.
 * 
 * @author yewei
 * 
 */
@Service("sessionServiceBean")
public class SessionServiceImpl implements SessionService {

    public static final Logger logger = LoggerFactory.getLogger(SessionServiceImpl.class);

    private static final String PRODUCTION_ENV = "production";

    @Autowired
    private Environment environment;

    @Autowired
    private GlobalService globalService;

    @Autowired
    private UserMapper userMapper;

    private User user;

    @PostConstruct
    public void init() {
        // 第一个为默认用户，所以取第二个用户做测试用户
        if (!environment.acceptsProfiles(PRODUCTION_ENV)) {
            // user = userMapper.selectByExample(new UserExample()).get(0);
            user = userMapper.selectByPrimaryKey(793343);
        }
    }

    @Override
    public User getCurrentUser() {
        if (globalService.getSession() == null) {
            logger.info("no session so use temp user");
            return (User) globalService.get(TEMP_USER);
        }
        Object obj = globalService.getSession().getAttribute(CURRENT_USER);

        // return obj != null ? (User) obj : null;
        if (obj != null) {
            return (User) obj;
        }
        if (environment.acceptsProfiles(PRODUCTION_ENV)) {
            return null;
        }

        setCurrentUser(user);

        return user;
    }

    @Override
    public Company getCurrentCompany() {
        Object company = globalService.getSession().getAttribute(CURRENT_COMPANY);
        if (company == null) {
            return null;
        }
        return (Company) company;
    }

    @Override
    public Project getCurrentProject() {
        Object project = globalService.getSession().getAttribute(CURRENT_PROJECT);
        if (project == null) {
            return null;
        }
        return (Project) project;
    }

    @Override
    public void setCurrentUser(User user) {
        globalService.getSession().setAttribute(CURRENT_USER, user);
    }

    @Override
    public void setCurrentCompany(Company company) {
        globalService.getSession().setAttribute(CURRENT_COMPANY, company);
    }

    @Override
    public void setCurrentProject(Project project) {
        globalService.getSession().setAttribute(CURRENT_PROJECT, project);
    }

    @Override
    public void removeUserInformation() {
        globalService.getSession().removeAttribute(CURRENT_USER);
        globalService.getSession().removeAttribute(CURRENT_COMPANY);
        globalService.getSession().removeAttribute(CURRENT_PROJECT);
    }

    @Override
    public void setTempUser(User user) {
        globalService.set(TEMP_USER, user);
    }
}
