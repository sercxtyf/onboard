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
package com.onboard.service.sampleProject.model;

import java.util.List;

import com.onboard.domain.model.Discussion;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.Todolist;

/**
 * 示例项目的model,与sample-project.json对应
 * 
 * @author xingliang
 * 
 */
public class SampleProject {

    private Project sampleProject;

    private List<Discussion> sampleDiscussions;

    private List<Todolist> sampleTodolists;

    private List<SampleFile> sampleFiles;

    public Project getSampleProject() {
        return sampleProject;
    }

    public void setSampleProject(Project sampleProject) {
        this.sampleProject = sampleProject;
    }

    public List<Discussion> project() {
        return sampleDiscussions;
    }

    public void setSampleDiscussions(List<Discussion> sampleDiscussions) {
        this.sampleDiscussions = sampleDiscussions;
    }

    public List<Todolist> getSampleTodolists() {
        return sampleTodolists;
    }

    public void setSampleTodolists(List<Todolist> sampleTodolists) {
        this.sampleTodolists = sampleTodolists;
    }

    public List<Discussion> getSampleDiscussions() {
        return sampleDiscussions;
    }

    public List<SampleFile> getSampleFiles() {
        return sampleFiles;
    }

    public void setSampleFiles(List<SampleFile> sampleFiles) {
        this.sampleFiles = sampleFiles;
    }

}
