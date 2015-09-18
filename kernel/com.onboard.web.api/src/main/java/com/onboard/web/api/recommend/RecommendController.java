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
package com.onboard.web.api.recommend;

import java.util.ArrayList;
import java.util.List;

import org.elevenframework.web.interceptor.Interceptors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.onboard.domain.model.Bug;
import com.onboard.domain.model.Keyword;
import com.onboard.domain.model.Step;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.User;
import com.onboard.domain.transform.UserTransform;
import com.onboard.dto.UserDTO;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.BugService;
import com.onboard.service.collaboration.KeywordService;
import com.onboard.service.collaboration.StepService;
import com.onboard.service.collaboration.TodoService;
import com.onboard.service.security.interceptors.ProjectChecking;
import com.onboard.service.security.interceptors.ProjectMemberRequired;

@RequestMapping(value = "/{companyId}/projects/{projectId}")
@Controller
public class RecommendController {

    @Autowired
    TodoService todoService;

    @Autowired
    private BugService bugService;

    @Autowired
    private KeywordService keywordService;

    @Autowired
    private UserService userService;

    @Autowired
    private StepService stepService;

    @RequestMapping(value = "/todos/{todoId}/recommend", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class, ProjectChecking.class })
    @ResponseBody
    public UserDTO getTodoRecommendAssignee(@PathVariable("companyId") int companyId,
            @PathVariable("projectId") int projectId, @PathVariable("todoId") int todoId) {
        Todo todo = todoService.getById(todoId);
        List<String> todoKeywords = keywordService.getKeywordsByText(todo.generateText());
        return deal(projectId, todoKeywords);
    }

    @RequestMapping(value = "/bugs/{bugId}/recommend", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class, ProjectChecking.class })
    @ResponseBody
    public UserDTO getBugRecommendAssignee(@PathVariable("companyId") int companyId,
            @PathVariable("projectId") int projectId, @PathVariable("bugId") int bugId) {
        Bug bug = bugService.getById(bugId);
        List<String> bugKeywords = keywordService.getKeywordsByText(bug.generateText());
        return deal(projectId, bugKeywords);
    }

    @RequestMapping(value = "/steps/{stepId}/recommend", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class, ProjectChecking.class })
    @ResponseBody
    public UserDTO getStepRecommendAssignee(@PathVariable("companyId") int companyId,
            @PathVariable("projectId") int projectId, @PathVariable("stepId") int stepId) {
        Step step = stepService.getById(stepId);
        List<String> stepKeywords = keywordService.getKeywordsByText(step.generateText());
        return deal(projectId, stepKeywords);
    }

    private UserDTO deal(int projectId, List<String> keywords) {
        List<User> users = userService.getUserByProjectId(projectId);
        List<List<Keyword>> userKeywordsList = new ArrayList<List<Keyword>>();
        for (User user : users) {
            List<Keyword> userKeywords = keywordService.getKeywordsByUser(user.getId(), 0, -1);
            userKeywordsList.add(userKeywords);
        }

        long total = 0;
        for (int i = 0; i < userKeywordsList.size(); i++) {
            long tot = 0;
            for (int j = 0; j < userKeywordsList.get(i).size(); j++) {
                tot += userKeywordsList.get(i).get(j).getTimes();
            }
            total += tot;
        }
        long maxPos = 0, maxValue = 0, totTimes = 0;
        float[] userValue = new float[users.size()];
        for (int i = 0; i < keywords.size(); i++) {
            maxPos = 0;
            maxValue = 0;
            totTimes = 0;
            for (int j = 0; j < userKeywordsList.size(); j++) {
                long value = 0;
                for (int k = 0; k < userKeywordsList.get(j).size(); k++) {
                    if (keywords.get(i).equals(userKeywordsList.get(j).get(k).getKeyword())) {
                        totTimes += userKeywordsList.get(j).get(k).getTimes();
                        value = userKeywordsList.get(j).get(k).getTimes();
                        if (value > maxValue || maxPos == 0) {
                            maxValue = value;
                            maxPos = j;
                        }
                        break;
                    }
                }
            }
            if (maxValue > 0) {
                userValue[(int) maxPos] += maxValue / total / (maxValue / totTimes);
            }
        }
        maxPos = 0;
        float max = 0;
        for (int i = 0; i < userValue.length; ++i) {
            if (userValue[i] > max || maxPos == 0) {
                max = userValue[i];
                maxPos = i;
            }
        }
        return UserTransform.userToUserDTO(users.get((int) maxPos));
    }
}
