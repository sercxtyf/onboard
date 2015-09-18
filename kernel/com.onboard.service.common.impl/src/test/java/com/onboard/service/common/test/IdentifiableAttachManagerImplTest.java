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

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.service.common.attach.IdentifiableAttachService;
import com.onboard.service.common.attach.impl.IdentifiableAttachManagerImpl;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class IdentifiableAttachManagerImplTest {

    private final static String IDENTIFIABLE_ATTACH_SERVICE_MAP_PARIVATE_FIELD = "identifiableAttachServiceMap";

    @Mock
    private IdentifiableAttachService mockedIdentifiableAttachService1;
    @Mock
    private IdentifiableAttachService mockedIdentifiableAttachService2;
    @Mock
    private IdentifiableAttachService mockedIdentifiableAttachService3;

    @Mock
    private Map<String, Map<String, IdentifiableAttachService>> identifiableAttachServiceMap;

    @SuppressWarnings("rawtypes")
    @Mock
    private List mockedIdentifiables;

    private final static int MOCKED_IDENTIFIABLES_SIZE = 1;

    @InjectMocks
    private IdentifiableAttachManagerImpl identifiableAttachManagerImpl;

    @SuppressWarnings("unchecked")
    private void setIdentifiableMockGetting(IdentifiableAttachService identifiableAttachService) {
        when(identifiableAttachService.getIdentifiablesByAttachId(Mockito.anyString(), Mockito.anyInt())).thenReturn(
                mockedIdentifiables);
    }

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        when(mockedIdentifiables.size()).thenReturn(MOCKED_IDENTIFIABLES_SIZE);
        when(mockedIdentifiableAttachService1.attachType()).thenReturn(ModuleHelper.type);
        when(mockedIdentifiableAttachService1.modelType()).thenReturn(ModuleHelper.type);
        setIdentifiableMockGetting(mockedIdentifiableAttachService1);
        when(mockedIdentifiableAttachService2.attachType()).thenReturn(ModuleHelper.type);
        when(mockedIdentifiableAttachService2.modelType()).thenReturn(ModuleHelper.content);
        setIdentifiableMockGetting(mockedIdentifiableAttachService2);
        when(mockedIdentifiableAttachService3.attachType()).thenReturn(ModuleHelper.content);
        when(mockedIdentifiableAttachService3.modelType()).thenReturn(ModuleHelper.type);
        setIdentifiableMockGetting(mockedIdentifiableAttachService3);
        Field field = null;
        try {
            field = IdentifiableAttachManagerImpl.class.getDeclaredField(IDENTIFIABLE_ATTACH_SERVICE_MAP_PARIVATE_FIELD);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        if (field != null) {
            try {
                field.setAccessible(true);
                identifiableAttachServiceMap = Collections
                        .synchronizedMap((Map<String, Map<String, IdentifiableAttachService>>) field
                                .get(identifiableAttachManagerImpl));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void addIdentifiablAttacheServiceTest() {
        identifiableAttachManagerImpl.addIdentifiablAttacheService(null);
        assertEquals(0, identifiableAttachServiceMap.size());

        identifiableAttachManagerImpl.addIdentifiablAttacheService(mockedIdentifiableAttachService1);
        assertEquals(1, identifiableAttachServiceMap.size());
        verify(mockedIdentifiableAttachService1, Mockito.times(1)).modelType();
        verify(mockedIdentifiableAttachService1, Mockito.times(1)).attachType();
        assertEquals(1, identifiableAttachServiceMap.get(ModuleHelper.type).size());

        identifiableAttachManagerImpl.addIdentifiablAttacheService(mockedIdentifiableAttachService1);
        assertEquals(1, identifiableAttachServiceMap.get(ModuleHelper.type).size());

        identifiableAttachManagerImpl.addIdentifiablAttacheService(mockedIdentifiableAttachService2);
        assertEquals(1, identifiableAttachServiceMap.get(ModuleHelper.type).size());
        assertEquals(2, identifiableAttachServiceMap.size());
        assertEquals(1, identifiableAttachServiceMap.get(ModuleHelper.content).size());

        identifiableAttachManagerImpl.addIdentifiablAttacheService(mockedIdentifiableAttachService3);
        assertEquals(2, identifiableAttachServiceMap.get(ModuleHelper.type).size());
        assertEquals(2, identifiableAttachServiceMap.size());
    }

    @Test
    public void removeIdentifiableServiceTest() {
        identifiableAttachManagerImpl.addIdentifiablAttacheService(mockedIdentifiableAttachService1);
        identifiableAttachManagerImpl.addIdentifiablAttacheService(mockedIdentifiableAttachService2);
        identifiableAttachManagerImpl.addIdentifiablAttacheService(mockedIdentifiableAttachService3);

        assertEquals(2, identifiableAttachServiceMap.size());
        assertEquals(2, identifiableAttachServiceMap.get(ModuleHelper.type).size());

        identifiableAttachManagerImpl.removeIdentifiableService(null);
        assertEquals(2, identifiableAttachServiceMap.size());
        assertEquals(2, identifiableAttachServiceMap.get(ModuleHelper.type).size());

        identifiableAttachManagerImpl.removeIdentifiableService(mockedIdentifiableAttachService1);
        assertEquals(2, identifiableAttachServiceMap.size());
        assertEquals(1, identifiableAttachServiceMap.get(ModuleHelper.type).size());

        identifiableAttachManagerImpl.removeIdentifiableService(mockedIdentifiableAttachService2);
        assertEquals(1, identifiableAttachServiceMap.size());
        assertEquals(1, identifiableAttachServiceMap.get(ModuleHelper.type).size());

        identifiableAttachManagerImpl.removeIdentifiableService(mockedIdentifiableAttachService3);
        assertEquals(0, identifiableAttachServiceMap.size());
    }

    @Test
    public void getIdentifiablesByTypeAndAttachTypeAndIdTest() {
        @SuppressWarnings("rawtypes")
        List result = identifiableAttachManagerImpl.getIdentifiablesByTypeAndAttachTypeAndId(ModuleHelper.type,
                ModuleHelper.type, ModuleHelper.id);
        assertEquals(0, result.size());
        identifiableAttachManagerImpl.addIdentifiablAttacheService(mockedIdentifiableAttachService1);
        result = identifiableAttachManagerImpl.getIdentifiablesByTypeAndAttachTypeAndId(ModuleHelper.type, ModuleHelper.type,
                ModuleHelper.id);
        assertEquals(MOCKED_IDENTIFIABLES_SIZE, result.size());
        result = identifiableAttachManagerImpl.getIdentifiablesByTypeAndAttachTypeAndId(ModuleHelper.type, ModuleHelper.content,
                ModuleHelper.id);
        assertEquals(0, result.size());

    }
}
