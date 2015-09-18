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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Story;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.service.activity.ActivityGenerator;
import com.onboard.service.activity.util.ActivityHelper;
import com.onboard.service.collaboration.StoryService;
import com.onboard.service.web.SessionService;

/**
 * 生成故事相关活动信息的辅助类
 * 
 * @author 邢亮
 * 
 */

@Service("storyActivityGeneratorBean")
public class StoryActivityGenerator implements ActivityGenerator {

    public static final Logger LOGGER = LoggerFactory.getLogger(StoryActivityGenerator.class);

    public static final String CREATE_STORY_SUBJECT = "创建了需求";
    public static final String UPDATE_STORY_SUBJECT = "更新了需求";
    public static final String DELETE_STORY_SUBJECT = "删除了需求";
    public static final String UPDATE_STORY_CONTENT_SUBJECT = "更改了详细描述";
    public static final String UPDATE_STORY_TITLE_SUBJECT = "标题由%s改为%s";
    public static final String UPDATE_STORY_PRIORITY_SUBJECT = "优先级由%s改为%s";
    public static final String UPDATE_STORY_ACCEPTANCE_LEVEL_SUBJECT = "更改了验收标准";
    public static final String UPDATE_STORY_COMPLETE_SUBJECT = "完成了需求";
    public static final String UPDATE_STORY_REOPEN_SUBJECT = "重新打开了需求";
    private final static Map<Integer, String> PR_MAP = new HashMap<Integer, String>() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        {
            put(1, "非常紧急");
            put(2, "紧急");
            put(3, "重要");
            put(4, "普通");
            put(5, "可忽略");
        }
    };

    @Autowired
    private StoryService storyService;

    @Autowired
    private SessionService sessionService;

    @Override
    public String modelType() {
        return new Story().getType();
    }

    @Override
    public String modelService() {
        return StoryService.class.getName();
    }

    @Override
    public BaseProjectItem enrichModel(BaseProjectItem identifiable) {
        return new Story(storyService.getById(identifiable.getId()));
    }

    private Activity generateActivityByActionType(String actionType, String subject, Story story) {
        Activity activity = ActivityHelper.generateActivityByActionType(actionType, subject, story);
        activity.setProjectId(story.getProjectId());
        activity.setCompanyId(story.getCompanyId());
        activity.setTarget(story.getDescription());
        activity.setContent(story.getDescription());
        return ActivityRecorderHelper.enrichActivity(activity);
    }

    @Override
    public Activity generateCreateActivity(BaseProjectItem item) {
        Story story = (Story) item;
        Activity activity = generateActivityByActionType(ActivityActionType.CREATE, CREATE_STORY_SUBJECT, story);
        return activity;
    }

    @Override
    public Activity generateUpdateActivity(BaseProjectItem item, BaseProjectItem modifiedItem) {
        Story updated = (Story) modifiedItem;
        Story origin = (Story) item;
        Activity activity = null;
        activity = generateActivityByActionType(ActivityActionType.UPDATE, UPDATE_STORY_SUBJECT, updated);
        if (updated.getPriority() != origin.getPriority()) {
            activity.setContent(String.format(UPDATE_STORY_PRIORITY_SUBJECT, PR_MAP.get(origin.getPriority()),
                    PR_MAP.get(updated.getPriority())));
        } else if (!updated.getDescription().equals(origin.getDescription())) {
            activity.setContent(String.format(UPDATE_STORY_TITLE_SUBJECT, origin.getDescription(), updated.getDescription()));
        } else if (!updated.getAcceptanceLevel().equals(origin.getAcceptanceLevel())) {
            activity.setContent(UPDATE_STORY_ACCEPTANCE_LEVEL_SUBJECT);
        } else if (updated.getCompleted() && !origin.getCompleted()) {
            activity.setSubject(UPDATE_STORY_COMPLETE_SUBJECT);
        } else if (!updated.getCompleted() && origin.getCompleted()) {
            activity.setContent("");
            activity.setSubject(UPDATE_STORY_REOPEN_SUBJECT);
        } else if (!origin.getDeleted() && updated.getDeleted()) {
            activity.setContent(DELETE_STORY_SUBJECT);
        } else {
            activity.setContent(UPDATE_STORY_CONTENT_SUBJECT);
        }
        return activity;
    }
}
