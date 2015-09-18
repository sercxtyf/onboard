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
package com.onboard.test.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.onboard.domain.mapper.base.BaseMapper;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.base.BaseService;
import com.onboard.service.web.SessionService;
import com.onboard.test.exampleutils.AbstractMatcher;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.moduleutils.ModuleHelper;

public abstract class AbstractProjectItemServiceTest<I extends BaseProjectItem, E extends BaseExample> {

    protected I item;
    protected E example;
    protected List<I> items;
    protected User user;
    protected int mapperReturnValue = 1;

    protected abstract BaseMapper<I, E> getMockMapper();
    protected abstract BaseService<I, E> getTestService();
    
    @Mock SessionService sessionService;
    
    private void fillBaseProperties(I i){
        i.setCompanyId(ModuleHelper.companyId);
        i.setCreated(ModuleHelper.created);
        i.setCreatorAvatar(ModuleHelper.creatorAvatar);
        i.setCreatorId(ModuleHelper.creatorId);
        i.setCreatorName(ModuleHelper.creatorName);
        i.setDeleted(false);
        i.setProjectId(ModuleHelper.projectId);
        i.setUpdated(ModuleHelper.updated);
    }
    
    protected I getSample(){
        I i = getTestService().newItem();
        fillBaseProperties(i);
        return i;
    }
    
    protected I getSample(Integer id){
        I i = getSample();
        i.setId(id);
        return i;
    }
    
    protected List<I> getSampleList(List<Integer> ids){
        List<I> list = Lists.newArrayList();
        for (Integer id : ids) {
            list.add(getSample(id));
        }
        return list;
    }
    
    protected List<I> getSampleList(){
        return getSampleList(ModuleHelper.ids);
    }
    
    protected void initFields(){
        item = getSample(ModuleHelper.id);
        example = getTestService().newExample();
        items = getSampleList();
        user = ModuleHelper.getASampleUser();
    }

    @SuppressWarnings("unchecked")
    protected void initMockMapper() {
        when(getMockMapper().countByExample((E) Mockito.any(example.getClass()))).thenReturn(ModuleHelper.count);
        when(getMockMapper().deleteByExample((E) Mockito.any(example.getClass()))).thenReturn(mapperReturnValue);
        when(getMockMapper().deleteByPrimaryKey(ModuleHelper.id)).thenReturn(mapperReturnValue);
        when(getMockMapper().insert((I) Mockito.any(item.getClass()))).thenReturn(mapperReturnValue);
        when(getMockMapper().insertSelective((I) Mockito.any(item.getClass()))).thenReturn(mapperReturnValue);
        when(getMockMapper().selectByExample((E) Mockito.any(example.getClass()))).thenReturn(items);
        when(getMockMapper().selectByPrimaryKey(ModuleHelper.id)).thenReturn(item);
        for (Integer id : ModuleHelper.ids) {
            when(getMockMapper().selectByPrimaryKey(id)).thenReturn(getSample(id));
        }
        when(getMockMapper().updateByExample((I) Mockito.any(item.getClass()), (E) Mockito.any(example.getClass()))).thenReturn(mapperReturnValue);
        when(getMockMapper().updateByExampleSelective((I) Mockito.any(item.getClass()),(E) Mockito.any(example.getClass()))).thenReturn(
                mapperReturnValue);
        when(getMockMapper().updateByPrimaryKey((I) Mockito.any(item.getClass()))).thenReturn(mapperReturnValue);
        when(getMockMapper().updateByPrimaryKeySelective((I) Mockito.any(item.getClass()))).thenReturn(mapperReturnValue);
        when(sessionService.getCurrentUser()).thenReturn(user);
    }

    @Before
    public void baseSetup() {
        initFields();
        initMockMapper();
    }
    
    protected void assertId(I expected, I actual){
        assertEquals(expected.getId(), actual.getId());
    }
    
    protected void assertId(List<I> expected, List<I> actual){
        assertEquals(expected.size(), actual.size());
        for(Integer i = 0; i < expected.size(); i++){
            assertId(expected.get(i), actual.get(i));
        }
    }

