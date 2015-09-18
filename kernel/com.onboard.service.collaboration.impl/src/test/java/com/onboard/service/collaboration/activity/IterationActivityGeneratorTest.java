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
package com.onboard.service.collaboration.activity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Iteration;
import com.onboard.domain.model.Step;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.collaboration.IterationService;
import com.onboard.service.web.SessionService;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class IterationActivityGeneratorTest {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日 ");

    public static final String START_ITERATION_ACTION = "start";
    public static final String COMPLETE_ITERATION_ACTION = "complete";
    public static final String CREATE_ITERATION_SUBJECT = "创建了迭代";
    public static final String UPDATE_ITERATION_SUBJECT = "更新了迭代";
    public static final String START_ITERATION_SUBJECT = "开始了迭代";
    public static final String COMPLETE_ITERATION_SUBJECT = "完成了迭代";

    public static final String UPDATE_ITERATION_NAME_CONTENT = "名称由“%s”变为“%s”";
    public static final String UPDATE_ITERATION_STARTTIME_CONTENT = "开始时间由“%s”变为“%s”";
    public static final String UPDATE_ITERATION_ENDTIME_CONTENT = "截止时间由“%s”变为“%s”";
    public static final String START_ITERATION_CONTENT = "开始时间为“%s”，截止时间为“%s”";

    @Mock
    private IterationService iterationService;

    @Mock
    private SessionService sessionService;

    @InjectMocks
    private IterationActivityGenerator iterationActivityGenerator;

    private Iteration iteration;
    private Date startTime = new Date(2015010100);
    private Date endTime = new Date(2015010400);

    private Iteration getASampleIteration(String status) {

        Iteration iteration = new Iteration();
        iteration.setId(ModuleHelper.id);
        iteration.setProjectId(ModuleHelper.projectId);
        iteration.setCompanyId(ModuleHelper.companyId);
        iteration.setStartTime(startTime);
        iteration.setEndTime(endTime);
        iteration.setStatus(status);
        return iteration;
    }

    @Before
    public void setupBefore() throws Exception {
        iteration = getASampleIteration("created");
    }

    @After
    public void tearDownAfter() throws Exception {
    }

    @Test
    public void testModelType() {
        String type = iterationActivityGenerator.modelType();
        assertEquals(type, "iteration");
    }

    @Test
    public void testEnrichModel() {
        Step step = new Step();
        step.setId(ModuleHelper.id);

        when(iterationService.getById(ModuleHelper.id)).thenReturn(iteration);

        BaseProjectItem baseProjectItem = iterationActivityGenerator.enrichModel(step);

        verify(iterationService).getById(ModuleHelper.id);
        assertEquals((int) baseProjectItem.getId(), ModuleHelper.id);
        assertEquals((int) baseProjectItem.getProjectId(), ModuleHelper.projectId);

    }

    @Test
    public void testGenerateUpdateActivity1() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String target = String.format("迭代%s--%s", sdf.format(startTime), sdf.format(endTime));
        String content = String.format(START_ITERATION_CONTENT, dateFormat.format(startTime), dateFormat.format(endTime));
        User user = new User();
        user.setId(ModuleHelper.id);
        user.setName(ModuleHelper.name);
        user.setAvatar(ModuleHelper.creatorAvatar);

        Iteration modifiedIteration = getASampleIteration("active");

        when(sessionService.getCurrentUser()).thenReturn(user);
        Activity activity = iterationActivityGenerator.generateUpdateActivity(iteration, modifiedIteration);

        verify(sessionService, times(5)).getCurrentUser();
        assertEquals(content, activity.getContent());
        assertEquals((int) activity.getAttachId(), ModuleHelper.id);
        assertEquals(activity.getAttachType(), "iteration");
        assertEquals(activity.getSubject(), START_ITERATION_SUBJECT);
        assertEquals(activity.getAction(), START_ITERATION_ACTION);
        assertEquals((int) activity.getProjectId(), ModuleHelper.projectId);
        assertEquals((int) activity.getCompanyId(), ModuleHelper.companyId);
        assertEquals(activity.getTarget(), target);
        assertEquals((int) activity.getCreatorId(), ModuleHelper.id);
        assertEquals(activity.getCreatorName(), ModuleHelper.name);
    }

    @Test
    public void testGenerateUpdateActivity2() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String target = String.format("迭代%s--%s", sdf.format(startTime), sdf.format(endTime));

        User user = new User();
        user.setId(ModuleHelper.id);
        user.setName(ModuleHelper.name);

        Iteration iteration = getASampleIteration("active");
        Iteration modifiedIteration = getASampleIteration("completed");

        when(sessionService.getCurrentUser()).thenReturn(user);
        Activity activity = iterationActivityGenerator.generateUpdateActivity(iteration, modifiedIteration);

        verify(sessionService, times(2)).getCurrentUser();
        assertEquals((int) activity.getAttachId(), ModuleHelper.id);
        assertEquals(activity.getAttachType(), "iteration");
        assertEquals(activity.getSubject(), COMPLETE_ITERATION_SUBJECT);
        assertEquals(activity.getAction(), COMPLETE_ITERATION_ACTION);
        assertEquals((int) activity.getProjectId(), ModuleHelper.projectId);
        assertEquals((int) activity.getCompanyId(), ModuleHelper.companyId);
        assertEquals(activity.getTarget(), target);
        assertEquals((int) activity.getCreatorId(), ModuleHelper.id);
        assertEquals(activity.getCreatorName(), ModuleHelper.name);
    }

    @Test
    public void testGenerateUpdateActivity3() {
        Date endTime = new Date(2116010490);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String target = String.format("迭代%s--%s", sdf.format(startTime), sdf.format(endTime));
        User user = new User();
        user.setId(ModuleHelper.id);
        user.setName(ModuleHelper.name);

        Iteration iteration = getASampleIteration("completed");
        Iteration modifiedIteration = getASampleIteration("completed");
        modifiedIteration.setEndTime(endTime);
        String content = String.format(UPDATE_ITERATION_NAME_CONTENT, iteration.getName(), modifiedIteration.getName());

        when(sessionService.getCurrentUser()).thenReturn(user);
        Activity activity = iterationActivityGenerator.generateUpdateActivity(iteration, modifiedIteration);

        verify(sessionService, times(2)).getCurrentUser();
        assertEquals(content, activity.getContent());
        assertEquals((int) activity.getAttachId(), ModuleHelper.id);
        assertEquals(activity.getAttachType(), "iteration");
        assertEquals(activity.getSubject(), UPDATE_ITERATION_SUBJECT);
        assertEquals(activity.getAction(), "update");
        assertEquals((int) activity.getProjectId(), ModuleHelper.projectId);
        assertEquals((int) activity.getCompanyId(), ModuleHelper.companyId);
        assertEquals(activity.getTarget(), target);
        assertEquals((int) activity.getCreatorId(), ModuleHelper.id);
        assertEquals(activity.getCreatorName(), ModuleHelper.name);
    }

    @Test
    public void testGenerateUpdateActivity4() {
        Date startTime = new Date(2016010490);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String target = String.format("迭代%s--%s", sdf.format(startTime), sdf.format(endTime));
        User user = new User();
        user.setId(ModuleHelper.id);
        user.setName(ModuleHelper.name);

        Iteration iteration = getASampleIteration("completed");
        Iteration modifiedIteration = getASampleIteration("completed");
        modifiedIteration.setStartTime(startTime);
        String content = String.format(UPDATE_ITERATION_STARTTIME_CONTENT, dateFormat.format(iteration.getStartTime()),
                dateFormat.format(modifiedIteration.getStartTime()));

        when(sessionService.getCurrentUser()).thenReturn(user);
        Activity activity = iterationActivityGenerator.generateUpdateActivity(iteration, modifiedIteration);

        verify(sessionService, times(2)).getCurrentUser();
        assertEquals(content, activity.getContent());
        assertEquals((int) activity.getAttachId(), ModuleHelper.id);
        assertEquals(activity.getAttachType(), "iteration");
        assertEquals(activity.getSubject(), UPDATE_ITERATION_SUBJECT);
        assertEquals(activity.getAction(), "update");
        assertEquals((int) activity.getProjectId(), ModuleHelper.projectId);
        assertEquals((int) activity.getCompanyId(), ModuleHelper.companyId);
        assertEquals(activity.getTarget(), target);
        assertEquals((int) activity.getCreatorId(), ModuleHelper.id);
        assertEquals(activity.getCreatorName(), ModuleHelper.name);
    }

    @Test
    public void testGenerateUpdateActivity5() {
        Date startTime = new Date(2016010490);
        Date endTime = new Date(2016010490);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String target = String.format("迭代%s--%s", sdf.format(startTime), sdf.format(endTime));
        User user = new User();
        user.setId(ModuleHelper.id);
        user.setName(ModuleHelper.name);

        Iteration iteration = getASampleIteration("completed");
        Iteration modifiedIteration = getASampleIteration("completed");
        modifiedIteration.setStartTime(startTime);
        modifiedIteration.setEndTime(endTime);

        String content = String.format(UPDATE_ITERATION_STARTTIME_CONTENT, dateFormat.format(iteration.getStartTime()),
                dateFormat.format(modifiedIteration.getStartTime()))
                + "，"
                + String.format(UPDATE_ITERATION_ENDTIME_CONTENT, dateFormat.format(iteration.getEndTime()),
                        dateFormat.format(modifiedIteration.getEndTime()));

        when(sessionService.getCurrentUser()).thenReturn(user);
        Activity activity = iterationActivityGenerator.generateUpdateActivity(iteration, modifiedIteration);

        verify(sessionService, times(2)).getCurrentUser();
        assertEquals(content, activity.getContent());
        assertEquals((int) activity.getAttachId(), ModuleHelper.id);
        assertEquals(activity.getAttachType(), "iteration");
        assertEquals(activity.getSubject(), UPDATE_ITERATION_SUBJECT);
        assertEquals(activity.getAction(), "update");
        assertEquals((int) activity.getProjectId(), ModuleHelper.projectId);
        assertEquals((int) activity.getCompanyId(), ModuleHelper.companyId);
        assertEquals(activity.getTarget(), target);
        assertEquals((int) activity.getCreatorId(), ModuleHelper.id);
        assertEquals(activity.getCreatorName(), ModuleHelper.name);
    }

    @Test
    public void testGenerateUpdateActivity6() {
        User user = new User();
        user.setId(ModuleHelper.id);
        user.setName(ModuleHelper.name);
        Iteration iteration = getASampleIteration("completed");
        Iteration modifiedIteration = getASampleIteration("completed");

        when(sessionService.getCurrentUser()).thenReturn(user);
        Activity activity = iterationActivityGenerator.generateUpdateActivity(iteration, modifiedIteration);

        verify(sessionService, times(0)).getCurrentUser();

        assertNull(activity);
    }
}
