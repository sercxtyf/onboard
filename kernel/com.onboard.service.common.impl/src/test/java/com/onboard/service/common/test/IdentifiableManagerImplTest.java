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
package com.onboard.service.common.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.model.type.BaseOperateItem;
import com.onboard.service.base.BaseService;
import com.onboard.service.common.identifiable.impl.IdentifiableManagerImpl;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class IdentifiableManagerImplTest {

    private final static boolean TRASH_REQUIRED = true;

    @SuppressWarnings("rawtypes")
    @Mock
    private BaseService mockedIdentifiableService;

    @InjectMocks
    private IdentifiableManagerImpl identifiableManagerImpl;

    @Mock
    private BaseOperateItem mockedIdentifiable;

    @Before
    public void setup() {
        when(mockedIdentifiable.getType()).thenReturn(ModuleHelper.type);
        when(mockedIdentifiableService.getModelType()).thenReturn(ModuleHelper.type);
        when(mockedIdentifiableService.getById(Mockito.anyInt())).thenReturn(mockedIdentifiable);
        when(mockedIdentifiableService.getByIdWithDetail(Mockito.anyInt())).thenReturn(mockedIdentifiable);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void addIdenifiableServiceTest() {
        assertNull(identifiableManagerImpl.getIdentifiableService(ModuleHelper.type));
        identifiableManagerImpl.addIdentifiableService(null);
        verify(mockedIdentifiableService, Mockito.times(0)).getModelType();
        assertNull(identifiableManagerImpl.getIdentifiableService(ModuleHelper.type));
        identifiableManagerImpl.addIdentifiableService(mockedIdentifiableService);
        verify(mockedIdentifiableService, Mockito.times(1)).getModelType();
        assertEquals(mockedIdentifiableService, identifiableManagerImpl.getIdentifiableService(ModuleHelper.type));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void removeIdentifiableServiceTest() {
        assertNull(identifiableManagerImpl.getIdentifiableService(ModuleHelper.type));
        identifiableManagerImpl.addIdentifiableService(mockedIdentifiableService);
        assertEquals(mockedIdentifiableService, identifiableManagerImpl.getIdentifiableService(ModuleHelper.type));
        identifiableManagerImpl.removeIdentifiableService(mockedIdentifiableService);
        assertNull(identifiableManagerImpl.getIdentifiableService(ModuleHelper.type));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getIdentifiableByTypeAndIdTest() {
        BaseOperateItem returnedValue = identifiableManagerImpl.getIdentifiableByTypeAndId(ModuleHelper.type, ModuleHelper.id);
        assertNull(returnedValue);
        identifiableManagerImpl.addIdentifiableService(mockedIdentifiableService);
        returnedValue = identifiableManagerImpl.getIdentifiableByTypeAndId(ModuleHelper.type, ModuleHelper.id);
        verify(mockedIdentifiableService, Mockito.times(1)).getById(Mockito.anyInt());
        assertEquals(mockedIdentifiable, returnedValue);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void identifiableRegisteredTest() {
        boolean returnedValue = identifiableManagerImpl.identifiableRegistered(ModuleHelper.type);
        assertEquals(false, returnedValue);
        identifiableManagerImpl.addIdentifiableService(mockedIdentifiableService);
        returnedValue = identifiableManagerImpl.identifiableRegistered(ModuleHelper.type);
        assertEquals(true, returnedValue);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getIdentifiableWithDetailByTypeAndIdTest() {
        BaseOperateItem returnedValue = identifiableManagerImpl.getIdentifiableWithDetailByTypeAndId(ModuleHelper.type,
                ModuleHelper.id);
        assertNull(returnedValue);
        identifiableManagerImpl.addIdentifiableService(mockedIdentifiableService);
        returnedValue = identifiableManagerImpl.getIdentifiableWithDetailByTypeAndId(ModuleHelper.type, ModuleHelper.id);
        verify(mockedIdentifiableService, Mockito.times(1)).getByIdWithDetail(Mockito.anyInt());
        assertEquals(mockedIdentifiable, returnedValue);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void deleteIdentifiableByTypeAndIdTest() {
        identifiableManagerImpl.deleteIdentifiableByTypeAndId(ModuleHelper.type, ModuleHelper.id);
        verify(mockedIdentifiableService, Mockito.times(0)).delete(Mockito.anyInt());

        identifiableManagerImpl.addIdentifiableService(mockedIdentifiableService);
        identifiableManagerImpl.deleteIdentifiableByTypeAndId(ModuleHelper.type, ModuleHelper.id);
        verify(mockedIdentifiableService, Mockito.times(1)).delete(Mockito.anyInt());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void getIdentifiableServiceTest() {
        BaseService returnedValue = identifiableManagerImpl.getIdentifiableService(ModuleHelper.type);
        assertNull(returnedValue);
        identifiableManagerImpl.addIdentifiableService(mockedIdentifiableService);
        returnedValue = identifiableManagerImpl.getIdentifiableService(ModuleHelper.type);
        assertEquals(mockedIdentifiableService, returnedValue);
    }

}
