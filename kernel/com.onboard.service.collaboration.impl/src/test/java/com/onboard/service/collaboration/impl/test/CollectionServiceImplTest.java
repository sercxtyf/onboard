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
package com.onboard.service.collaboration.impl.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import com.onboard.domain.mapper.model.CollectionExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Collection;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.collaboration.impl.CollectionServiceImpl;
import com.onboard.service.collaboration.impl.abstractfiles.AbstractCollectionTest;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.exampleutils.ObjectMatcher;
import com.onboard.test.moduleutils.ModuleHelper;

public class CollectionServiceImplTest extends AbstractCollectionTest {

    @InjectMocks
    private CollectionServiceImpl collectionServiceImpl;

    private void runAsserts(Collection collection, boolean deleted) {
        assertEquals(ModuleHelper.id, (int) collection.getId());
        assertEquals(ModuleHelper.companyId, (int) collection.getCompanyId());
        assertEquals(ModuleHelper.projectId, (int) collection.getProjectId());
        assertEquals(ModuleHelper.title, collection.getTitle());
        assertEquals(ModuleHelper.creatorId, (int) collection.getCreatorId());
        assertEquals(deleted, collection.getDeleted());
        assertEquals(ModuleHelper.creatorName, collection.getCreatorName());
        assertEquals(ModuleHelper.attachId, (int) collection.getAttachId());
        assertEquals(ModuleHelper.attachType, collection.getAttachType());
        assertEquals(ModuleHelper.projectName, collection.getProjectName());
        assertEquals(ModuleHelper.title, collection.getTitle());
        assertEquals(ModuleHelper.userId, (int) collection.getUserId());
    }

    private void runAsserts(List<Collection> collections, boolean deleted) {
        for (Collection collection : collections) {
            runAsserts(collection, deleted);
        }
    }

    @Test
    public void testGetCollectionById() {
        Collection collection = collectionServiceImpl.getCollectionById(ModuleHelper.id);
        verify(mockedCollectionMapper, times(1)).selectByPrimaryKey(ModuleHelper.id);
        Mockito.verifyNoMoreInteractions(mockedCollectionMapper);
        runAsserts(collection, false);
    }

    @Test
    public void testCreateCollection() {
        Collection collection = collectionServiceImpl.createCollection(ModuleHelper.userId, ModuleHelper.attachId,
                ModuleHelper.attachType);
        verify(mockedIdentifiableManager, times(1)).getIdentifiableByTypeAndId(ModuleHelper.attachType, ModuleHelper.attachId);
        BaseProjectItem identifiable = (BaseProjectItem) mockedIdentifiableManager.getIdentifiableByTypeAndId(
                ModuleHelper.attachType, ModuleHelper.attachId);
        verify(mockedProjectService, times(1)).getById(identifiable.getProjectId());

        assertEquals(identifiable.getCompanyId(), collection.getCompanyId());
        assertEquals(identifiable.getProjectId(), collection.getProjectId());
        assertEquals(mockedProjectService.getById(identifiable.getProjectId()).getName(), collection.getProjectName());
        assertEquals(identifiable.getCreatorId(), collection.getCreatorId());
        assertEquals(identifiable.getCreatorName(), collection.getCreatorName());
        assertEquals(false, collection.getDeleted());
    }

    @Test
    public void testGetCollectionsByUserId() {
        List<Collection> collections = collectionServiceImpl.getCollectionsByUserId(ModuleHelper.userId);
        verify(mockedCollectionMapper, times(1)).selectByExample(Mockito.argThat(new ExampleMatcher<CollectionExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "userId", ModuleHelper.userId);
            }
        }));
        Mockito.verifyNoMoreInteractions(mockedCollectionMapper);
        runAsserts(collections, false);
    }

    @Test
    public void testDeleteCollection() {
        collectionServiceImpl.deleteCollection(ModuleHelper.id);
        Collection deleteCollectionExample = new Collection(true);
        deleteCollectionExample.setId(ModuleHelper.id);
        verify(mockedCollectionMapper, times(1)).updateByPrimaryKeySelective(Mockito.argThat(new ObjectMatcher<Collection>() {

            @Override
            public boolean verifymatches(Collection item) {
                return item.getId().equals(ModuleHelper.id) && item.getDeleted();
            }

        }));
        Mockito.verifyNoMoreInteractions(mockedCollectionMapper);
    }

    @Test
    public void testGetCollectionsByAttachTypeAndId() {

        List<Collection> collections = collectionServiceImpl.getCollectionsByAttachTypeAndId(ModuleHelper.userId,
                ModuleHelper.attachId, ModuleHelper.attachType);
        verify(mockedCollectionMapper, times(1)).selectByExample(Mockito.argThat(new ExampleMatcher<CollectionExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "userId", ModuleHelper.userId)
                        && CriterionVerifier.verifyEqualTo(example, "attachType", ModuleHelper.attachType)
                        && CriterionVerifier.verifyEqualTo(example, "attachId", ModuleHelper.attachId);
            }
        }));
        Mockito.verifyNoMoreInteractions(mockedCollectionMapper);
        runAsserts(collections, false);
    }

}
