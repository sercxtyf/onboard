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
package com.onboard.web.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.onboard.domain.model.IterationItemStatus;
import com.onboard.domain.model.Step;
import com.onboard.service.collaboration.KeywordService;
import com.onboard.service.collaboration.StepService;
import com.onboard.service.collaboration.TodoService;

@Controller
@RequestMapping("/")
public class MigrationController {

    private final static Logger LOGGER = LoggerFactory.getLogger(MigrationController.class);

    @Autowired
    private TodoService todoService;

    @Autowired
    private KeywordService keywordService;

    @Autowired
    private StepService stepService;

    @RequestMapping(value = "/migrate-todo", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void migrateTodoKeywords() {
        todoService.postgenerateTodoKeywords();
        postGenerateStepKeywords();
        keywordService.regenerateKeywordTfidf();
    }

    private void postGenerateStepKeywords() {
        Step stepExample = new Step();
        stepExample.setStatus(IterationItemStatus.CLOSED.getValue());
        List<Step> steps = stepService.getBySample(stepExample, 0, -1);
        if (steps != null) {
            int size = steps.size();
            int hasFinished = 0;
            for (Step completedStep : steps) {
                keywordService.generateOrUpdateKeywordsByIdentifiable(completedStep);
                if (completedStep.getAssigneeId() != null) {
                    keywordService.addKeywordToUser(completedStep, completedStep.getAssigneeId());
                }
                LOGGER.info(String.format("finish generating step:%d, has finished %d/%d", completedStep.getId(), ++hasFinished,
                        size));
            }
        }
    }

    @RequestMapping(value = "/migrate-todo-tfidf", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void migrateTodoTfidf() {
        keywordService.regenerateKeywordTfidf();
    }
}
