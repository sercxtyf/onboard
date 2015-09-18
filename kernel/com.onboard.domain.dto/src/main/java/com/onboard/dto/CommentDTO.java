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
package com.onboard.dto;

import java.util.Date;
import java.util.List;

public class CommentDTO implements DTO {

    private Integer id;
    private Integer companyId;
    private Integer projectId;
    private Integer creatorId;
    private Integer attachId;
    private String attachType;
    private Boolean deleted;
    private Date created;
    private Date updated;
    private String creatorName;
    private String content;

    private UserDTO creatorDTO;
    private List<Integer> subscriberIds;
    private List<UserDTO> subscriberDTOs;
    private List<AttachmentDTO> attachmentDTOs;
    private List<AttachmentDTO> discardAttachmentDTOs;

    public CommentDTO() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getAttachId() {
        return attachId;
    }

    public void setAttachId(Integer attachId) {
        this.attachId = attachId;
    }

    public String getAttachType() {
        return attachType;
    }

    public void setAttachType(String attachType) {
        this.attachType = attachType;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserDTO getCreatorDTO() {
        return creatorDTO;
    }

    public void setCreatorDTO(UserDTO creatorDTO) {
        this.creatorDTO = creatorDTO;
    }

    public List<UserDTO> getSubscriberDTOs() {
        return subscriberDTOs;
    }

    public void setSubscriberDTOs(List<UserDTO> subscriberDTOs) {
        this.subscriberDTOs = subscriberDTOs;
    }

    public List<AttachmentDTO> getAttachmentDTOs() {
        return attachmentDTOs;
    }

    public void setAttachmentDTOs(List<AttachmentDTO> attachmentDTOs) {
        this.attachmentDTOs = attachmentDTOs;
    }

    public List<AttachmentDTO> getDiscardAttachmentDTOs() {
        return discardAttachmentDTOs;
    }

    public void setDiscardAttachmentDTOs(List<AttachmentDTO> discardAttachmentDTOs) {
        this.discardAttachmentDTOs = discardAttachmentDTOs;
    }

    public List<Integer> getSubscriberIds() {
        return subscriberIds;
    }

    public void setSubscriberIds(List<Integer> subscriberIds) {
        this.subscriberIds = subscriberIds;
    }
}
