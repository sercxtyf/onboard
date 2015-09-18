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
import com.onboard.domain.model.Comment;
import com.onboard.domain.model.User;
import com.onboard.dto.CommentDTO;

public class CommentTransform {

    public static final Function<Comment, CommentDTO> COMMENT_TO_DTO_FUNCTION = new Function<Comment, CommentDTO>() {
        @Override
        public CommentDTO apply(Comment input) {
            if (input.getCreator() != null) {
                return commentAndCreatorToCommentDTO(input, input.getCreator());
            } else {
                return commentToCommentDTO(input);
            }
        }
    };
    public static final Function<CommentDTO, Comment> DTO_TO_COMMENT_FUNCTION = new Function<CommentDTO, Comment>() {
        @Override
        public Comment apply(CommentDTO input) {
            return commentDTOtoComment(input);
        }
    };

    public static CommentDTO commentAndCreatorToCommentDTO(Comment comment, User creator) {
        CommentDTO commentDTO = new CommentDTO();
        BeanUtils.copyProperties(comment, commentDTO);

        if (creator != null) {
            commentDTO.setCreatorDTO(UserTransform.userToUserDTO(creator));
        }
        if (comment.getSubscribers() != null) {
            commentDTO.setSubscriberDTOs(Lists.transform(comment.getSubscribers(), UserTransform.USER_TO_USERDTO_FUNCTION));
        }
        if (comment.getAttachments() != null) {
            commentDTO.setAttachmentDTOs(Lists.transform(comment.getAttachments(),
                    AttachmentTransform.ATTACHMENT_TO_ATTACHMENTDTO_FUNCTION));
        }
        if (comment.getDiscardAttachments() != null) {
            commentDTO.setDiscardAttachmentDTOs(Lists.transform(comment.getDiscardAttachments(),
                    AttachmentTransform.ATTACHMENT_TO_ATTACHMENTDTO_FUNCTION));
        }
        return commentDTO;
    }

    public static CommentDTO commentToCommentDTO(Comment comment) {
        return commentAndCreatorToCommentDTO(comment, null);
    }

    public static Comment commentDTOtoComment(CommentDTO commentDTO) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDTO, comment);
        if (commentDTO.getSubscriberDTOs() != null) {
            comment.setSubscribers(Lists.transform(commentDTO.getSubscriberDTOs(), UserTransform.USERDTO_TO_USER_FUNCTION));
        }
        if (commentDTO.getAttachmentDTOs() != null) {
            comment.setAttachments(Lists.transform(commentDTO.getAttachmentDTOs(),
                    AttachmentTransform.ATTACHMENTDTO_TO_ATTACHMENT_FUNCTION));
        }
        if (commentDTO.getDiscardAttachmentDTOs() != null) {
            comment.setAttachments(Lists.transform(commentDTO.getDiscardAttachmentDTOs(),
                    AttachmentTransform.ATTACHMENTDTO_TO_ATTACHMENT_FUNCTION));
        }
        return comment;
    }

}
