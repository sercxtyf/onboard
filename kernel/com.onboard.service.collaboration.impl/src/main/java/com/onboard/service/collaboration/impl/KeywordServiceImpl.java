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
package com.onboard.service.collaboration.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hankcs.hanlp.HanLP;
import com.onboard.domain.mapper.KeywordMapper;
import com.onboard.domain.mapper.model.KeywordExample;
import com.onboard.domain.model.Keyword;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.domain.model.type.Recommendable;
import com.onboard.service.collaboration.KeywordService;

/**
 * {@link com.onboard.service.collaboration.KeywordService} Service implementation
 * 
 * @generated_by_elevenframework
 * 
 */
@Transactional
@Service("keywordServiceBean")
public class KeywordServiceImpl implements KeywordService {

    @Autowired
    private KeywordMapper keywordMapper;

    @Override
    public List<Keyword> getKeywordsByUser(int userId, int start, int limit) {
        Keyword keyword = new Keyword(false);
        keyword.setAttachType(new User().getType());
        keyword.setAttachId(userId);
        KeywordExample keywordExample = new KeywordExample(keyword);
        keywordExample.setOrderByClause("tfidf desc");
        keywordExample.setStart(start);
        keywordExample.setLimit(limit);
        return keywordMapper.selectByExample(keywordExample);
    }

    @Override
    public List<Keyword> getKeywordsByUserByCompany(int companyId, int userId, int start, int limit) {
        Keyword keyword = new Keyword(false);
        keyword.setAttachType(new User().getType());
        keyword.setAttachId(userId);
        keyword.setCompanyId(companyId);
        KeywordExample keywordExample = new KeywordExample(keyword);
        keywordExample.setOrderByClause("tfidf desc");
        keywordExample.setStart(start);
        keywordExample.setLimit(limit);
        return keywordMapper.selectByExample(keywordExample);
    }

    @Override
    public void generateOrUpdateKeywordsByIdentifiable(Recommendable identifiable) {
        deleteKeywordsByIdentifiable(identifiable);
        List<String> keywords = HanLP.extractKeyword(identifiable.generateText(), PER_IDENTIFIABLE_KEYWORD_COUNT);
        for (String keyword : keywords) {
            keywordMapper.insert(generateKeywordObjectByIdentifiableAndString(identifiable, keyword));
        }
    }

    private Keyword generateKeywordObjectByIdentifiableAndString(BaseProjectItem identifiable, String keyword) {
        Keyword result = new Keyword(false);
        result.setAttachType(identifiable.getType());
        result.setAttachId(identifiable.getId());
        result.setCompanyId(identifiable.getCompanyId());
        result.setProjectId(identifiable.getProjectId());
        result.setKeyword(keyword);
        result.setTimes(1l);
        return result;
    }

    private Keyword generateUserKeywordObject(int companyId, int projectId, int userId, String keyword) {
        Keyword result = new Keyword(false);
        result.setAttachType(new User().getType());
        result.setAttachId(userId);
        result.setCompanyId(companyId);
        result.setProjectId(projectId);
        result.setKeyword(keyword);
        return result;
    }

    @Override
    public void deleteKeywordsByIdentifiable(Recommendable identifiable) {
        Keyword keyword = new Keyword(false);
        keyword.setAttachId(identifiable.getId());
        keyword.setAttachType(identifiable.getType());
        KeywordExample keywordExample = new KeywordExample(keyword);
        List<Keyword> keywords = keywordMapper.selectByExample(keywordExample);
        if (null != keywords && 0 != keywords.size()) {
            for (Keyword toDeleteKeyword : keywords) {
                toDeleteKeyword.setDeleted(true);
                keywordMapper.updateByPrimaryKey(toDeleteKeyword);
            }
        }
    }

    @Override
    public List<String> getKeywordsByText(String text) {
        return HanLP.extractKeyword(text, PER_IDENTIFIABLE_KEYWORD_COUNT);
    }

