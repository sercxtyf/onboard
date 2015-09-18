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
package com.onboard.service.index.impl;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.type.Indexable;
import com.onboard.service.index.IndexAspect;
import com.onboard.service.index.custom.IndexableService;
import com.onboard.service.index.custom.IndexableServices;

/**
 * 进行索引操作的辅助类，可作为Aspect织入到mapper操作中
 * 
 * @author yewei
 * 
 */
@Service("indexAspectBean")
public class IndexAspectImpl implements IndexAspect {

    public static final Logger logger = LoggerFactory.getLogger(IndexAspectImpl.class);

    @Autowired
    private IndexServices indexServices;
    
    @Autowired
    private IndexableServices indexableServices;

    /**
     * 获取solr中索引文档的id
     * 
     * TODO:文档id的命名规则可以自定义
     * 
     * @param type
     * @param id
     * @return
     */
    private String getDocumentId(String type, Integer id) {
        // TODO: set tableName as full name of model type
        type = type.substring(type.lastIndexOf(".") + 1).toLowerCase();
        return type + "_" + id;
    }

    @Override
    public void insert(Indexable item) {
        insertSelective(item);
    }

    @Override
    public void insertSelective(Indexable item) {
        indexServices.getIndexService().addIndex(item);
    }

    @Override
    public void updateByPrimaryKey(Indexable item) {
        updateByPrimaryKeySelective(item);
    }

    @Override
    public void updateByPrimaryKeySelective(Indexable item) {
        IndexableService indexableService = indexableServices.getIndexableService(item);
        if (indexableService != null) {
            indexServices.getIndexService().updateIndex(item);
        }
    }

    @Override
    public void updateByExample(Indexable item, BaseExample itemExample) {
        updateByExampleSelective(item, itemExample);
    }

    @Override
    public void updateByExampleSelective(Indexable item, BaseExample itemExample) {
        if(item == null){
            return;
        }
        IndexableService indexableService = indexableServices.getIndexableService(item);
        if(indexableService == null || !indexableService.indexableToIndexDocument(item).needIndex()){
            return;
        }
        List<Indexable> items = indexableService.getIndexablesByExample(itemExample);
        for (Indexable indexable : items) {
            updateByPrimaryKeySelective(indexable);
        }
    }

    @Override
    public Object deleteByPrimaryKey(ProceedingJoinPoint joinpoint) {
        Object returnVal = null;
        //String modelType = joinpoint.getTarget().getClass().getName();
        // TODO: get model type from mapper
        String modelType = "";
        IndexableService indexableService = indexableServices.getIndexableService(modelType);

        try {
            returnVal = joinpoint.proceed();
            if (indexableService != null) {
                String documentId = this.getDocumentId(indexableService.modelType(), (Integer) joinpoint.getArgs()[0]);
                indexServices.getIndexService().deleteIndexById(documentId);
            }
        } catch (Throwable e) {
            // TODO 是否应该让方法将异常抛出，Activity的around advice同样有这个问题
            logger.error("fail to update index: ", e);
        }
        return returnVal;
    }

    @Override
    public Object deleteByExample(ProceedingJoinPoint joinpoint) {
        Object returnVal = null;
        //String modelType = joinpoint.getTarget().getClass().getName();
        // TODO: get model type from mapper
        String modelType = "";
        IndexableService indexableService = indexableServices.getIndexableService(modelType);
        List<String> documentIdList = new ArrayList<String>();
        if (indexableService != null) {
            BaseExample example = (BaseExample) joinpoint.getArgs()[0];
            List<Indexable> items = indexableService.getIndexablesByExample(example);

            documentIdList = new ArrayList<String>();
            for (Indexable item : items) {
                documentIdList.add(this.getDocumentId(item.getType(), item.getId()));
            }
        }
        try {
            returnVal = joinpoint.proceed();
            if (!documentIdList.isEmpty()) {
                indexServices.getIndexService().deleteIndexByIdList(documentIdList);
            }

        } catch (Throwable e) {
            logger.error("fail to update index: ", e);
        }
        return returnVal;
    }
}
