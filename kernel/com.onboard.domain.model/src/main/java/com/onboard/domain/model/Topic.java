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

import com.onboard.domain.mapper.model.TopicObject;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.domain.model.type.Recommendable;

/**
 * 领域模型：Topic
 * 
 * @author yewei
 * 
 */
public class Topic extends TopicObject implements BaseProjectItem, Recommendable {

    private static final long serialVersionUID = -5954216412712546676L;

    private User lastUpdator;

    private String attachUrl;

    private User targetCreator;

    public Topic() {
        super();
    }

    public Topic(int id) {
        super(id);
    }

    public Topic(boolean deleted) {
        super(deleted);
    }

    public Topic(int id, boolean deleted) {
        super(id, deleted);
    }

    public Topic(TopicObject obj) {
        super(obj);
    }

    public User getLastUpdator() {
        return lastUpdator;
    }

    public void setLastUpdator(User lastUpdator) {
        this.lastUpdator = lastUpdator;
    }

    public String getAttachUrl() {
        return attachUrl;
    }

    public void setAttachUrl(String attachUrl) {
        this.attachUrl = attachUrl;
    }

    public User getTargetCreator() {
        return targetCreator;
    }

    public void setTargetCreator(User creator) {
        this.targetCreator = creator;
    }

    @Override
    public String getType() {
        return "topic";
    }

    @Override
    public Integer getCreatorId() {
        return targetCreator.getId();
    }

    @Override
    public void setCreatorId(Integer creatorId) {

    }

    @Override
    public String getCreatorName() {
        return targetCreator.getName();
    }

    @Override
    public void setCreatorName(String creatorName) {
    }

    @Override
    public String getCreatorAvatar() {
        return targetCreator.getAvatar();
    }

    @Override
    public void setCreatorAvatar(String creatorAvatar) {
    }

    @Override
    public String generateText() {
        return null;
    }

    @Override
    public boolean trashRequried() {
        return false;
    }
}
