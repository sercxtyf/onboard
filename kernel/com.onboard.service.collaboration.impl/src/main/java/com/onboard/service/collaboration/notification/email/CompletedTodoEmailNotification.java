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
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.Subscribable;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.collaboration.TodoService;
import com.onboard.service.collaboration.TodolistService;
import com.onboard.service.notification.NotificationMethod;
import com.onboard.service.notification.NotificationRule;
import com.onboard.service.notification.email.AbstractEmailNotification;

/**
 * {@link Todo}完成时所需要进行的提醒，实现为{@link NotificationMethod}
 * 
 * @author XR, yewei
 * 
 */
@Service("completedTodoEmailNotificationBean")
public class CompletedTodoEmailNotification extends AbstractEmailNotification {

    private static final String VM_NAME = "todo-completed.vm";

    @Autowired
    @Qualifier("completedTodoNotificationRuleBean")
    private NotificationRule notificationRule;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TodolistService todolistService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserService userService;

    @Override
    public String getEmailSubject(Activity activity, Subscribable item) {
        return subjectInSpecificProject(activity, item, String.format("任务完成：%s", ((Todo) item).getContent()));
    }

    @Override
    public String modelType() {
        return new Todo().getType();
    }

    @Override
    protected List<User> getSubsribers(Subscribable item) {
        List<User> users = new ArrayList<User>();
        if (((Todo) item).getCreator() == null) {
            users.add(userService.getById(((Todo) item).getCreatorId()));
        } else {
            users.add(((Todo) item).getCreator());
        }
        return users;
    }

    @Override
    public Map<String, Object> getModel(Activity activity, Subscribable item, Map<String, Object> model) {
        Todo updatedTodo = (Todo) item;
        model.put("currentUser", this.getOwner(activity));
        model.put("todo", updatedTodo);
        model.put("project", projectService.getById(updatedTodo.getProjectId()));
        model.put("todolist", todolistService.getById(updatedTodo.getTodolistId()));
        return model;
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
    protected Subscribable enrichSubscribable(Subscribable item) {
        Todo todo = todoService.getById(item.getId());
        todo.setCreator(userService.getById(todo.getCreatorId()));
        return todo;
    }
}
