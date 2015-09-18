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
package com.onboard.service.base;

import java.util.Date;
import java.util.List;

import com.onboard.domain.mapper.base.BaseMapper;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.type.BaseOperateItem;

public abstract class AbstractBaseService<I extends BaseOperateItem, E extends BaseExample> implements BaseService<I, E> {

    protected abstract BaseMapper<I, E> getBaseMapper();
    
    @Override
    public I getById(int id) {
        return getBaseMapper().selectByPrimaryKey(id);
    }
    
    @Override
    public I getByIdWithDetail(int id){
        return getById(id);
    }
    
    @Override
    public List<I> getAll(){
        return getAll(0, -1);
    }

    @Override
    public List<I> getAll(int start, int limit) {
        E example = newExample();
        example.setLimit(start, limit);
        return getBaseMapper().selectByExample(example);
    }

    @Override
    public List<I> getBySample(I item) {
        return getBySample(item, 0, -1);
    }

    @Override
    public List<I> getBySample(I item, int start, int limit) {
        E example = newExample(item);
        example.setLimit(start, limit);
        example.setOrderByClause("created desc");
        return getBaseMapper().selectByExample(example);
    }

    @Override
    public int countBySample(I item) {
        E example = newExample(item);
        return getBaseMapper().countByExample(example);
    }

    @Override
    public List<I> getByExample(E example) {
        return getBaseMapper().selectByExample(example);
    }

    @Override
    public int countByExample(E example) {
        return getBaseMapper().countByExample(example);
    }
    
    protected I fillItemBeforeCreate(I item){
        return item;
    }

    @Override
    public I create(I item) {
        item.setCreated(new Date());
        item.setDeleted(false);
        item.setUpdated(new Date());
        fillItemBeforeCreate(item);
        getBaseMapper().insert(item);
        return item;
    }

    @Override
    public I update(I item) {
        if(item == null || item.getId() == null){
            return null;
        }
        item.setUpdated(new Date());
        getBaseMapper().updateByPrimaryKey(item);
        return item;
    }

    @Override
    public I updateSelective(I item) {
        if(item == null || item.getId() == null){
            return null;
        }
        item.setUpdated(new Date());
        getBaseMapper().updateByPrimaryKeySelective(item);
        return item;
    }

    @Override
    public void delete(int id) {
        if(newItem().trashRequried()){
            I item = newItem();
            item.setId(id);
            item.setDeleted(true);
            getBaseMapper().updateByPrimaryKeySelective(item);
        }else {
            deleteFromTrash(id);
        }
    }
    
    @Override
    public void recover(int id){
        if(!newItem().trashRequried()){
            return;
        }
        I item = newItem();
        item.setId(id);
        item.setDeleted(false);
        getBaseMapper().updateByPrimaryKeySelective(item);
    }
    
    @Override
    public void deleteFromTrash(int id){
        getBaseMapper().deleteByPrimaryKey(id);
    }

    @Override
    public final String getModelType() {
        return newItem().getType();
    }
    
}