    @Test
    public void getByIdExist(){
        I result = getTestService().getById(ModuleHelper.id);
        verify(getMockMapper()).selectByPrimaryKey(ModuleHelper.id);
        assertId(item, result);
    }
    
    @Test
    public void getByIdNotExist(){
        Integer id = ModuleHelper.id - 1;
        I result = getTestService().getById(id);
        verify(getMockMapper()).selectByPrimaryKey(id);
        assertNull(result);
    }

    @Test
    public void getByIdWithDetailExist(){
        I result = getTestService().getByIdWithDetail(ModuleHelper.id);
        verify(getMockMapper()).selectByPrimaryKey(ModuleHelper.id);
        assertId(item, result);
    }
    
    @Test
    public void getByIdWithDetailNotExist(){
        Integer id = ModuleHelper.id - 1;
        I result = getTestService().getByIdWithDetail(id);
        verify(getMockMapper()).selectByPrimaryKey(id);
        assertNull(result);
    }
    
    @Test
    public void getAll(){
        List<I> result = getTestService().getAll();
        //sub class may have different implementation, so we do not need to check other properties of example, 
        //but start and limit is needed
        verify(getMockMapper()).selectByExample(Mockito.argThat(new ExampleMatcher<E>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyLimit(example, ModuleHelper.ALL_LIMIT)
                        && CriterionVerifier.verifyStart(example, ModuleHelper.ALL_START);
            }
        }));
        assertId(items, result);
    }
    
    @Test
    public void getAllByStartAndLimit(){
        List<I> result = getTestService().getAll(ModuleHelper.start, ModuleHelper.limit);
        verify(getMockMapper()).selectByExample(Mockito.argThat(new ExampleMatcher<E>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyLimit(example, ModuleHelper.limit)
                        && CriterionVerifier.verifyStart(example, ModuleHelper.start);
            }
        }));
        assertId(items, result);
    }
    
    protected boolean verifyBeseProperties(Integer start, Integer limit, BaseExample example){
        return CriterionVerifier.verifyLimit(example, limit)
            && CriterionVerifier.verifyStart(example, start)
            && CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId)
            && CriterionVerifier.verifyEqualTo(example, "created", ModuleHelper.created)
            && CriterionVerifier.verifyEqualTo(example, "creatorAvatar", ModuleHelper.creatorAvatar)
            && CriterionVerifier.verifyEqualTo(example, "creatorId", ModuleHelper.creatorId)
            && CriterionVerifier.verifyEqualTo(example, "creatorName", ModuleHelper.creatorName)
            && CriterionVerifier.verifyEqualTo(example, "deleted", false)
            && CriterionVerifier.verifyEqualTo(example, "projectId", ModuleHelper.projectId)
            && CriterionVerifier.verifyEqualTo(example, "updated", ModuleHelper.updated);
    }
    
    @Test
    public void getBySample(){
        I sample = getTestService().newItem();
        fillBaseProperties(sample);
        List<I> result = getTestService().getBySample(sample);
        verify(getMockMapper()).selectByExample(Mockito.argThat(new ExampleMatcher<E>() {
            @Override
            public boolean matches(BaseExample example) {
                return verifyBeseProperties(ModuleHelper.ALL_START, ModuleHelper.ALL_LIMIT, example);
            }
        }));
        assertId(items, result);
    }
    
    @Test
    public void getBySampleWithStartAndLimit(){
        I sample = getTestService().newItem();
        fillBaseProperties(sample);
        List<I> result = getTestService().getBySample(sample, ModuleHelper.start, ModuleHelper.limit);
        verify(getMockMapper()).selectByExample(Mockito.argThat(new ExampleMatcher<E>() {
            @Override
            public boolean matches(BaseExample example) {
                return verifyBeseProperties(ModuleHelper.start, ModuleHelper.limit, example);
            }
        }));
        assertId(items, result);
    }
    
    @Test
    public void getByExample(){
        List<I> result = getTestService().getByExample(example);
        verify(getMockMapper()).selectByExample(example);
        assertId(items, result);
    }
    
    @Test
    public void countByExample(){
        int result = getTestService().countByExample(example);
        verify(getMockMapper()).countByExample(example);
        assertEquals(ModuleHelper.count, result);
    }
    
    protected boolean isNow(Date date){
        DateTime now = DateTime.now();
        return (now.isAfter(date.getTime()) || now.isEqual(date.getTime()))
                && now.plusSeconds(-1).isBefore(date.getTime());
    }
    
    protected void assertNow(Date date){
        DateTime now = DateTime.now();
        //some times now will equal date, may be it is too short
        assertTrue(now.isAfter(date.getTime()) || now.isEqual(date.getTime()));
        assertTrue(now.plusSeconds(-1).isBefore(date.getTime()));
    }
    
    protected void verifyCreatedItem(I item){
        assertNow(item.getCreated());
        assertFalse(item.getDeleted());
        assertNow(item.getUpdated());
        assertEquals(item.getCreatorId(), user.getId());
        assertEquals(item.getCreatorAvatar(), user.getAvatar());
        assertEquals(item.getCreatorName(), user.getUsername());
    }
    
    /**
     * TODO: shall we process create null
     */
    public void createNull(){
        getTestService().create(null);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void create(){
        I result = getTestService().create(getSample());
        verifyCreatedItem(result);
        //TODO: inert or insert selective. or we may delete one
        verify(getMockMapper()).insert((I) Mockito.any(item.getClass()));
    }
    
    @Test
    public void updateNull(){
        I result = getTestService().update(null);
        assertNull(result);
        verify(getMockMapper(), times(0)).updateByPrimaryKey(result);
    }
    
    @Test
    public void updateIdNull(){
        I result = getTestService().update(getSample());
        assertNull(result);
        verify(getMockMapper(), times(0)).updateByPrimaryKey(result);
    }
    
    @Test
    public void update(){
        I sample = getSample(ModuleHelper.id);
        I result = getTestService().update(sample);
        assertId(result, sample);
        assertNow(result.getUpdated());
        verify(getMockMapper()).updateByPrimaryKey(result);
    }
    
    @Test
    public void updateSelectiveNull(){
        I result = getTestService().updateSelective(null);
        assertNull(result);
        verify(getMockMapper(), times(0)).updateByPrimaryKeySelective(result);
    }
    
    @Test
    public void updateSelectiveIdNull(){
        I result = getTestService().updateSelective(getSample());
        assertNull(result);
        verify(getMockMapper(), times(0)).updateByPrimaryKeySelective(result);
    }
    
    @Test
    public void updateSelective(){
        I sample = getSample(ModuleHelper.id);
        I result = getTestService().updateSelective(sample);
        assertId(result, sample);
        assertNow(result.getUpdated());
        verify(getMockMapper()).updateByPrimaryKeySelective(result);
    }
    
    @Test
    public void delete(){
        getTestService().delete(ModuleHelper.id);
        if(item.trashRequried()){
            verify(getMockMapper()).updateByPrimaryKeySelective(Mockito.argThat(new AbstractMatcher<I>() {
                @SuppressWarnings("unchecked")
                @Override
                public boolean matches(Object arg0) {
                    I item = (I)arg0;
                    return item.getId().equals(ModuleHelper.id) && item.getDeleted();
                }
            }));
        }else {
            verify(getMockMapper()).deleteByPrimaryKey(ModuleHelper.id);
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void recover(){
        getTestService().recover(ModuleHelper.id);
        if(item.trashRequried()){
            verify(getMockMapper()).updateByPrimaryKeySelective(Mockito.argThat(new AbstractMatcher<I>() {
                @Override
                public boolean matches(Object arg0) {
                    I item = (I)arg0;
                    return item.getId().equals(ModuleHelper.id) && !item.getDeleted();
                }
            }));
        }else {
            verify(getMockMapper(), times(0)).updateByPrimaryKeySelective((I) Mockito.any(item.getClass()));
        }
    }
    
    @Test
    public void deleteFromTrash(){
        getTestService().deleteFromTrash(ModuleHelper.id);
        verify(getMockMapper()).deleteByPrimaryKey(ModuleHelper.id);
    }
    
    @Test
    public void getModelType(){
        String result = getTestService().getModelType();
        assertEquals(item.getType(), result);
    }

}
