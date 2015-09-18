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
package com.onboard.service.collaboration.notification.email;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.Subscribable;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.collaboration.TodoService;
import com.onboard.service.notification.NotificationMethod;
import com.onboard.service.notification.NotificationRule;
import com.onboard.service.notification.email.AbstractEmailNotification;

/**
 * 针对{@link Todo}实现的{@link NotificationMethod}
 * 
 * @author XR, yewei
 * 
 */
@Service("todoEmailNotificationBean")
public class TodoEmailNotification extends AbstractEmailNotification {

    private static final String VM_NAME = "todo-created.vm";

    @Autowired
    @Qualifier("todoNotificationRuleBean")
    private NotificationRule notificationRule;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private TodoService todoService;

    @Override
    public String getEmailSubject(Activity activity, Subscribable item) {
        return subjectInSpecificProject(activity, item, "您有新的任务：" + item.getSubscribableSubject());
    }

    @Override
    public String getTemplatePath() {
        return VM_PATH + VM_NAME;
    }

    @Override
    public NotificationRule getNotificationRule() {
        return this.notificationRule;
    }

    @Override
    public String modelType() {
        return new Todo().getType();
    }

    @Override
    protected List<User> getSubsribers(Subscribable item) {
        List<User> users = new ArrayList<User>();
        users.add(userService.getById(((Todo) item).getAssigneeId()));
        return users;
    }

    @Override
    public Map<String, Object> getModel(Activity activity, Subscribable item, Map<String, Object> model) {
        Todo todo = (Todo) item;
        Project project = this.projectService.getById(todo.getProjectId());
        model.put("userName", this.getOwner(activity).getName());
        model.put("projectName", project.getName());
        model.put("content", todo.getContent());
        return model;
    }

    @Override
    protected Subscribable enrichSubscribable(Subscribable item) {
        return todoService.getById(item.getId());
    }
}
