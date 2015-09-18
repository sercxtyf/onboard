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
package com.onboard.service.help;

import java.util.List;
import java.util.Map;

import com.onboard.domain.model.HelpTip;

/**
 * {@link HelpTip} Service Interface
 * 
 * @generated_by_elevenframework
 * 
 */
public interface HelpTipService {
    /**
     * Get item by id
     * 
     * @param id
     * @return item
     */
    HelpTip getHelpTipById(int id);

    /**
     * Get item list
     * 
     * @param start
     * @param limit
     * @return the item list
     */
    List<HelpTip> getHelpTips(int start, int limit);

    /**
     * Get item list by example
     * 
     * @param item an example of item
     * @param start
     * @param limit
     * @return the item list
     */
    List<HelpTip> getHelpTipsByExample(HelpTip item, int start, int limit);

    /**
     * Get item count by example
     * 
     * @param item an example of item
     * @return the count
     */
    int countByExample(HelpTip item);

    /**
     * Create
     * 
     * @param item
     * @return the created HelpTip
     */
    HelpTip createHelpTip(HelpTip item);

    /**
     * Update
     * 
     * @param item
     * @return the updated item
     */
    HelpTip updateHelpTip(HelpTip item);

    /**
     * Delete
     * 
     * @param id
     */
    void deleteHelpTip(int id);

    /**
     * Split helpTips into groups according to title
     * 
     * @param helpTips
     * @return grouped helpTips
     */
    Map<String, List<HelpTip>> groupHelpTipsByTitle(List<HelpTip> helpTips);

    /**
     * Update the title of a group
     * 
     * @param title
     * @param newTitle
     * @return updated helpTips
     */
    List<HelpTip> updateHelpTipByGroupTitle(String title, String newTitle);

    /**
     * Delete helpTips by title 
     * 
     * @param title
     */
    void deleteHelpTipByGroupTitle(String title);

    /**
     * Discard helpTip by id
     * 
     * @param id
     */
    void discardHelpTip(int id);

    /**
     * Discard helpTips by group-title
     * @param title
     */
    void discardHelpTipsByGroupTitle(String title);
}
