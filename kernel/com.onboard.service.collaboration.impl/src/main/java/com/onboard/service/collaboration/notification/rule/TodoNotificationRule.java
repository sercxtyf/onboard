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

import java.util.Date;

import org.springframework.stereotype.Service;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.type.Subscribable;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.service.notification.NotificationRule;
import com.onboard.service.notification.SimpleNotificationRule;

/**
 * {@link Todo}操作需要进行通知的条件，实现为{@link NotificationRule}
 * 
 * @author yewei
 * 
 */

@Service("todoNotificationRuleBean")
public class TodoNotificationRule extends SimpleNotificationRule {

    @Override
    public boolean ifNotify(Activity activity, Subscribable subscribable) {
        return activity.getAction().equals(ActivityActionType.CREATE) && ((Todo) subscribable).getAssigneeId() != null;
    }

    /*****
     * get the time span between the createData and Duedate of the Todo.
     * 
     * @param todo
     * @param timeSpanGrainSize
     * @return
     */
    public long gettimeSpanBetweenCreateDateAndDueDateOfTodo(Todo todo, String timeSpanGrainSize) {
        if (todo == null || todo.getDueDate() != null || todo.getCreated() == null)
            return 0;
        long res = getTimeSpanBetweenTwoDate(todo.getDueDate(), todo.getCreated(), timeSpanGrainSize);
        return res;
    }

    /***
     * get the timespan between two dates. we support three grain size for the returned result: hour,minute,second the order for
     * the arguments don't matter
     * 
     * @param date1
     * @param date2
     * @param timeSpanGrainSize
     * @return
     */
    public long getTimeSpanBetweenTwoDate(Date date1, Date date2, String timeSpanGrainSize) {
        long res = date1.getTime() - date2.getTime();
        if (res < 0)
            res = -res;
        if (timeSpanGrainSize.equals("Hour")) {
            res = res / (60 * 60 * 1000);
        } else if (timeSpanGrainSize.equals("Minute")) {
            res = res / (60 * 1000);
        } else if (timeSpanGrainSize.equals("Second")) {
            res = res / (1000);
        } else
            res = res / (60 * 1000);// default set actually
        return 0;
    }

    /***
     * 
     * @param todo
     * @return how many minutes is the timespan between todo.duedate and todo.creatdate
     */
    public long gettimeSpanBetweenCreateDates(Todo updatedTodo, Todo originalTodo, String timeSpanGrainSize) {
        if (updatedTodo == null || updatedTodo.getCreated() != null || originalTodo == null || originalTodo.getCreated() == null)
            return 0;
        long res = getTimeSpanBetweenTwoDate(updatedTodo.getUpdated(), originalTodo.getUpdated(), timeSpanGrainSize);
        return res;
    }

    @Override
    public boolean ifNotify(Activity activity, Subscribable original, Subscribable updated) {

        if (updated instanceof Todo && original instanceof Todo) {
            Todo updatedTodo = (Todo) updated;
            Todo originalTodo = (Todo) original;
            if (updatedTodo.getDueDate() != null) {
                if (updatedTodo != null && updatedTodo.getDueDate() != null) {
                    if (gettimeSpanBetweenCreateDateAndDueDateOfTodo(originalTodo, "Minute") <= 60 * 24) {
                        long timeSpanBetweenCreateDates = gettimeSpanBetweenCreateDates(updatedTodo, originalTodo, "Second");
                        if (timeSpanBetweenCreateDates != 0 && timeSpanBetweenCreateDates <= 60)
                            return false;
                    }
                }
            }
        }
        return activity.getAction().equals(ActivityActionType.UPDATE) && ((Todo) updated).getAssigneeId() != null
                && ((Todo) updated).getAssigneeId() != ((Todo) original).getAssigneeId();
    }

    @Override
    public String modelType() {
        return new Todo().getType();
    }

}
