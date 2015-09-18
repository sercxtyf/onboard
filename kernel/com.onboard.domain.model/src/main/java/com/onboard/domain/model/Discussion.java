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

import java.io.Serializable;
import java.util.List;

import com.onboard.domain.mapper.model.DiscussionObject;
import com.onboard.domain.model.type.Attachable;
import com.onboard.domain.model.type.Commentable;
import com.onboard.domain.model.type.Indexable;
import com.onboard.domain.model.type.Recommendable;
import com.onboard.domain.model.type.Subscribable;

/**
 * 领域模型：Discussion
 * 
 * @author yewei
 * 
 */
public class Discussion extends DiscussionObject implements Commentable, Subscribable, Attachable, Indexable, Serializable,
        Recommendable {

    private static final long serialVersionUID = 1L;

    private List<Comment> comments;

    private List<Attachment> attachments;

    private List<User> subscribers;

    private List<Attachment> discardAttachments;

    private User creator;

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Discussion() {
        super();
    }

    public Discussion(int id) {
        super(id);
    }

    public Discussion(boolean deleted) {
        super(deleted);
    }

    public Discussion(int id, boolean deleted) {
        super(id, deleted);
    }

    public Discussion(DiscussionObject obj) {
        super(obj);
    }

    @Override
    public List<Attachment> getDiscardAttachments() {
        return discardAttachments;
    }

    @Override
    public void setDiscardAttachments(List<Attachment> discardAttachments) {
        this.discardAttachments = discardAttachments;
    }

    @Override
    public List<Comment> getComments() {
        return comments;
    }

    @Override
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public List<Attachment> getAttachments() {
        return attachments;
    }

    @Override
    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public List<User> getSubscribers() {
        return subscribers;
    }

    @Override
    public void setSubscribers(List<User> subscribers) {
        this.subscribers = subscribers;
    }

    @Override
    public String getType() {
        return "discussion";
    }

    @Override
    public String getCommentSubject() {
        return getSubject();
    }

    @Override
    public String getSubscribableType() {
        return "discussion";
    }

    @Override
    public Integer getSubscribableId() {
        return getId();
    }

    @Override
    public String getSubscribableSubject() {
        return getSubject();
    }

    @Override
    public String generateText() {
        return getSubject();
    }

    @Override
    public boolean trashRequried() {
        return true;
    }
}
