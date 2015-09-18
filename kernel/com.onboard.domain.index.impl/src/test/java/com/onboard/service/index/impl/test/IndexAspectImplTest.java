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
package com.onboard.service.index.impl.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.type.Indexable;
import com.onboard.service.index.custom.IndexService;
import com.onboard.service.index.custom.IndexableService;
import com.onboard.service.index.custom.IndexableServices;
import com.onboard.service.index.impl.IndexAspectImpl;
import com.onboard.service.index.impl.IndexServices;
import com.onboard.service.index.model.IndexDocument;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class IndexAspectImplTest {

    @Mock
    private IndexableServices mockedIndexableServices;

    @Mock
    private IndexableService mockedIndexableService;

    @Mock
    private IndexServices mockedIndexServices;

    @Mock
    private IndexService mockedIndexService;

    @Mock
    private IndexDocument mockedIndexDocument;

    @Mock
    private ProceedingJoinPoint mockedJoinPoint;

    private Object jointPointRetureValue;

    private Object[] jointPointArgs;

    @InjectMocks
    private IndexAspectImpl indexAsapectImpl;

    @Before
    public void setup() {
        setupIndexableServices();
        setupIndexServices();
        setupJointPoint();
    }

    private void setupIndexableServices() {
        when(mockedIndexableService.getIndexablesByExample(Mockito.any(BaseExample.class))).thenReturn(
                IndexSampleHelper.getASampleIndexableList());
        when(mockedIndexableService.modelType()).thenReturn(ModuleHelper.type);
        when(mockedIndexDocument.needIndex()).thenReturn(true);
        when(mockedIndexableService.indexableToIndexDocument(Mockito.any(Indexable.class))).thenReturn(mockedIndexDocument);
        when(mockedIndexServices.getIndexService()).thenReturn(mockedIndexService);
        when(mockedIndexableServices.getIndexableService(Mockito.anyString())).thenReturn(mockedIndexableService);
    }

    private void setupIndexServices() {
        when(mockedIndexableServices.getIndexableService(Mockito.any(Indexable.class))).thenReturn(mockedIndexableService);
    }

    private void setupJointPoint() {
        jointPointRetureValue = new Object();
        try {
            when(mockedJoinPoint.proceed()).thenReturn(jointPointRetureValue);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        jointPointArgs = new Object[2];
        jointPointArgs[0] = 1;
        jointPointArgs[1] = 2;
        when(mockedJoinPoint.getArgs()).thenReturn(jointPointArgs);
    }

    @Test
    public void testInsertSelective() {
        Indexable item = IndexSampleHelper.getASampleIndexable();
        indexAsapectImpl.insertSelective(item);
        verifyInsert();
    }

    private void verifyInsert() {
        verify(mockedIndexServices, Mockito.times(1)).getIndexService();
        verify(mockedIndexService, Mockito.times(1)).addIndex(Mockito.any(Indexable.class));
    }

    @Test
    public void testInsert() {
        Indexable item = IndexSampleHelper.getASampleIndexable();
        indexAsapectImpl.insert(item);
        verifyInsert();
    }

    @Test
    public void testUpdateByPrimaryKeySelective() {
        Indexable item = IndexSampleHelper.getASampleIndexable();
        indexAsapectImpl.updateByPrimaryKeySelective(item);
        verifyUpdateByPrimaryKey(item);
    }

    private void verifyUpdateByPrimaryKey(Indexable item) {
        verify(mockedIndexableServices, Mockito.times(1)).getIndexableService(item);
        verify(mockedIndexServices, Mockito.times(1)).getIndexService();
        verify(mockedIndexService, Mockito.times(1)).updateIndex(item);
    }

    @Test
    public void testUpdateByPrimaryKey() {
        Indexable item = IndexSampleHelper.getASampleIndexable();
        indexAsapectImpl.updateByPrimaryKey(item);
        verifyUpdateByPrimaryKey(item);
    }

    private void invokeUpdateByExampleBySelective(Boolean selective, Indexable item, BaseExample itemExample) {
        if (selective) {
            indexAsapectImpl.updateByExampleSelective(item, itemExample);
        } else {
            indexAsapectImpl.updateByExample(item, itemExample);
        }
    }

    private void testCommonUpdateByExample(Boolean selective) {
        Indexable item = IndexSampleHelper.getASampleIndexable();
        BaseExample itemExample = IndexSampleHelper.getASampleBaseExample();
        // need Index
        invokeUpdateByExampleBySelective(selective, item, itemExample);
        verify(mockedIndexableService, Mockito.times(1)).getIndexablesByExample(itemExample);
        verify(mockedIndexableServices, Mockito.times(2)).getIndexableService(Mockito.any(Indexable.class));
        verify(mockedIndexServices, Mockito.times(1)).getIndexService();
        verify(mockedIndexService, Mockito.times(1)).updateIndex(Mockito.any(Indexable.class));

        // don't need index
        when(mockedIndexDocument.needIndex()).thenReturn(false);
        invokeUpdateByExampleBySelective(selective, item, itemExample);
        // 2 + 1
        verify(mockedIndexableServices, Mockito.times(3)).getIndexableService(Mockito.any(Indexable.class));

        // indexableService == null
        when(mockedIndexableServices.getIndexableService(Mockito.any(Indexable.class))).thenReturn(null);
        invokeUpdateByExampleBySelective(selective, item, itemExample);
        // 3 + 1
        verify(mockedIndexableServices, Mockito.times(4)).getIndexableService(Mockito.any(Indexable.class));
    }

    @Test
    public void testUpdateByExampleSelective() {
        testCommonUpdateByExample(true);
    }

    @Test
    public void testUpdateByExample() {
        testCommonUpdateByExample(false);
    }

    @Test
    public void testDeleteByPrimaryKey() {
        Object actualReturnValue = indexAsapectImpl.deleteByPrimaryKey(mockedJoinPoint);
        verify(mockedIndexableServices, Mockito.times(1)).getIndexableService(Mockito.anyString());
        try {
            verify(mockedJoinPoint, Mockito.times(1)).proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        verify(mockedJoinPoint, Mockito.times(1)).getArgs();
        verify(mockedIndexServices, Mockito.times(1)).getIndexService();
        String type = mockedIndexableService.modelType();
        Integer id = (Integer) mockedJoinPoint.getArgs()[0];
        String documentId = type.substring(type.lastIndexOf(".") + 1).toLowerCase() + "_" + id;
        verify(mockedIndexService, Mockito.times(1)).deleteIndexById(Mockito.contains(documentId));
        assertEquals(jointPointRetureValue, actualReturnValue);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDeleteByExample() {
        jointPointArgs = new Object[2];
        jointPointArgs[0] = IndexSampleHelper.getASampleBaseExample();
        jointPointArgs[1] = IndexSampleHelper.getASampleBaseExample();
        when(mockedJoinPoint.getArgs()).thenReturn(jointPointArgs);
        Object actualReturnValue = indexAsapectImpl.deleteByExample(mockedJoinPoint);
        verify(mockedIndexableServices, Mockito.times(1)).getIndexableService(Mockito.anyString());
        verify(mockedJoinPoint, Mockito.times(1)).getArgs();
        verify(mockedIndexableService, Mockito.times(1)).getIndexablesByExample(Mockito.any(BaseExample.class));
        verify(mockedIndexServices, Mockito.times(1)).getIndexService();
        verify(mockedIndexService, Mockito.times(1)).deleteIndexByIdList(Mockito.anyList());
        assertEquals(jointPointRetureValue, actualReturnValue);
    }
}
