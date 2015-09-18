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
package com.onboard.service.collaboration.scheduler;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.onboard.domain.mapper.TodoMapper;
import com.onboard.domain.mapper.model.TodoExample;
import com.onboard.domain.model.IterationItemStatus;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.User;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.common.identifiable.IdentifiableManager;
import com.onboard.service.email.EmailService;
import com.onboard.service.email.TemplateEngineService;
import com.onboard.service.email.exception.MessageSendingException;
import com.onboard.utils.DataCipher;

@Service
public class DueTodosScheduler {

    private static final String VM_PATH = "templates/todo-due.vm";

    public static final Logger logger = LoggerFactory.getLogger(DueTodosScheduler.class);

    private final String protocol = "https://";

    @Value("${site.host}")
    private String host;

    @Autowired
    private TodoMapper todoMapper;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    protected IdentifiableManager identifiableManager;

    @Autowired
    protected TemplateEngineService templateEngineService;

    private List<Todo> getDueTodosOfToday() {
        Todo sample = new Todo(false);
        sample.setStatus(IterationItemStatus.TODO.getValue());
        TodoExample example = new TodoExample(sample);
        DateTime today = new DateTime().withTimeAtStartOfDay();
        example.getOredCriteria().get(0).andDueDateGreaterThanOrEqualTo(today.toDate())
                .andDueDateLessThan(today.plusDays(1).toDate());

        return todoMapper.selectByExample(example);
    }

    @Scheduled(cron = "0 1 0 * * ?")
    public void notifyDueTodo() throws MessageSendingException {
        List<Todo> todos = this.getDueTodosOfToday();

        logger.info("todos of day {} counts : {}", new DateTime(), todos.size());
        for (Todo todo : todos) {
            if (todo.getAssigneeId() == null) {
                continue;
            }
            Project project = projectService.getById(todo.getProjectId());
            User user = userService.getById(todo.getAssigneeId());

            Map<String, Object> model = new HashMap<String, Object>();
            model.put("content", todo.getContent());
            model.put("host", protocol + this.host);
            model.put("date", new SimpleDateFormat("yyyy年MM月dd日").format(todo.getDueDate()));
            //TODO: get url
//            String url = identifiableManager.getIdentifiableURL(todo);
            String url = String.format("https://onboard.cn/teams/%s/projects/%s/todolists/open?id=%s1&type=todo", 
                    todo.getCompanyId(), todo.getProjectId(), todo.getId());
            model.put("url", url);

            String content = templateEngineService.process(getClass(), VM_PATH, model);
            String replyTo = StringUtils.arrayToDelimitedString(
                    new String[] { todo.getType(), String.valueOf(todo.getId()), DataCipher.encode(user.getEmail()) },
                    "-");
            emailService.sendEmail("OnBoard", user.getEmail(), null, null,
                    String.format("[%s]%s 快要到期了！", project.getName(), todo.getContent()), content, replyTo);
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
