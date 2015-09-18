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
package com.onboard.service.activity.impl.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.mapper.ActivityMapper;
import com.onboard.domain.mapper.ProjectMapper;
import com.onboard.domain.mapper.model.ActivityExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.activity.ActivityGenerator;
import com.onboard.service.activity.ActivityHook;
import com.onboard.service.activity.ActivityService;
import com.onboard.service.activity.SynchronizedActivityHook;
import com.onboard.service.activity.impl.ActivityRecorderImpl;
import com.onboard.service.activity.impl.util.ActivityHookHelper;
import com.onboard.service.web.SessionService;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class ActivityRecorderImplTest {

    @Mock
    private ActivityMapper mockActivityMapper;

    @Mock
    private ProjectMapper mockProjectMapper;

    @Mock
    private SessionService mockSession;

    @Mock
    private ActivityHookHelper activityHookHelper;

    @InjectMocks
    private ActivityRecorderImpl activityRecorderImpl;

    private Activity activity;
    private Project project;
    private User user;
    private BaseProjectItem identifiable;
    private BaseProjectItem original;
    private ActivityGenerator activityGenerator;
    private ProceedingJoinPoint joinpoint;

    private ActivityHook activityHook;

    private ProceedingJoinPoint getASampleProceedingJoinPoint() throws Throwable {
        ProceedingJoinPoint p = mock(ProceedingJoinPoint.class);
        when(p.getArgs()).thenReturn(new Object[] { original });
        when(p.proceed()).thenReturn(identifiable);
        ActivityService object = mock(ActivityService.class);
        when(p.getTarget()).thenReturn(object);
        return p;
    }

    private ActivityGenerator getASampleActivityGenerator() {
        ActivityGenerator activityGenerator = mock(ActivityGenerator.class);
        when(activityGenerator.enrichModel(any(BaseProjectItem.class))).thenReturn(original);
        when(activityGenerator.generateCreateActivity(any(BaseProjectItem.class))).thenReturn(activity);
        when(activityGenerator.generateUpdateActivity(any(BaseProjectItem.class), any(BaseProjectItem.class))).thenReturn(activity);
        when(activityGenerator.modelService()).thenReturn(ModuleHelper.modelService);
        when(activityGenerator.modelType()).thenReturn(ModuleHelper.attachType);
        return activityGenerator;
    }

    private Activity getASampleActivity() {
        Activity a = mock(Activity.class);
        doNothing().when(a).setCreatorName(anyString());
        doNothing().when(a).setCreatorId(anyInt());
        doNothing().when(a).setCreated(any(Date.class));
        doNothing().when(a).setTarget(anyString());
        doNothing().when(a).setProjectName(anyString());
        when(a.getId()).thenReturn(ModuleHelper.activityId);
        when(a.getAction()).thenReturn(ModuleHelper.action);
        when(a.getAttachId()).thenReturn(ModuleHelper.attachId);
        when(a.getAttachType()).thenReturn(ModuleHelper.attachType);
        when(a.getCompanyId()).thenReturn(ModuleHelper.companyId);
        when(a.getContent()).thenReturn(ModuleHelper.content);
        when(a.getCreated()).thenReturn(ModuleHelper.created);
        when(a.getCreatorId()).thenReturn(ModuleHelper.userId);
        when(a.getCreatorName()).thenReturn(ModuleHelper.userName);
        when(a.getProjectId()).thenReturn(ModuleHelper.projectId);
        // ActivityRecorderImpl填充该字段
        when(a.getProjectName()).thenReturn(null);
        when(a.getSubject()).thenReturn(ModuleHelper.subject);
        when(a.getTarget()).thenReturn(ModuleHelper.target);
        return a;
    }

    @Before
    public void setUpBefore() throws Throwable {
        activity = getASampleActivity();
        project = ModuleHelper.getASampleProject();
        user = ModuleHelper.getASampleUser();
        identifiable = ModuleHelper.getASampleIdentifiable();
        original = ModuleHelper.getASampleIdentifiable();
        activityGenerator = getASampleActivityGenerator();
        activityHook = ModuleHelper.getASampleActivityHook();
        joinpoint = getASampleProceedingJoinPoint();

        activityRecorderImpl.addActivityGenerator(activityGenerator);
        activityRecorderImpl.addActivityHook(activityHook);

        when(mockActivityMapper.deleteByExample(any(ActivityExample.class))).thenReturn(ModuleHelper.activityId);
        when(mockActivityMapper.insert(any(Activity.class))).thenReturn(ModuleHelper.activityId);
        when(mockSession.getCurrentUser()).thenReturn(user);
        when(mockProjectMapper.selectByPrimaryKey(ModuleHelper.projectId)).thenReturn(project);
    }

    @After
    public void tearDownAfter() throws Exception {
        activityRecorderImpl.removeActivityGenerator(activityGenerator);
        activityRecorderImpl.removeActivityHook(activityHook);
    }

    /**
     * enrichActivity
     */
    private void mockEnrichActivityWithUserIdNull() {
        User u = new User();
        when(mockSession.getCurrentUser()).thenReturn(u);
    }

    private void mockEnrichActivityWithActivityProjectIdNull() {
        when(activity.getProjectId()).thenReturn(null);
    }

    private void mockEnrichActivityWithProjectNull() {
        when(mockProjectMapper.selectByPrimaryKey(anyInt())).thenReturn(null);
    }

    private void verifyEnrichActivity() {
        verify(mockProjectMapper).selectByPrimaryKey(ModuleHelper.projectId);
        verifyActivitySetWithProjectName();
    }

    private void verifyEnrichActivityWithUserIdNull() {
        verify(mockProjectMapper, times(0)).selectByPrimaryKey(ModuleHelper.projectId);
        verifyActivitySetNotCalled();
    }

    private void verifyEnrichActivityWithProjectNull() {
        verify(mockProjectMapper).selectByPrimaryKey(ModuleHelper.projectId);
        verifyActivitySetWithoutProjectName();
    }

    private void verifyEnrichActivityWithActivityProjectName() {
        verify(mockProjectMapper, times(0)).selectByPrimaryKey(ModuleHelper.projectId);
        verifyActivitySetWithoutProjectName();
    }

    private void verifyEnrichActivityNotCalled() {
        verify(mockSession, times(0)).getCurrentUser();
        verify(mockProjectMapper, times(0)).selectByPrimaryKey(anyInt());
        verifyActivitySetNotCalled();
    }

    private void verifyActivitySetWithoutProjectName() {
        verify(activity).setCreatorName(anyString());
        verify(activity).setCreatorId(anyInt());
        verify(activity).setCreated(any(Date.class));
        verify(activity).setTarget(anyString());
        verify(activity, times(0)).setProjectName(anyString());
    }

    private void verifyActivitySetNotCalled() {
        verify(activity, times(0)).setCreatorName(anyString());
        verify(activity, times(0)).setCreatorId(anyInt());
        verify(activity, times(0)).setCreated(any(Date.class));
        verify(activity, times(0)).setTarget(anyString());
        verify(activity, times(0)).setProjectName(anyString());
    }

    private void verifyActivitySetWithProjectName() {
        verify(activity).setCreatorName(anyString());
        verify(activity).setCreatorId(anyInt());
        verify(activity).setCreated(any(Date.class));
        verify(activity).setTarget(anyString());
        verify(activity).setProjectName(anyString());
    }

    private void verifyCallActivityHookWithCreation() {
        verify(activityHookHelper, times(0)).callOneSynchronizedActivityHook(any(SynchronizedActivityHook.class),
                any(Activity.class), any(BaseProjectItem.class), isNull(BaseProjectItem.class));
        verify(activityHookHelper, times(1)).callOneAsynchronizedActivityHook(any(ActivityHook.class), any(User.class),
                any(Activity.class), any(BaseProjectItem.class), isNull(BaseProjectItem.class));
    }

    private void verifyCallActivityHookWithUpdate() {
        verify(activityHookHelper, times(0)).callOneSynchronizedActivityHook(any(SynchronizedActivityHook.class),
                any(Activity.class), any(BaseProjectItem.class), any(BaseProjectItem.class));
        verify(activityHookHelper, times(1)).callOneAsynchronizedActivityHook(any(ActivityHook.class), any(User.class),
                any(Activity.class), any(BaseProjectItem.class), any(BaseProjectItem.class));
    }

    private void verifyCallActivityHookWithCreationNotCalled() {
        verify(activityHookHelper, times(0)).callOneSynchronizedActivityHook(any(SynchronizedActivityHook.class),
                any(Activity.class), any(BaseProjectItem.class), isNull(BaseProjectItem.class));
        verify(activityHookHelper, times(0)).callOneAsynchronizedActivityHook(any(ActivityHook.class), any(User.class),
                any(Activity.class), any(BaseProjectItem.class), isNull(BaseProjectItem.class));
    }

    private void verifyCallActivityHookWithUpdateNotCalled() {
        verify(activityHookHelper, times(0)).callOneSynchronizedActivityHook(any(SynchronizedActivityHook.class),
                any(Activity.class), any(BaseProjectItem.class), isNull(BaseProjectItem.class));
        verify(activityHookHelper, times(0)).callOneAsynchronizedActivityHook(any(ActivityHook.class), any(User.class),
                any(Activity.class), any(BaseProjectItem.class), isNull(BaseProjectItem.class));
    }

    @Test
    public void recordCreationActivity() throws Throwable {
        // mock
        // test for activityRecorderImpl remove null
        activityRecorderImpl.removeActivityGenerator(null);

        // run
        activityRecorderImpl.recordCreationActivity(identifiable);

        // verify
        verify(activityGenerator).generateCreateActivity(identifiable);
        this.verifyEnrichActivity();
        verify(mockActivityMapper).insert(activity);
        verifyCallActivityHookWithCreation();
    }

    @Ignore
    public void recordCreationActivityWithActivityName() throws Throwable {
        // mock
        when(activity.getProjectName()).thenReturn(ModuleHelper.projectName);

        // run
        activityRecorderImpl.recordCreationActivity(identifiable);

        // verify
        verify(activityGenerator).generateCreateActivity(identifiable);
        this.verifyEnrichActivityWithActivityProjectName();
        verify(mockActivityMapper).insert(activity);
        verifyCallActivityHookWithCreation();
    }

    @Ignore
    public void recordCreationActivityWithProjectNull() throws Throwable {
        // mock
        mockEnrichActivityWithProjectNull();

        // run
        activityRecorderImpl.recordCreationActivity(identifiable);

        // verify
        verify(activityGenerator).generateCreateActivity(identifiable);
        verifyEnrichActivityWithProjectNull();
        verify(mockActivityMapper).insert(activity);
        verifyCallActivityHookWithCreation();
    }

    @Test
    public void recordCreationActivityWithActivityGeneratorNull() throws Throwable {
        activityRecorderImpl.removeActivityGenerator(activityGenerator);
        // run
        activityRecorderImpl.recordCreationActivity(identifiable);

        // verify
        verify(activityGenerator, times(0)).generateCreateActivity(identifiable);
        this.verifyEnrichActivityNotCalled();
        verify(mockActivityMapper, times(0)).insert(activity);
        verifyCallActivityHookWithCreationNotCalled();
    }

    @Test
    public void recordCreationActivityWithActivityNull() throws Throwable {
        // mock
        when(activityGenerator.generateCreateActivity(any(BaseProjectItem.class))).thenReturn(null);

        // run
        activityRecorderImpl.recordCreationActivity(identifiable);

        // verify
        verify(activityGenerator).generateCreateActivity(identifiable);
        this.verifyEnrichActivityNotCalled();
        verify(mockActivityMapper, times(0)).insert(activity);
        verifyCallActivityHookWithCreationNotCalled();
    }

    @Test
    public void recordCreationActivityWithActivityRecorderException() throws Throwable {
        // mock
        this.mockEnrichActivityWithUserIdNull();

        // run
        activityRecorderImpl.recordCreationActivity(identifiable);

        // verify
        verify(activityGenerator).generateCreateActivity(identifiable);
        this.verifyEnrichActivityWithUserIdNull();
        verify(mockActivityMapper, times(0)).insert(activity);
        verifyCallActivityHookWithCreationNotCalled();
    }

    @Test
    public void recordUpdateActivity() throws Throwable {
        // mock

        // run
        Object result = activityRecorderImpl.recordUpdateActivity(joinpoint);

        // verify
        verify(activityGenerator, times(2)).enrichModel(any(BaseProjectItem.class));
        verify(joinpoint).proceed();
        verify(activityGenerator).generateUpdateActivity(any(BaseProjectItem.class), any(BaseProjectItem.class));
        this.verifyEnrichActivity();
        verify(mockActivityMapper).insert(activity);
        verifyCallActivityHookWithUpdate();
        assertEquals(identifiable.getId(), ((BaseProjectItem) result).getId());
    }

    @Test
    public void recordUpdateActivityWithOriginalNull() throws Throwable {
        // mock
        when(joinpoint.getArgs()).thenReturn(new Object[] { null });

        // run
        Object result = activityRecorderImpl.recordUpdateActivity(joinpoint);

        // verify
        verify(activityGenerator, times(0)).enrichModel(original);
        verify(joinpoint, times(0)).proceed();
        verify(activityGenerator, times(0)).generateUpdateActivity(identifiable, identifiable);
        this.verifyEnrichActivityNotCalled();
        verify(mockActivityMapper, times(0)).insert(activity);
        verifyCallActivityHookWithUpdateNotCalled();
        assertEquals(null, result);
    }

    @Test
    public void recordUpdateActivityWithActivityGeneratorNull() throws Throwable {
        // mock
        activityRecorderImpl.removeActivityGenerator(activityGenerator);

        // run
        Object result = activityRecorderImpl.recordUpdateActivity(joinpoint);

        // verify
        verify(activityGenerator, times(0)).enrichModel(original);
        verify(joinpoint).proceed();
        verify(activityGenerator, times(0)).generateUpdateActivity(identifiable, identifiable);
        this.verifyEnrichActivityNotCalled();
        verify(mockActivityMapper, times(0)).insert(activity);
        verifyCallActivityHookWithUpdateNotCalled();
        assertEquals(identifiable, result);
    }

    @Test
    public void recordUpdateActivityWithActivityNull() throws Throwable {
        // mock
        when(activityGenerator.generateUpdateActivity(any(BaseProjectItem.class), any(BaseProjectItem.class))).thenReturn(null);

        // run
        BaseProjectItem result = (BaseProjectItem) activityRecorderImpl.recordUpdateActivity(joinpoint);

        // verify
        verify(activityGenerator).enrichModel(original);
        verify(joinpoint).proceed();
        verify(activityGenerator).generateUpdateActivity(any(BaseProjectItem.class), any(BaseProjectItem.class));
        this.verifyEnrichActivityNotCalled();
        verify(mockActivityMapper, times(0)).insert(activity);
        verifyCallActivityHookWithUpdateNotCalled();
        assertEquals(identifiable.getId(), result.getId());
    }

    @Test
    public void recordUpdateActivityWithInsertThrowable() throws Throwable {
        // mock
        when(mockActivityMapper.insert(activity)).thenThrow(new RuntimeException());

        // run
        BaseProjectItem result = (BaseProjectItem) activityRecorderImpl.recordUpdateActivity(joinpoint);

        // verify
        verify(activityGenerator).enrichModel(original);
        verify(joinpoint).proceed();
        verify(activityGenerator).generateUpdateActivity(any(BaseProjectItem.class), any(BaseProjectItem.class));
        this.verifyEnrichActivity();
        verify(mockActivityMapper).insert(activity);
        verifyCallActivityHookWithUpdateNotCalled();
        assertEquals(original.getId(), result.getId());
    }

    @Test
    public void recordUpdateActivityWithActivityRecorderException() throws Throwable {
        // mock
        this.mockEnrichActivityWithActivityProjectIdNull();

        // run
        BaseProjectItem result = (BaseProjectItem) activityRecorderImpl.recordUpdateActivity(joinpoint);

        // verify
        verify(activityGenerator).enrichModel(original);
        verify(joinpoint).proceed();
        verify(activityGenerator).generateUpdateActivity(any(BaseProjectItem.class), any(BaseProjectItem.class));
        this.verifyEnrichActivityWithUserIdNull();
        verify(mockActivityMapper, times(0)).insert(activity);
        verifyCallActivityHookWithUpdateNotCalled();
        assertEquals(original.getId(), result.getId());
    }

    @Test
    public void deleteActivity() throws Throwable {
        // mock
        when(mockActivityMapper.deleteByExample(any(ActivityExample.class))).thenReturn(ModuleHelper.activityId);
        when(joinpoint.getArgs()).thenReturn(new Object[] { ModuleHelper.attachId });

        // run
        Object result = activityRecorderImpl.deleteActivity(joinpoint);

        // verify
        verify(mockActivityMapper).deleteByExample(argThat(new ExampleMatcher<ActivityExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "attachType", ModuleHelper.attachType)
                        && CriterionVerifier.verifyEqualTo(example, "attachId", ModuleHelper.attachId);
            }
        }));
        verify(joinpoint).proceed();
        assertEquals(identifiable, result);
    }

    @Test
    public void deleteActivityWithModelTypeNull() throws Throwable {
        // mock
        activityRecorderImpl.removeActivityGenerator(activityGenerator);
        when(joinpoint.getArgs()).thenReturn(new Object[] { ModuleHelper.attachId });

        // run
        Object result = activityRecorderImpl.deleteActivity(joinpoint);

        // verify
        verify(mockActivityMapper, times(0)).deleteByExample(any(ActivityExample.class));
        verify(joinpoint).proceed();
        assertEquals(identifiable, result);
    }

    @Test
    public void deleteActivityThrowable() throws Throwable {
        // mock
        when(mockActivityMapper.deleteByExample(any(ActivityExample.class))).thenReturn(ModuleHelper.activityId);
        when(joinpoint.getArgs()).thenReturn(new Object[] { ModuleHelper.attachId });
        when(joinpoint.proceed()).thenThrow(new RuntimeException());

        // run
        Object result = activityRecorderImpl.deleteActivity(joinpoint);

        // verify
        verify(mockActivityMapper).deleteByExample(any(ActivityExample.class));
        verify(joinpoint).proceed();
        assertEquals(null, result);
    }
}
