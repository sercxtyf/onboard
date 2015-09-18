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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.service.activity.ActivityGenerator;
import com.onboard.service.collaboration.ProjectService;

/**
 * 生成项目相关活动信息的辅助类
 * 
 * @author yewei
 * 
 */
@Service("projectActivityGeneratorBean")
public class ProjectActivityGenerator implements ActivityGenerator {

    public static final String CREATE_SUBJECT = "创建了项目";
    public static final String UPDATE_SUBJECT = "更新了项目信息";
    public static final String DISCARD_SUBJECT = "删除了项目";
    public static final String RECOVER_SUBJECT = "从回收站还原了项目";
    public static final String ARCHIVE_SUBJECT = "归档了项目";
    public static final String ACTIVATE_SUBJECT = "重新激活了项目";

    public static final String NAME_UPDATE = "名称由“%s”变为“%s”";
    public static final String DESCRIPTION_UPDATE = "描述由“%s”变为“%s”";
    public static final String NAME_AND_DESCRIPTION_UPDATE = NAME_UPDATE + "，" + DESCRIPTION_UPDATE;

    @Autowired
    private ProjectService projectService;

    @Override
    public Activity generateCreateActivity(BaseProjectItem item) {

        Project project = (Project) item;

        return this.generateActivityByActionType(ActivityActionType.CREATE, CREATE_SUBJECT, project);
    }

    private Activity generateActivityByActionType(String actionType, String subject, Project project) {

        Activity activity = ActivityRecorderHelper.generateActivityByActionType(actionType, subject, project);

        activity.setTarget(project.getName());
        activity.setContent(project.getDescription());
        activity.setProjectName(project.getName());

        activity.setProjectId(project.getId());
        activity.setCompanyId(project.getCompanyId());

        return ActivityRecorderHelper.enrichActivity(activity);

    }

    private Activity generateUpdateActivity(Project p1, Project p2) {
        Activity activity = this.generateActivityByActionType(ActivityActionType.UPDATE, UPDATE_SUBJECT, p1);

        boolean changeName = p2.getName() != null && !p2.getName().equals(p1.getName());
        boolean changeDescription = p2.getDescription() != null && !p2.getDescription().equals(p1.getDescription());

        if (changeName & changeDescription) {
            activity.setContent(String.format(NAME_AND_DESCRIPTION_UPDATE, p1.getName(), p2.getName(), p1.getDescription(),
                    p2.getDescription()));
        } else if (changeName) {
            activity.setContent(String.format(NAME_UPDATE, p1.getName(), p2.getName()));
        } else if (changeDescription) {
            activity.setContent(String.format(DESCRIPTION_UPDATE, p1.getDescription(), p2.getDescription()));
        }

        return activity;
    }

    @Override
    public Activity generateUpdateActivity(BaseProjectItem item, BaseProjectItem modifiedItem) {
        Project p1 = (Project) item;
        Project p2 = (Project) modifiedItem;

        if (p2.getDeleted() != null && p1.getDeleted() != p2.getDeleted()) {
            if (p2.getDeleted()) {
                return this.generateActivityByActionType(ActivityActionType.DISCARD, DISCARD_SUBJECT, p1);
            } else {
                return this.generateActivityByActionType(ActivityActionType.RECOVER, RECOVER_SUBJECT, p1);

            }
        } else if (p2.getArchived() != null && p1.getArchived() != p2.getArchived()) {
            if (p2.getArchived()) {
                return this.generateActivityByActionType(ActivityActionType.ARCHIVE, ARCHIVE_SUBJECT, p1);
            } else {
                return this.generateActivityByActionType(ActivityActionType.ACTIVATE, ACTIVATE_SUBJECT, p1);
            }
        } else {
            return this.generateUpdateActivity(p1, p2);
        }
    }

    @Override
    public String modelType() {
        return new Project().getType();
    }

    @Override
    public BaseProjectItem enrichModel(BaseProjectItem identifiable) {
        return new Project(projectService.getById(identifiable.getId()));
    }

    @Override
    public String modelService() {
        // project的service不需要返回
        return null;
    }
}
