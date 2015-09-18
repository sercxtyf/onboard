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
package com.onboard.service.collaboration.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.onboard.domain.mapper.AttachmentMapper;
import com.onboard.domain.mapper.base.BaseMapper;
import com.onboard.domain.mapper.model.AttachmentExample;
import com.onboard.domain.model.Attachment;
import com.onboard.domain.model.Comment;
import com.onboard.domain.model.type.Attachable;
import com.onboard.service.account.UserService;
import com.onboard.service.base.AbstractBaseService;
import com.onboard.service.collaboration.AttachmentService;
import com.onboard.service.collaboration.TagService;
import com.onboard.service.common.identifiable.IdentifiableManager;
import com.onboard.service.file.FileService;
import com.onboard.service.web.SessionService;

/**
 * {@link AttachmentService}接口实现
 * 
 * @author yewei
 */
@Transactional
@Service("attachmentServiceBean")
public class AttachmentServiceImpl extends AbstractBaseService<Attachment, AttachmentExample> implements AttachmentService {

    public static final Logger logger = LoggerFactory.getLogger(AttachmentServiceImpl.class);

    public static final int DEFAULT_LIMIT = -1;

    @Autowired
    AttachmentMapper attachmentMapper;
    @Autowired
    SessionService sessionService;
    @Autowired
    FileService fileService;
    @Autowired
    TagService tagService;
    @Autowired
    IdentifiableManager identifiableManager;
    @Autowired
    UserService userService;

    @Override
    public Attachment getAttachmentByIdWithUrl(int id) {
        return generateUrlOfAttachment(attachmentMapper.selectByPrimaryKey(id));
    }

    private Attachment generateUrlOfAttachment(Attachment a) {
        if (a != null) {
            // BUG FIX BY CL
            a.setCreator(userService.getById(a.getCreatorId()));
            tagService.fillTags(a);
        }
        return a;
    }

    private List<Attachment> generateUrlOfAttachments(List<Attachment> attachments) {
        for (Attachment a : attachments) {
            generateUrlOfAttachment(a);
        }
        return attachments;
    }

    @Override
    public List<Attachment> getAttachmentsByProjectId(int projectId, int start, int limit) {
        Attachment sample = new Attachment(false);
        sample.setProjectId(projectId);
        AttachmentExample example = new AttachmentExample(sample);
        example.setLimit(start, limit);
        example.setOrderByClause("id desc");
        example.getOredCriteria().get(0).andAttachIdGreaterThan(0);

        List<Attachment> attachments = attachmentMapper.selectByExample(example);

        return generateUrlOfAttachments(attachments);
    }

    @Override
    public List<Attachment> getAttachmentsByUserId(int companyId, int userId, int start, int limit, List<Integer> projectList) {
        Attachment attachment = new Attachment(false);
        attachment.setCreatorId(userId);
        attachment.setCompanyId(companyId);
        AttachmentExample example = new AttachmentExample(attachment);
        example.setOrderByClause("id desc");
        example.setLimit(start, limit);
        example.getOredCriteria().get(0).andAttachIdGreaterThan(0);

        if (projectList != null) {
            if (projectList.size() > 0) {
                example.getOredCriteria().get(0).andProjectIdIn(projectList);
            } else {
                return new ArrayList<Attachment>();
            }
        }

        List<Attachment> attachments = attachmentMapper.selectByExample(example);

        return generateUrlOfAttachments(attachments);
    }

    private List<Attachment> appendAttachmentsOfLastDay(List<Attachment> attachments, Date until, AttachmentExample example) {
        if (attachments != null && attachments.size() > 0) {
            Attachment lastAttachment = attachments.get(attachments.size() - 1);
            Date newUntil = new DateTime(lastAttachment.getCreated()).withTimeAtStartOfDay().toDate();
            example.getOredCriteria().get(0).andCreatedBetween(newUntil, until);
            attachments = attachmentMapper.selectByExample(example);
        }

        return attachments;
    }

