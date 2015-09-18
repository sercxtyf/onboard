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
package com.onboard.domain.model;

import java.util.Date;
import java.util.List;

import com.onboard.domain.mapper.model.AttachmentObject;
import com.onboard.domain.model.type.Taggable;

/**
 * 领域模型：Activity
 * 
 * @author yewei
 */
public class Attachment extends AttachmentObject implements Taggable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String attachUrl;

    private User creator;

    private Project project;

    private List<Tag> tags;

    @Override
    public List<Tag> getTags() {
        return tags;
    }

    @Override
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Attachment() {
        super();
    }

    public Attachment(int id) {
        super(id);
    }

    public Attachment(boolean deleted) {
        super(deleted);
    }

    public Attachment(int id, boolean deleted) {
        super(id, deleted);
    }

    public Attachment(AttachmentObject obj) {
        super(obj);
    }

    public String getAttachUrl() {
        return attachUrl;
    }

    public void setAttachUrl(String attachUrl) {
        this.attachUrl = attachUrl;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    @Override
    public String getType() {
        return "attachment";
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Attachment)) {
            return false;
        }
        Attachment att = (Attachment) obj;
        return this.getId().equals(att.getId());
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public Date getUpdated() {
        return getCreated();
    }

    @Override
    public void setUpdated(Date updated) {
        // TODO
    }

    @Override
    public boolean trashRequried() {
        return false;
    }

}
