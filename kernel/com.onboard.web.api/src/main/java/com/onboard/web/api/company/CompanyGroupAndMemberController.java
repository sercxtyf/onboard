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
package com.onboard.web.api.company;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elevenframework.web.interceptor.Interceptors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.onboard.domain.model.Department;
import com.onboard.domain.model.Invitation;
import com.onboard.domain.model.User;
import com.onboard.domain.transform.DepartmentTransform;
import com.onboard.domain.transform.UserTransform;
import com.onboard.dto.DepartmentDTO;
import com.onboard.dto.UserDTO;
import com.onboard.service.account.AccountService;
import com.onboard.service.account.UserService;
import com.onboard.service.activity.ActivityService;
import com.onboard.service.security.interceptors.CompanyMemberRequired;

@Controller
@RequestMapping("/{companyId}/users")
public class CompanyGroupAndMemberController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class })
    @ResponseBody
    public Map<String, ?> getProjectsAndUsers(@PathVariable("companyId") int companyId) {
        List<User> users = userService.getUnDepartmentedUsersByCompanyId(companyId);
        List<Invitation> invitations = accountService.getAllInvitations(companyId);
        Map<Department, List<User>> userGroups = userService.getDepartmentedUserByCompanyId(companyId);
        List<DepartmentDTO> departments = Lists.newArrayList(Iterables.transform(userGroups.entrySet(),
                new Function<Map.Entry<Department, List<User>>, DepartmentDTO>() {
                    @Override
                    public DepartmentDTO apply(Map.Entry<Department, List<User>> input) {
                        return DepartmentTransform.departmentAndUsersToDepartmentDTO(input.getKey(), input.getValue());
                    }
                }));
        return ImmutableMap.of("unGroupUsers", users, "invitations", invitations, "groups", departments);
    }

    @RequestMapping(value = "/orderedByActivitiesCountInLastWeek", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class })
    @ResponseBody
    public List<UserDTO> getUsersActivitiesCountInLastWeek(@PathVariable("companyId") final int companyId) {
        List<User> users = userService.getUserByCompanyId(companyId);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.WEEK_OF_MONTH, -1);

        final Map<Integer, Integer> activitiesCount = new HashMap<Integer, Integer>();
        for (User user : users) {
            activitiesCount.put(user.getId(),
                    activityService.getLatestByUserSince(companyId, user.getId(), null, calendar.getTime()).size());
        }

        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return activitiesCount.get(u2.getId()) - activitiesCount.get(u1.getId());
            }
        });

        return Lists.transform(users, UserTransform.USER_TO_USERDTO_FUNCTION);
    }
}
