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
package com.onboard.web.api.bug;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.elevenframework.web.interceptor.Interceptors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.onboard.domain.model.Bug;
import com.onboard.domain.model.User;
import com.onboard.domain.transform.BugTransForm;
import com.onboard.dto.BugDTO;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.BugService;
import com.onboard.service.collaboration.KeywordService;
import com.onboard.service.security.interceptors.ProjectMemberRequired;
import com.onboard.service.web.SessionService;
import com.onboard.web.api.exception.ResourceNotFoundException;
import com.onboard.web.api.iteration.IterationApiController;

@RequestMapping(value = "/{companyId}/projects/{projectId}/bugs")
@Controller
public class BugApiController {

    private static int BUG_PER_PAGE = 50;

    @Autowired
    private BugService bugService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private KeywordService keywordService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public Map<String, ?> getBugs(@PathVariable int projectId,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page) {

        int start = page * BUG_PER_PAGE;
        List<Bug> bugs = bugService.getOpenedBugsByProject(projectId, start, BUG_PER_PAGE);

        // check whether has next page
        int nextPage = (page + 1) * BUG_PER_PAGE;
        List<Bug> nextBugs = bugService.getOpenedBugsByProject(projectId, nextPage, BUG_PER_PAGE);
        boolean hasNext = nextBugs.size() > 0 ? true : false;

        for (Bug bug : bugs) {
            User user = userService.getById(bug.getAssigneeId());
            bug.setAssignee(user);
        }
        return ImmutableMap.of("bugs", Lists.transform(bugs, BugTransForm.BUG_TO_BUGDTO_FUNCTION), "hasNext", hasNext,
                "nextPage", page + 1);
    }

