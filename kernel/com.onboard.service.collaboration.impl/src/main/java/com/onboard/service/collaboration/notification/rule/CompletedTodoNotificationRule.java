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
package com.onboard.service.collaboration.notification.rule;

import org.springframework.stereotype.Service;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.IterationItemStatus;
import com.onboard.domain.model.type.Subscribable;
import com.onboard.service.notification.NotificationRule;
import com.onboard.service.notification.SimpleNotificationRule;

/**
 * 表示{@link Todo}完成对应的{@link NotificationRule}
 * 
 * @author yewei
 * 
 */
@Service("completedTodoNotificationRuleBean")
public class CompletedTodoNotificationRule extends SimpleNotificationRule {

    @Override
    public boolean ifNotify(Activity activity, Subscribable subscribable) {
        return activity.getAction().equals(IterationItemStatus.CLOSED);
    }

    @Override
    public String modelType() {
        return new Todo().getType();
    }

}
