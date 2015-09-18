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
package com.onboard.service.account.impl.test;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.mapper.InvitationMapper;
import com.onboard.domain.mapper.InvitationProjectsMapper;
import com.onboard.domain.mapper.model.InvitationExample;
import com.onboard.domain.mapper.model.InvitationProjectsExample;
import com.onboard.domain.model.Invitation;
import com.onboard.domain.model.InvitationProjects;
import com.onboard.domain.model.Project;
import com.onboard.service.account.impl.AccountConfigure;
import com.onboard.service.web.SessionService;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractInvitationManager {

    @Mock
    protected InvitationMapper mockedInvitationMapper;

    @Mock
    protected InvitationProjectsMapper mockedInvitationProjectsMapper;

    @Mock
    protected AccountConfigure mockedConfigurer;

    @Mock
    protected SessionService mockedSession;
    
    protected Invitation invitation;
    protected List<Invitation> invitationList;
    protected InvitationExample invitationExample;
    
    protected List<Project> projectList, projectListEmpty;
    protected List<InvitationProjects> invitationProjects;
    
    @Before
    public void setupTest() {
        invitation = getASampleInvitation();
        invitationList = getAListOfInvitations();
        invitationExample = getASampleInvitationExample();
        
        projectList = getAListOfProjects(2);
        projectListEmpty = getAListOfProjects(0);
        
        invitationProjects = getAListOfInvitationProjects();
        
        initInvitationMapper();
        initInvitationProjectsMapper();
        initAccountConfigure();
        initSessionService();
        
    }
    
    /** initInvitationMapper **/
    private void initInvitationMapper() {
        when(mockedInvitationMapper.insert(Mockito.any(Invitation.class))).thenReturn(1);
        when(mockedInvitationMapper.selectByPrimaryKey(anyInt())).thenReturn(invitation);
        when(mockedInvitationMapper.deleteByPrimaryKey(anyInt())).thenReturn(1);
        when(mockedInvitationMapper.updateByPrimaryKey(Mockito.any(Invitation.class))).thenReturn(1);
        
    }
    
    /** initInvitationProjectsMapper **/
    private void initInvitationProjectsMapper() {
        when(mockedInvitationProjectsMapper.insert(Mockito.any(InvitationProjects.class))).thenReturn(1);
        when(mockedInvitationProjectsMapper.selectByExample(Mockito.any(InvitationProjectsExample.class))).thenReturn(invitationProjects);
        when(mockedInvitationProjectsMapper.deleteByExample(Mockito.any(InvitationProjectsExample.class))).thenReturn(1);
    }
    
    /** initAccountConfigure **/
    private void initAccountConfigure() {
        when(mockedConfigurer.getTokenExpired()).thenReturn(0);
    }
    
    /** initSessionService **/
    private void initSessionService() {
        when(mockedSession.getCurrentUser()).thenReturn(ModuleHelper.getASampleUser());
    }
    
    /** **/
    private Invitation getASampleInvitation() {
        Invitation i = new Invitation();
        i.setCreated(new Date());
        i.setId(ModuleHelper.id);
        return i;
    }
    
    private List<Invitation> getAListOfInvitations() {
        List<Invitation> list = new ArrayList<Invitation>();
        list.add(invitation);
        list.add(getASampleInvitation());
        return list;
    }
    
    private InvitationExample getASampleInvitationExample() {
        InvitationExample ie = new InvitationExample();
        return ie;
    }
    
    private List<Project> getAListOfProjects(int n) {
        List<Project> list = new ArrayList<Project>();
        for (int i = 0; i < n; ++i)
            list.add(ModuleHelper.getASampleProject());
        return list;
    }
    
    private InvitationProjects getASampleInvitationProjects() {
        InvitationProjects ip = new InvitationProjects();
        ip.setId(ModuleHelper.id);
        ip.setProjectId(ModuleHelper.projectId);
        return ip;
    }
    
    private List<InvitationProjects> getAListOfInvitationProjects() {
        List<InvitationProjects> list = new ArrayList<InvitationProjects>();
        list.add(getASampleInvitationProjects());
        list.add(getASampleInvitationProjects());
        return list;
    }
}