    @Override
    public TreeMap<Date, List<Attachment>> getAttachmentsByUserGroupByDate(int companyId, int userId, List<Integer> projectList,
            Date until, int limit) {
        Attachment sample = new Attachment(false);
        sample.setCreatorId(userId);
        sample.setCompanyId(companyId);
        AttachmentExample example = new AttachmentExample(sample);
        if (projectList != null) {
            if (projectList.size() > 0) {
                example.getOredCriteria().get(0).andProjectIdIn(projectList);
            } else {
                return new TreeMap<Date, List<Attachment>>();
            }
        }
        example.getOredCriteria().get(0).andCreatedLessThanOrEqualTo(until);
        example.getOredCriteria().get(0).andAttachIdGreaterThan(0);
        example.setLimit(limit);
        example.setOrderByClause("created desc");
        List<Attachment> originAttachments = attachmentMapper.selectByExample(example);
        if (originAttachments == null || originAttachments.size() == 0) {
            return new TreeMap<Date, List<Attachment>>();
        }
        List<Attachment> attachments = appendAttachmentsOfLastDay(originAttachments, until, example);
        TreeMap<Date, List<Attachment>> dateMap = getAttachmentsByUserGroupByDate(generateUrlOfAttachments(attachments));

        return dateMap;

    }

    private TreeMap<Date, List<Attachment>> getAttachmentsByUserGroupByDate(List<Attachment> attachments) {
        TreeMap<Date, List<Attachment>> map = new TreeMap<Date, List<Attachment>>(new Comparator<Date>() {
            @Override
            public int compare(Date o1, Date o2) {
                return o2.compareTo(o1);
            }
        });
        for (Attachment ac : attachments) {
            Date d = new DateTime(ac.getCreated()).withTimeAtStartOfDay().toDate();
            if (!map.containsKey(d)) {
                List<Attachment> list = new ArrayList<Attachment>();
                list.add(ac);
                map.put(d, list);
            } else {
                map.get(d).add(ac);
            }
        }
        return map;
    }

    @Override
    public List<Attachment> getAttachmentsByTypeAndId(String type, int id, int start, int limit) {
        return getAttachmentsByTypeAndIdHelper(type, id, start, limit, false);
    }

    private List<Attachment> getAttachmentsByTypeAndIdHelper(String type, int id, int start, int limit, boolean deleteflag) {
        Attachment attachment = new Attachment(deleteflag);
        attachment.setAttachId(id);
        attachment.setAttachType(type);

        AttachmentExample example = new AttachmentExample(attachment);
        example.setLimit(start, limit);
        example.getOredCriteria().get(0).andAttachIdGreaterThan(0);

        return generateUrlOfAttachments(attachmentMapper.selectByExample(example));
    }

    @Override
    public List<Attachment> getAttachmentsByTypeAndIdWithNotDiscard(String type, int id, int start, int limit) {
        return getAttachmentsByTypeAndIdHelper(type, id, start, limit, false);
    }

    @Override
    public List<Attachment> getAttachmentsByTypeAndIdWithDiscard(String type, int id, int start, int limit) {
        return getAttachmentsByTypeAndIdHelper(type, id, start, limit, true);
    }

    @Override
    public int countBySample(Attachment item) {
        AttachmentExample example = new AttachmentExample(item);
        example.getOredCriteria().get(0).andAttachIdGreaterThan(0);
        return attachmentMapper.countByExample(example);
    }

    @Override
    public void fillAttachments(Attachable attachable, int start, int limit) {
        if (attachable != null) {
            attachable.setAttachments(getAttachmentsByTypeAndId(attachable.getType(), attachable.getId(), start, limit));
        }
    }

    @Override
    public void fillAttachmentsWithNotDiscard(Attachable attachable, int start, int limit) {
        if (attachable != null) {
            attachable.setAttachments(getAttachmentsByTypeAndIdWithNotDiscard(attachable.getType(), attachable.getId(), start,
                    limit));
        }
    }

    @Override
    public void fillAttachmentsWithDiscard(Attachable attachable, int start, int limit) {
        if (attachable != null) {
            attachable.setDiscardAttachments(getAttachmentsByTypeAndIdWithDiscard(attachable.getType(), attachable.getId(),
                    start, limit));
        }
    }

    @Override
    public void discardAttachment(String attachType, int attachId) {
        Attachment attachment = new Attachment(true);

        Attachment example = new Attachment();
        example.setAttachId(attachId);
        example.setAttachType(attachType);

        // no need to generate activity info, so call mapper directly
        attachmentMapper.updateByExampleSelective(attachment, new AttachmentExample(example));
    }

    @Override
    public void deleteAttachmentByAttachTypeAndId(String attachType, int attachId) {
        Attachment example = new Attachment();
        example.setAttachId(attachId);
        example.setAttachType(attachType);
        attachmentMapper.deleteByExample(new AttachmentExample(example));
    }

