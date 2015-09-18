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
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import com.onboard.domain.mapper.model.TrashExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Trash;
import com.onboard.service.collaboration.impl.TrashServiceImpl;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.exampleutils.ObjectMatcher;
import com.onboard.test.moduleutils.ModuleHelper;

public class TrashServiceImplTest extends AbstractTrashServiceTest{

    @InjectMocks
    private TrashServiceImpl trashServiceImpl;
    
    @Test
    public void testGetTrashById() {
        Trash ret = trashServiceImpl.getTrashById(ModuleHelper.id);
        verify(mockedTrashMapper, times(1)).selectByPrimaryKey(Mockito.argThat(new ObjectMatcher<Integer>() {
            @Override
            public boolean verifymatches(Integer item) {
                return item.equals(ModuleHelper.id);
            }
        }));
        verify(mockedIdentifiableManager, times(1)).getIdentifiableByTypeAndId(Mockito.argThat(new ObjectMatcher<String>() {
            @Override
            public boolean verifymatches(String item) {
                return item.equals(ModuleHelper.attachType);
            }
        }), Mockito.argThat(new ObjectMatcher<Integer>() {
            @Override
            public boolean verifymatches(Integer item) {
                return item.equals(ModuleHelper.attachId);
            }
        }));
        assertEquals(ret.getId(), new Integer(ModuleHelper.id));
    }
    
    @Test
    public void testGetTrashes() {
        List<Trash> ret = trashServiceImpl.getTrashes(ModuleHelper.start, ModuleHelper.limit);
        verify(mockedTrashMapper, times(1)).selectByExample(Mockito.argThat(new ExampleMatcher<TrashExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return example.getStart() == ModuleHelper.start && example.getLimit() == ModuleHelper.limit;
            }
        }));
        assertEquals(ret.size(), 2);
        assertEquals(ret.get(0).getId(), new Integer(ModuleHelper.id));
    }
    
    @Test
    public void testGetTrashesByExample() {
        List<Trash> ret = trashServiceImpl.getTrashesByExample(trash, ModuleHelper.start, ModuleHelper.limit);
        verify(mockedTrashMapper, times(1)).selectByExample(Mockito.argThat(new ExampleMatcher<TrashExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return example.getStart() == ModuleHelper.start && example.getLimit() == ModuleHelper.limit;
            }
        }));
        assertEquals(ret.size(), 2);
        assertEquals(ret.get(0).getId(), new Integer(ModuleHelper.id));
    }
    
    @Test
    public void testCountByExample() {
        int ret = trashServiceImpl.countByExample(trash);
        verify(mockedTrashMapper, times(1)).countByExample(Mockito.any(TrashExample.class));
        assertEquals(ret, ModuleHelper.count);
    }
    
    @Test
    public void testAddTrash() {
        Trash ret = trashServiceImpl.addTrash(trash);
        verify(mockedTrashMapper, times(1)).insert(Mockito.any(Trash.class));
        assertSame(ret, trash);
    }
    
    @Test
    public void testUpdateTrash() {
        Trash ret = trashServiceImpl.updateTrash(trash);
        verify(mockedTrashMapper, times(1)).updateByPrimaryKey(Mockito.any(Trash.class));
        assertSame(ret, trash);
    }
    
    @Test
    public void testDeleteTrash() {
        trashServiceImpl.deleteTrash(ModuleHelper.id);
        verify(mockedTrashMapper, times(1)).deleteByPrimaryKey(anyInt());
    }
    
    @Test
    public void testDeleteTrashByExample() {
        trashServiceImpl.deleteTrashByExample(trash);
        verify(mockedTrashMapper, times(1)).deleteByExample(Mockito.any(TrashExample.class));
    }
}
