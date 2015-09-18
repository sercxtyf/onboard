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
package com.onboard.domain.transform;

import org.springframework.beans.BeanUtils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.onboard.domain.model.Discussion;
import com.onboard.dto.DiscussionDTO;

public class DiscussionTransform {

    public static final Function<Discussion, DiscussionDTO> DISCUSSION_DTO_FUNCTION = new Function<Discussion, DiscussionDTO>() {
        @Override
        public DiscussionDTO apply(Discussion discussion) {
            return discussionToDiscussionDTO(discussion);
        }
    };

    public static DiscussionDTO discussionToDiscussionDTO(Discussion discussion) {
        DiscussionDTO discussionDTO = new DiscussionDTO();
        BeanUtils.copyProperties(discussion, discussionDTO);
        return discussionDTO;
    }

    public static DiscussionDTO discussionToDiscussionDTOWithDetail(Discussion discussion) {
        DiscussionDTO discussionDTO = new DiscussionDTO();
        BeanUtils.copyProperties(discussion, discussionDTO);
        if (discussion.getComments() != null) {
            discussionDTO.setComments(Lists.transform(discussion.getComments(),
                    CommentTransform.COMMENT_TO_DTO_FUNCTION));
        }
        if (discussion.getSubscribers() != null) {
            discussionDTO.setSubscribers(Lists.transform(discussion.getSubscribers(),
                    UserTransform.USER_TO_USERDTO_FUNCTION));
        }
        if (discussion.getAttachments() != null) {
            discussionDTO.setAttachments(Lists.transform(discussion.getAttachments(),
                    AttachmentTransform.ATTACHMENT_TO_ATTACHMENTDTO_FUNCTION));
        }
        return discussionDTO;
    }

}
