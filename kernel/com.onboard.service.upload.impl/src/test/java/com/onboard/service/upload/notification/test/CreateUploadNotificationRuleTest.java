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
package com.onboard.service.upload.notification.test;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.type.Subscribable;
import com.onboard.service.upload.notification.CreateUploadNotificationRule;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class CreateUploadNotificationRuleTest {

    @InjectMocks
    private CreateUploadNotificationRule createUploadNotificationRule;

    @Mock
    private Subscribable subscribable;

    private static final String CREATE = "create";
    private static final String NOTCREATE = "not-create";
    private static int id = 1;
    private static int companyId = 3;

    private Activity getASampleActivity(String action) {
        Activity activity = new Activity();
        activity.setAction(action);
        activity.setId(id);
        activity.setCompanyId(companyId);

        return activity;
    }

    @Before
    public void setUpBefore() throws Exception {
        CreateUploadNotificationRule createUploadNotificationRuleSpy = Mockito.spy(createUploadNotificationRule);
        Mockito.doReturn(ModuleHelper.type).when(createUploadNotificationRuleSpy).modelType();
    }

    @After
    public void tearDownAfter() throws Exception {
    }

    @Test
    public void testIfNotifyFirstBranch() {
        CreateUploadNotificationRule createUploadNotificationRuleSpy = Mockito.spy(createUploadNotificationRule);
        Boolean ifNotify = createUploadNotificationRuleSpy.ifNotify(getASampleActivity(CREATE), subscribable);

        assertEquals(ifNotify, Boolean.valueOf(true));
    }

    @Test
    public void testIfNotifySecondBranch() {
        CreateUploadNotificationRule createUploadNotificationRuleSpy = Mockito.spy(createUploadNotificationRule);
        Boolean ifNotify = createUploadNotificationRuleSpy.ifNotify(getASampleActivity(NOTCREATE), subscribable);

        assertEquals(ifNotify, Boolean.valueOf(false));
    }
}
