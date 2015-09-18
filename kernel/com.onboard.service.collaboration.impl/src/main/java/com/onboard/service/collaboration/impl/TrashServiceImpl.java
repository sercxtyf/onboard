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

import com.onboard.domain.mapper.TrashMapper;
import com.onboard.domain.mapper.model.TrashExample;
import com.onboard.domain.model.Trash;
import com.onboard.service.collaboration.TrashService;
import com.onboard.service.common.identifiable.IdentifiableManager;

/**
 * {@link com.onboard.service.collaboration.TrashService} Service implementation
 * 
 * @generated_by_elevenframework
 * 
 */
@Transactional
@Service("trashServiceBean")
public class TrashServiceImpl implements TrashService {

    @Autowired
    private TrashMapper trashMapper;

    @Autowired
    private IdentifiableManager identifiableManager;

    @Override
    public Trash getTrashById(int id) {
        Trash trash = trashMapper.selectByPrimaryKey(id);
        trash.setIdentifiable(identifiableManager.getIdentifiableByTypeAndId(trash.getAttachType(), trash.getAttachId()));
        return trash;
    }

    @Override
    public List<Trash> getTrashes(int start, int limit) {
        TrashExample example = new TrashExample(new Trash());
        example.setLimit(start, limit);
        return trashMapper.selectByExample(example);
    }

    @Override
    public List<Trash> getTrashesByExample(Trash item, int start, int limit) {
        TrashExample example = new TrashExample(item);
        example.setLimit(start, limit);
        return trashMapper.selectByExample(example);
    }

    @Override
    public int countByExample(Trash item) {
        TrashExample example = new TrashExample(item);
        return trashMapper.countByExample(example);
    }

    @Override
    public Trash addTrash(Trash item) {
        trashMapper.insert(item);
        return item;
    }

    @Override
    public Trash updateTrash(Trash item) {
        trashMapper.updateByPrimaryKey(item);
        return item;
    }

    @Override
    public void deleteTrash(int id) {
        trashMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void deleteTrashByExample(Trash trash) {
        trashMapper.deleteByExample(new TrashExample(trash));
    }

}
