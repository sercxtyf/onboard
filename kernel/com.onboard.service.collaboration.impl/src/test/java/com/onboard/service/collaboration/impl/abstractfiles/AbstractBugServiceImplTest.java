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
package com.onboard.service.collaboration.impl.abstractfiles;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.mapper.BugMapper;
import com.onboard.domain.mapper.model.BugExample;
import com.onboard.domain.model.Bug;
import com.onboard.domain.model.Bug.BugPriorities;
import com.onboard.domain.model.Bug.BugStatus;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractBugServiceImplTest {
    @Mock
    public BugMapper mockBugMapper;

    protected Bug bug;
    protected List<Bug> listOfBugs;
    protected static int mapperReturnValue = 1;
    protected static int DEFAULT_LIMIT = -1;
    protected static int newUserId = 123;

    @Before
    public void setupCollectionTest() {
        initBugMapper();
    }

    private void initBugMapper() {

        bug = getASampleBug();
        listOfBugs = getAListofBugs();

        when(mockBugMapper.countByExample(Mockito.any(BugExample.class))).thenReturn(ModuleHelper.count);

        when(mockBugMapper.deleteByExample(Mockito.any(BugExample.class))).thenReturn(mapperReturnValue);
        when(mockBugMapper.deleteByPrimaryKey(ModuleHelper.id)).thenReturn(mapperReturnValue);

        when(mockBugMapper.insert(Mockito.any(Bug.class))).thenReturn(mapperReturnValue);
        when(mockBugMapper.insertSelective(Mockito.any(Bug.class))).thenReturn(mapperReturnValue);

        when(mockBugMapper.selectByExample(Mockito.any(BugExample.class))).thenReturn(listOfBugs);
        when(mockBugMapper.selectByPrimaryKey(ModuleHelper.id)).thenReturn(bug);

        when(mockBugMapper.updateByExample(Mockito.any(Bug.class), Mockito.any(BugExample.class))).thenReturn(mapperReturnValue);
        when(mockBugMapper.updateByExampleSelective(Mockito.any(Bug.class), Mockito.any(BugExample.class))).thenReturn(
                mapperReturnValue);
        when(mockBugMapper.updateByPrimaryKey(Mockito.any(Bug.class))).thenReturn(mapperReturnValue);
        when(mockBugMapper.updateByPrimaryKeySelective(Mockito.any(Bug.class))).thenReturn(mapperReturnValue);
    }

    public List<Bug> getAListofBugs() {
        List<Bug> list = new ArrayList<Bug>();
        list.add(getASampleBug());
        list.add(getASampleBug());
        return list;
    }

    public Bug getASampleBug() {
        Bug bug = new Bug();
        bug.setAssignee(ModuleHelper.getASampleUser());
        bug.setAssigneeId(ModuleHelper.assignId);
        bug.setCompanyId(ModuleHelper.companyId);
        bug.setCompleted(false);
        bug.setCompletedTime(ModuleHelper.completed);
        bug.setCreatedTime(ModuleHelper.created);
        bug.setCreatorId(ModuleHelper.creatorId);
        bug.setCreatorName(ModuleHelper.creatorName);
        bug.setDeleted(false);
        bug.setDueTime(ModuleHelper.dueTime);
        bug.setId(ModuleHelper.id);
        bug.setIdInProject(ModuleHelper.idInProject);
        bug.setDescription(ModuleHelper.description);
        bug.setPriority(BugPriorities.BLOCKER.getValue());
        bug.setProjectId(ModuleHelper.projectId);
        bug.setStatus(BugStatus.TODO.getValue());
        bug.setTitle(ModuleHelper.title);

        return bug;
    }
}