    @Override
    public Attachment stageAttachment(int companyId, int projectId, String name, long size, String contentType, byte[] file) {

        Attachment attachment = new Attachment();
        attachment.setName(name);
        attachment.setContentType(contentType);
        attachment.setAttachId(NONE_ATTACH_ID);
        attachment.setAttachType(NONE_ATTACH_TYPE);
        attachment.setCreated(new Date());
        attachment.setSize(size);
        attachment.setCreatorId(sessionService.getCurrentUser().getId());
        attachment.setCreatorName(sessionService.getCurrentUser().getName());
        attachment.setCreatorAvatar(sessionService.getCurrentUser().getAvatar());
        attachment.setProjectId(projectId);
        attachment.setCompanyId(companyId);
        attachment = create(attachment);

        String path = StringUtils.arrayToDelimitedString(new String[] { "", "attachment", String.valueOf(projectId), "stage",
                String.valueOf(attachment.getId()) }, "/");
        if (fileService.writeFile(path, file)) {
            return attachment;
        } else {
            deleteFromTrash(attachment.getId());
            return null;
        }
    }

    /**
     * @author Chenlong
     */
    @Override
    public void copyAttachments(Attachable oldItem, Attachable newItem) {
        List<Attachment> attachments = getAttachmentsByTypeAndId(oldItem.getType(), oldItem.getId(), 0, -1);
        logger.info("Detected: " + attachments.size() + " attachments in " + oldItem.getType());
        int targetId = newItem.getId();
        if (newItem instanceof Comment) {
            Comment comment = (Comment) newItem;
            targetId = comment.getAttachId();
        }
        for (Attachment a : attachments) {
            a.setTargetId(targetId);
            a.setAttachId(newItem.getId());
            byte[] originFile = getAttachmentContentById(a.getProjectId(), a.getId());
            if (originFile == null) {
                logger.error("When copying attachment original file not found!");
            }
            a.setProjectId(newItem.getProjectId());
            attachmentMapper.insertSelective(a);
            logger.info("new attachment id: " + a.getId());
            String newPath = getAttachmentPath(a.getProjectId(), a.getId());
            if (fileService.writeFile(newPath, originFile)) {
                return;
            } else {
                // 写文件失败就删除当前附件
                delete(a.getId());
            }
        }
    }

    /**
     * @author Chenlong
     */
    @Override
    public void relocateAttachment(Attachable item, int projectId) {
        // relocate attachments
        List<Attachment> attachments = getAttachmentsByTypeAndId(item.getType(), item.getId(), 0, -1);
        for (Attachment a : attachments) {
            String originPath = getAttachmentPath(a.getProjectId(), a.getId());
            byte[] originFile = fileService.readFile(originPath);
            if (originFile == null) {
                logger.error("When copying attachment original file not found!");
            }
            Attachment example = new Attachment();
            example.setId(a.getId());
            example.setProjectId(projectId);
            attachmentMapper.updateByPrimaryKeySelective(example);

            String newPath = getAttachmentPath(projectId, a.getId());

            if (!fileService.writeFile(newPath, originFile)) {
                logger.error("Fail to write attachment original file to new path!");
            } else {
                byte[] newFile = fileService.readFile(newPath);
                logger.info("New file exists: " + newFile.length);
                if (!fileService.deleteFile(originPath)) {
                    logger.error("Failed to delete original file");
                } else {
                    logger.info("Original file has been deleted!");
                }
            }
        }
    }

    /**
     * 根据附件的ID和项目的ID生成附件的位置
     * 
     * @param projectId
     * @param id
     * @return
     */
    private String getAttachmentPath(int projectId, int id) {
        String path = StringUtils.arrayToDelimitedString(
                new String[] { "", "attachment", String.valueOf(projectId), String.valueOf(id) }, "/");
        return path;
    }

    @Override
    public byte[] getAttachmentContentById(int projectId, int id) {
        String path = getAttachmentPath(projectId, id);
        return fileService.readFile(path);
    }

    @Override
    public boolean moveStageAttachment(int attachmentId, int projectId) {
        String oldPath = StringUtils.arrayToDelimitedString(new String[] { "", "attachment", String.valueOf(projectId), "stage",
                String.valueOf(attachmentId) }, "/");
        String newPath = getAttachmentPath(projectId, attachmentId);
        return fileService.renameFile(oldPath, newPath, true);

    }