    @RequestMapping(value = "/all-bugs", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public List<BugStat> getAllBugs(@PathVariable int projectId) {
        List<Bug> bugs = bugService.getAllBugsByProject(projectId);
        List<BugStat> result = Lists.newArrayList();
        for (Bug bug : bugs) {
            result.add(new BugStat(bug));
        }
        return result;
    }

    @RequestMapping(value = "/open-bugs", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public Map<String, ?> getOpenBugs(@PathVariable int projectId) {
        List<Bug> bugs = bugService.getOpenedBugsByProject(projectId, 0, -1);
        for (Bug bug : bugs) {
            User user = userService.getById(bug.getAssigneeId());
            bug.setAssignee(user);
        }
        return ImmutableMap.of("bugs", Lists.transform(bugs, BugTransForm.BUG_TO_BUGDTO_FUNCTION));
    }

    @RequestMapping(value = "/closed-bugs", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public List<BugStat> getClosedBugs(@PathVariable int projectId) {
        List<Bug> bugs = bugService.getFinishedBugsByProject(projectId, 0, -1);
        List<BugStat> result = Lists.newArrayList();
        for (Bug bug : bugs) {
            result.add(new BugStat(bug));
        }
        return result;
    }

    @RequestMapping(value = "/finished", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public Map<String, ?> getBugsFinished(@PathVariable int projectId,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page) {

        int start = page * BUG_PER_PAGE;
        List<Bug> bugs = bugService.getFinishedBugsByProject(projectId, start, BUG_PER_PAGE);

        // check whether has next page
        int nextPage = (page + 1) * BUG_PER_PAGE;
        List<Bug> nextBugs = bugService.getFinishedBugsByProject(projectId, nextPage, BUG_PER_PAGE);
        boolean hasNext = nextBugs.size() > 0 ? true : false;

        for (Bug bug : bugs) {
            User user = userService.getById(bug.getAssigneeId());
            bug.setAssignee(user);
        }
        return ImmutableMap.of("bugs", Lists.transform(bugs, BugTransForm.BUG_TO_BUGDTO_FUNCTION), "hasNext", hasNext,
                "nextPage", page + 1);
    }

    @RequestMapping(value = "/{bugId}", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public BugDTO getBugById(@PathVariable int bugId) {
        BugDTO bugDTO = BugTransForm.bugToBugDTO(bugService.getBugByIdWithCommentAndSubscriable(bugId));
        return bugDTO;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @Interceptors({ ProjectMemberRequired.class })
    @Caching(evict = { @CacheEvict(value = IterationApiController.ITERATION_CACHE_NAME, key = "#projectId + '*'"),
            @CacheEvict(value = IterationApiController.ITERATIONS_CACHE_NAME, key = "#projectId + '*'") })
    @ResponseBody
    public BugDTO createBug(@PathVariable int companyId, @PathVariable int projectId, @RequestBody BugDTO bugDTO) {
        Bug bug = BugTransForm.bugDTOToBug(bugDTO);
        bug.setCompanyId(companyId);
        bug.setProjectId(projectId);
        bug.setCreatorId(sessionService.getCurrentUser().getId());
        bug.setCreatorName(sessionService.getCurrentUser().getName());
        bug.setDeleted(false);
        bug.setCreatedTime(new Date());
        bug.setDueTime(bugDTO.getDueTime());

        bugService.create(bug);

        return BugTransForm.bugToBugDTO(bug);
    }

    @RequestMapping(value = "/{bugId}", method = RequestMethod.PUT)
    @Interceptors({ ProjectMemberRequired.class })
    @Caching(evict = { @CacheEvict(value = IterationApiController.ITERATION_CACHE_NAME, key = "#projectId + '*'"),
            @CacheEvict(value = IterationApiController.ITERATIONS_CACHE_NAME, key = "#projectId + '*'") })
    @ResponseBody
    public BugDTO updateBug(@PathVariable int bugId, @RequestBody BugDTO bugDTO, @PathVariable int projectId) {
        bugDTO.setId(bugId);
        Bug bug = BugTransForm.bugDTOToBug(bugDTO);
        return BugTransForm.bugToBugDTO(bugService.updateSelective(bug));
    }

    @RequestMapping(value = "/{bugId}", method = RequestMethod.DELETE)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    @Caching(evict = { @CacheEvict(value = IterationApiController.ITERATION_CACHE_NAME, key = "#projectId + '*'"),
            @CacheEvict(value = IterationApiController.ITERATIONS_CACHE_NAME, key = "#projectId + '*'") })
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteBug(@PathVariable int bugId, @PathVariable int projectId) {
        bugService.delete(bugId);
    }

    /**
     * @author Chenlong
     * @param projectId
     * @return
     */
    @RequestMapping(value = "/aveDuration", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public Long getBugAveDurationByProject(@PathVariable int projectId) {
        return bugService.getCompletedBugAveDurationByProjectIdDateBackByMonth(projectId, 3);
    }

    /**
     * @author Chenlong
     * @param projectId
     * @return
     */
    @RequestMapping(value = "/thirdQuarterDuration", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public Long getBugThirdQuarterDurationByProject(@PathVariable int projectId) {
        return bugService.getCompletedBugThirdQuarterDurationByProjectIdDateBackByMonth(projectId, 3);
    }

    @RequestMapping(value = "/{bugId}/keywords", method = RequestMethod.GET)
    @ResponseBody
    @Interceptors({ ProjectMemberRequired.class })
    public List<String> getBugKeywords(@PathVariable("bugId") int bugId) {
        Bug bug = bugService.getById(bugId);
        if (bug == null) {
            throw new ResourceNotFoundException();
        }
        return keywordService.getKeywordsByText(bug.generateText());
    }

    class BugStat {
        private String creatorName;
        private String assigneeName;
        private Date createdTime;
        private Date completedTime;
        private Integer assigneeId;

        public BugStat(Bug bug) {
            this.completedTime = bug.getCompletedTime();
            this.creatorName = bug.getCreatorName();
            this.createdTime = bug.getCreatedTime();
            this.assigneeId = bug.getAssigneeId();
            User assignee = userService.getById(this.assigneeId);
            if (assignee != null)
                this.assigneeName = assignee.getName();
        }

        public String getCreatorName() {
            return creatorName;
        }

        public void setCreatorName(String creatorName) {
            this.creatorName = creatorName;
        }

        public String getAssigneeName() {
            return assigneeName;
        }

        public void setAssigneeName(String assigneeName) {
            this.assigneeName = assigneeName;
        }

        public Date getCreatedTime() {
            return createdTime;
        }

        public void setCreatedTime(Date createdTime) {
            this.createdTime = createdTime;
        }

        public Date getCompletedTime() {
            return completedTime;
        }

        public void setCompletedTime(Date completedTime) {
            this.completedTime = completedTime;
        }

        public Integer getAssigneeId() {
            return assigneeId;
        }

        public void setAssigneeId(Integer assigneeId) {
            this.assigneeId = assigneeId;
        }
    }
}
