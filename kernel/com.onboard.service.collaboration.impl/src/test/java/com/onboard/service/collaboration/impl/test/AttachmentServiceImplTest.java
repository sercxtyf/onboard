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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.mapper.AttachmentMapper;
import com.onboard.domain.mapper.model.AttachmentExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Attachment;
import com.onboard.domain.model.Comment;
import com.onboard.domain.model.Discussion;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.Attachable;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.AttachmentService;
import com.onboard.service.collaboration.TagService;
import com.onboard.service.collaboration.impl.AttachmentServiceImpl;
import com.onboard.service.common.identifiable.IdentifiableManager;
import com.onboard.service.file.FileService;
import com.onboard.service.web.SessionService;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.exampleutils.ObjectMatcher;

@RunWith(MockitoJUnitRunner.class)
public class AttachmentServiceImplTest {

    @Mock
    private AttachmentMapper mockAttachmentMapper;

    @Mock
    private UserService mockUserService;

    @Mock
    private TagService mockTagService;

    @Mock
    private SessionService mockSessionService;

    @Mock
    private FileService mockFileService;

    @Mock
    private IdentifiableManager mockIdentifiableManager;

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    private static int start = 1;
    private static int limit = 5;

    private static int id = 1;
    private static String attachTypeString = "comment";
    private static String contentTypeString = "image/jpeg";

    private static String creatorNameString = "Test User";
    private static String userEmailString = "test@test.com";

    private Attachment attachment;
    private User user;
    private List<Attachment> list;

    @Before
    public void setUpBefore() throws Exception {

        this.attachment = getASampleAttachment();
        this.user = getASampleUser();
        this.list = getASampleListAttachments();

        when(mockUserService.getById(id)).thenReturn(user);
        doNothing().when(mockTagService).fillTags(attachment);
        when(mockSessionService.getCurrentUser()).thenReturn(user).thenReturn(user).thenReturn(user);

        when(mockAttachmentMapper.selectByPrimaryKey(id)).thenReturn(attachment);
        when(mockAttachmentMapper.selectByExample(any(AttachmentExample.class))).thenReturn(list);
        when(mockAttachmentMapper.updateByPrimaryKeySelective(any(Attachment.class))).thenReturn(1);
        when(mockAttachmentMapper.updateByPrimaryKeySelective(any(Attachment.class))).thenReturn(1);
        when(mockAttachmentMapper.deleteByPrimaryKey(id)).thenReturn(1);
        when(mockAttachmentMapper.deleteByExample(any(AttachmentExample.class))).thenReturn(1);
        when(mockAttachmentMapper.insertSelective(any(Attachment.class))).thenReturn(1);

    }

    @After
    public void tearDownAfter() throws Exception {
    }

    private Attachment getASampleAttachment() {
        Attachment attachment = new Attachment();
        attachment.setId(id);
        attachment.setProjectId(id);
        attachment.setCompanyId(id);
        attachment.setContentType(contentTypeString);
        attachment.setCreatorId(id);
        attachment.setCreatorName(creatorNameString);
        attachment.setAttachType(attachTypeString);
        attachment.setAttachId(id);
        attachment.setTargetType(attachTypeString);
        attachment.setTargetId(id);
        return attachment;
    }

    private Attachment getASampleAttachment(boolean deleted) {
        Attachment attachment = getASampleAttachment();
        attachment.setDeleted(deleted);
        return attachment;
    }

    private List<Attachment> getASampleListAttachments() {
        Attachment attachment1 = getASampleAttachment();
        Attachment attachment2 = getASampleAttachment(false);
        List<Attachment> list = new ArrayList<Attachment>();
        list.add(attachment1);
        list.add(attachment2);
        return list;
    }

    private User getASampleUser() {
        User user = new User();
        user.setId(id);
        user.setName(creatorNameString);
        user.setEmail(userEmailString);
        return user;
    }

    private List<Integer> getProjectList() {
        List<Integer> projectList = new ArrayList<Integer>();
        projectList.add(1);
        projectList.add(2);

        return projectList;
    }

    @Test
    public void testGetAttachmentByIdWithUrl() {

        Attachment result = attachmentService.getAttachmentByIdWithUrl(id);

        verify(mockAttachmentMapper).selectByPrimaryKey(id);
        verify(mockUserService).getById(id);
        verify(mockTagService).fillTags(attachment);

        assertEquals((int) result.getId(), id);
        assertEquals(result.getAttachType(), attachTypeString);
        assertEquals(result.getCreator(), user);
        assertEquals(attachment, result);
    }

