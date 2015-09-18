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
package com.onboard.service.help.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.onboard.domain.mapper.HelpTipMapper;
import com.onboard.domain.mapper.model.HelpTipExample;
import com.onboard.domain.model.HelpTip;
import com.onboard.service.help.HelpTipService;

/**
 * {@link com.onboard.service.help.HelpTipService} Service implementation
 * 
 * @generated_by_elevenframework
 * 
 */
@Transactional
@Service("helpTipServiceBean")
public class HelpTipServiceImpl implements HelpTipService {

    @Autowired
    private HelpTipMapper helpTipMapper;

    @Override
    public HelpTip getHelpTipById(int id) {
        return helpTipMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<HelpTip> getHelpTips(int start, int limit) {
        HelpTip helpTip = new HelpTip();
        helpTip.setDeleted(false);
        HelpTipExample example = new HelpTipExample(helpTip);
        example.setLimit(start, limit);
        return helpTipMapper.selectByExample(example);
    }

    @Override
    public List<HelpTip> getHelpTipsByExample(HelpTip item, int start, int limit) {
        HelpTipExample example = new HelpTipExample(item);
        example.setLimit(start, limit);
        example.setOrderByClause("id desc");
        return helpTipMapper.selectByExample(example);
    }

    @Override
    public int countByExample(HelpTip item) {
        HelpTipExample example = new HelpTipExample(item);
        return helpTipMapper.countByExample(example);
    }

    @Override
    public HelpTip createHelpTip(HelpTip item) {
        item.setCreated(new Date());
        item.setDeleted(false);
        helpTipMapper.insert(item);
        return item;
    }

    @Override
    public HelpTip updateHelpTip(HelpTip item) {
        item.setUpdated(new Date());
        helpTipMapper.updateByPrimaryKey(item);
        return item;
    }

    @Override
    public void deleteHelpTip(int id) {
        helpTipMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Map<String, List<HelpTip>> groupHelpTipsByTitle(List<HelpTip> helpTips) {
        Map<String, List<HelpTip>> helpTipsMap = seperateHelpTipByTitle(helpTips);
        return helpTipsMap;
    }

    private Map<String, List<HelpTip>> seperateHelpTipByTitle(List<HelpTip> helpTips) {
        Map<String, List<HelpTip>> helpTipsGroupByTitleMap = new HashMap<String, List<HelpTip>>();
        for (HelpTip helpTip : helpTips) {
            String title = helpTip.getTitle();
            if (!helpTipsGroupByTitleMap.containsKey(title)) {
                List<HelpTip> list = Lists.newArrayList();
                list.add(helpTip);
                helpTipsGroupByTitleMap.put(title, list);
            } else {
                helpTipsGroupByTitleMap.get(title).add(helpTip);
            }
        }

        return helpTipsGroupByTitleMap;
    }

    @Override
    public List<HelpTip> updateHelpTipByGroupTitle(String title, String newTitle) {
        HelpTip item = new HelpTip();
        item.setTitle(title);
        HelpTipExample example = new HelpTipExample(item);
        example.setLimit(0, -1);
        List<HelpTip> helpTips = helpTipMapper.selectByExample(example);
        for (HelpTip helpTip : helpTips) {
            helpTip.setTitle(newTitle);
            helpTipMapper.updateByPrimaryKey(helpTip);
        }
        return helpTips;
    }

    @Override
    public void deleteHelpTipByGroupTitle(String title) {
        HelpTip item = new HelpTip();
        item.setTitle(title);
        HelpTipExample example = new HelpTipExample(item);
        example.setLimit(0, -1);
        List<HelpTip> helpTips = helpTipMapper.selectByExample(example);
        for (HelpTip helpTip : helpTips) {
            helpTipMapper.deleteByExample(new HelpTipExample(helpTip));
        }

    }

    @Override
    public void discardHelpTip(int id) {
        HelpTip helpTip = helpTipMapper.selectByPrimaryKey(id);
        helpTip.setDeleted(true);
        helpTipMapper.updateByPrimaryKeySelective(helpTip);

    }

    @Override
    public void discardHelpTipsByGroupTitle(String title) {
        HelpTip item = new HelpTip();
        item.setTitle(title);
        HelpTipExample example = new HelpTipExample(item);

        example.setLimit(0, -1);
        List<HelpTip> helpTips = helpTipMapper.selectByExample(example);
        for (HelpTip helpTip : helpTips) {
            helpTip.setDeleted(true);
            helpTipMapper.updateByPrimaryKeySelective(helpTip);
        }
    }
}
