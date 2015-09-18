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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.mapper.TagAttachMapper;
import com.onboard.domain.mapper.TagMapper;
import com.onboard.domain.mapper.model.TagAttachExample;
import com.onboard.domain.mapper.model.TagExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Attachment;
import com.onboard.domain.model.Tag;
import com.onboard.domain.model.TagAttach;
import com.onboard.domain.model.type.Taggable;
import com.onboard.service.collaboration.impl.TagServiceImpl;
import com.onboard.service.common.identifiable.IdentifiableManager;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.exampleutils.ObjectMatcher;

@RunWith(MockitoJUnitRunner.class)
public class TagServiceImplTest {

    private static final int start = 0;
    private static final int limit = 5;
    private static final int id = 1;
    private static final String tagname = "Tag Name";
    private static final int count = 5;
    private static final String attachType = "attachment";

    @Mock
    private IdentifiableManager mockIdentifiableManager;

    @Mock
    private TagMapper mockTagMapper;

    @Mock
    private TagAttachMapper mockTagAttachMapper;

    @InjectMocks
    private TagServiceImpl tagServiceImpl;

    private Tag sampleTag;
    private List<Tag> listOfTags;
    private Attachment sampleAttachment;
    private List<TagAttach> listOfTagAttach;
    private List<Taggable> listOfTaggable;
    private TagAttach sampleTagAttach;

    private Tag getAsampleTag() {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setProjectId(id);
        tag.setTagname(tagname);

        return tag;
    }

    private Tag getAsampleTag2() {
        Tag tag = new Tag();
        tag.setId(2);
        tag.setProjectId(2);
        tag.setTagname(tagname + tagname);

        return tag;
    }

    private Attachment getASampleAttachment() {
        Attachment sample = new Attachment();
        sample.setId(id);
        return sample;
    }

    private List<TagAttach> getASampleListOfTagAttachs() {
        List<TagAttach> list = new ArrayList<TagAttach>();
        list.add(getASampleTagAttach());
        list.add(getASampleTagAttach());
        return list;

    }

    private TagAttach getASampleTagAttach() {
        TagAttach sample = new TagAttach();
        sample.setAttachId(id);
        sample.setAttachType(attachType);
        sample.setId(id);
        sample.setTagId(id);
        return sample;
    }

    private List<Taggable> getASampleListOfTaggable() {
        List<Taggable> list = new ArrayList<Taggable>();
        list.add(getASampleTaggable());
        list.add(getASampleTaggable());
        return list;
    }

    private Taggable getASampleTaggable() {
        Taggable sample = getASampleAttachment();
        return sample;
    }

    @Before
    public void setUp() throws Exception {
        this.sampleTag = getAsampleTag();
        this.listOfTags = new ArrayList<Tag>();
        this.listOfTags.add(getAsampleTag2());
        this.listOfTags.add(getAsampleTag());
        this.sampleAttachment = getASampleAttachment();
        this.listOfTagAttach = getASampleListOfTagAttachs();
        this.listOfTaggable = getASampleListOfTaggable();
        this.sampleTagAttach = getASampleTagAttach();

        when(mockTagMapper.selectByPrimaryKey(id)).thenReturn(sampleTag);
        when(mockTagMapper.selectByExample(any(TagExample.class))).thenReturn(listOfTags);
        when(mockTagMapper.countByExample(any(TagExample.class))).thenReturn(count);
        when(mockTagMapper.insert(sampleTag)).thenReturn(1);
        when(mockTagMapper.updateByPrimaryKey(sampleTag)).thenReturn(1);
        when(mockTagMapper.deleteByPrimaryKey(id)).thenReturn(1);

        when(mockTagAttachMapper.selectByExample(any(TagAttachExample.class))).thenReturn(listOfTagAttach);
        when(mockTagAttachMapper.deleteByExample(any(TagAttachExample.class))).thenReturn(1);
        when(mockTagAttachMapper.countByExample(any(TagAttachExample.class))).thenReturn(count);

        when(mockIdentifiableManager.getIdentifiableByTypeAndId(attachType, id)).thenReturn(sampleAttachment);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetTagById() {
        Tag ret = tagServiceImpl.getTagById(id);

        verify(mockTagMapper).selectByPrimaryKey(id);
        assertNotNull(ret);
        assertEquals(sampleTag, ret);
    }

    @Test
    public void testGetTagsIntInt() {
        List<Tag> ret = tagServiceImpl.getTags(start, limit);
        verify(mockTagMapper).selectByExample(argThat(new ExampleMatcher<TagExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyLimit(example, limit) && CriterionVerifier.verifyStart(example, start);
            }
        }));

