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
package com.onboard.service.collaboration.attach;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.onboard.domain.model.Comment;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.domain.model.type.IdentifiableOperator;
import com.onboard.service.collaboration.CommentService;
import com.onboard.service.common.attach.IdentifiableAttachService;

@Service("commentAttachServiceBean")
public class CommentAttachServiceImpl implements IdentifiableAttachService {

    @Autowired
    CommentService commentService;

    @Override
    public String attachType() {
        return IdentifiableOperator.NONE_TYPE;
    }

    @Override
    public List<? extends BaseProjectItem> getIdentifiablesByAttachId(int attachId) {
        return Lists.newArrayList();
    }

    @Override
    public String modelType() {
        return new Comment().getType();
    }

    @Override
    public List<? extends BaseProjectItem> getIdentifiablesByAttachId(String attachType, int attachId) {
        return commentService.getCommentsByTopic(attachType, attachId, 0, -1);
    }

}
