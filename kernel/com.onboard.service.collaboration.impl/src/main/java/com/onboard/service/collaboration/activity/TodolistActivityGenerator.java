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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Todolist;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.service.activity.ActivityGenerator;
import com.onboard.service.collaboration.TodolistService;

/**
 * 生成任务列表相关活动信息的辅助类
 * 
 * @author yewei
 * 
 */
@Service("todolistActivityGeneratorBean")
public class TodolistActivityGenerator implements ActivityGenerator {

    public static final String CREATE_SUBJECT = "创建了任务分组";
    public static final String COPY_SUBJECT = "复制创建了任务分组";
    public static final String UPDATE_SUBJECT = "更新了任务分组信息";
    public static final String DISCARD_SUBJECT = "删除了任务分组";
    public static final String RECOVER_SUBJECT = "从回收站还原了任务分组";
    public static final String MOVE_SUBJECT = "移动了任务分组";
    public static final String RELOCATE_SUBJECT = "搬运了任务分组";
    public static final String ARCHIVE_SUBJECT = "归档了任务分组";
    public static final String ACTIVATE_SUBJECT = "激活了任务分组";
    public static final String NAME_UPDATE = "名称由“%s”变为“%s”";
    public static final String DESCRIPTION_UPDATE = "描述由“%s”变为“%s”";
    public static final String NAME_AND_DESCRIPTION_UPDATE = NAME_UPDATE + "，" + DESCRIPTION_UPDATE;

    private static final Logger logger = LoggerFactory.getLogger(TodolistActivityGenerator.class);

    @Autowired
    private TodolistService todolistService;

    private Activity generateActivityByActionType(String actionType, String subject, Todolist todolist) {

        Activity activity = ActivityRecorderHelper.generateActivityByActionType(actionType, subject, todolist);

        activity.setTarget(todolist.getName());

        activity.setProjectId(todolist.getProjectId());
        activity.setCompanyId(todolist.getCompanyId());

        return ActivityRecorderHelper.enrichActivity(activity);
    }

    @Override
    public String modelType() {
        return new Todolist().getType();
    }

    @Override
    public Activity generateCreateActivity(BaseProjectItem item) {

        Todolist todolist = (Todolist) item;

        String subject = CREATE_SUBJECT;
        String type = ActivityActionType.CREATE;

        if (todolist.getTodos() != null) {
            subject = COPY_SUBJECT;
            type = ActivityActionType.COPY;
        }

        Activity activity = this.generateActivityByActionType(type, subject, todolist);

        activity.setContent(todolist.getDescription());

        return activity;
    }

    private Activity generateUpdateActivity(Todolist originalList, Todolist modifiedList) {

        if (modifiedList.getProjectId() != null && !originalList.getProjectId().equals(modifiedList.getProjectId())) {
            // logger.info("modified: " + modifiedList.getProjectId());
            // logger.info("original: " + originalList.getProjectId());
            Activity activity = this.generateActivityByActionType(ActivityActionType.RELOCATE, RELOCATE_SUBJECT, originalList);

            ActivityRecorderHelper.setupMoveInformation(originalList.getProjectId(), modifiedList.getProjectId(), activity);

            return activity;
        } else {

            Activity activity = this.generateActivityByActionType(ActivityActionType.UPDATE, UPDATE_SUBJECT, originalList);

            boolean changeName = modifiedList.getName() != null && !modifiedList.getName().equals(originalList.getName());
            boolean changeDescription = modifiedList.getDescription() != null
                    && !modifiedList.getDescription().equals(originalList.getDescription());

            if (changeName & changeDescription) {
                activity.setContent(String.format(NAME_AND_DESCRIPTION_UPDATE, originalList.getName(), modifiedList.getName(),
                        originalList.getDescription(), modifiedList.getDescription()));
            } else if (changeName) {
                activity.setContent(String.format(NAME_UPDATE, originalList.getName(), modifiedList.getName()));
            } else if (changeDescription) {
                activity.setContent(String.format(DESCRIPTION_UPDATE, originalList.getDescription(),
                        modifiedList.getDescription()));
            } else {
                return null;
            }

            return activity;
        }
    }

    @Override
    public Activity generateUpdateActivity(BaseProjectItem item, BaseProjectItem modifiedItem) {
        Todolist t1 = (Todolist) item;
        Todolist t2 = (Todolist) modifiedItem;

        if (t2.getDeleted() != null && t1.getDeleted() != t2.getDeleted()) {
            if (t2.getDeleted()) {
                return this.generateActivityByActionType(ActivityActionType.DISCARD, DISCARD_SUBJECT, t1);
            } else {
                return this.generateActivityByActionType(ActivityActionType.RECOVER, RECOVER_SUBJECT, t1);
            }
        } else if (t2.getArchived() != null && t1.getArchived() != t2.getArchived()) {
            if (t2.getArchived()) {
                return this.generateActivityByActionType(ActivityActionType.ARCHIVE, ARCHIVE_SUBJECT, t1);
            } else {
                return this.generateActivityByActionType(ActivityActionType.ACTIVATE, ACTIVATE_SUBJECT, t1);
            }
        } else {
            logger.info("To move todolist...");
            return this.generateUpdateActivity(t1, t2);
        }
    }

    @Override
    public BaseProjectItem enrichModel(BaseProjectItem identifiable) {
        return new Todolist(todolistService.getById(identifiable.getId()));
    }

    @Override
    public String modelService() {
        return TodolistService.class.getName();
    }
}
