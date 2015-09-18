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
//package com.onboard.service.collaboration.activity;
//
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.runners.MockitoJUnitRunner;
//
//import com.onboard.domain.model.Activity;
//import com.onboard.domain.model.Project;
//import com.onboard.service.account.UserService;
//import com.onboard.service.collaboration.ProjectService;
//import com.onboard.service.web.SessionService;
//import com.onboard.test.moduleutils.ModuleHelper;
//
//@RunWith(MockitoJUnitRunner.class)
//public class ActivityRecorderHelperTest {
//    public static final String PROJECT_MOVE = "从项目“%s”搬运到项目“%s”";
//
//    public static final String USER_REMOVE = "将%s移出了项目“%s”";
//
//    public static final String USER_REMOVE_SUBJECT = "更新了项目成员";
//    @Mock
//    private ProjectService projectService;
//
//    @Mock
//    private SessionService sessionService;
//
//    @Mock
//    private UserService userService;
//
//    @InjectMocks
//    private ActivityRecorderHelper activityRecorderHelper;
//
//    private Project project;
//
//    private Project getASampleProject() {
//        Project project = new Project();
//
//        project.setId(ModuleHelper.id);
//        project.setName(ModuleHelper.projectName);
//
//        return project;
//    }
//
//    @Before
//    public void setUpBefore() throws Exception {
//        project = this.getASampleProject();
//        when(projectService.getById(ModuleHelper.id)).thenReturn(project);
//    }
//
//    @After
//    public void tearDownAfter() throws Exception {
//    }
//
//    @Test
//    public void testSetupMoveInformation1() {
//        Activity activity = Mockito.mock(Activity.class);
//        doNothing().when(activity).setContent(PROJECT_MOVE);
//
//        ActivityRecorderHelper.setupMoveInformation(ModuleHelper.id, ModuleHelper.id, activity);
//        
//        verify(projectService, times(0)).getById(ModuleHelper.id);
//        verify(activity, times(0)).setContent(String.format(PROJECT_MOVE, ModuleHelper.projectName, ModuleHelper.projectName));
//    }
//    
//    @Test
//    public void testSetupMoveInformation2() {
//        Activity activity = Mockito.mock(Activity.class);
//        doNothing().when(activity).setContent(PROJECT_MOVE);
//
//        activityRecorderHelper.setupMoveInformation(ModuleHelper.id, ModuleHelper.id, activity);
//        
//        verify(projectService, times(2)).getById(ModuleHelper.id);
//        verify(activity, times(0)).setContent(String.format(PROJECT_MOVE, ModuleHelper.projectName, ModuleHelper.projectName));
//    }
//
// }
