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
package com.onboard.test.moduleutils;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Bug;
import com.onboard.domain.model.Comment;
import com.onboard.domain.model.Company;
import com.onboard.domain.model.Department;
import com.onboard.domain.model.IterationItemStatus;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.Step;
import com.onboard.domain.model.Story;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.User;
import com.onboard.domain.model.UserCompany;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.service.activity.ActivityHook;
import com.onboard.service.activity.ActivityService;
import com.onboard.service.activity.SynchronizedActivityHook;

public class ModuleHelper {

    public static int id = 1000;
    public static List<Integer> ids = Lists.newArrayList(1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009);
    public static int activityId = 1;
    public static int attachId = 2;
    public static int companyId = 3;
    public static int projectId = 4;
    public static int userId = 5;
    public static int todoId = 6;
    public static int assignId = 7;
    public static int creatorId = 8;
    public static int identifibleId = 9;
    public static int todolistId = 10;
    public static int count = 11;
    public static int documentId = 12;
    public static int updatorId = 13;
    public static int start = 0;
    public static int limit = 5;
    public static int departmentId = 14;
    public static int userCompanyId = 15;
    public static int groupId = 16;
    public static int tokenExpired = 17;
    public static int projectId1 = 18;
    public static int projectId2 = 19;
    public static int stepId = 18;
    public static int assigneeId = 19;
    public static int bugType = 0;
    public static int idInProject = 20;
    public static int storyId = 21;
    public static long times = 22;
    public static int topicId = 23;
    public static int bugId = 24;
    public static int commentId = 25;

    public static int ALL_START = 0;
    public static int ALL_LIMIT = -1;

    public static String note = "test_note";
    public static String attachType = new Todo().getType();
    public static String action = ActivityActionType.CREATE;
    public static String subject = "创建了任务";
    public static String code = "code";
    public static String codeHost = "codeHost";
    public static String contectEmail = "contectEmail@contectEmail.net";
    public static String teamName = "teamName";
    public static String teamSize = "teamSize";
    public static String target = "title";
    public static String content = "content";
    public static String name = "name";
    public static String contectName = "contectName";
    public static String description = "discription";
    public static String title = "title";
    public static String version = "2014070120140701";
    public static String type = "type";
    public static String generateText = "generateText";
    public static String email = "email@domain.com";
    public static String emailContent = "emailContent";
    public static String wrongEmail = "wrong@domain.com";
    public static String password = "password";
    public static String newPassword = "newPassword";
    public static String creatorAvatar = "creatorAvatar";
    public static String token = "token";
    public static String protocol = "protocol";
    public static String host = "host";
    public static String acceptanceLevel = "acceptanceLevel";
    public static String emailOrUsername = "emailOrUsername";
    public static String username = "username";
    public static String companyName = "companyName";
    public static String commentTargetName = "commentTargetName";

    public static Date created = getDateByString("2014-03-04 00:00");
    public static Date updated = getDateByString("2014-03-06 00:00");
    public static Date since = getDateByString("2014-01-01 00:00");
    public static Date until = getDateByString("2014-04-01 00:00");
    public static Date completed = getDateByString("2014-03-07 00:00");
    public static Date dueTime = getDateByString("2014-03-30 00:00");
    public static Date dueDate = getDateByString("2014-03-30 00:00");

    public static String projectName = "test_project";
    public static String userName = "test_user";
    public static String creatorName = "test_creator";
    public static String updatorName = "test_updator";

    public static String modelService = ActivityService.class.getName();
    public static String modelType = "type";

    public static Date getDateByString(String strDate) {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date = null;
        try {
            date = fmt.parse(strDate);
        } catch (ParseException e) {
        }
        return date;
    }

    public static Activity getASampleActivity() {
        Activity a = new Activity();
        a.setId(activityId);
        a.setAction(action);
        a.setAttachId(attachId);
        a.setAttachType(attachType);
        a.setCompanyId(companyId);
        a.setContent(content);
        a.setCreated(created);
        a.setCreatorId(creatorId);
        a.setCreatorName(userName);
        a.setProjectId(projectId);
        a.setSubject(subject);
        a.setTarget(target);
        return a;
    }

    public static Department getASampleDepartment() {
        Department d = new Department();
        d.setId(departmentId);
        return d;
    }

    public static UserCompany getASampleUserCompany() {
        UserCompany uc = new UserCompany();
        uc.setId(userCompanyId);
        uc.setDepartmentId(groupId);
        return uc;
    }