    @Override
    public void addKeywordToUser(Recommendable identifiable, int userId) {
        Keyword keywordExample = new Keyword(false);
        keywordExample.setAttachId(identifiable.getId());
        keywordExample.setAttachType(identifiable.getType());
        List<Keyword> attachKeywords = keywordMapper.selectByExample(new KeywordExample(keywordExample));
        if (attachKeywords != null) {
            for (Keyword keyword : attachKeywords) {
                List<Keyword> existUserKeywords = keywordMapper.selectByExample(new KeywordExample(generateUserKeywordObject(
                        identifiable.getCompanyId(), identifiable.getProjectId(), userId, keyword.getKeyword())));
                if (null == existUserKeywords || 0 == existUserKeywords.size()) {
                    Keyword newKeyword = generateUserKeywordObject(identifiable.getCompanyId(), identifiable.getProjectId(),
                            userId, keyword.getKeyword());
                    newKeyword.setTimes(1l);
                    keywordMapper.insert(newKeyword);
                } else {
                    for (Keyword existUserKeyword : existUserKeywords) {
                        existUserKeyword.addTimes(1);
                        keywordMapper.updateByPrimaryKey(existUserKeyword);
                    }
                }
            }
        }
    }

    /**
     * 获取tfidf
     * 
     * @param keyword
     *            关键词
     * @param times
     *            关键词的次数
     * @param userId
     *            关联的用户
     * @param userIds
     *            所有用户id
     * @param userKeywordMap
     *            用户关键词映射
     * @return
     */
    private Double getTfidf(String keyword, long times, int userId, Map<Integer, List<Keyword>> userKeywordMap) {
        long wordCount = times;
        long totalCount = 1;
        List<Keyword> userKeywords = userKeywordMap.get(userId);
        if (userKeywords == null) {
            userKeywords = Lists.newArrayList();
        }
        for (Keyword userKeyword : userKeywords) {
            totalCount += userKeyword.getTimes();
        }
        double tf = (double) wordCount / totalCount;
        long totalDocumentCount = userKeywordMap.keySet().size();
        long includeWordDocumentCount = 1;
        for (Integer thisUserId : userKeywordMap.keySet()) {
            List<Keyword> thisUserKeywords = userKeywordMap.get(thisUserId);
            boolean containKeyword = false;
            for (Keyword thisKeyword : thisUserKeywords) {
                if (thisKeyword.getKeyword().equals(keyword)) {
                    containKeyword = true;
                    break;
                }
            }
            if (containKeyword) {
                includeWordDocumentCount++;
            }
        }
        double idf = Math.log((double) totalDocumentCount / includeWordDocumentCount);
        return tf * idf;
    }

    @Override
    public void deleteKeywordByExample(KeywordExample keywordExample) {
        List<Keyword> keywords = keywordMapper.selectByExample(keywordExample);
        for (Keyword keyword : keywords) {
            keyword.setDeleted(true);
            keywordMapper.updateByPrimaryKey(keyword);
        }
    }

    @Override
    public void regenerateKeywordTfidf() {
        Keyword keywordExample = new Keyword(false);
        keywordExample.setAttachType(new User().getType());
        List<Keyword> projectKeywords = keywordMapper.selectByExample(new KeywordExample(keywordExample));
        Map<Integer, List<Keyword>> userKeywordMap = Maps.newHashMap();
        for (Keyword keyword : projectKeywords) {
            if (userKeywordMap.containsKey(keyword.getAttachId())) {
                List<Keyword> userKeywords = userKeywordMap.get(keyword.getAttachId());
                userKeywords.add(keyword);
                userKeywordMap.put(keyword.getAttachId(), userKeywords);
            } else {
                userKeywordMap.put(keyword.getAttachId(), Lists.newArrayList(keyword));
            }
        }
        for (Keyword keyword : projectKeywords) {
            Double beforeTfidf = keyword.getTfidf();
            keyword.setTfidf(getTfidf(keyword.getKeyword(), keyword.getTimes(), keyword.getAttachId(), userKeywordMap));
            if (keyword.getTfidf() != beforeTfidf) {
                keywordMapper.updateByPrimaryKey(keyword);
            }
        }
    }
}
