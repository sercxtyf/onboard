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
package com.onboard.service.sampleProject.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.onboard.domain.model.Discussion;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.Todolist;
import com.onboard.domain.model.User;
import com.onboard.domain.transform.ProjectTransform;
import com.onboard.service.collaboration.AttachmentService;
import com.onboard.service.collaboration.DiscussionService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.collaboration.TodoService;
import com.onboard.service.collaboration.TodolistService;
import com.onboard.service.sampleProject.SampleProjectService;
import com.onboard.service.sampleProject.model.SampleProject;
import com.onboard.service.upload.UploadService;

/**
 * {@link com.onboard.plugin.sampleProject.SampleProjectService} Service
 * 
 * @author xingliang
 * 
 */
@Service("sampleProjectServiceBean")
public class SampleProjectServiceImpl implements SampleProjectService {

    @Autowired
    ProjectService projectService;
    @Autowired
    DiscussionService discussionService;
    @Autowired
    TodolistService todolistService;
    @Autowired
    TodoService todoService;
    @Autowired
    UploadService uploadService;
    @Autowired
    AttachmentService attachmentService;
    @Autowired
    ApplicationContext applicationContext;

    public static final Logger logger = LoggerFactory.getLogger(SampleProjectServiceImpl.class);
    private static final String RESOURCE_PATH = "com/onboard/service/service/sampleProject/impl/";
    private static final String JSON_NAME = "sample-project.json";

    @Override
    public void createSampleProjectByCompanyId(Integer companyId, User creator) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Resource[] resource = applicationContext.getResources(String.format("classpath*:**%s**", RESOURCE_PATH + JSON_NAME));
            if (resource == null || resource.length == 0) {
                logger.error("sampleProject.json is not exist");
                return;
            }
            File json = resource[0].getFile();
            SampleProject sample = mapper.readValue(json, SampleProject.class);
            Project project = sample.getSampleProject();
            project.setCompanyId(companyId);
            project.setCreatorId(creator.getId());
            project = projectService.createProject(ProjectTransform.projectToProjectDTO(project));
            // 示例discussion
            for (Discussion discussion : sample.getSampleDiscussions()) {
                discussion.setCompanyId(companyId);
                discussion.setProjectId(project.getId());
                discussion.setCreatorId(creator.getId());
                discussion.setCreatorName(creator.getName());
                discussionService.create(discussion);
            }
            // 示例todo和todolist
            List<Todolist> todolists = sample.getSampleTodolists();
            for (Todolist todolist : todolists) {
                todolist.setCompanyId(companyId);
                todolist.setProjectId(project.getId());
                todolist.setCreatorId(creator.getId());
                todolist.setCreatorName(creator.getName());
                todolist = todolistService.create(todolist);
                for (Todo todo : todolist.getTodos()) {
                    todo.setCompanyId(companyId);
                    todo.setProjectId(project.getId());
                    todo.setTodolistId(todolist.getId());
                    todo.setCreatorId(creator.getId());
                    todo.setCreatorName(creator.getName());
                    todo.setPosition(0.0);
                    todo.setTodoType("task");
                    todo.setPriority(1);
                    todoService.create(todo);
                }
            }

        } catch (JsonParseException e) {
            logger.error("JsonParse failed when creating sampleProject");
        } catch (JsonMappingException e) {
            logger.error("JsonMapping failed when creating sampleProject");
        } catch (IOException e) {
            logger.error("cannot read sampleProject.json");
        }

    }

}
