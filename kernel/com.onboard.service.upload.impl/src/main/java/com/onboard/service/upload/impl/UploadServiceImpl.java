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
package com.onboard.service.upload.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onboard.domain.mapper.UploadMapper;
import com.onboard.domain.mapper.base.BaseMapper;
import com.onboard.domain.mapper.model.UploadExample;
import com.onboard.domain.model.Attachment;
import com.onboard.domain.model.Comment;
import com.onboard.domain.model.Upload;
import com.onboard.service.account.UserService;
import com.onboard.service.base.AbstractBaseService;
import com.onboard.service.collaboration.AttachmentService;
import com.onboard.service.collaboration.CommentService;
import com.onboard.service.collaboration.TopicService;
import com.onboard.service.common.identifiable.IdentifiableManager;
import com.onboard.service.common.subscrible.SubscriberService;
import com.onboard.service.upload.UploadService;
import com.onboard.service.web.SessionService;

/**
 * {@link UploadService}接口实现
 * 
 * @author yewei
 * 
 */
@Transactional
@Service("uploadServiceBean")
public class UploadServiceImpl extends AbstractBaseService<Upload, UploadExample> implements UploadService {

    public static final int DEFAULT_LIMIT = -1;

    @Autowired
    AttachmentService attachmentService;
    @Autowired
    CommentService commentService;
    @Autowired
    SubscriberService subscriberService;
    @Autowired
    UserService userService;
    @Autowired
    TopicService topicService;
    @Autowired
    UploadMapper uploadMapper;
    @Autowired
    IdentifiableManager identifiableManager;

    @Autowired
    private SessionService sessionService;

    @Override
    public Upload getByIdWithDetail(int id) {
        Upload upload = uploadMapper.selectByPrimaryKey(id);
        if (upload != null) {
            attachmentService.fillAttachmentsWithNotDiscard(upload, 0, DEFAULT_LIMIT);
            attachmentService.fillAttachmentsWithDiscard(upload, 0, DEFAULT_LIMIT);
            commentService.fillCommentable(upload, 0, DEFAULT_LIMIT);
            subscriberService.fillSubcribers(upload);
        }
        return upload;

    }

    @Override
    public Upload create(Upload upload) {
        upload.setDeleted(false);
        upload.setCreated(new Date());
        upload.setUpdated(upload.getCreated());
        upload.setCreatorAvatar(sessionService.getCurrentUser().getAvatar());
        uploadMapper.insert(upload);
        subscriberService.generateSubscribers(upload, userService.getById(upload.getCreatorId()));
        subscriberService.addSubscribers(upload);

        Attachment attach = attachmentService.addAttachmentForAttachable(upload, upload.getAttachments().get(0));
        // TODO
        // attach.setAttachUrl(identifiableManager.getIdentifiableURL(upload));
        List<Attachment> attachments = new ArrayList<Attachment>();
        attachments.add(attach);
        upload.setAttachments(attachments);
        return upload;
    }

    @Override
    public List<Upload> getUploadsByProject(int projectId, int start, int limit) {
        Upload uploads = new Upload();
        uploads.setProjectId(projectId);
        UploadExample uploadsExample = new UploadExample(uploads);
        uploadsExample.setLimit(start, limit);
        return uploadMapper.selectByExample(uploadsExample);
    }

    @Override
    public Upload updateSelective(Upload upload) {
        Upload srcUpload = getById(upload.getId());
        upload.setUpdated(new Date());
        uploadMapper.updateByPrimaryKeySelective(upload);

        if (upload.getDeleted() != null && upload.getDeleted()) {
            topicService.discardTopcicByTypeAndId(upload.getType(), upload.getId());
            attachmentService.discardAttachment(upload.getType(), upload.getId());
        } else if (upload.getDeleted() != null && srcUpload != null && srcUpload.getDeleted()) {
            topicService.recoverTopcicByTypeAndId(upload.getType(), upload.getId());
            attachmentService.recoverAttachment(upload.getType(), upload.getId());
        }

        return upload;
    }

    @Override
    public void deleteFromTrash(int id) {
        String type = new Upload().getType();
        attachmentService.deleteAttachmentByAttachTypeAndId(type, id);
        commentService.deleteCommentByAttachTypeAndId(type, id);
        uploadMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void delete(int id) {
        String type = new Upload().getType();
        Upload upload = new Upload(id, true);
        attachmentService.discardAttachment(type, id);
        List<Comment> comments = commentService.getCommentsByTopic(type, id, 0, -1);
        for (Comment comment : comments) {
            commentService.delete(comment.getId());
        }
        updateSelective(upload);
    }

    @Override
    public void recover(int id) {
        Upload upload = new Upload(id, false);
        updateSelective(upload);
    }

    @Override
    public void moveUpload(Upload upload, int projectId) {
        Upload example = new Upload(upload.getId());
        example.setProjectId(projectId);

        updateSelective(example);
    }

    @Override
    protected BaseMapper<Upload, UploadExample> getBaseMapper() {
        return uploadMapper;
    }

    @Override
    public Upload newItem() {
        return new Upload();
    }

    @Override
    public UploadExample newExample() {
        return new UploadExample();
    }

    @Override
    public UploadExample newExample(Upload item) {
        return new UploadExample(item);
    }

}