    @Test
    public void testGetAttachmentsByProjectId() {

        List<Attachment> resultList = attachmentService.getAttachmentsByProjectId(id, start, limit);

        verify(mockAttachmentMapper).selectByExample(argThat(new ExampleMatcher<AttachmentExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyStart(example, start) && CriterionVerifier.verifyLimit(example, limit)
                        && CriterionVerifier.verifyOrderByClause(example, "id desc")
                        && CriterionVerifier.verifyGraterThan(example, "attachId", 0)
                        && CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyEqualTo(example, "projectId", id);
            }
        }));
        verify(mockUserService, times(2)).getById(id);
        verify(mockTagService, times(2)).fillTags(attachment);

        assertEquals(list, resultList);
    }

    @Test
    public void testGetAttachmentsByUserId_NormalTest() {

        final List<Integer> projectList = new ArrayList<Integer>();
        projectList.add(1);
        projectList.add(2);

        List<Attachment> resultList = attachmentService.getAttachmentsByUserId(id, id, start, limit, projectList);

        verify(mockAttachmentMapper).selectByExample(argThat(new ExampleMatcher<AttachmentExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyEqualTo(example, "creatorId", id)
                        && CriterionVerifier.verifyEqualTo(example, "companyId", id)
                        && CriterionVerifier.verifyStart(example, start) && CriterionVerifier.verifyLimit(example, limit)
                        && CriterionVerifier.verifyOrderByClause(example, "id desc")
                        && CriterionVerifier.verifyIn(example, "projectId", projectList);
            }
        }));
        verify(mockUserService, times(2)).getById(id);
        verify(mockTagService, times(2)).fillTags(attachment);

        assertEquals(list, resultList);
    }

    @Test
    public void testGetAttachmentsByUserId_ZeroProjectListTest() {

        final List<Integer> projectList = new ArrayList<Integer>();

        List<Attachment> resultList = attachmentService.getAttachmentsByUserId(id, id, start, limit, projectList);

        assertNotNull(resultList);
        assertTrue(resultList.size() == 0);
    }

    @Test
    public void testGetAttachmentsByUserId_NullProjectListTest() {

        List<Attachment> resultList = attachmentService.getAttachmentsByUserId(id, id, start, limit, null);
        verify(mockUserService, times(2)).getById(id);
        verify(mockTagService, times(2)).fillTags(attachment);

        assertEquals(list, resultList);
    }

    @Test
    public void testGetAttachmentsByUserGroupByDate_Test1() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2012, 1, 1);
        Date date = calendar.getTime();

        final List<Integer> projectList = new ArrayList<Integer>();

        TreeMap<Date, List<Attachment>> resulTreeMap = attachmentService.getAttachmentsByUserGroupByDate(id, id, projectList,
                date, limit);

        assertNotNull(resulTreeMap);
        assertEquals(0, resulTreeMap.size());

    }

    @Test
    public void testGetAttachmentsByUserGroupByDate_Test2() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 1, 1);
        final Date date = calendar.getTime();

        List<Attachment> attachmentList = new ArrayList<Attachment>();

        final List<Integer> projectList = getProjectList();
        when(mockAttachmentMapper.selectByExample(any(AttachmentExample.class))).thenReturn(attachmentList);

        TreeMap<Date, List<Attachment>> resulTreeMap = attachmentService.getAttachmentsByUserGroupByDate(id, id, projectList,
                date, limit);
        verify(mockAttachmentMapper).selectByExample(Mockito.argThat(new ExampleMatcher<AttachmentExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", id)
                        && CriterionVerifier.verifyEqualTo(example, "creatorId", id)
                        && CriterionVerifier.verifyLimit(example, limit)
                        && CriterionVerifier.verifyOrderByClause(example, "created desc")
                        && CriterionVerifier.verifyIn(example, "projectId", projectList)
                        && CriterionVerifier.verifyEqualTo(example, "deleted", false);
            }
        }));
        assertNotNull(resulTreeMap);
        assertEquals(0, resulTreeMap.size());
    }

    @Test
    public void testGetAttachmentsByUserGroupByDate_Test3() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 1, 1);
        Date date = calendar.getTime();

        final List<Integer> projectList = getProjectList();
        when(mockAttachmentMapper.selectByExample(any(AttachmentExample.class))).thenReturn(null);

        TreeMap<Date, List<Attachment>> resulTreeMap = attachmentService.getAttachmentsByUserGroupByDate(id, id, projectList,
                date, limit);
        verify(mockAttachmentMapper).selectByExample(Mockito.argThat(new ExampleMatcher<AttachmentExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", id)
                        && CriterionVerifier.verifyEqualTo(example, "creatorId", id)
                        && CriterionVerifier.verifyLimit(example, limit)
                        && CriterionVerifier.verifyOrderByClause(example, "created desc")
                        && CriterionVerifier.verifyIn(example, "projectId", projectList)
                        && CriterionVerifier.verifyEqualTo(example, "deleted", false);
            }
        }));
        assertNotNull(resulTreeMap);
        assertEquals(0, resulTreeMap.size());
    }

    @Test
    public void testGetAttachmentsByUserGroupByDate_Test4() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 1, 1);
        Date date = calendar.getTime();

        final List<Integer> projectList = getProjectList();
        when(mockAttachmentMapper.selectByExample(any(AttachmentExample.class))).thenReturn(list);

        TreeMap<Date, List<Attachment>> resulTreeMap = attachmentService.getAttachmentsByUserGroupByDate(id, id, projectList,
                date, limit);
        verify(mockAttachmentMapper, times(2)).selectByExample(Mockito.argThat(new ExampleMatcher<AttachmentExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", id)
                        && CriterionVerifier.verifyEqualTo(example, "creatorId", id)
                        && CriterionVerifier.verifyLimit(example, limit)
                        && CriterionVerifier.verifyOrderByClause(example, "created desc")
                        && CriterionVerifier.verifyIn(example, "projectId", projectList)
                        && CriterionVerifier.verifyEqualTo(example, "deleted", false);
            }
        }));

        verify(mockUserService, times(2)).getById(id);
        verify(mockTagService, times(2)).fillTags(attachment);

        assertNotNull(resulTreeMap);
        assertEquals(1, resulTreeMap.size());
    }

    @Test
    public void testGetAttachmentsByTypeAndId() {

        List<Attachment> attachments = attachmentService.getAttachmentsByTypeAndId(attachTypeString, id, start, limit);

        verify(mockAttachmentMapper).selectByExample(Mockito.argThat(new ExampleMatcher<AttachmentExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "attachId", id)
                        && CriterionVerifier.verifyEqualTo(example, "attachType", attachTypeString)
                        && CriterionVerifier.verifyLimit(example, limit) && CriterionVerifier.verifyStart(example, start)
                        && CriterionVerifier.verifyEqualTo(example, "deleted", false);
            }
        }));
        verify(mockUserService, times(2)).getById(id);
        verify(mockTagService, times(2)).fillTags(attachment);

        assertNotNull(attachments);
        assertEquals(attachments.size(), 2);
    }

    @Test
    public void testGetAttachmentsByTypeAndIdWithDiscard() {
        List<Attachment> attachments = attachmentService.getAttachmentsByTypeAndIdWithDiscard(attachTypeString, id, start, limit);

        verify(mockAttachmentMapper).selectByExample(Mockito.argThat(new ExampleMatcher<AttachmentExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "attachId", id)
                        && CriterionVerifier.verifyEqualTo(example, "attachType", attachTypeString)
                        && CriterionVerifier.verifyLimit(example, limit) && CriterionVerifier.verifyStart(example, start)
                        && CriterionVerifier.verifyEqualTo(example, "deleted", true);
            }
        }));
        verify(mockUserService, times(2)).getById(id);
        verify(mockTagService, times(2)).fillTags(attachment);

        assertNotNull(attachments);
        assertEquals(attachments.size(), 2);
    }

    @Test
    public void testCountByExample() {
        int size = 5;
        Attachment sample = getASampleAttachment();

        when(mockAttachmentMapper.countByExample(any(AttachmentExample.class))).thenReturn(size);

        int result = attachmentService.countBySample(sample);

        verify(mockAttachmentMapper).countByExample(argThat(new ExampleMatcher<AttachmentExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", id)
                        && CriterionVerifier.verifyEqualTo(example, "contentType", contentTypeString)
                        && CriterionVerifier.verifyEqualTo(example, "creatorId", id)
                        && CriterionVerifier.verifyEqualTo(example, "attachType", attachTypeString);
            }
        }));
        assertEquals(result, size);
    }

    @Test
    public void testFillAttachments() {

        List<Attachment> list = getASampleListAttachments();

        Attachable mockAttachable = mock(Attachable.class);
        doNothing().when(mockAttachable).setAttachments(list);
        when(mockAttachable.getType()).thenReturn(attachTypeString);
        when(mockAttachable.getId()).thenReturn(id);

        // If you want to mock other methods within same class, use "SPY", refer to Mockito documentation.
        AttachmentService spyAttachmentService = spy(attachmentService);
        doReturn(list).when(spyAttachmentService).getAttachmentsByTypeAndId(attachTypeString, id, start, limit);

        spyAttachmentService.fillAttachments(null, start, limit);
        verify(mockAttachable, times(0)).getType();
        verify(mockAttachable, times(0)).getId();
        verify(spyAttachmentService, times(0)).getAttachmentsByTypeAndId(attachTypeString, id, start, limit);

        spyAttachmentService.fillAttachments(mockAttachable, start, limit);
        verify(mockAttachable, times(1)).getType();
        verify(mockAttachable, times(1)).getId();
        verify(spyAttachmentService, times(1)).getAttachmentsByTypeAndId(attachTypeString, id, start, limit);

    }

    @Test
    public void testDiscardAttachmentInt() {
        when(mockAttachmentMapper.updateByExample(any(Attachment.class), any(AttachmentExample.class))).thenReturn(1);

        attachmentService.discardAttachment(attachTypeString, id);

        verify(mockAttachmentMapper).updateByExampleSelective(any(Attachment.class), any(AttachmentExample.class));
    }

    @Test
    public void testDeleteAttachmentByAttachTypeAndId() {

        attachmentService.deleteAttachmentByAttachTypeAndId(attachTypeString, id);
        verify(mockAttachmentMapper).deleteByExample(argThat(new ExampleMatcher<AttachmentExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "attachId", id)
                        && CriterionVerifier.verifyEqualTo(example, "attachType", attachTypeString);
            }
        }));
    }

    @Test
    public void testGetAttachmentContentById() {

        String expectString = "/attachment/1/2";
        byte[] returnBytes = expectString.getBytes();
        when(mockFileService.readFile(expectString)).thenReturn(returnBytes);

        byte[] result = attachmentService.getAttachmentContentById(1, 2);
        verify(mockFileService).readFile(expectString);
        assertEquals(returnBytes, result);
    }

    @Test
    public void testStageAttachment() {
        int companyId = 1;
        int projectId = 2;
        String name = "ANewAttachment";
        long size = 12312312;
        byte[] file = name.getBytes();
        String expectPath = "/attachment/2/stage/1";

        Attachment sampleAttachment = getASampleAttachment();
        AttachmentService spyAttachmentService = spy(attachmentService);
        doReturn(sampleAttachment).when(spyAttachmentService).create(any(Attachment.class));

        when(mockFileService.writeFile(expectPath, file)).thenReturn(true);

        Attachment result = spyAttachmentService.stageAttachment(companyId, projectId, name, size, contentTypeString, file);
        assertNotNull(result);
        assertEquals(sampleAttachment, result);
        verify(mockFileService).writeFile(expectPath, file);
    }

    @Test
    public void testStageAttachment_NullTest() {
        int companyId = 1;
        int projectId = 2;
        String name = "ANewAttachment";
        long size = 12312312;
        byte[] file = name.getBytes();
        String expectPath = "/attachment/2/stage/1";

        Attachment sampleAttachment = getASampleAttachment();
        AttachmentService spyAttachmentService = spy(attachmentService);
        doReturn(sampleAttachment).when(spyAttachmentService).create(any(Attachment.class));
        doNothing().when(spyAttachmentService).deleteFromTrash(id);
        when(mockFileService.writeFile(expectPath, file)).thenReturn(false);

        Attachment result = spyAttachmentService.stageAttachment(companyId, projectId, name, size, contentTypeString, file);
        assertNull(result);
        verify(mockFileService).writeFile(expectPath, file);
        verify(spyAttachmentService).deleteFromTrash(id);

    }

    @Test
    public void testCopyAttachmentsNotCommentReturn() {
        Discussion discussion = new Discussion();
        discussion.setId(id);
        discussion.setProjectId(id);

        String str = "ANewAttachment";
        byte[] file = str.getBytes();
        String newPath = "/attachment/1/1";

        AttachmentService spyAttachmentService = spy(attachmentService);
        doReturn(list).when(spyAttachmentService).getAttachmentsByTypeAndId(attachTypeString, id, start, limit);
        doReturn(file).when(spyAttachmentService).getAttachmentContentById(id, id);
        when(mockFileService.writeFile(newPath, file)).thenReturn(true);

        spyAttachmentService.copyAttachments(discussion, discussion);

        verify(mockAttachmentMapper).insertSelective(attachment);
        verify(spyAttachmentService).getAttachmentsByTypeAndId("discussion", id, 0, -1);
        verify(spyAttachmentService, times(1)).getAttachmentContentById(id, id);
        verify(mockFileService).writeFile(newPath, file);

    }

    @Test
    public void testCopyAttachmentsNotCommentDelete() {
        Discussion discussion = new Discussion();
        discussion.setId(id);
        discussion.setProjectId(id);

        String str = "ANewAttachment";
        byte[] file = str.getBytes();
        String newPath = "/attachment/1/1";

        AttachmentService spyAttachmentService = spy(attachmentService);
        doReturn(list).when(spyAttachmentService).getAttachmentsByTypeAndId(attachTypeString, id, start, limit);
        doReturn(file).when(spyAttachmentService).getAttachmentContentById(id, id);
        when(mockFileService.writeFile(newPath, file)).thenReturn(false);
        doNothing().when(spyAttachmentService).delete(id);

        spyAttachmentService.copyAttachments(discussion, discussion);

        verify(mockAttachmentMapper, times(2)).insertSelective(attachment);
        verify(spyAttachmentService).getAttachmentsByTypeAndId("discussion", id, 0, -1);
        verify(spyAttachmentService, times(2)).getAttachmentContentById(id, id);
        verify(mockFileService, times(2)).writeFile(newPath, file);
        verify(spyAttachmentService, times(2)).delete(id);

    }

    @Test
    public void testCopyAttachmentsComment() {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setProjectId(id);
        comment.setAttachId(id);

        String str = "ANewAttachment";
        byte[] file = str.getBytes();
        String newPath = "/attachment/1/1";

        AttachmentService spyAttachmentService = spy(attachmentService);
        doReturn(list).when(spyAttachmentService).getAttachmentsByTypeAndId(attachTypeString, id, start, limit);
        doReturn(file).when(spyAttachmentService).getAttachmentContentById(id, id);
        when(mockFileService.writeFile(newPath, file)).thenReturn(true);

        spyAttachmentService.copyAttachments(comment, comment);

        verify(mockAttachmentMapper).insertSelective(attachment);
        verify(spyAttachmentService).getAttachmentsByTypeAndId("comment", id, 0, -1);
        verify(spyAttachmentService, times(1)).getAttachmentContentById(id, id);
        verify(mockFileService).writeFile(newPath, file);

    }

    @Test
    public void testRelocateAttachment1() {
        Discussion discussion = new Discussion();
        discussion.setId(id);
        discussion.setProjectId(id);

        final Attachment attachment = new Attachment();
        attachment.setId(id);
        attachment.setProjectId(id);

        String str = "ANewAttachment";
        byte[] file = str.getBytes();
        String newPath = "/attachment/1/1";

        AttachmentService spyAttachmentService = spy(attachmentService);
        doReturn(list).when(spyAttachmentService).getAttachmentsByTypeAndId(attachTypeString, id, start, limit);
        when(mockFileService.readFile(newPath)).thenReturn(file);
        when(mockFileService.writeFile(newPath, file)).thenReturn(false);

        spyAttachmentService.relocateAttachment(discussion, id);

        verify(mockAttachmentMapper, times(2)).updateByPrimaryKeySelective(argThat(new ObjectMatcher<Attachment>() {
            @Override
            public boolean verifymatches(Attachment item) {
                return (item.getId() == id) && ((item.getProjectId() == id));
            }
        }));
        verify(mockFileService, times(2)).readFile(newPath);
        verify(mockFileService, times(2)).writeFile(newPath, file);
        verify(spyAttachmentService).getAttachmentsByTypeAndId("discussion", id, 0, -1);

    }

    @Test
    public void testRelocateAttachment2() {
        Discussion discussion = new Discussion();
        discussion.setId(id);
        discussion.setProjectId(id);

        String str = "ANewAttachment";
        byte[] file = str.getBytes();
        String newPath = "/attachment/1/1";

        AttachmentService spyAttachmentService = spy(attachmentService);
        doReturn(list).when(spyAttachmentService).getAttachmentsByTypeAndId(attachTypeString, id, start, limit);
        when(mockFileService.readFile(newPath)).thenReturn(file);
        when(mockFileService.writeFile(newPath, file)).thenReturn(true);
        when(mockFileService.deleteFile(newPath)).thenReturn(true);

        spyAttachmentService.relocateAttachment(discussion, id);

        verify(mockAttachmentMapper, times(2)).updateByPrimaryKeySelective(attachment);
        verify(mockFileService, times(4)).readFile(newPath);
        verify(mockFileService, times(2)).writeFile(newPath, file);
        verify(mockFileService, times(2)).deleteFile(newPath);
        verify(spyAttachmentService).getAttachmentsByTypeAndId("discussion", id, 0, -1);

    }

    @Test
    public void testMoveStageAttachment() {
        String oldPath = "/attachment/1/stage/123";
        String newPath = "/attachment/1/123";

        when(mockFileService.renameFile(oldPath, newPath, true)).thenReturn(false);
        attachmentService.moveStageAttachment(123, 1);
        verify(mockFileService).renameFile(oldPath, newPath, true);
    }

    @Test
    public void testGetAttachmentsByProjectIdWithNotDiscard() {

        List<Attachment> resultList = attachmentService.getAttachmentsByProjectIdWithNotDiscard(id, start, limit);

        verify(mockAttachmentMapper).selectByExample(argThat(new ExampleMatcher<AttachmentExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyStart(example, start) && CriterionVerifier.verifyLimit(example, limit)
                        && CriterionVerifier.verifyOrderByClause(example, "id desc")
                        && CriterionVerifier.verifyGraterThan(example, "attachId", 0)
                        && CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyEqualTo(example, "projectId", id);
            }
        }));
        verify(mockUserService, times(2)).getById(id);
        verify(mockTagService, times(2)).fillTags(attachment);

        assertEquals(list, resultList);
    }

    @Test
    public void testDiffIncomingOfAttachable() {
        testDiffAttachable(true, 0);
    }

    @SuppressWarnings("unchecked")
    private void testDiffAttachable(boolean diffFlag, int size) {
        Attachable mockOrig = mock(Attachable.class);
        Attachable mockNew = mock(Attachable.class);
        List<Attachment> sample = new ArrayList<Attachment>();
        List<Attachment> sample2 = new ArrayList<Attachment>();
        Attachment a = new Attachment(1);
        Attachment b = new Attachment(2);
        Attachment c = new Attachment(3);
        sample.add(a);
        sample.add(b);
        sample.add(c);
        sample2.add(c);

        when(mockOrig.getAttachments()).thenReturn(null).thenReturn(sample);
        doNothing().when(mockOrig).setAttachments(any(ArrayList.class));
        when(mockNew.getAttachments()).thenReturn(null).thenReturn(sample2);
        doNothing().when(mockNew).setAttachments(any(ArrayList.class));

        List<Attachment> result = null;

        if (diffFlag) {
            result = attachmentService.diffIncomingOfAttachable(mockOrig, mockNew);
        } else {
            result = attachmentService.diffDeletedOfAttachable(mockOrig, mockNew);
        }

        verify(mockOrig, times(2)).getAttachments();
        verify(mockOrig).setAttachments(any(ArrayList.class));
        verify(mockNew, times(2)).getAttachments();
        verify(mockNew).setAttachments(any(ArrayList.class));
        assertEquals(result.size(), size);
    }

    @Test
    public void testDiffDeletedOfAttachable() {
        testDiffAttachable(false, 2);
    }

    @Test
    public void testDiffDeletedOfAttachable_NullTest() {
        Attachable mockOrig = mock(Attachable.class);
        Attachable mockNew = mock(Attachable.class);

        List<Attachment> resAttachments = attachmentService.diffDeletedOfAttachable(null, mockNew);
        assertNull(resAttachments);

        List<Attachment> resAttachments2 = attachmentService.diffDeletedOfAttachable(mockOrig, null);
        assertNull(resAttachments2);

        List<Attachment> resAttachments3 = attachmentService.diffDeletedOfAttachable(null, null);
        assertNull(resAttachments3);
    }

    @Test
    public void testRecoverAttachment() {

        attachmentService.recoverAttachment(attachTypeString, id);
        verify(mockAttachmentMapper).updateByExampleSelective(any(Attachment.class), any(AttachmentExample.class));
    }

    @Test
    public void testAddAttachmentsForAttachable_NullListAttachmentTest() {

        Discussion discussion = new Discussion();
        List<Attachment> ret = attachmentService.addAttachmentsForAttachable(discussion, null);

        assertNotNull(ret);
        assertEquals(0, ret.size());
    }

    @Test
    public void testAddAttachmentsForAttachable_NormalTest() {
        Discussion discussion = new Discussion(id);

        AttachmentServiceImpl spyAttachmentServiceImpl = spy(attachmentService);
        doReturn(attachment).when(spyAttachmentServiceImpl).addAttachmentForAttachable(discussion, attachment);

        List<Attachment> ret = spyAttachmentServiceImpl.addAttachmentsForAttachable(discussion, list);

        verify(spyAttachmentServiceImpl, times(2)).addAttachmentForAttachable(discussion, attachment);
        assertNotNull(ret);
        assertEquals(2, ret.size());
        assertEquals(list, ret);

    }

    @Test
    public void testAppendAttachmentsForAttachable() {
        Discussion discussion = new Discussion();
        discussion.setId(id);

        AttachmentService spyAttachmentServiceImpl = spy(attachmentService);
        doNothing().when(spyAttachmentServiceImpl).fillAttachments(any(Attachable.class), Mockito.eq(start), Mockito.eq(limit));
        doReturn(list).when(spyAttachmentServiceImpl).diffIncomingOfAttachable(any(Attachable.class), any(Attachable.class));
        doReturn(list).when(spyAttachmentServiceImpl).diffDeletedOfAttachable(any(Attachable.class), any(Attachable.class));
        doReturn(attachment).when(spyAttachmentServiceImpl).addAttachmentForAttachable(any(Attachable.class),
                Mockito.eq(attachment));
        doNothing().when(spyAttachmentServiceImpl).delete(attachment.getId());
        when(mockIdentifiableManager.getIdentifiableByTypeAndId(attachTypeString, id)).thenReturn(discussion);

        spyAttachmentServiceImpl.appendAttachmentsForAttachable(discussion);

        verify(mockIdentifiableManager).getIdentifiableByTypeAndId("discussion", id);
        verify(spyAttachmentServiceImpl).fillAttachments(any(Attachable.class), Mockito.eq(0), Mockito.eq(-1));
        verify(spyAttachmentServiceImpl).diffIncomingOfAttachable(any(Attachable.class), any(Attachable.class));
        verify(spyAttachmentServiceImpl, times(2)).addAttachmentForAttachable(any(Attachable.class), Mockito.eq(attachment));
        verify(spyAttachmentServiceImpl, times(2)).delete(id);
    }

    @Test
    public void testAddAttachmentForAttachable_AttachmentNull() {
        Attachment ret = attachmentService.addAttachmentForAttachable(null, null);
        assertNull(ret);
    }

    @Test
    public void testAddAttachmentForAttachable_Normal() {
        Discussion inputDiscussion = new Discussion();
        inputDiscussion.setProjectId(id);
        inputDiscussion.setId(id);
        inputDiscussion.setCreatorName("Test User");

        AttachmentServiceImpl spyAttachmentServiceImpl = spy(attachmentService);
        doReturn(true).when(spyAttachmentServiceImpl).moveStageAttachment(id, id);
        doReturn(attachment).when(spyAttachmentServiceImpl).updateSelective(attachment);

        Attachment ret = spyAttachmentServiceImpl.addAttachmentForAttachable(inputDiscussion, attachment);

        verify(spyAttachmentServiceImpl).updateSelective(attachment);
        verify(spyAttachmentServiceImpl).moveStageAttachment(id, id);

        assertNotNull(ret);
        assertEquals(id, (int) ret.getProjectId());
        assertEquals(id, (int) ret.getAttachId());
        assertEquals("discussion", ret.getAttachType());
        assertEquals(id, (int) ret.getTargetId());
        assertEquals("discussion", ret.getTargetType());
        assertEquals("Test User", ret.getCreatorName());
    }
}
