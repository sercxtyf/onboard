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
package com.onboard.service.notification;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.type.Subscribable;

public abstract class SimpleNotificationMethod implements NotificationMethod {

    @Override
    public void notifySubsribers(Activity activity, Subscribable item) {
        // 什么都不干，由继承者自己实现
        return;
    }

    @Override
    public void notifySubsribers(Activity activity, Subscribable original, Subscribable updated) {
        notifySubsribers(activity, updated);
    }

}