    @Override
    public List<Attachment> getAttachmentsByProjectIdWithNotDiscard(int projectId, int start, int limit) {
        AttachmentExample example = new AttachmentExample();
        example.setLimit(start, limit);
        example.setOrderByClause("id desc");
        example.or().andAttachIdGreaterThan(0).andProjectIdEqualTo(projectId).andDeletedEqualTo(false);

        List<Attachment> attachments = attachmentMapper.selectByExample(example);

        return generateUrlOfAttachments(attachments);
    }

    @Override
    public List<Attachment> getAttachmentsByCompanyIdWithNotDiscard(int companyId, int start, int limit) {
        AttachmentExample example = new AttachmentExample();
        example.setLimit(start, limit);
        example.setOrderByClause("id desc");
        example.or().andAttachIdGreaterThan(0).andCompanyIdEqualTo(companyId).andDeletedEqualTo(false);
        List<Attachment> attachments = attachmentMapper.selectByExample(example);

        return generateUrlOfAttachments(attachments);
    }

    @Override
    public List<Attachment> diffIncomingOfAttachable(Attachable origin, Attachable newest) {
        return diffAttachable(newest, origin);
    }

    @Override
    public List<Attachment> diffDeletedOfAttachable(Attachable origin, Attachable newest) {
        return diffAttachable(origin, newest);
    }

    private List<Attachment> diffAttachable(Attachable origin, Attachable newest) {

        if (origin == null || newest == null) {
            return null;
        }
        // 如果newest中没有attachments，设置一个空List以便diff
        if (newest.getAttachments() == null) {
            newest.setAttachments(new ArrayList<Attachment>());
        }

        // 如果newest中没有attachments，设置一个空List以便diff
        if (origin.getAttachments() == null) {
            origin.setAttachments(new ArrayList<Attachment>());
        }

        return Lists.newArrayList(Sets.difference(Sets.newHashSet(origin.getAttachments()),
                Sets.newHashSet(newest.getAttachments())).immutableCopy());
    }

    @Override
    public void recoverAttachment(String attachType, int attachId) {
        Attachment attachment = new Attachment(false);

        Attachment example = new Attachment();
        example.setAttachId(attachId);
        example.setAttachType(attachType);

        // no need to generate activity info, so call mapper directly
        attachmentMapper.updateByExampleSelective(attachment, new AttachmentExample(example));
    }

    @Override
    public List<Attachment> addAttachmentsForAttachable(Attachable attachable, List<Attachment> attachments) {
        if (attachments == null) {
            return new ArrayList<Attachment>();
        }
        List<Attachment> updatedAttachments = new ArrayList<Attachment>();
        for (Attachment a : attachments) {
            updatedAttachments.add(addAttachmentForAttachable(attachable, getById(a.getId())));
        }
        return updatedAttachments;
    }

    @Override
    public void appendAttachmentsForAttachable(Attachable attachable) {

        Attachable srcAttachable = (Attachable) identifiableManager.getIdentifiableByTypeAndId(attachable.getType(),
                attachable.getId());

        fillAttachments(srcAttachable, 0, -1);

        List<Attachment> incoming = diffIncomingOfAttachable(srcAttachable, attachable);
        List<Attachment> deleted = diffDeletedOfAttachable(srcAttachable, attachable);

        for (Attachment a : incoming) {
            addAttachmentForAttachable(srcAttachable, a);
        }
        for (Attachment a : deleted) {
            delete(a.getId());
        }
    }

    @Override
    public Attachment addAttachmentForAttachable(Attachable attachable, Attachment attachment) {
        if (attachment == null) {
            return null;
        }
        attachment.setProjectId(attachable.getProjectId());
        attachment.setAttachId(attachable.getId());
        attachment.setAttachType(attachable.getType());
        if (attachable instanceof Comment) {
            Comment comment = (Comment) attachable;
            attachment.setTargetId(comment.getAttachId());
            attachment.setTargetType(comment.getAttachType());
        } else {
            attachment.setTargetId(attachable.getId());
            attachment.setTargetType(attachable.getType());
        }
        attachment.setCreatorName(attachable.getCreatorName());
        updateSelective(attachment);
        moveStageAttachment(attachment.getId(), attachment.getProjectId());
        return attachment;
    }

    @Override
    protected BaseMapper<Attachment, AttachmentExample> getBaseMapper() {
        return attachmentMapper;
    }

    @Override
    public Attachment newItem() {
        return new Attachment();
    }

    @Override
    public AttachmentExample newExample() {
        return new AttachmentExample();
    }

    @Override
    public AttachmentExample newExample(Attachment item) {
        return new AttachmentExample(item);
    }

}
