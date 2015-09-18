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
package com.onboard.service.collaboration.index;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.onboard.domain.mapper.DiscussionMapper;
import com.onboard.domain.mapper.model.DiscussionExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Discussion;
import com.onboard.domain.model.type.Indexable;
import com.onboard.service.index.custom.IndexableService;
import com.onboard.service.index.model.IndexDocument;
import com.onboard.service.index.model.IndexDocumentBuilder;

/**
 * 针对{@link Discussion}实现的{@link IndexableService}
 * 
 * @author yewei
 *
 */
@Service("discussionIndexableServiceBean")
public class DiscussionIndexableService implements IndexableService {

    @Autowired
    private DiscussionMapper discussionMapper;

    @Override
    public String modelType() {
        return new Discussion().getType();
    }

    @Override
    public List<Indexable> getIndexablesByExample(BaseExample baseExample) {
        List<Indexable> items = new ArrayList<Indexable>();
        List<Discussion> discussions = discussionMapper.selectByExample((DiscussionExample) baseExample);
        items.addAll(discussions);
        return items;
    }

    @Override
    public IndexDocument indexableToIndexDocument(Indexable indexable) {
        Discussion discussion = discussionMapper.selectByPrimaryKey(indexable.getId());
        List<Integer> relators = Lists.newArrayList(discussion.getCreatorId());
        return IndexDocumentBuilder.getBuilder()
                .indexable(discussion)
                .content(discussion.getContent())
                .title(discussion.getSubject())
                .relatorIds(relators)
                .build();
    }

}
