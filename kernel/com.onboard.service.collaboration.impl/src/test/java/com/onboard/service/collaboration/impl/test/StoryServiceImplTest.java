package com.onboard.service.collaboration.impl.test;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.mapper.StoryMapper;
import com.onboard.domain.model.Story;
import com.onboard.service.collaboration.impl.StoryServiceImpl;
import com.onboard.test.moduleutils.ModuleHelper;

/**
 * This is a unit test class for StoryServiceImpl.java
 * 
 * @author Steven
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class StoryServiceImplTest {

    @InjectMocks
    StoryServiceImpl testedStoryServiceImpl;

    @Mock
    StoryMapper mockStoryMapper;
    
    @Before
    public void setupStoryServiceImplTest() {
    	
    }
    
    /**
     * @author Steven
     * Test that no such story exists
     */
    @Test
    public void testGetById_Test1() {
    	Mockito.doReturn(null).when(mockStoryMapper).selectByPrimaryKey(ModuleHelper.storyId);
    	
    	Story story = testedStoryServiceImpl.getById(ModuleHelper.storyId);
    	
    	Mockito.verify(mockStoryMapper, times(1)).selectByPrimaryKey(ModuleHelper.storyId);
    	Mockito.verifyNoMoreInteractions(mockStoryMapper);
    	
    	assertNull("", story);
    }
}
