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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.onboard.domain.mapper.KeywordMapper;
import com.onboard.domain.mapper.model.KeywordExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Keyword;
import com.onboard.domain.model.type.Recommendable;
import com.onboard.service.collaboration.impl.KeywordServiceImpl;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.exampleutils.ObjectMatcher;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class KeywordServiceImplTest {

    @Mock
    private KeywordMapper mockedKeywordMapper;

    @Mock
    private Recommendable mockedRecommendable;

    @InjectMocks
    private KeywordServiceImpl keywordServiceImpl;

    public Keyword getASampleKeyword() {
        Keyword keyword = new Keyword();
        keyword.setId(ModuleHelper.id);
        keyword.setAttachId(ModuleHelper.attachId);
        keyword.setAttachType(ModuleHelper.attachType);
        keyword.setCompanyId(ModuleHelper.companyId);
        keyword.setKeyword(ModuleHelper.content);
        keyword.setTimes(ModuleHelper.times);
        keyword.setProjectId(ModuleHelper.projectId);
        return keyword;
    }

    public List<Keyword> getASampleKeywordList() {
        List<Keyword> keywords = Lists.newArrayList();
        keywords.add(getASampleKeyword());
        keywords.add(getASampleKeyword());
        return keywords;
    }

    private void runAssert(Keyword keyword) {
        assertEquals(ModuleHelper.id, (int) keyword.getId());
        assertEquals(ModuleHelper.attachId, (int) keyword.getAttachId());
        assertEquals(ModuleHelper.attachType, keyword.getAttachType());
        assertEquals(ModuleHelper.companyId, (int) keyword.getCompanyId());
        assertEquals(ModuleHelper.content, keyword.getKeyword());
        assertEquals(ModuleHelper.projectId, (int) keyword.getProjectId());
    }

    @Before
    public void setup() {
        when(mockedKeywordMapper.selectByExample(Mockito.any(KeywordExample.class))).thenReturn(getASampleKeywordList());
        when(mockedRecommendable.getId()).thenReturn(ModuleHelper.id);
        when(mockedRecommendable.getType()).thenReturn(ModuleHelper.type);
        when(mockedRecommendable.generateText()).thenReturn(ModuleHelper.content);
    }

    @Test
    public void getKeywordsByUserTest() {
        List<Keyword> result = keywordServiceImpl.getKeywordsByUser(ModuleHelper.userId, ModuleHelper.start, ModuleHelper.limit);
        verify(mockedKeywordMapper).selectByExample(Mockito.argThat(new ExampleMatcher<KeywordExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "attachType", "user")
                        && CriterionVerifier.verifyEqualTo(example, "attachId", ModuleHelper.userId)
                        && CriterionVerifier.verifyOrderByClause(example, "tfidf desc")
                        && CriterionVerifier.verifyStart(example, ModuleHelper.start)
                        && CriterionVerifier.verifyLimit(example, ModuleHelper.limit)
                        && CriterionVerifier.verifyEqualTo(example, "deleted", false);
            }
        }));
        for (Keyword keyword : result) {
            runAssert(keyword);
        }
    }

    @Test
    public void getKeywordsByUserByCompanyTest() {
        List<Keyword> result = keywordServiceImpl.getKeywordsByUserByCompany(ModuleHelper.companyId, ModuleHelper.userId,
                ModuleHelper.start, ModuleHelper.limit);
        verify(mockedKeywordMapper).selectByExample(Mockito.argThat(new ExampleMatcher<KeywordExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "attachType", "user")
                        && CriterionVerifier.verifyEqualTo(example, "attachId", ModuleHelper.userId)
                        && CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId)
                        && CriterionVerifier.verifyOrderByClause(example, "tfidf desc")
                        && CriterionVerifier.verifyStart(example, ModuleHelper.start)
                        && CriterionVerifier.verifyLimit(example, ModuleHelper.limit)
                        && CriterionVerifier.verifyEqualTo(example, "deleted", false);
            }
        }));
        for (Keyword keyword : result) {
            runAssert(keyword);
        }
    }

    @Test
    public void generateOrUpdateKeywordsByIdentifiableTest() {
        keywordServiceImpl.generateOrUpdateKeywordsByIdentifiable(mockedRecommendable);
        verify(mockedKeywordMapper, Mockito.times(getASampleKeywordList().size())).updateByPrimaryKey(
                Mockito.argThat(new ObjectMatcher<Keyword>() {

                    @Override
                    public boolean verifymatches(Keyword item) {
                        return item.getDeleted();
                    }
                }));
        verify(mockedKeywordMapper).insert(Mockito.any(Keyword.class));
    }

    @Test
    public void deleteKeywordsByIdentifiableTest() {
        keywordServiceImpl.deleteKeywordsByIdentifiable(mockedRecommendable);
        verify(mockedKeywordMapper).selectByExample(Mockito.argThat(new ExampleMatcher<KeywordExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "attachType", ModuleHelper.type)
                        && CriterionVerifier.verifyEqualTo(example, "attachId", ModuleHelper.id)
                        && CriterionVerifier.verifyEqualTo(example, "deleted", false);
            }
        }));
        verify(mockedKeywordMapper, Mockito.times(getASampleKeywordList().size())).updateByPrimaryKey(
                Mockito.argThat(new ObjectMatcher<Keyword>() {

                    @Override
                    public boolean verifymatches(Keyword item) {
                        return item.getDeleted();
                    }
                }));
    }

    @Test
    public void getKeywordsByTextTest() {
        List<String> result = keywordServiceImpl.getKeywordsByText("这是一个测试");
        assertEquals(1, result.size());
        assertEquals("测试", result.get(0));
    }

    @Test
    public void addKeywordToUserTestTrueBranch() {
        keywordServiceImpl.addKeywordToUser(mockedRecommendable, ModuleHelper.userId);
        verify(mockedKeywordMapper, Mockito.times(3)).selectByExample(Mockito.any(KeywordExample.class));
        verify(mockedKeywordMapper, Mockito.times(getASampleKeywordList().size() * 2)).updateByPrimaryKey(
                Mockito.any(Keyword.class));
    }

    @Test
    public void addKeywordToUserTestFalseBranch() {
        when(mockedKeywordMapper.selectByExample(Mockito.any(KeywordExample.class))).thenReturn(null);
        keywordServiceImpl.addKeywordToUser(mockedRecommendable, ModuleHelper.userId);
        verify(mockedKeywordMapper, Mockito.times(1)).selectByExample(Mockito.any(KeywordExample.class));
        verify(mockedKeywordMapper, Mockito.times(0)).updateByPrimaryKey(Mockito.any(Keyword.class));

    }

    @Test
    public void deleteKeywordByExampleTest() {
        keywordServiceImpl.deleteKeywordByExample(new KeywordExample(getASampleKeyword()));
        verify(mockedKeywordMapper, Mockito.times(getASampleKeywordList().size())).updateByPrimaryKey(
                Mockito.argThat(new ObjectMatcher<Keyword>() {

                    @Override
                    public boolean verifymatches(Keyword item) {
                        return item.getDeleted();
                    }
                }));
    }

    @Test
    public void regenerateKeywordTfidfTest() {
        keywordServiceImpl.regenerateKeywordTfidf();
        verify(mockedKeywordMapper).selectByExample(Mockito.argThat(new ExampleMatcher<KeywordExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyEqualTo(example, "attachType", "user")
                        && CriterionVerifier.verifyEqualTo(example, "deleted", false);
            }
        }));

    }

}
