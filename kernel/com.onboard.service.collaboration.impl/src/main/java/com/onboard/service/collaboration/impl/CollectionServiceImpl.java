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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onboard.domain.mapper.CollectionMapper;
import com.onboard.domain.mapper.model.CollectionExample;
import com.onboard.domain.model.Collection;
import com.onboard.domain.model.type.Recommendable;
import com.onboard.service.collaboration.CollectionService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.common.identifiable.IdentifiableManager;

@Transactional
@Service("collectionServiceBean")
public class CollectionServiceImpl implements CollectionService {

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private IdentifiableManager identifiableManager;

    @Autowired
    private ProjectService projectService;

    @Override
    public Collection getCollectionById(int id) {
        return collectionMapper.selectByPrimaryKey(id);
    }

    @Override
    public Collection createCollection(int userId, int attachId, String attachType) {
        Recommendable identifiable = (Recommendable) identifiableManager.getIdentifiableByTypeAndId(attachType, attachId);
        if (identifiable != null) {
            Collection collection = new Collection(false);
            collection.setAttachId(attachId);
            collection.setAttachType(attachType);
            collection.setUserId(userId);
            collection.setTitle(identifiable.generateText());
            collection.setCompanyId(identifiable.getCompanyId());
            collection.setProjectId(identifiable.getProjectId());
            collection.setCreatorId(identifiable.getCreatorId());
            collection.setCreatorName(identifiable.getCreatorName());
            collection.setCreatorAvatar(identifiable.getCreatorAvatar());
            collection.setProjectName(projectService.getById(identifiable.getProjectId()).getName());
            collectionMapper.insertSelective(collection);
            return collection;
        }
        return null;
    }

    @Override
    public List<Collection> getCollectionsByUserId(int userId) {
        Collection collectionExample = new Collection(false);
        collectionExample.setUserId(userId);
        return collectionMapper.selectByExample(new CollectionExample(collectionExample));
    }

    @Override
    public void deleteCollection(int id) {
        Collection collection = new Collection();
        collection.setId(id);
        collection.setDeleted(true);
        collectionMapper.updateByPrimaryKeySelective(collection);
    }

    @Override
    public List<Collection> getCollectionsByAttachTypeAndId(int userId, int attachId, String attachType) {
        Collection collectionExample = new Collection(false);
        collectionExample.setUserId(userId);
        collectionExample.setAttachId(attachId);
        collectionExample.setAttachType(attachType);

        return collectionMapper.selectByExample(new CollectionExample(collectionExample));
    }

}
