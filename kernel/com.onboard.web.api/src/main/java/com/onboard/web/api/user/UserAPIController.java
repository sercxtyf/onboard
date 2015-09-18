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
package com.onboard.web.api.user;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.elevenframework.web.interceptor.Interceptors;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Attachment;
import com.onboard.domain.model.Bug;
import com.onboard.domain.model.Company;
import com.onboard.domain.model.Keyword;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.Step;
import com.onboard.domain.model.Todolist;
import com.onboard.domain.model.User;
import com.onboard.domain.transform.ActivityTransForm;
import com.onboard.domain.transform.AttachmentTransform;
import com.onboard.domain.transform.BugTransForm;
import com.onboard.domain.transform.StepTransform;
import com.onboard.domain.transform.TodolistTransform;
import com.onboard.domain.transform.UserTransform;
import com.onboard.dto.ActivityDTO;
import com.onboard.dto.AttachmentDTO;
import com.onboard.dto.BugDTO;
import com.onboard.dto.StepDTO;
import com.onboard.dto.TodolistDTO;
import com.onboard.dto.UserDTO;
import com.onboard.service.account.CompanyService;
import com.onboard.service.account.UserService;
import com.onboard.service.activity.ActivityService;
import com.onboard.service.collaboration.AttachmentService;
import com.onboard.service.collaboration.BugService;
import com.onboard.service.collaboration.KeywordService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.collaboration.StepService;
import com.onboard.service.collaboration.TodoService;
import com.onboard.service.security.interceptors.CompanyMemberRequired;
import com.onboard.service.security.interceptors.UserChecking;
import com.onboard.service.web.SessionService;

@RequestMapping(value = "/")
@Controller
public class UserAPIController {
    public static final Logger logger = LoggerFactory.getLogger(UserAPIController.class);
    private static final int ACTIVITY_PER_PAGE = 50;
    private static final int PER_PAGE = 30;
    private static DateTimeFormatter dtf = org.joda.time.format.DateTimeFormat.forPattern("yyyy-MM-dd");
    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private KeywordService keywordService;

    @Autowired
    private BugService bugService;

    @Autowired
    private StepService stepService;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ResponseBody
    public UserDTO getUserByEmailOrUsername(
            @RequestParam(value = "email-or-username", required = true) String emailOrUsername) {
        User user = userService.getUserByEmailOrUsername(emailOrUsername);
        if (null == user) {
            return null;
        }
        return UserTransform.userToUserDTO(user);
    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public UserDTO getUserById(@PathVariable("userId") int userId) {

        return UserTransform.userToUserDTO(userService.getById(userId));
    }

    @RequestMapping(value = "/users/{userId}/keywords", method = RequestMethod.GET)
    @ResponseBody
    public List<Keyword> getUserKeywordsById(@PathVariable("userId") int userId, @RequestParam(required = false,
            defaultValue = "100") int limit) {
        return keywordService.getKeywordsByUser(userId, 0, limit);
    }

    @RequestMapping(value = "/{companyId}/user/{userId}/keywords", method = RequestMethod.GET)
    @ResponseBody
    public List<Keyword> getCompanyUserKeywordsById(@PathVariable("companyId") int companyId,
            @PathVariable("userId") int userId, @RequestParam(required = false, defaultValue = "100") int limit) {
        return keywordService.getKeywordsByUserByCompany(companyId, userId, 0, limit);
    }

    private List<Integer> getProjectListOfCurrentUser(int companyId) {
        return projectService
                .getProjectIdListByUserByCompany(sessionService.getCurrentUser().getId(), companyId, 0, -1);
    }

    private TreeMap<String, List<ActivityDTO>> makeMapSerilizable(TreeMap<Date, List<Activity>> map) {
        TreeMap<String, List<ActivityDTO>> mapDTO = new TreeMap<String, List<ActivityDTO>>();

        Set<Date> dates = map.keySet();
        for (Date date : dates) {
            List<Activity> activities = map.get(date);
            mapDTO.put(new SimpleDateFormat("yyyy-MM-dd").format(date),
                    Lists.transform(activities, ActivityTransForm.ACTIVITY_TO_ACTIVITYDTO_FUNCTION));
        }
        return mapDTO;
    }

    @RequestMapping(value = "/{companyId}/user/{userId}", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class })
    @ResponseBody
    public UserDTO getCompanyUserUserById(@PathVariable("userId") int userId) {

        return UserTransform.userToUserDTO(userService.getById(userId));
    }

