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
package com.onboard.web.api.search;

import java.util.List;

import org.elevenframework.web.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.onboard.domain.transform.SearchResultTransform;
import com.onboard.dto.SearchResultDTO;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.index.SearchService;
import com.onboard.service.index.model.SearchQueryBuilder;
import com.onboard.service.index.model.SearchResult;
import com.onboard.service.security.interceptors.LoginRequired;
import com.onboard.service.web.SessionService;

@RequestMapping("/{companyId}/search")
@Controller
public class SearchApiController {

    private static final int DEFAULT_ITEMS_NO_PER_PAGE = 30;
    public static final Logger logger = LoggerFactory.getLogger(SearchApiController.class);

    @Autowired
    private SearchService searchService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SessionService session;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @Interceptors({ LoginRequired.class })
    @ResponseBody
    public SearchResultDTO doSearch(@PathVariable int companyId,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer currentPage,
            @RequestParam(required = false, defaultValue = "") String key, @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer userId) {
        logger.info("start to search: " + key);
        SearchResult items;

        List<Integer> projectList = projectService.getProjectIdListByUserByCompany(session.getCurrentUser().getId(),
                companyId, 0, -1);
        SearchQueryBuilder searchQueryBuilder = SearchQueryBuilder.getBuilder().projectIds(projectList);
        if (type != null && !type.equals("all")) {
            searchQueryBuilder.modelTypes(Lists.newArrayList(type));
        }
        if (userId != null) {
            logger.info("start to search by userId: " + userId);
            if (userService.isUserInCompany(userId, companyId))
                searchQueryBuilder.relatorIds(Lists.newArrayList(userId));
        }
        items = searchService.search(key, searchQueryBuilder.build(), (currentPage - 1) * DEFAULT_ITEMS_NO_PER_PAGE,
                DEFAULT_ITEMS_NO_PER_PAGE);
        SearchResultDTO searchResultDTO = SearchResultTransform.searchItemsToSearchResult(items);
        return searchResultDTO;
    }
}
