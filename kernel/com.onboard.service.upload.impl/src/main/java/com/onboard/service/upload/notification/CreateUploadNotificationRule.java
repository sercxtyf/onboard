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
package com.onboard.service.upload.notification;

import org.springframework.stereotype.Service;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Upload;
import com.onboard.domain.model.type.Subscribable;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.service.notification.SimpleNotificationRule;

@Service("createUploadNotificationRuleBean")
public class CreateUploadNotificationRule extends SimpleNotificationRule {

    @Override
    public String modelType() {
        return new Upload().getType();
    }

    @Override
    public boolean ifNotify(Activity activity, Subscribable subscribable) {
        return activity.getAction().equals(ActivityActionType.CREATE);
    }

}
