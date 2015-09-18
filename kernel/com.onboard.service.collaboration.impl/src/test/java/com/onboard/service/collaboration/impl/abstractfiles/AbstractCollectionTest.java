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
package com.onboard.service.collaboration.impl.abstractfiles;

import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.onboard.domain.mapper.CollectionMapper;
import com.onboard.domain.mapper.model.CollectionExample;
import com.onboard.domain.mapper.model.common.BaseItem;
import com.onboard.domain.model.Collection;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.domain.model.type.Recommendable;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.common.identifiable.IdentifiableManager;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractCollectionTest {
    @Mock
    protected CollectionMapper mockedCollectionMapper;

    @Mock
    protected IdentifiableManager mockedIdentifiableManager;

    @Mock
    protected ProjectService mockedProjectService;

    protected Collection collection;
    protected List<Collection> collections;
    protected CollectionExample collectionExample;

    public static int mapperReturnValue = 1;

    @Before
    public void setupCollectionTest() {
        initCollectionMapper();
        initIdentifiableManager();
        initProjectService();
    }

    private void initCollectionMapper() {
        collection = getASampleCollection();
        collections = getAListOfSampleCollection();
        collectionExample = getASampleCollectionExample();

        when(mockedCollectionMapper.countByExample(Mockito.any(CollectionExample.class))).thenReturn(ModuleHelper.count);

        when(mockedCollectionMapper.deleteByExample(Mockito.any(CollectionExample.class))).thenReturn(mapperReturnValue);
        when(mockedCollectionMapper.deleteByPrimaryKey(ModuleHelper.id)).thenReturn(mapperReturnValue);

        when(mockedCollectionMapper.insert(Mockito.any(Collection.class))).thenReturn(mapperReturnValue);
        when(mockedCollectionMapper.insertSelective(Mockito.any(Collection.class))).thenReturn(mapperReturnValue);

        when(mockedCollectionMapper.selectByExample(Mockito.any(CollectionExample.class))).thenReturn(collections);
        when(mockedCollectionMapper.selectByPrimaryKey(ModuleHelper.id)).thenReturn(collection);

        when(mockedCollectionMapper.updateByExample(Mockito.any(Collection.class), Mockito.any(CollectionExample.class)))
                .thenReturn(mapperReturnValue);
        when(mockedCollectionMapper.updateByExampleSelective(Mockito.any(Collection.class), Mockito.any(CollectionExample.class)))
                .thenReturn(mapperReturnValue);
        when(mockedCollectionMapper.updateByPrimaryKey(Mockito.any(Collection.class))).thenReturn(mapperReturnValue);
        when(mockedCollectionMapper.updateByPrimaryKeySelective(Mockito.any(Collection.class))).thenReturn(mapperReturnValue);

    }

    private void initIdentifiableManager() {
    	Recommendable identifiable = AbstractCollectionTest.getASampleRecommendable();

        when(mockedIdentifiableManager.getIdentifiableByTypeAndId(Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(identifiable);
    }

    private void initProjectService() {
        Project project = AbstractProjectTest.getASampleProject();

        when(mockedProjectService.getById(Mockito.anyInt())).thenReturn(project);
    }

    public static Collection getASampleCollection() {
        Collection collection = new Collection(false);
        collection.setId(ModuleHelper.id);
        collection.setCompanyId(ModuleHelper.companyId);
        collection.setProjectId(ModuleHelper.projectId);
        collection.setAttachId(ModuleHelper.attachId);
        collection.setAttachType(ModuleHelper.attachType);
        collection.setCreatorId(ModuleHelper.creatorId);
        collection.setCreatorName(ModuleHelper.creatorName);
        collection.setProjectName(ModuleHelper.projectName);
        collection.setTitle(ModuleHelper.title);
        collection.setUserId(ModuleHelper.userId);
        return collection;
    }

    public static Collection getASampleDeletedCollection() {
        Collection collection = getASampleCollection();
        collection.setDeleted(true);
        return collection;
    }

    public static List<Collection> getAListOfSampleCollection() {
        return Lists.newArrayList(getASampleCollection(), getASampleCollection());
    }

    public static CollectionExample getASampleCollectionExample() {
        return new CollectionExample(getASampleCollection());
    }

    public static Recommendable getASampleRecommendable() {
        return new Recommendable() {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public String getType() {
                return ModuleHelper.type;
            }

            @Override
            public Integer getProjectId() {
                return ModuleHelper.projectId;
            }

            @Override
            public Integer getId() {
                return ModuleHelper.id;
            }

            @Override
            public Boolean getDeleted() {
                return false;
            }

            @Override
            public String getCreatorName() {
                return ModuleHelper.creatorName;
            }

            @Override
            public Integer getCreatorId() {
                return ModuleHelper.creatorId;
            }

            @Override
            public Integer getCompanyId() {
                return ModuleHelper.companyId;
            }

            @Override
            public void setCompanyId(Integer companyId) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setCreatorId(Integer creatorId) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setCreatorName(String creatorName) {
                // TODO Auto-generated method stub

            }

            @Override
            public String getCreatorAvatar() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void setCreatorAvatar(String creatorAvatar) {
                // TODO Auto-generated method stub

            }

            @Override
            public Date getCreated() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void setCreated(Date created) {
                // TODO Auto-generated method stub

            }

            @Override
            public Date getUpdated() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void setUpdated(Date updated) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setDeleted(Boolean deleted) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setId(Integer id) {
                // TODO Auto-generated method stub

            }

            @Override
            public BaseItem copy() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void setProjectId(Integer projectId) {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean trashRequried() {
                // TODO Auto-generated method stub
                return false;
            }

			@Override
			public String generateText() {
				// TODO Auto-generated method stub
				return null;
			}
        };
    }

}