        assertNotNull(ret);
        assertEquals(listOfTags, ret);
    }

    @Test
    public void testGetTagsByExample() {
        List<Tag> ret = tagServiceImpl.getTagsByExample(sampleTag, start, limit);

        verify(mockTagMapper).selectByExample(Mockito.any(TagExample.class));

        assertNotNull(ret);
        assertEquals(listOfTags, ret);
    }

    @Test
    public void testCountByExample() {
        int ret = tagServiceImpl.countByExample(getAsampleTag());
        verify(mockTagMapper).countByExample(argThat(new ExampleMatcher<TagExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", id)
                        && CriterionVerifier.verifyEqualTo(example, "tagname", tagname);
            }
        }));

        assertEquals(count, ret);
    }

    @Test
    public void testCreateTag() {
        Tag ret = tagServiceImpl.createTag(sampleTag);
        verify(mockTagMapper).insert(sampleTag);
        assertEquals(sampleTag, ret);
    }

    @Test
    public void testUpdateTag() {
        Tag ret = tagServiceImpl.updateTag(sampleTag);
        verify(mockTagMapper).updateByPrimaryKey(sampleTag);
        assertEquals(sampleTag, ret);
    }

    @Test
    public void testDeleteTag() {
        tagServiceImpl.deleteTag(id);
        verify(mockTagMapper).deleteByPrimaryKey(id);
    }

    @Test
    public void testGetOrCreateTag_ReturnNullList() {
        reset(mockTagMapper);
        when(mockTagMapper.selectByExample(any(TagExample.class))).thenReturn(null);

        Tag ret = tagServiceImpl.getOrCreateTag(sampleTag);

        verify(mockTagMapper).selectByExample(argThat(new ExampleMatcher<TagExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", id)
                        && CriterionVerifier.verifyEqualTo(example, "tagname", tagname);
            }
        }));
        verify(mockTagMapper).insert(sampleTag);
        assertEquals(sampleTag, ret);
    }

    @Test
    public void testGetOrCreateTag_ReturnZeroList() {
        reset(mockTagMapper);
        List<Tag> list = new ArrayList<Tag>();
        when(mockTagMapper.selectByExample(any(TagExample.class))).thenReturn(list);

        Tag ret = tagServiceImpl.getOrCreateTag(sampleTag);

        verify(mockTagMapper).selectByExample(argThat(new ExampleMatcher<TagExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", id)
                        && CriterionVerifier.verifyEqualTo(example, "tagname", tagname);
            }
        }));
        verify(mockTagMapper).insert(sampleTag);
        assertEquals(sampleTag, ret);
    }

    @Test
    public void testGetOrCreateTag_Normal() {

        Tag ret = tagServiceImpl.getOrCreateTag(sampleTag);

        verify(mockTagMapper).selectByExample(argThat(new ExampleMatcher<TagExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", id)
                        && CriterionVerifier.verifyEqualTo(example, "tagname", tagname);
            }
        }));
        assertEquals(2, (int) ret.getId());
        assertEquals(2, (int) ret.getProjectId());
        assertEquals(tagname + tagname, ret.getTagname());
    }

    @Test
    public void testGetTagsTaggable() {
        List<Tag> ret = tagServiceImpl.getTags(sampleAttachment);

        verify(mockTagAttachMapper).selectByExample(argThat(new ExampleMatcher<TagAttachExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "attachId", id)
                        && CriterionVerifier.verifyEqualTo(example, "attachType", attachType);
            }
        }));

        verify(mockTagMapper, times(2)).selectByPrimaryKey(id);

        assertNotNull(ret);
        assertEquals(2, ret.size());
    }

    @Test
    public void testSetTags() {
        TagServiceImpl spyTagServiceImpl = spy(tagServiceImpl);
        doReturn(listOfTags).when(spyTagServiceImpl).getTags(sampleAttachment);

        spyTagServiceImpl.setTags(listOfTaggable);

        verify(spyTagServiceImpl, times(2)).getTags(sampleAttachment);
        for (Taggable element : listOfTaggable) {
            assertEquals(listOfTags, element.getTags());
        }
    }

    @Test
    public void testDeleteAttachmentTag_NullTest() {
        reset(mockTagAttachMapper);
        when(mockTagAttachMapper.selectByExample(any(TagAttachExample.class))).thenReturn(null);
        when(mockTagAttachMapper.deleteByExample(any(TagAttachExample.class))).thenReturn(1);

        TagServiceImpl spyTagServiceImpl = spy(tagServiceImpl);
        doNothing().when(spyTagServiceImpl).deleteTag(id);

        boolean ret = spyTagServiceImpl.deleteAttachmentTag(sampleTagAttach);

        verify(mockTagAttachMapper).deleteByExample(argThat(new ExampleMatcher<TagAttachExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "attachId", id)
                        && CriterionVerifier.verifyEqualTo(example, "attachType", attachType)
                        && CriterionVerifier.verifyEqualTo(example, "tagId", id);
            }
        }));
        verify(spyTagServiceImpl).deleteTag(id);
        assertTrue(ret);
    }

    @Test
    public void testDeleteAttachmentTag_ZeroTest() {
        reset(mockTagAttachMapper);
        when(mockTagAttachMapper.selectByExample(any(TagAttachExample.class))).thenReturn(new ArrayList<TagAttach>());
        when(mockTagAttachMapper.deleteByExample(any(TagAttachExample.class))).thenReturn(1);

        TagServiceImpl spyTagServiceImpl = spy(tagServiceImpl);
        doNothing().when(spyTagServiceImpl).deleteTag(id);

        boolean ret = spyTagServiceImpl.deleteAttachmentTag(sampleTagAttach);

        verify(mockTagAttachMapper).deleteByExample(argThat(new ExampleMatcher<TagAttachExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "attachId", id)
                        && CriterionVerifier.verifyEqualTo(example, "attachType", attachType)
                        && CriterionVerifier.verifyEqualTo(example, "tagId", id);
            }
        }));
        verify(spyTagServiceImpl).deleteTag(id);
        assertTrue(ret);
    }

    @Test
    public void testDeleteAttachmentTag_NormalTest() {
        boolean ret = tagServiceImpl.deleteAttachmentTag(sampleTagAttach);

        verify(mockTagAttachMapper).deleteByExample(argThat(new ExampleMatcher<TagAttachExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "attachId", id)
                        && CriterionVerifier.verifyEqualTo(example, "attachType", attachType)
                        && CriterionVerifier.verifyEqualTo(example, "tagId", id);
            }
        }));

        verify(mockTagAttachMapper).selectByExample(argThat(new ExampleMatcher<TagAttachExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "tagId", id);
            }
        }));

        assertFalse(ret);
    }

    @Test
    public void testGetTagTarget() {

        TagServiceImpl spyTagServiceImpl = spy(tagServiceImpl);
        doReturn(listOfTags).when(spyTagServiceImpl).getTags(sampleAttachment);

        List<Taggable> ret = spyTagServiceImpl.getTagTarget(sampleTag, attachType, start, limit);

        verify(mockTagAttachMapper).selectByExample(argThat(new ExampleMatcher<TagAttachExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "tagId", id)
                        && CriterionVerifier.verifyEqualTo(example, "attachType", attachType)
                        && CriterionVerifier.verifyLimit(example, limit) && CriterionVerifier.verifyStart(example, start)
                        && CriterionVerifier.verifyOrderByClause(example, "id desc");
            }
        }));
        verify(mockIdentifiableManager, times(2)).getIdentifiableByTypeAndId(attachType, id);
        verify(spyTagServiceImpl, times(2)).getTags(sampleAttachment);
        assertNotNull(ret);
        assertEquals(2, ret.size());
        for (Taggable taggable : ret) {
            assertEquals(listOfTags, taggable.getTags());
        }
    }

    @Test
    public void testGetTagsByProjectId() {
        List<Tag> ret = tagServiceImpl.getTagsByProjectId(id);
        verify(mockTagMapper).selectByExample(argThat(new ExampleMatcher<TagExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", id);
            }
        }));
        assertEquals(listOfTags, ret);
    }

    @Test
    public void testCountByTagId() {
        int ret = tagServiceImpl.countByTagId(id);

        verify(mockTagAttachMapper).countByExample(argThat(new ExampleMatcher<TagAttachExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "tagId", id);
            }
        }));
        assertEquals(count, ret);
    }

    @Test
    public void testAddTagWithAttacTypeAndId_NullReturnTest() {
        reset(mockTagAttachMapper);
        when(mockTagAttachMapper.selectByExample(any(TagAttachExample.class))).thenReturn(null);

        boolean ret = tagServiceImpl.addTagWithAttacTypeAndId(sampleTag, attachType, id);
        verify(mockTagAttachMapper).selectByExample(argThat(new ExampleMatcher<TagAttachExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "attachId", id)
                        && CriterionVerifier.verifyEqualTo(example, "attachType", attachType)
                        && CriterionVerifier.verifyEqualTo(example, "tagId", id);
            }
        }));
        verify(mockTagAttachMapper).insert(argThat(new ObjectMatcher<TagAttach>() {
            @Override
            public boolean verifymatches(TagAttach item) {
                return item.getAttachId() == id && item.getAttachType() == attachType && item.getTagId() == id;
            }
        }));

        assertTrue(ret);
    }

    @Test
    public void testAddTagWithAttacTypeAndId_ZeroReturnTest() {
        reset(mockTagAttachMapper);
        when(mockTagAttachMapper.selectByExample(any(TagAttachExample.class))).thenReturn(new ArrayList<TagAttach>());

        boolean ret = tagServiceImpl.addTagWithAttacTypeAndId(sampleTag, attachType, id);
        verify(mockTagAttachMapper).selectByExample(argThat(new ExampleMatcher<TagAttachExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "attachId", id)
                        && CriterionVerifier.verifyEqualTo(example, "attachType", attachType)
                        && CriterionVerifier.verifyEqualTo(example, "tagId", id);
            }
        }));
        verify(mockTagAttachMapper).insert(argThat(new ObjectMatcher<TagAttach>() {
            @Override
            public boolean verifymatches(TagAttach item) {
                return item.getAttachId() == id && item.getAttachType() == attachType && item.getTagId() == id;
            }
        }));

        assertTrue(ret);
    }

    @Test
    public void testAddTagWithAttacTypeAndId_NormalTest() {
        boolean ret = tagServiceImpl.addTagWithAttacTypeAndId(sampleTag, attachType, id);
        verify(mockTagAttachMapper).selectByExample(argThat(new ExampleMatcher<TagAttachExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "attachId", id)
                        && CriterionVerifier.verifyEqualTo(example, "attachType", attachType)
                        && CriterionVerifier.verifyEqualTo(example, "tagId", id);
            }
        }));

        assertFalse(ret);

    }

    @Test
    public void testDeleteTagWithAttachTypeAndId() {
        tagServiceImpl.deleteTagWithAttachTypeAndId(sampleTag, attachType, id);

        verify(mockTagAttachMapper).deleteByExample(argThat(new ExampleMatcher<TagAttachExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "attachId", id)
                        && CriterionVerifier.verifyEqualTo(example, "attachType", attachType)
                        && CriterionVerifier.verifyEqualTo(example, "tagId", id);
            }
        }));
    }

    @Test
    public void testFillTags() {

        tagServiceImpl.fillTags(sampleAttachment);

        verify(mockTagAttachMapper).selectByExample(argThat(new ExampleMatcher<TagAttachExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "attachId", id)
                        && CriterionVerifier.verifyEqualTo(example, "attachType", attachType);
            }
        }));

        verify(mockTagMapper, times(2)).selectByPrimaryKey(id);

        assertNotNull(sampleAttachment.getTags());
        assertEquals(2, sampleAttachment.getTags().size());
        for (Tag tag : sampleAttachment.getTags()) {
            assertEquals(sampleTag, tag);
        }
    }

}
