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
package com.onboard.service.collaboration.activity;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.account.UserService;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.service.activity.util.ActivityHelper;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.web.SessionService;

@Service("activityRecorderHelper")
public class ActivityRecorderHelper {

    public static final String PROJECT_MOVE = "从项目“%s”搬运到项目“%s”";

    public static final String USER_REMOVE = "将%s移出了项目“%s”";

    public static final String USER_REMOVE_SUBJECT = "更新了项目成员";

    private static ProjectService projectService;

    private static UserService userService;

    private static SessionService session;

    public static void setupMoveInformation(int projectIdFrom, int projectIdTo, Activity activity) {
        if (projectIdFrom != projectIdTo) {

            Project projectFrom = projectService.getById(projectIdFrom);
            Project projectTo = projectService.getById(projectIdTo);
            if (projectFrom == null || projectTo == null) {
                return;
            }

            activity.setContent(String.format(PROJECT_MOVE, projectFrom.getName(), projectTo.getName()));
        }
    }

    public static Activity generateActivityByActionType(String actionType, String subject, BaseProjectItem item) {
        return ActivityHelper.generateActivityByActionType(actionType, subject, item);
    }

    public static Activity generateActivityOfRemoveUser(int projectId, int userId) {

        Project project = projectService.getById(projectId);
        User user = userService.getById(userId);

        String content = ActivityHelper.cutoffActivityContent(String.format(USER_REMOVE, user.getName(), project.getName()));

        if (project != null && user != null) {

            Activity activity = new Activity();
            activity.setAttachId(project.getCompanyId());
            activity.setAttachType(project.getType());
            activity.setProjectId(project.getId());
            activity.setProjectName(project.getName());
            activity.setAction(ActivityActionType.REMOVE);
            activity.setContent(content);
            activity.setSubject(USER_REMOVE_SUBJECT);
            activity.setTarget(project.getName());
            activity.setCompanyId(project.getCompanyId());
            activity.setCreatorId(session.getCurrentUser().getId());
            activity.setCreatorName(session.getCurrentUser().getName());
            activity.setCreated(new Date());
            activity.setCreatorAvatar(session.getCurrentUser().getAvatar());

            return activity;
        }
        return null;
    }

    @Autowired
    public void setProjectService(ProjectService projectService) {
        ActivityRecorderHelper.projectService = projectService;
    }

    @Autowired
    public void setSession(SessionService session) {
        ActivityRecorderHelper.session = session;
    }

    @Autowired
    public void setUserService(UserService userService) {
        ActivityRecorderHelper.userService = userService;
    }

    public static Activity enrichActivity(Activity activity) {

        activity.setCreatorId(session.getCurrentUser().getId());
        activity.setCreatorName(session.getCurrentUser().getName());
        activity.setCreated(new Date());
        activity.setCreatorAvatar(session.getCurrentUser().getAvatar());
        return activity;
    }

}
