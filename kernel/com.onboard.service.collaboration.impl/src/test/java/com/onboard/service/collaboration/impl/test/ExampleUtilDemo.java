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
//package com.onboard.service.collaboration.impl.test;
//
//import static org.junit.Assert.assertTrue;
//import static org.mockito.Matchers.any;
//import static org.mockito.Matchers.argThat;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.runners.MockitoJUnitRunner;
//
//import com.onboard.domain.mapper.AttachmentMapper;
//import com.onboard.domain.mapper.UserMapper;
//import com.onboard.domain.mapper.common.BaseExample;
//import com.onboard.domain.mapper.model.AttachmentExample;
//import com.onboard.domain.model.Attachment;
//import com.onboard.service.collaboration.TagService;
//import com.onboard.service.collaboration.impl.AttachmentServiceImpl;
//import com.onboard.test.exampleutils.CriterionVerifier;
//import com.onboard.test.exampleutils.ExampleMatcher;
//
//@RunWith(MockitoJUnitRunner.class)
//public class ExampleUtilDemo {
//
//    @Mock
//    private AttachmentMapper mockAttachmentMapper;
//
//    @Mock
//    private UserMapper mockUserMapper;
//
//    @Mock
//    private TagService mockTagService;
//
//    @InjectMocks
//    private AttachmentServiceImpl attachmentService;
//
//    @Test
//    public void ExampleMatcherUsage() {
//
//        int projectId = 123;
//        int start = 0;
//        int limit = 5;
//        List<Attachment> list = new ArrayList<Attachment>();
//
//        Attachment sample1 = new Attachment();
//        Attachment sample2 = new Attachment();
//        sample1.setProjectId(projectId);
//        sample2.setProjectId(projectId);
//        list.add(sample1);
//        list.add(sample2);
//
//        when(mockAttachmentMapper.selectByExample(any(AttachmentExample.class))).thenReturn(list);
//        attachmentService.getAttachmentsByProjectId(projectId, start, limit);
//
//        verify(mockAttachmentMapper).selectByExample(argThat(new ExampleMatcher<AttachmentExample>() {
//            /**
//             * 重载matches方法即可
//             */
//            @Override
//            public boolean matches(BaseExample baseExample) {
//                return CriterionVerifier.verifyStart(baseExample, 0) && CriterionVerifier.verifyLimit(baseExample, 5)
//                        && CriterionVerifier.verifyEqualTo(baseExample, "projectId", 123);
//            }
//        }));
//
//    }
//
//    @Test
//    public void CriterionVerifierUsage() {
//
//        Attachment sample = new Attachment();
//        sample.setProjectId(123);
//        sample.setDeleted(true);
//        sample.setAttachId(2);
//
//        AttachmentExample example = new AttachmentExample(sample);
//
//        example.setStart(1);
//        example.setLimit(5);
//        example.setDistinct(true);
//        example.setOrderByClause("id desc");
//
//        assertTrue(CriterionVerifier.verifyStart(example, 1));
//        assertTrue(CriterionVerifier.verifyLimit(example, 5));
//        assertTrue(CriterionVerifier.verifyDistinct(example, true));
//        assertTrue(CriterionVerifier.verifyOrderByClause(example, "id desc"));
//        assertTrue(CriterionVerifier.verifyEqualTo(example, "attachId", 2));
//        assertTrue(CriterionVerifier.verifyEqualTo(example, "projectId", 123));
//        assertTrue(CriterionVerifier.verifyEqualTo(example, "deleted", true));
//
//        example.getOredCriteria().get(0).andAttachIdBetween(1, 3);
//        assertTrue(CriterionVerifier.verifyBetween(example, "attachId", 1, 3));
//
//        example = new AttachmentExample(sample);
//        example.getOredCriteria().get(0).andAttachIdNotBetween(1, 3);
//        assertTrue(CriterionVerifier.verifyNotBetween(example, "attachId", 1, 3));
//
//        sample.setAttachId(null);
//        example = new AttachmentExample(sample);
//        example.getOredCriteria().get(0).andAttachIdIsNull();
//        assertTrue(CriterionVerifier.verifyIsNull(example, "attachId"));
//
//        example = new AttachmentExample(sample);
//        example.getOredCriteria().get(0).andAttachIdIsNotNull();
//        assertTrue(CriterionVerifier.verifyIsNotNull(example, "attachId"));
//
//        example = new AttachmentExample(sample);
//        List<Integer> list = new ArrayList<Integer>();
//        list.add(5);
//        list.add(6);
//        example.getOredCriteria().get(0).andAttachIdIn(list);
//        List<Integer> list2 = new ArrayList<Integer>();
//        list2.add(5);
//        list2.add(6);
//        assertTrue(CriterionVerifier.verifyIn(example, "attachId", list2));
//
//        example = new AttachmentExample(sample);
//        list = new ArrayList<Integer>();
//        list.add(5);
//        list.add(6);
//        example.getOredCriteria().get(0).andAttachIdNotIn(list);
//        list2 = new ArrayList<Integer>();
//        list2.add(5);
//        list2.add(6);
//        assertTrue(CriterionVerifier.verifyNotIn(example, "attachId", list2));
//
//        example = new AttachmentExample(sample);
//        example.getOredCriteria().get(0).andAttachIdGreaterThan(3);
//        assertTrue(CriterionVerifier.verifyGraterThan(example, "attachId", 3));
//
//        example = new AttachmentExample(sample);
//        example.getOredCriteria().get(0).andAttachIdGreaterThanOrEqualTo(4);
//        assertTrue(CriterionVerifier.verifyGraterThanOrEqualTo(example, "attachId", 4));
//
//        example = new AttachmentExample(sample);
//        example.getOredCriteria().get(0).andAttachIdLessThan(7);
//        assertTrue(CriterionVerifier.verifyLessThan(example, "attachId", 7));
//
//        example = new AttachmentExample(sample);
//        example.getOredCriteria().get(0).andAttachIdLessThanOrEqualTo(8);
//        assertTrue(CriterionVerifier.verifyLessThanOrEqualTo(example, "attachId", 8));
//
//        example = new AttachmentExample(sample);
//        example.getOredCriteria().get(0).andAttachIdNotEqualTo(16);
//        assertTrue(CriterionVerifier.verifyNotEqualTo(example, "attachId", 16));
//
//    }
//
//    public UserMapper getMockUserMapper() {
//        return mockUserMapper;
//    }
//
//    public void setMockUserMapper(UserMapper mockUserMapper) {
//        this.mockUserMapper = mockUserMapper;
//    }
//
//    public TagService getMockTagService() {
//        return mockTagService;
//    }
//
//    public void setMockTagService(TagService mockTagService) {
//        this.mockTagService = mockTagService;
//    }
//}
