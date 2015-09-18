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
package com.onboard.service.account.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onboard.domain.mapper.ActivityMapper;
import com.onboard.domain.mapper.CompanyMapper;
import com.onboard.domain.mapper.ProjectMapper;
import com.onboard.domain.mapper.UserCompanyMapper;
import com.onboard.domain.mapper.UserMapper;
import com.onboard.domain.mapper.UserProjectMapper;
import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Invitation;
import com.onboard.domain.model.InvitationProjects;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.User;
import com.onboard.domain.model.UserCompany;
import com.onboard.domain.model.UserProject;
import com.onboard.service.account.AccountService;
import com.onboard.service.account.UserService;
import com.onboard.service.account.utils.PasswordUtils;
import com.onboard.service.email.EmailService;
import com.onboard.service.email.TemplateEngineService;
import com.onboard.service.web.SessionService;

@Transactional
@Service("accountServiceBean")
public class AccountServiceImpl implements AccountService {

    public static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private static final String INVITATION_TPL = "templates/Invitation.html";
    private static final String ACTIVITY_INFO = "加入了项目：";
    private static final String JOIN_ACTION = "join"; // not used now

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserCompanyMapper userCompanyMapper;

    @Autowired
    private UserProjectMapper userProjectMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private AccountConfigure configurer;

    @Autowired
    private SessionService session;

    @Autowired
    private InvitationManager invitationManager;

    @Autowired
    private TemplateEngineService templateEngineService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    // private static final String INVITE_TEMPLATE = "com/onboard/service/account/vm/invitation.vm";

    @Override
    public void sendInvitation(int companyId, String email) {
        sendInvitation(companyId, email, null);
    }

    @Override
    public void sendInvitation(int companyId, String email, List<Project> projects) {
        User user = userService.getUserByEmail(email);
        // 用户已经在团队中，直接加入项目即可
        if (user != null && userService.isUserInCompany(user.getId(), companyId) && projects != null) {
            for (Project p : projects) {
                if (userService.isUserInProject(user.getId(), companyId, p.getId())) {
                    continue;
                }
                UserProject userProject = new UserProject();
                userProject.setUserId(user.getId());
                userProject.setProjectId(p.getId());
                userProject.setCompanyId(companyId);
                userProjectMapper.insert(userProject);
            }
            return;
        }
        // 检查是否曾经发出邀请
        String token;
        Invitation invitation = invitationManager.getExistInvitationByEmail(companyId, email);
        if (invitation == null) {
            token = UUID.randomUUID().toString();
            invitation = invitationManager.insertInvitation(companyId, email, token);
        } else {
            token = invitation.getToken();
            invitationManager.updateInvitationDate(invitation);
        }

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("invitor", session.getCurrentUser());
        model.put("host", configurer.getProtocol() + configurer.getHost());
        model.put("company", companyMapper.selectByPrimaryKey(companyId));
        model.put("token", token);
        model.put("projects", invitationManager.addInvitationProjects(invitation, projects));

        String text = templateEngineService.process(getClass(), INVITATION_TPL, model);
        emailService.sendEmail(session.getCurrentUser().getName(), email, null, null, "您被邀请加入OnBoard", text, null);
    }

    @Override
    public String authenticateInvitation(int companyId, String token) {
        Invitation invitation = invitationManager.getExistInvitationByToken(companyId, token);
        return invitation == null && invitationManager.isInvitationExpired(invitation) ? null : invitation.getEmail();
    }

    /**
     * 当把一个用户加入到项目时，产生一条Activity信息
     * 
     */
    @Override
    public void addActivityInfo(User user, int projectId) {

        Project project = projectMapper.selectByPrimaryKey(projectId);

        Activity activity = new Activity();

        activity.setCreated(new Date());
        activity.setCompanyId(project.getCompanyId());
        activity.setAttachId(project.getCompanyId());
        activity.setAttachType(project.getType());
        activity.setProjectId(project.getId());
        activity.setProjectName(project.getName());
        activity.setAction(JOIN_ACTION);
        activity.setCreatorId(user.getId());
        activity.setCreatorName(user.getName());
        activity.setSubject(ACTIVITY_INFO);
        activity.setCreatorAvatar(user.getAvatar());
        activity.setTarget(project.getName());

        activityMapper.insert(activity);

    }

    @Override
    public void completeInvitation(int companyId, User user, String token) {
        Invitation invitation = invitationManager.getExistInvitationByToken(companyId, token);

        if (invitation == null) {
            throw new RuntimeException("token invalid");
        }
        // if user is an InvitationRegistrationForm
        if (user.getId() == null) {
            user.setActivated(true);
            user.setEmail(invitation.getEmail());
            user.setCreated(new Date());
            user.setUpdated(user.getCreated());
            user.setNewPassword(PasswordUtils.createPassword(user.getPassword(), user.getCreated().toString()));
            userMapper.insertSelective(user);
        }

        if (!userService.isUserInCompany(user.getId(), companyId)) {
            UserCompany userCompany = new UserCompany();
            userCompany.setUserId(user.getId());
            userCompany.setCompanyId(companyId);
            userCompanyMapper.insert(userCompany);
        }

        List<InvitationProjects> ips = invitationManager.getInvitationProjectsByInvitationId(invitation.getId());
        for (InvitationProjects ip : ips) {
            UserProject userProject = new UserProject();
            userProject.setUserId(user.getId());
            userProject.setProjectId(ip.getProjectId());
            userProject.setCompanyId(companyId);
            userProjectMapper.insert(userProject);

            this.addActivityInfo(user, ip.getProjectId());
        }
        invitationManager.deleteInvitationById(invitation.getId());
    }

    @Override
    public List<Invitation> getAllInvitations(int companyId) {
        Invitation sample = new Invitation();
        sample.setCompanyId(companyId);

        return invitationManager.getInvitationsBySample(sample);
    }

    @Override
    public Invitation getInvitationById(int id) {
        return invitationManager.getInvitationById(id);
    }

    @Override
    public void deleteInvitationById(int id) {
        invitationManager.deleteInvitationById(id);
    }
}