    @RequestMapping(value = "/{companyId}/user/{userId}/activities", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class, UserChecking.class })
    @ResponseBody
    public Map<String, Object> viewUserActivities(@PathVariable int companyId, @PathVariable int userId,
            @RequestParam(value = "until", required = false) @DateTimeFormat(iso = ISO.DATE) Date until, Model model) {

        Builder<String, Object> builder = ImmutableMap.builder();

        DateTime dt = until == null ? new DateTime() : new DateTime(until);
        until = dt.withTime(0, 0, 0, 0).plusDays(1).plusMillis(-1).toDate();
        List<Integer> projectList = getProjectListOfCurrentUser(companyId);
        TreeMap<Date, List<Activity>> map = activityService.getByUserGroupByDate(companyId, userId, ACTIVITY_PER_PAGE,
                projectList, until);

        TreeMap<String, List<ActivityDTO>> mapDTO = makeMapSerilizable(map);
        builder.put("activities", mapDTO);
        boolean hasNext = false;

        if (map != null && map.size() > 0) {
            Date newUntil = new DateTime(map.lastKey()).plusDays(-1).withTime(0, 0, 0, 0).toDate();
            TreeMap<Date, List<Activity>> nextMap = activityService.getByUserGroupByDate(companyId, userId,
                    ACTIVITY_PER_PAGE, projectList, newUntil);
            hasNext = nextMap != null;
            builder.put("nextPage", dtf.print(new DateTime(newUntil)));
        }
        builder.put("hasNext", hasNext);

        return builder.build();
    }

    private TreeMap<String, List<AttachmentDTO>> makeUserAttachmentsMapSerilizable(TreeMap<Date, List<Attachment>> map) {
        TreeMap<String, List<AttachmentDTO>> mapDTO = new TreeMap<String, List<AttachmentDTO>>();

        Set<Date> dates = map.keySet();
        for (Date date : dates) {
            List<Attachment> attachments = map.get(date);
            mapDTO.put(new SimpleDateFormat("yyyy-MM-dd").format(date),
                    Lists.transform(attachments, AttachmentTransform.ATTACHMENT_TO_ATTACHMENTDTO_FUNCTION));
        }
        return mapDTO;
    }

    /**
     * 查看用户所有附件
     * 
     * @param companyId
     * @param userId
     * @param until
     * @param model
     * @return
     */
    @RequestMapping(value = "/{companyId}/user/{userId}/attachments", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class, UserChecking.class })
    @ResponseBody
    public ImmutableMap<String, ?> viewUserAttachments(@PathVariable int companyId, @PathVariable int userId,
            @RequestParam(value = "until", required = false) @DateTimeFormat(iso = ISO.DATE) Date until,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page) {

        Builder<String, Object> builder = ImmutableMap.builder();

        DateTime dt = until == null ? new DateTime() : new DateTime(until);
        until = dt.withTime(0, 0, 0, 0).plusDays(1).plusMillis(-1).toDate();
        List<Integer> projectList = getProjectListOfCurrentUser(companyId);

        TreeMap<Date, List<Attachment>> map = attachmentService.getAttachmentsByUserGroupByDate(companyId, userId,
                projectList, until, PER_PAGE);
        TreeMap<String, List<AttachmentDTO>> mapDTO = makeUserAttachmentsMapSerilizable(map);
        builder.put("userAttachments", mapDTO);

        // UserDTO userDto = new UserDTO(userService.getUserById(userId));
        boolean hasNext = false;
        if (map != null && map.size() > 0) {
            Date newUntil = new DateTime(map.lastKey()).withTime(0, 0, 0, 0).plusMillis(-1).toDate();
            TreeMap<Date, List<Attachment>> nextMap = attachmentService.getAttachmentsByUserGroupByDate(companyId,
                    userId, projectList, newUntil, PER_PAGE);
            hasNext = nextMap.size() > 0;

            builder.put("nextPage", dtf.print(new DateTime(newUntil)));
        }
        builder.put("hasNext", hasNext);

        return builder.build();
    }

    private TreeMap<String, List<TodolistDTO>> makeUserCompletedTodosMapSerilizable(TreeMap<Date, List<Todolist>> map) {
        TreeMap<String, List<TodolistDTO>> mapDTO = new TreeMap<String, List<TodolistDTO>>();
        Set<Date> dates = map.keySet();
        for (Date date : dates) {
            List<Todolist> completedTodos = map.get(date);
            mapDTO.put(new SimpleDateFormat("yyyy-MM-dd").format(date),
                    Lists.transform(completedTodos, TodolistTransform.TODOLIST_DTO_TODOS_FUNCTION));
        }
        return mapDTO;
    }

    /**
     * 看一个人完成的所有todo
     * 
     * @param companyId
     * @param userId
     * @param model
     * @return
     */
    @RequestMapping(value = "/{companyId}/user/{userId}/completed_todos", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class, UserChecking.class })
    @ResponseBody
    public Map<String, Object> viewUserCompletedTodos(@PathVariable int companyId, @PathVariable int userId,
            @RequestParam(value = "until", required = false) @DateTimeFormat(iso = ISO.DATE) Date until, Model model) {
        Builder<String, Object> builder = ImmutableMap.builder();
        DateTime dt = until == null ? new DateTime() : new DateTime(until);
        until = dt.withTime(0, 0, 0, 0).plusDays(1).plusMillis(-1).toDate();
        List<Integer> projectList = getProjectListOfCurrentUser(companyId);

        TreeMap<Date, List<Todolist>> map = todoService.getCompletedTodolistsGroupByDateByUser(companyId, userId,
                projectList, until, PER_PAGE);
        TreeMap<String, List<TodolistDTO>> mapDTO = makeUserCompletedTodosMapSerilizable(map);
        builder.put("completedTodos", mapDTO);
        Map<Integer, String> projectIdToName = getProjectIdAndNameByCompanyId(companyId);
        builder.put("projectsName", projectIdToName);

        boolean hasNext = false;

        if (map != null && map.size() > 0) {
            Date newUntil = new DateTime(map.lastKey()).withTime(0, 0, 0, 0).plusMillis(-1).toDate();

            TreeMap<Date, List<Todolist>> nextMap = todoService.getCompletedTodolistsGroupByDateByUser(companyId,
                    userId, projectList, newUntil, PER_PAGE);
            hasNext = nextMap.size() > 0;
            builder.put("nextPage", dtf.print(new DateTime(newUntil)));
        }

        builder.put("hasNext", hasNext);

        return builder.build();
    }

    /**
     * 获取project的名字
     * 
     * @param companyId
     * @return
     */
    private Map<Integer, String> getProjectIdAndNameByCompanyId(int companyId) {
        Map<Integer, String> projectIdToName = new HashMap<Integer, String>();
        List<Project> projects = projectService.getProjectsByCompany(companyId, 0, -1);
        for (Project project : projects) {
            projectIdToName.put(project.getId(), project.getName());
        }
        return projectIdToName;
    }

    private Map<Integer, List<UserDTO>> makeUserUncompletedTodosMapSerilizable(Map<Integer, List<User>> map) {
        Map<Integer, List<UserDTO>> mapDTO = new HashMap<Integer, List<UserDTO>>();

        Set<Integer> projectIds = map.keySet();
        for (Integer projectId : projectIds) {
            List<User> users = map.get(projectId);
            mapDTO.put(projectId, Lists.transform(users, UserTransform.USER_TO_USERDTO_FUNCTION));
        }
        return mapDTO;
    }

    /**
     * 获取未完成的todos
     * 
     * @param companyId
     * @param userId
     * @param model
     * @return
     */
    @RequestMapping(value = "/{companyId}/user/{userId}/open_todos", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class, UserChecking.class })
    @ResponseBody
    public Map<String, Object> viewUserOpenTodos(@PathVariable int companyId, @PathVariable int userId, Model model) {
        Builder<String, Object> builder = ImmutableMap.builder();
        Map<Integer, String> projectIdToName = getProjectIdAndNameByCompanyId(companyId);
        builder.put("projectsName", projectIdToName);
        List<Integer> projectList = getProjectListOfCurrentUser(companyId);
        List<Todolist> todolists = todoService.getOpenTodosByUser(userId, projectList);
        List<TodolistDTO> todolistDto = Lists.transform(todolists, TodolistTransform.TODOLIST_DTO_ALL_FUNCTION);
        builder.put("uncompletedTodos", todolistDto);

        Map<Integer, List<User>> users = userService.getAllProjectUsersInCompany(companyId);
        Map<Integer, List<UserDTO>> userDtos = makeUserUncompletedTodosMapSerilizable(users);
        builder.put("userDtos", userDtos);
        UserDTO userDto = UserTransform.userToUserDTO(userService.getById(userId));
        builder.put("user", userDto);

        return builder.build();
    }

    @RequestMapping(value = "/users/{userId}/companies", method = RequestMethod.GET)
    @ResponseBody
    public List<Company> getUserCompanies(@PathVariable int userId) {
        return companyService.getCompaniesByUserId(userId);
    }

    private TreeMap<String, Map<Integer, List<BugDTO>>> makeUserCompletedBugsMapSerilizable(
            TreeMap<Date, Map<Integer, List<Bug>>> map) {
        TreeMap<String, Map<Integer, List<BugDTO>>> mapDTO = new TreeMap<String, Map<Integer, List<BugDTO>>>();
        Set<Date> dates = map.keySet();

        for (Date date : dates) {
            Map<Integer, List<BugDTO>> bugGroupByProjectIdMap = new TreeMap<Integer, List<BugDTO>>();
            Map<Integer, List<Bug>> bugs = map.get(date);
            Set<Integer> projectIdKeys = bugs.keySet();
            for (Integer projectId : projectIdKeys) {
                bugGroupByProjectIdMap.put(projectId,
                        Lists.transform(bugs.get(projectId), BugTransForm.BUG_TO_BUGDTO_FUNCTION));
            }
            mapDTO.put(new SimpleDateFormat("yyyy-MM-dd").format(date), bugGroupByProjectIdMap);
        }
        return mapDTO;
    }

    /**
     * 获取完成的bugs
     * 
     * @param companyId
     * @param userId
     * @param until
     * @return
     */
    @RequestMapping(value = "/{companyId}/user/{userId}/completedBugs", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class, UserChecking.class })
    @ResponseBody
    public Map<String, Object> viewUserCompletedBugs(@PathVariable int companyId, @PathVariable int userId,
            @RequestParam(value = "until", required = false) @DateTimeFormat(iso = ISO.DATE) Date until) {
        Builder<String, Object> builder = ImmutableMap.builder();
        DateTime dt = until == null ? new DateTime() : new DateTime(until);
        until = dt.withTime(0, 0, 0, 0).plusDays(1).plusMillis(-1).toDate();
        List<Integer> projectList = getProjectListOfCurrentUser(companyId);

        TreeMap<Date, Map<Integer, List<Bug>>> map = bugService.getCompletedBugsGroupByDateByUser(companyId, userId,
                projectList, until, PER_PAGE);
        TreeMap<String, Map<Integer, List<BugDTO>>> mapDTO = makeUserCompletedBugsMapSerilizable(map);
        builder.put("completedBugs", mapDTO);
        Map<Integer, String> projectIdToName = getProjectIdAndNameByCompanyId(companyId);
        builder.put("projectsName", projectIdToName);

        boolean hasNext = false;
        if (map != null && map.size() > 0) {
            Date newUntil = new DateTime(map.firstKey()).withTime(0, 0, 0, 0).plusMillis(-1).toDate();
            TreeMap<Date, Map<Integer, List<Bug>>> nextMap = bugService.getCompletedBugsGroupByDateByUser(companyId,
                    userId, projectList, newUntil, PER_PAGE);
            hasNext = nextMap.size() > 0;

            builder.put("nextPage", dtf.print(new DateTime(newUntil)));
        }
        builder.put("hasNext", hasNext);

        return builder.build();
    }

    private TreeMap<Integer, List<BugDTO>> makeUserUncompletedBugsMapSerilizable(TreeMap<Integer, List<Bug>> map) {
        TreeMap<Integer, List<BugDTO>> mapDTO = new TreeMap<Integer, List<BugDTO>>();
        Set<Integer> keys = map.keySet();
        for (Integer i : keys) {
            List<Bug> bugs = map.get(i);
            mapDTO.put(i, Lists.transform(bugs, BugTransForm.BUG_TO_BUGDTO_FUNCTION));
        }
        return mapDTO;
    }

    /**
     * 获取未完成的bugs
     * 
     * @param companyId
     * @param userId
     * @return
     */
    @RequestMapping(value = "/{companyId}/user/{userId}/uncompletedBugs", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class, UserChecking.class })
    @ResponseBody
    public Map<String, Object> viewUserOpenBugs(@PathVariable int companyId, @PathVariable int userId) {
        Builder<String, Object> builder = ImmutableMap.builder();
        Map<Integer, String> projectIdToName = getProjectIdAndNameByCompanyId(companyId);
        builder.put("projectsName", projectIdToName);
        List<Integer> projectList = getProjectListOfCurrentUser(companyId);
        TreeMap<Integer, List<Bug>> bugs = bugService.getOpenBugsByUser(userId, projectList);
        TreeMap<Integer, List<BugDTO>> bugsDto = makeUserUncompletedBugsMapSerilizable(bugs);
        builder.put("uncompletedBugs", bugsDto);

        Map<Integer, List<User>> users = userService.getAllProjectUsersInCompany(companyId);
        Map<Integer, List<UserDTO>> userDtos = makeUserUncompletedTodosMapSerilizable(users);
        builder.put("userDtos", userDtos);
        UserDTO userDto = UserTransform.userToUserDTO(userService.getById(userId));
        builder.put("user", userDto);

        return builder.build();
    }

    private TreeMap<String, Map<Integer, List<StepDTO>>> makeUserCompletedStepsMapSerilizable(
            TreeMap<Date, Map<Integer, List<Step>>> map) {
        TreeMap<String, Map<Integer, List<StepDTO>>> mapDTO = new TreeMap<String, Map<Integer, List<StepDTO>>>();
        Set<Date> dates = map.keySet();

        for (Date date : dates) {
            Map<Integer, List<StepDTO>> stepGroupByProjectIdMap = new TreeMap<Integer, List<StepDTO>>();
            Map<Integer, List<Step>> steps = map.get(date);
            Set<Integer> projectIdKeys = steps.keySet();
            for (Integer projectId : projectIdKeys) {
                stepGroupByProjectIdMap.put(projectId,
                        Lists.transform(steps.get(projectId), StepTransform.STEP_DTO_FUNCTION));
            }
            mapDTO.put(new SimpleDateFormat("yyyy-MM-dd").format(date), stepGroupByProjectIdMap);
        }
        return mapDTO;
    }

    /**
     * 获取完成的steps
     * 
     * @param companyId
     * @param userId
     * @param until
     * @return
     */
    @RequestMapping(value = "/{companyId}/user/{userId}/completedSteps", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class, UserChecking.class })
    @ResponseBody
    public Map<String, Object> viewUserCompletedSteps(@PathVariable int companyId, @PathVariable int userId,
            @RequestParam(value = "until", required = false) @DateTimeFormat(iso = ISO.DATE) Date until) {
        Builder<String, Object> builder = ImmutableMap.builder();
        DateTime dt = until == null ? new DateTime() : new DateTime(until);
        until = dt.withTime(0, 0, 0, 0).plusDays(1).plusMillis(-1).toDate();
        List<Integer> projectList = getProjectListOfCurrentUser(companyId);

        TreeMap<Date, Map<Integer, List<Step>>> map = stepService.getCompletedStepsGroupByDateByUser(companyId, userId,
                projectList, until, PER_PAGE);
        TreeMap<String, Map<Integer, List<StepDTO>>> mapDTO = makeUserCompletedStepsMapSerilizable(map);
        builder.put("completedSteps", mapDTO);
        Map<Integer, String> projectIdToName = getProjectIdAndNameByCompanyId(companyId);
        builder.put("projectsName", projectIdToName);

        boolean hasNext = false;
        if (map != null && map.size() > 0) {
            Date newUntil = new DateTime(map.firstKey()).withTime(0, 0, 0, 0).plusMillis(-1).toDate();
            TreeMap<Date, Map<Integer, List<Bug>>> nextMap = bugService.getCompletedBugsGroupByDateByUser(companyId,
                    userId, projectList, newUntil, PER_PAGE);
            hasNext = nextMap.size() > 0;

            builder.put("nextPage", dtf.print(new DateTime(newUntil)));
        }
        builder.put("hasNext", hasNext);

        return builder.build();
    }

    private TreeMap<Integer, List<StepDTO>> makeUserUncompletedStepsMapSerilizable(Map<Integer, List<Step>> map) {
        TreeMap<Integer, List<StepDTO>> mapDTO = new TreeMap<Integer, List<StepDTO>>();
        Set<Integer> keys = map.keySet();
        for (Integer i : keys) {
            List<Step> steps = map.get(i);
            mapDTO.put(i, Lists.transform(steps, StepTransform.STEP_DTO_FUNCTION));
        }
        return mapDTO;
    }

    /**
     * 获取未完成的steps
     * 
     * @param companyId
     * @param userId
     * @return
     */
    @RequestMapping(value = "/{companyId}/user/{userId}/uncompletedSteps", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class, UserChecking.class })
    @ResponseBody
    public Map<String, Object> viewUserOpenSteps(@PathVariable int companyId, @PathVariable int userId) {
        Builder<String, Object> builder = ImmutableMap.builder();
        Map<Integer, String> projectIdToName = getProjectIdAndNameByCompanyId(companyId);
        builder.put("projectsName", projectIdToName);
        List<Integer> projectList = getProjectListOfCurrentUser(companyId);
        Map<Integer, List<Step>> steps = stepService.getOpenStepsByUser(userId, projectList);
        Map<Integer, List<StepDTO>> stepsDto = makeUserUncompletedStepsMapSerilizable(steps);
        builder.put("uncompletedSteps", stepsDto);

        Map<Integer, List<User>> users = userService.getAllProjectUsersInCompany(companyId);
        Map<Integer, List<UserDTO>> userDtos = makeUserUncompletedTodosMapSerilizable(users);
        builder.put("userDtos", userDtos);
        UserDTO userDto = UserTransform.userToUserDTO(userService.getById(userId));
        builder.put("user", userDto);

        return builder.build();
    }
}
