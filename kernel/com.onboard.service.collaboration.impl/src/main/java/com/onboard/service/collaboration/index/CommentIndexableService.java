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
import com.onboard.domain.mapper.CommentMapper;
import com.onboard.domain.mapper.model.CommentExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Comment;
import com.onboard.domain.model.type.Commentable;
import com.onboard.domain.model.type.Indexable;
import com.onboard.service.collaboration.CommentService;
import com.onboard.service.index.custom.IndexableService;
import com.onboard.service.index.model.IndexDocument;
import com.onboard.service.index.model.IndexDocumentBuilder;

/**
 * 针对{@link Comment}实现的{@link IndexableService}
 * 
 * @author yewei
 *
 */
@Service("commentIndexableServiceBean")
public class CommentIndexableService implements IndexableService {

    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private CommentService commentService;

    @Override
    public String modelType() {
        return new Comment().getType();
    }

    @Override
    public List<Indexable> getIndexablesByExample(BaseExample baseExample) {
        List<Indexable> items = new ArrayList<Indexable>();
        List<Comment> comments = commentMapper.selectByExample((CommentExample) baseExample);
        items.addAll(comments);
        return items;
    }

    @Override
    public IndexDocument indexableToIndexDocument(Indexable indexable) {
        Comment comment = commentMapper.selectByPrimaryKey(indexable.getId());
        Commentable commentable = commentService.getCommentTarget(
                comment.getAttachType(), comment.getAttachId());
        List<Integer> relators = Lists.newArrayList(comment.getCreatorId());
        return IndexDocumentBuilder
                .getBuilder()
                .indexable(comment)
                .content(comment.getContent())
                .attachTtitle(commentable.getCommentSubject())
                .relatorIds(relators)
                .build();
    }

}
