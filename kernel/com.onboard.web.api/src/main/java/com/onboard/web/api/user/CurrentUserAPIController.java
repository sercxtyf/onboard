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

import java.util.Date;
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
import com.onboard.domain.model.Bug;
import com.onboard.domain.model.Step;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.User;
import com.onboard.domain.transform.BugTransForm;
import com.onboard.domain.transform.StepTransform;
import com.onboard.domain.transform.TodoTransform;
import com.onboard.domain.transform.UserTransform;
import com.onboard.dto.BugDTO;
import com.onboard.dto.StepDTO;
import com.onboard.dto.TodoDTO;
import com.onboard.dto.UserDTO;
import com.onboard.service.collaboration.BugService;
import com.onboard.service.collaboration.StepService;
import com.onboard.service.collaboration.TodoService;
import com.onboard.service.security.interceptors.LoginRequired;
import com.onboard.service.web.SessionService;

@Controller
public class CurrentUserAPIController {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private BugService bugService;

    @Autowired
    private StepService stepService;

    private static Logger logger = LoggerFactory.getLogger(CurrentUserAPIController.class);

    @RequestMapping(value = "/currentUser", method = RequestMethod.GET)
    @Interceptors({ LoginRequired.class })
    @ResponseBody
    public UserDTO getCurrentUser() {

        return UserTransform.userToUserDTO(sessionService.getCurrentUser());
    }

    @RequestMapping(value = "/currentUser/openTodos", method = RequestMethod.GET)
    @Interceptors({ LoginRequired.class })
    @ResponseBody
    public List<TodoDTO> getCurrentUserOpenTodos(@PathVariable int companyId,
            @RequestParam(value = "start") long start, @RequestParam(value = "end") long end) {
        Date since = new Date(start);
        Date until = new Date(end);
        User user = sessionService.getCurrentUser();
        List<Todo> todos = todoService.getOpenTodosBetweenDatesByUser(companyId, user.getId(), since, until);
        logger.info("There are {} todos found", todos.size());
        return Lists.transform(todos, TodoTransform.TODO_DTO_FUNCTION);
    }

    @RequestMapping(value = "/currentUser/openBugs", method = RequestMethod.GET)
    @Interceptors({ LoginRequired.class })
    @ResponseBody
    public List<BugDTO> getCurrentUserOpenBugs(@PathVariable int companyId, @RequestParam(value = "start") long start,
            @RequestParam(value = "end") long end) {
        Date since = new Date(start);
        Date until = new Date(end);
        User user = sessionService.getCurrentUser();
        List<Bug> bugs = bugService.getOpenBugsBetweenDatesByUser(companyId, user.getId(), since, until);
        logger.info("There are {} bugs found", bugs.size());
        return Lists.transform(bugs, BugTransForm.BUG_TO_BUGDTO_FUNCTION);
    }

    @RequestMapping(value = "/currentUser/openSteps", method = RequestMethod.GET)
    @Interceptors({ LoginRequired.class })
    @ResponseBody
    public List<StepDTO> getCurrentUserOpenSteps(@PathVariable int companyId,
            @RequestParam(value = "start") long start, @RequestParam(value = "end") long end) {
        Date since = new Date(start);
        Date until = new Date(end);
        User user = sessionService.getCurrentUser();
        List<Step> steps = stepService.getOpenStepsBetweenDatesByUser(companyId, user.getId(), since, until);
        logger.info("There are {} steps found", steps.size());
        return Lists.transform(steps, StepTransform.STEP_DTO_FUNCTION);
    }

    @RequestMapping(value = "/currentUser/isManager", method = RequestMethod.GET)
    @ResponseBody
    public Boolean judgeCurrnetUserOnboardManagerById() {
        User user = sessionService.getCurrentUser();
        if (user == null) {
            return false;
        }
        return user.getIsManager();
    }

}
