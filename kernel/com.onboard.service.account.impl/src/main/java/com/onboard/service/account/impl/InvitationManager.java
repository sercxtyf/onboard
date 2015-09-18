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
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.onboard.domain.mapper.InvitationMapper;
import com.onboard.domain.mapper.InvitationProjectsMapper;
import com.onboard.domain.mapper.model.InvitationExample;
import com.onboard.domain.mapper.model.InvitationProjectsExample;
import com.onboard.domain.model.Invitation;
import com.onboard.domain.model.InvitationProjects;
import com.onboard.domain.model.Project;
import com.onboard.service.account.function.InvitationProjectFilter;
import com.onboard.service.web.SessionService;

@Component
public class InvitationManager {

    @Autowired
    InvitationMapper invitationMapper;

    @Autowired
    private InvitationProjectsMapper invitationProjectsMapper;

    @Autowired
    private AccountConfigure configurer;

    @Autowired
    private SessionService session;

    public boolean isInvitationExpired(Invitation invitation) {
        DateTime created = new DateTime(invitation.getCreated());
        return created.plusSeconds(configurer.getTokenExpired()).isBeforeNow();
    }

    /**
     * 根据Example获取已经存在的Invitation 如果Invitation已经过期，则删除后返回null
     * 
     * @param companyId
     * @param email
     * @return
     */
    private Invitation getExistInvitationByExample(InvitationExample example) {
        List<Invitation> invitations = invitationMapper.selectByExample(example);
        if (invitations.isEmpty()) {
            return null;
        }
        Invitation invitation = invitations.get(0);
        return invitation;
    }

    public Invitation getExistInvitationByEmail(int companyId, String email) {
        Invitation sample = new Invitation();
        sample.setEmail(email);
        sample.setCompanyId(companyId);

        return this.getExistInvitationByExample(new InvitationExample(sample));
    }

    public Invitation getExistInvitationByToken(int companyId, String token) {
        Invitation sample = new Invitation();
        sample.setToken(token);
        sample.setCompanyId(companyId);

        return this.getExistInvitationByExample(new InvitationExample(sample));
    }

    /**
     * 添加Invitation
     * 
     * @param companyId
     * @param email
     * @param token
     * @return
     */
    public Invitation insertInvitation(int companyId, String email, String token) {
        Invitation invitation = new Invitation();
        invitation.setCompanyId(companyId);
        invitation.setEmail(email);
        invitation.setUserId(session.getCurrentUser().getId());
        invitation.setCreated(new Date());
        invitation.setToken(token);
        invitationMapper.insert(invitation);
        return invitation;
    }

    /**
     * 添加InvitationProject
     * 
     * @param invitation
     * @param projects
     * @return
     */
    public List<Project> addInvitationProjects(Invitation invitation, List<Project> projects) {
        if (projects == null || projects.isEmpty()) {
            return projects;
        }
        InvitationProjects sample = new InvitationProjects();
        sample.setInvitationId(invitation.getId());
        List<InvitationProjects> ips = invitationProjectsMapper.selectByExample(new InvitationProjectsExample(sample));
        List<Project> ret = Lists.newArrayList(Iterables.filter(projects, new InvitationProjectFilter(ips)));
        for (Project project : ret) {
            insertInvitationProjects(invitation.getId(), project.getId());
        }

        return ret;
    }

    public InvitationProjects insertInvitationProjects(Integer invitationId, Integer projectId) {
        InvitationProjects ip = new InvitationProjects();
        ip.setInvitationId(invitationId);
        ip.setProjectId(projectId);
        invitationProjectsMapper.insert(ip);
        return ip;
    }

    /**
     * 通过id获取Invitation
     * 
     * @param invitationId
     * @return
     */
    public Invitation getInvitationById(int invitationId) {
        return invitationMapper.selectByPrimaryKey(invitationId);
    }

    /**
     * 根据id删除Invitation
     * 
     * @param id
     */
    public void deleteInvitationById(int id) {
        InvitationProjects sample = new InvitationProjects();
        sample.setInvitationId(id);
        invitationProjectsMapper.deleteByExample(new InvitationProjectsExample(sample));
        invitationMapper.deleteByPrimaryKey(id);
    }

    public void updateInvitationDate(Invitation invitation) {
        invitation.setCreated(new Date());
        invitationMapper.updateByPrimaryKey(invitation);
    }

    public List<InvitationProjects> getInvitationProjectsByInvitationId(int invitationId) {
        InvitationProjects sample = new InvitationProjects();
        sample.setInvitationId(invitationId);
        return invitationProjectsMapper.selectByExample(new InvitationProjectsExample(sample));
    }

    public List<Invitation> getInvitationsBySample(Invitation sample) {
        return invitationMapper.selectByExample(new InvitationExample(sample));
    }
}
