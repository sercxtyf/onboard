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
import com.onboard.domain.model.Comment;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.domain.model.utils.HtmlTextParser;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.service.activity.ActivityGenerator;
import com.onboard.service.activity.util.ActivityHelper;
import com.onboard.service.collaboration.CommentService;

/**
 * 生成评论相关活动信息的辅助类
 * 
 * @author yewei
 * 
 */
@Service("commentActivityGeneratorBean")
public class CommentActivityGenerator implements ActivityGenerator {

    public static final String CREATE_SUBJECT = "回复了";
    public static final String DISCARD_SUBJECT = "删除了回复";
    public static final String RECOVER_SUBJECT = "从回收站还原了回复";
    public static final String UPDATE_SUBJECT = "更新了";
    public static final String DISCARD_SUBJECT_ACTION = "reply-discard";

    public static final Logger logger = LoggerFactory.getLogger(CommentActivityGenerator.class);

    @Autowired
    private CommentService commentService;

    private Activity generateActivityByActionType(String actionType, String subject, Comment comment) {
        Activity activity = ActivityHelper.generateActivityByActionType(actionType, subject, comment);
        String target = commentService.getCommentTargetName(comment.getAttachType(), comment.getAttachId());
        activity.setTarget(target);
        activity.setContent(ActivityHelper.cutoffActivityContent(HtmlTextParser.getPlainText(comment.getContent())));
        activity.setProjectId(comment.getProjectId());
        activity.setCompanyId(comment.getCompanyId());

        return ActivityRecorderHelper.enrichActivity(activity);
    }

    @Override
    public Activity generateCreateActivity(BaseProjectItem item) {
        return generateActivityByActionType(ActivityActionType.REPLY, CREATE_SUBJECT, (Comment) item);
    }

    @Override
    public Activity generateUpdateActivity(BaseProjectItem item, BaseProjectItem modifiedItem) {
        Comment c1 = (Comment) item;
        Comment c2 = (Comment) modifiedItem;

        if (c2.getDeleted() != null && c1.getDeleted() != c2.getDeleted()) {
            if (c2.getDeleted()) {
                return generateActivityByActionType(ActivityActionType.DISCARD, DISCARD_SUBJECT, c1);
            } else {
                return generateActivityByActionType(ActivityActionType.RECOVER, RECOVER_SUBJECT, c1);
            }
        }

        // bugfix: 修改comment时候，点击修改，然后按钮没反应，刷新页面后已经更新
        // 原因是websocket根据activity去更新HTML，但是comment原来只对删除和恢复产生activity，
        // 这里设置对update也产生activity，并且返回c2更新后的comment
        return generateActivityByActionType(ActivityActionType.UPDATE, UPDATE_SUBJECT, c2);
    }

    @Override
    public String modelType() {
        return new Comment().getType();
    }

    @Override
    public BaseProjectItem enrichModel(BaseProjectItem identifiable) {
        return new Comment(commentService.getById(identifiable.getId()));

    }

    @Override
    public String modelService() {
        return CommentService.class.getName();
    }

}