    public static Todo getASampleTodo() {
        Todo todo = new Todo();
        todo.setAssigneeId(assignId);
        todo.setCompanyId(companyId);
        todo.setCompleted(false);
        todo.setStatus(IterationItemStatus.TODO.getValue());
        todo.setContent(content);
        todo.setCreated(created);
        todo.setCreatorId(creatorId);
        todo.setDeleted(false);
        todo.setDueDate(created);
        todo.setId(todoId);
        todo.setProjectId(projectId);
        todo.setTodolistId(todolistId);
        todo.setUpdated(created);
        return todo;
    }

    public static User getASampleUser() {
        User user = new User();
        user.setId(userId);
        user.setName(userName);
        user.setCreated(created);
        user.setUpdated(created);
        return user;
    }

    public static Boolean isSampleUser(User user) {
        return user.getId() == userId && user.getName() == userName && user.getCreated() == created
                && user.getUpdated() == created;
    }

    public static Project getASampleProject() {
        Project project = new Project();
        project.setId(projectId);
        project.setName(projectName);
        project.setCreated(created);
        project.setCompanyId(companyId);
        project.setCreatorId(creatorId);
        project.setDeleted(false);
        project.setUpdated(created);
        project.setArchived(false);
        return project;
    }

    public static Story getASampleStory() {
        Story story = new Story();
        story.setProjectId(projectId);
        story.setCompanyId(companyId);
        story.setDescription(description);
        story.setId(storyId);
        story.setAcceptanceLevel(acceptanceLevel);
        story.setCompleted(false);
        story.setDeleted(false);
        return story;
    }

    public static Step getASampleStep() {
        Step step = new Step();
        step.setAssigneeId(userId);
        step.setId(stepId);
        step.setContent(content);
        step.setProjectId(projectId);
        step.setCompanyId(companyId);
        step.setDueDate(dueDate);
        return step;
    }

    public static Bug getASampleBug() {
        Bug bug = new Bug();
        bug.setId(bugId);
        bug.setProjectId(projectId);
        bug.setCompanyId(companyId);
        bug.setDeleted(false);
        bug.setCompleted(false);
        return bug;
    }

    public static Comment getASampleComment() {
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setAttachType(attachType);
        comment.setAttachId(attachId);
        comment.setProjectId(projectId);
        comment.setCompanyId(companyId);
        comment.setContent(content);
        comment.setDeleted(false);
        return comment;
    }

    public static Company getASampleCompany() {
        Company company = new Company();
        company.setCreated(created);
        company.setCreatorId(creatorId);
        company.setDeleted(false);
        company.setName(companyName);
        company.setUpdated(updated);
        company.setId(companyId);
        return company;
    }

    public static BaseProjectItem getASampleIdentifiable() {
        BaseProjectItem identifiable = mock(BaseProjectItem.class);
        when(identifiable.getId()).thenReturn(identifibleId);
        when(identifiable.getType()).thenReturn(attachType);
        when(identifiable.getProjectId()).thenReturn(projectId);
        when(identifiable.getCompanyId()).thenReturn(companyId);
        when(identifiable.getCreatorId()).thenReturn(creatorId);
        when(identifiable.getCreatorName()).thenReturn(userName);
        when(identifiable.getDeleted()).thenReturn(false);
        return identifiable;
    }

    public static ActivityHook getASampleActivityHook() throws Throwable {
        ActivityHook activityHook = mock(ActivityHook.class);
        doNothing().when(activityHook).whenCreationActivityCreated(any(User.class), any(Activity.class),
                any(BaseProjectItem.class));
        doNothing().when(activityHook).whenUpdateActivityCreated(any(User.class), any(Activity.class),
                any(BaseProjectItem.class), any(BaseProjectItem.class));
        return activityHook;
    }

    public static SynchronizedActivityHook getASampleSynchronizedActivityHook() throws Throwable {
        SynchronizedActivityHook SynchronizedActivityHook = mock(SynchronizedActivityHook.class);
        doNothing().when(SynchronizedActivityHook).whenCreationActivityCreated(any(Activity.class),
                any(BaseProjectItem.class));
        doNothing().when(SynchronizedActivityHook).whenUpdateActivityCreated(any(Activity.class),
                any(BaseProjectItem.class), any(BaseProjectItem.class));
        return SynchronizedActivityHook;
    }

    public static boolean compareCreatedItemDateWithToday(Date date) {
        // This method is to compare a new created item's created date or
        // updated date with a new date to check their year, month
        // and day are the same
        long dayInMillis = 24 * 3600 * 1000;
        Date laterDate = new Date();

        long l1 = laterDate.getTime() / dayInMillis;
        long d2 = date.getTime() / dayInMillis;
        // company two date's Year, Month and Day
        // make sure laterDate is equal or after input date
        return (laterDate.after(date) || laterDate.equals(date)) && l1 == d2;
    }
}
