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
package com.onboard.service.activity.impl.util.test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.activity.ActivityHook;
import com.onboard.service.activity.SynchronizedActivityHook;
import com.onboard.service.activity.impl.util.ActivityHookHelper;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class ActivityHookHelperTest {

    private ActivityHook activityHook;
    private SynchronizedActivityHook synchronizedActivityHook;

    private List<ActivityHook> activityHooks;
    private List<SynchronizedActivityHook> synchronizedActivityHooks;

    private Activity activity;

    private User user;

    @InjectMocks
    private ActivityHookHelper activityHookHelper;

    @Before
    public void setUpBefore() throws Throwable {
        activityHook = ModuleHelper.getASampleActivityHook();
        synchronizedActivityHook = ModuleHelper.getASampleSynchronizedActivityHook();
        activityHooks = new ArrayList<ActivityHook>();
        synchronizedActivityHooks = new ArrayList<SynchronizedActivityHook>();
        activityHooks.add(activityHook);
        synchronizedActivityHooks.add(synchronizedActivityHook);
        activity = ModuleHelper.getASampleActivity();
        user = ModuleHelper.getASampleUser();
    }

    @After
    public void tearDownAfter() throws Exception {
    }

    @Test
    public void callSynchronizedActivityHookForCreateThrowsThrowable() throws Throwable {
        doThrow(new RuntimeException()).when(synchronizedActivityHook).whenCreationActivityCreated(any(Activity.class),
                any(BaseProjectItem.class));
        activityHookHelper.callOneSynchronizedActivityHook(synchronizedActivityHooks.get(0), activity,
                ModuleHelper.getASampleTodo(), null);

        verify(synchronizedActivityHook).whenCreationActivityCreated(any(Activity.class), any(BaseProjectItem.class));
        verify(synchronizedActivityHook, times(0)).whenUpdateActivityCreated(any(Activity.class), any(BaseProjectItem.class),
                any(BaseProjectItem.class));
    }

    @Test
    public void callSynchronizedActivityHookForUpdateThrowsThrowable() throws Throwable {
        doThrow(new RuntimeException()).when(synchronizedActivityHook).whenUpdateActivityCreated(any(Activity.class),
                any(BaseProjectItem.class), any(BaseProjectItem.class));
        activityHookHelper.callOneSynchronizedActivityHook(synchronizedActivityHooks.get(0), activity,
                ModuleHelper.getASampleTodo(),
                ModuleHelper.getASampleTodo());

        verify(synchronizedActivityHook, times(0)).whenCreationActivityCreated(any(Activity.class), any(BaseProjectItem.class));
        verify(synchronizedActivityHook).whenUpdateActivityCreated(any(Activity.class), any(BaseProjectItem.class),
                any(BaseProjectItem.class));
    }

    @Test
    public void callSynchronizedActivityHookForCreate() throws Throwable {
        activityHookHelper.callOneSynchronizedActivityHook(synchronizedActivityHooks.get(0), activity,
                ModuleHelper.getASampleTodo(), null);
        verify(synchronizedActivityHook).whenCreationActivityCreated(any(Activity.class), any(BaseProjectItem.class));
        verify(synchronizedActivityHook, times(0)).whenUpdateActivityCreated(any(Activity.class), any(BaseProjectItem.class),
                any(BaseProjectItem.class));
    }

    @Test
    public void callSynchronizedActivityHookForUpdate() throws Throwable {
        activityHookHelper.callOneSynchronizedActivityHook(synchronizedActivityHooks.get(0), activity,
                ModuleHelper.getASampleTodo(),
                ModuleHelper.getASampleTodo());
        verify(synchronizedActivityHook, times(0)).whenCreationActivityCreated(any(Activity.class), any(BaseProjectItem.class));
        verify(synchronizedActivityHook).whenUpdateActivityCreated(any(Activity.class), any(BaseProjectItem.class),
                any(BaseProjectItem.class));
    }


    @Test
    public void callAsynchronizedActivityHookForCreateThrowsThrowable() throws Throwable {
        doThrow(new RuntimeException()).when(activityHook).whenCreationActivityCreated(any(User.class), any(Activity.class),
                any(BaseProjectItem.class));
        activityHookHelper.callOneAsynchronizedActivityHook(activityHooks.get(0), user, activity, ModuleHelper.getASampleTodo(),
                null);

        verify(activityHook).whenCreationActivityCreated(any(User.class), any(Activity.class), any(BaseProjectItem.class));
        verify(activityHook, times(0)).whenUpdateActivityCreated(any(User.class), any(Activity.class), any(BaseProjectItem.class),
                any(BaseProjectItem.class));
    }

    @Test
    public void callAsynchronizedActivityHookForUpdateThrowsThrowable() throws Throwable {
        doThrow(new RuntimeException()).when(activityHook).whenUpdateActivityCreated(any(User.class), any(Activity.class),
                any(BaseProjectItem.class), any(BaseProjectItem.class));
        activityHookHelper.callOneAsynchronizedActivityHook(activityHooks.get(0), user, activity, ModuleHelper.getASampleTodo(),
                ModuleHelper.getASampleTodo());

        verify(activityHook, times(0)).whenCreationActivityCreated(any(User.class), any(Activity.class), any(BaseProjectItem.class));
        verify(activityHook).whenUpdateActivityCreated(any(User.class), any(Activity.class), any(BaseProjectItem.class),
                any(BaseProjectItem.class));
    }

    @Test
    public void callAsynchronizedActivityHookForCreate() throws Throwable {
        activityHookHelper.callOneAsynchronizedActivityHook(activityHooks.get(0), user, activity, ModuleHelper.getASampleTodo(),
                null);
        verify(activityHook).whenCreationActivityCreated(any(User.class), any(Activity.class), any(BaseProjectItem.class));
        verify(activityHook, times(0)).whenUpdateActivityCreated(any(User.class), any(Activity.class), any(BaseProjectItem.class),
                any(BaseProjectItem.class));
    }

    @Test
    public void callAsynchronizedActivityHookForUpdate() throws Throwable {
        activityHookHelper.callOneAsynchronizedActivityHook(activityHooks.get(0), user, activity, ModuleHelper.getASampleTodo(),
                ModuleHelper.getASampleTodo());
        verify(activityHook, times(0)).whenCreationActivityCreated(any(User.class), any(Activity.class), any(BaseProjectItem.class));
        verify(activityHook).whenUpdateActivityCreated(any(User.class), any(Activity.class), any(BaseProjectItem.class),
                any(BaseProjectItem.class));
    }

}
