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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.onboard.domain.mapper.TodoMapper;
import com.onboard.domain.mapper.model.TodoExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.type.Indexable;
import com.onboard.service.index.custom.IndexableService;
import com.onboard.service.index.model.IndexDocument;
import com.onboard.service.index.model.IndexDocumentBuilder;

/**
 * 针对{@link Todo}实现的{@link IndexableService}
 * 
 * @author yewei
 * 
 */
@Service("todoIndexableServiceBean")
public class TodoIndexableService implements IndexableService {

    @Autowired
    private TodoMapper todoMapper;

    @Override
    public String modelType() {
        return new Todo().getType();
    }

    @Override
    public List<Indexable> getIndexablesByExample(BaseExample baseExample) {
        List<Indexable> items = new ArrayList<Indexable>();
        List<Todo> todos = todoMapper.selectByExample((TodoExample) baseExample);
        items.addAll(todos);
        return items;
    }

    @Override
    public IndexDocument indexableToIndexDocument(Indexable indexable) {
        Todo todo = todoMapper.selectByPrimaryKey(indexable.getId());
        List<Integer> relators = Lists.newArrayList(todo.getAssigneeId());
        relators.add(todo.getCreatorId());
        return IndexDocumentBuilder.getBuilder().indexable(todo).title(todo.getContent())
                .relatorIds(relators)
                .extendIndexFields(ImmutableMap.of("deuDate_dt", (Object) todo.getDueDate()))
                .build();
    }

}
