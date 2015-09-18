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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import com.onboard.domain.mapper.model.InvitationExample;
import com.onboard.domain.mapper.model.InvitationProjectsExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Invitation;
import com.onboard.domain.model.InvitationProjects;
import com.onboard.domain.model.Project;
import com.onboard.service.account.impl.InvitationManager;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.exampleutils.ObjectMatcher;
import com.onboard.test.moduleutils.ModuleHelper;

public class InvitationManagerTest extends AbstractInvitationManager {

    @InjectMocks
    private InvitationManager invitationManager;
    
    @Test
    public void testIsInvitationExpired() {
        invitationManager.isInvitationExpired(invitation);
        verify(mockedConfigurer, times(1)).getTokenExpired();
    }
    
    @Test
    public void testGetExistInvitationByEmailWithNull() {
        
        when(mockedInvitationMapper.selectByExample(Mockito.any(InvitationExample.class))).thenReturn(new ArrayList<Invitation>());
        
        Invitation ret = invitationManager.getExistInvitationByEmail(ModuleHelper.companyId, ModuleHelper.email);
        
        verify(mockedInvitationMapper, times(1)).selectByExample(Mockito.argThat(new ExampleMatcher<InvitationExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "email", ModuleHelper.email) && CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId);
            }
            
        }));
        
        assertEquals(ret, null);
    }
    
    @Test
    public void testGetExistInvitationByEmail() {
        
        when(mockedInvitationMapper.selectByExample(Mockito.any(InvitationExample.class))).thenReturn(invitationList);
        
        Invitation ret = invitationManager.getExistInvitationByEmail(ModuleHelper.companyId, ModuleHelper.email);
        
        verify(mockedInvitationMapper, times(1)).selectByExample(Mockito.argThat(new ExampleMatcher<InvitationExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "email", ModuleHelper.email) && CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId);
            }
            
        }));
        
        assertSame(ret, invitation);
    }
    
    @Test
    public void testGetExistInvitationByToken() {
        
        when(mockedInvitationMapper.selectByExample(Mockito.any(InvitationExample.class))).thenReturn(invitationList);
        
        Invitation ret = invitationManager.getExistInvitationByToken(ModuleHelper.companyId, ModuleHelper.token);
        
        verify(mockedInvitationMapper, times(1)).selectByExample(Mockito.argThat(new ExampleMatcher<InvitationExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "token", ModuleHelper.token) && CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId);
            }
            
        }));
        
        assertSame(ret, invitation);
    }

    @Test
    public void testInsertInvitation() {
        Invitation ret = invitationManager.insertInvitation(ModuleHelper.companyId, ModuleHelper.email, ModuleHelper.token);
        
        verify(mockedInvitationMapper, times(1)).insert(Mockito.argThat(new ObjectMatcher<Invitation>() {

            @Override
            public boolean verifymatches(Invitation item) {
                return item.getCompanyId().equals(ModuleHelper.companyId) && item.getEmail().equals(ModuleHelper.email) && item.getToken().equals(ModuleHelper.token);
            }
        }));

        verify(mockedSession, times(1)).getCurrentUser();
        
        assertEquals(ret.getCompanyId(), new Integer(ModuleHelper.companyId));
        assertEquals(ret.getEmail(), ModuleHelper.email);
        assertEquals(ret.getToken(), ModuleHelper.token);
        assertEquals(ret.getUserId(), new Integer(ModuleHelper.userId));
    }
    
    @Test
    public void testAddInvitationProjectsWithNull() {
        List<Project> ret = invitationManager.addInvitationProjects(invitation, null);
        assertEquals(ret, null);
    }
    
    @Test
    public void testAddInvitationProjectsWithEmpty() {
        List<Project> ret = invitationManager.addInvitationProjects(invitation, projectListEmpty);
        assertSame(ret, projectListEmpty);
    }
    
    @Test
    public void testAddInvitationProjects() {
        invitationManager.addInvitationProjects(invitation, projectList);
        verify(mockedInvitationProjectsMapper, times(1)).selectByExample(Mockito.any(InvitationProjectsExample.class));
    }
    
    @Test
    public void testInsertInvitationProjects() {
        InvitationProjects ret = invitationManager.insertInvitationProjects(ModuleHelper.id, ModuleHelper.projectId);
        verify(mockedInvitationProjectsMapper, times(1)).insert(Mockito.argThat(new ObjectMatcher<InvitationProjects>() {
            
            @Override
            public boolean verifymatches(InvitationProjects item) {
                // TODO Auto-generated method stub
                return item.getInvitationId().equals(ModuleHelper.id) && item.getProjectId().equals(ModuleHelper.projectId);
            }
        }));
        
        assertEquals(ret.getInvitationId(), new Integer(ModuleHelper.id));
        assertEquals(ret.getProjectId(), new Integer(ModuleHelper.projectId));
    }
    
    @Test
    public void testGetInvitationById() {
        Invitation ret = invitationManager.getInvitationById(ModuleHelper.id);
        verify(mockedInvitationMapper, times(1)).selectByPrimaryKey(Mockito.argThat(new ObjectMatcher<Integer>() {
            @Override
            public boolean verifymatches(Integer item) {
                // TODO Auto-generated method stub
                return item.equals(ModuleHelper.id);
            }
        }));
        assertSame(ret, invitation);
    }
    
    @Test
    public void DeleteInvitationById() {
        invitationManager.deleteInvitationById(ModuleHelper.id);
        verify(mockedInvitationMapper, times(1)).deleteByPrimaryKey(anyInt());
        verify(mockedInvitationProjectsMapper, times(1)).deleteByExample(Mockito.any(InvitationProjectsExample.class));
    }
    
    @Test
    public void testUpdateInvitationDate() {
        invitationManager.updateInvitationDate(invitation);
        verify(mockedInvitationMapper, times(1)).updateByPrimaryKey(Mockito.any(Invitation.class));
    }
    
    @Test
    public void testGetInvitationProjectsByInvitationId() {
        List<InvitationProjects> ret = invitationManager.getInvitationProjectsByInvitationId(ModuleHelper.id);
        verify(mockedInvitationProjectsMapper, times(1)).selectByExample(Mockito.any(InvitationProjectsExample.class));
        assertSame(ret, invitationProjects);
    }
    
    @Test
    public void testGetInvitationsBySample() {

        when(mockedInvitationMapper.selectByExample(Mockito.any(InvitationExample.class))).thenReturn(invitationList);
        
        List<Invitation> ret = invitationManager.getInvitationsBySample(invitation);
        verify(mockedInvitationMapper, times(1)).selectByExample(Mockito.any(InvitationExample.class));
        
        assertSame(ret, invitationList);
    }
}
