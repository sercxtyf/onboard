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

import java.util.List;

import com.onboard.domain.mapper.model.CommentObject;
import com.onboard.domain.model.type.Attachable;
import com.onboard.domain.model.type.Indexable;
import com.onboard.domain.model.type.Recommendable;
import com.onboard.domain.model.type.Subscribable;

/**
 * 领域模型：Comment
 * 
 * @author yewei
 * 
 */
public class Comment extends CommentObject implements Subscribable, Attachable, Indexable, Recommendable {

    private static final long serialVersionUID = -9169376019504457581L;

    private List<Attachment> attachments;

    private User creator;

    private List<User> subscribers;

    private Subscribable attach;

    private List<Attachment> discardAttachments;

    public Comment() {
        super();
    }

    public Comment(int id) {
        super(id);
    }

    public Comment(boolean deleted) {
        super(deleted);
    }

    public Comment(int id, boolean deleted) {
        super(id, deleted);
    }

    public Comment(CommentObject obj) {
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
    public List<Attachment> getAttachments() {
        return attachments;
    }

    @Override
    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String getType() {
        return "comment";
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
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
    public String getSubscribableType() {
        return getAttachType();
    }

    @Override
    public Integer getSubscribableId() {
        return getAttachId();
    }

    @Override
    public String getSubscribableSubject() {
        if (attach == null) {
            return "";
        }
        return attach.getSubscribableSubject();
    }

    public Subscribable getAttach() {
        return attach;
    }

    public void setAttach(Subscribable attach) {
        this.attach = attach;
    }

    @Override
    public String generateText() {
        return getContent();
    }

    @Override
    public boolean trashRequried() {
        return true;
    }
}
