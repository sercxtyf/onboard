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

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.mapper.TrashMapper;
import com.onboard.domain.mapper.model.TrashExample;
import com.onboard.domain.model.Trash;
import com.onboard.domain.model.type.BaseOperateItem;
import com.onboard.service.common.identifiable.IdentifiableManager;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractTrashServiceTest {

    @Mock
    protected TrashMapper mockedTrashMapper;

    @Mock
    protected IdentifiableManager mockedIdentifiableManager;
    
    @Mock 
    protected BaseOperateItem baseOperateItem;
    
    protected Trash trash;
    protected List<Trash> trashList;
    
    @Before
    public void setupTest() {
        initTrashMapper();
        initIdentifiableManager();
    }
    
    /** initTrashMapper **/
    private void initTrashMapper() {
        trash = getASampleTrash();
        trashList = getAListOfTrashes();
        when(mockedTrashMapper.selectByPrimaryKey(anyInt())).thenReturn(trash);
        when(mockedTrashMapper.deleteByPrimaryKey(anyInt())).thenReturn(1);
        when(mockedTrashMapper.selectByExample(Mockito.any(TrashExample.class))).thenReturn(trashList);
        when(mockedTrashMapper.countByExample(Mockito.any(TrashExample.class))).thenReturn(ModuleHelper.count);
        when(mockedTrashMapper.insert(Mockito.any(Trash.class))).thenReturn(1);
        when(mockedTrashMapper.deleteByExample(Mockito.any(TrashExample.class))).thenReturn(1);
        when(mockedTrashMapper.updateByPrimaryKey(Mockito.any(Trash.class))).thenReturn(1);
       
    }
    
    /** initIdentifiableManager **/
    private void initIdentifiableManager() {
        when(mockedIdentifiableManager.getIdentifiableByTypeAndId(anyString(), anyInt())).thenReturn(baseOperateItem);
    }
    
    /** **/
    private Trash getASampleTrash() {
        Trash t = new Trash();
        t.setId(ModuleHelper.id);
        t.setAttachId(ModuleHelper.attachId);
        t.setAttachType(ModuleHelper.attachType);
        return t;
    }
    
    private List<Trash> getAListOfTrashes() {
        List<Trash> list = new ArrayList<Trash>();
        list.add( getASampleTrash() );
        list.add( getASampleTrash() );
        return list;
    }
    
}
