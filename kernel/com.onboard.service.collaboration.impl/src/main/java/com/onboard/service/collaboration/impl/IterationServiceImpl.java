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
package com.onboard.service.collaboration.impl;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.onboard.domain.mapper.IterationAttachMapper;
import com.onboard.domain.mapper.IterationMapper;
import com.onboard.domain.mapper.base.BaseMapper;
import com.onboard.domain.mapper.model.IterationAttachExample;
import com.onboard.domain.mapper.model.IterationExample;
import com.onboard.domain.model.Iteration;
import com.onboard.domain.model.Iteration.IterationStatus;
import com.onboard.domain.model.IterationAttach;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.type.BaseOperateItem;
import com.onboard.domain.model.type.Iterable;
import com.onboard.service.base.AbstractBaseService;
import com.onboard.service.collaboration.IterationService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.common.identifiable.IdentifiableManager;
import com.onboard.service.web.SessionService;

/**
 * {@link com.onboard.service.collaboration.IterationService} Service implementation
 * 
 * @generated_by_elevenframework
 * 
 */
@Transactional
@Service("iterationServiceBean")
public class IterationServiceImpl extends AbstractBaseService<Iteration, IterationExample> implements IterationService {

    @Autowired
    private IterationMapper iterationMapper;

    @Autowired
    private IterationAttachMapper iterationAttachMapper;

    @Autowired
    // TODO: to delete
    private SessionService sessionService;

    @Autowired
    private IdentifiableManager identifiableManager;

    @Autowired
    private ProjectService projectService;

    @Override
    public Iteration getById(int id) {
        Iteration iteration = iterationMapper.selectByPrimaryKey(id);
        if (iteration == null) {
            return null;
        }
        iteration.setIterables(getIterablesWithBoardablesByIteration(id));
        return iteration;
    }

    @Override
    protected Iteration fillItemBeforeCreate(Iteration item) {
        if (item.getEndTime() != null) {
            DateTime dt = new DateTime(item.getEndTime());
            item.setEndTime(dt.withTimeAtStartOfDay().plusDays(1).plusSeconds(-1).toDate());
        }
        item.setStatus(Iteration.IterationStatus.CREATED.getValue());
        item.setCreatorId(sessionService.getCurrentUser().getId());
        item.setCreatorAvatar(sessionService.getCurrentUser().getAvatar());
        item.setCreatorName(sessionService.getCurrentUser().getUsername());
        return item;
    }

    @Override
    public Iteration updateSelective(Iteration item) {
        if (item == null || item.getId() == null) {
            return null;
        }
        Iteration original = iterationMapper.selectByPrimaryKey(item.getId());
        if (item.getEndTime() != null) {
            DateTime dt = new DateTime(item.getEndTime());
            item.setEndTime(dt.withTimeAtStartOfDay().plusDays(1).plusSeconds(-1).toDate());
        }
        if (isCompleteIteration(original, item)) {
            // 更新终止时间
            item.setEndTime(new Date());
            addNewIterationForProject(item, projectService.getById(original.getProjectId()));
        }
        item.setUpdated(new Date());
        iterationMapper.updateByPrimaryKeySelective(item);
        return item;
    }

    private boolean isCompleteIteration(Iteration original, Iteration updated) {
        return updated.getStatus() != null && !original.getStatus().equals(IterationStatus.COMPLETED.getValue())
                && updated.getStatus().equals(IterationStatus.COMPLETED.getValue());
    }

    @Override
    public Iteration getCurrentIterationByProjectId(int projectId) {
        Iteration iteration = new Iteration();
        iteration.setProjectId(projectId);
        IterationExample iterationExample = new IterationExample(iteration);
        List<String> status = Lists.newArrayList(IterationStatus.ACTIVE.getValue(), IterationStatus.CREATED.getValue());
        iterationExample.getOredCriteria().get(0).andStatusIn(status);
        List<Iteration> iterations = iterationMapper.selectByExample(iterationExample);
        if (iterations.isEmpty()) {
            return null;
        }
        iterations.get(0).setIterables(getIterablesWithBoardablesByIteration(iterations.get(0).getId()));
        return iterations.get(0);
    }

    @Override
    public List<Iteration> getCompleteIterationsByProjectId(int projectId) {
        Iteration sample = new Iteration();
        sample.setProjectId(projectId);
        IterationExample example = new IterationExample(sample);
        example.setOrderByClause("id desc");
        example.getOredCriteria().get(0).andStatusIn(Lists.newArrayList(IterationStatus.COMPLETED.getValue()));
        List<Iteration> iterations = iterationMapper.selectByExample(example);
        for (Iteration iteration : iterations) {
            iteration.setIterables(getIterablesByIteration(iteration.getId()));
        }
        return iterations;
    }

    @Override
    public List<Iteration> getCompleteIterationsByProjectId(int projectId, int start, int limit) {
        Iteration sample = new Iteration();
        sample.setProjectId(projectId);
        IterationExample example = new IterationExample(sample);
        example.setLimit(start, limit);
        example.setOrderByClause("id desc");
        example.getOredCriteria().get(0).andStatusIn(Lists.newArrayList(IterationStatus.COMPLETED.getValue()));
        List<Iteration> iterations = iterationMapper.selectByExample(example);
        for (Iteration iteration : iterations) {
            iteration.setIterables(getIterablesByIteration(iteration.getId()));
        }
        return iterations;
    }

    private Iteration addNewIterationForProject(Iteration lastIteration, Project project) {
        Iteration iteration = addNewIterationForProject(project);

        List<IterationAttach> iteratiAttachs = getIterationAttachsByIteration(lastIteration.getId());
        for (IterationAttach iterationAttach : iteratiAttachs) {
            if (iterationAttach.getIterable().getCompleted()) {
                iterationAttach.setCompleted(true);
                iterationAttach.setCompletedTime(new Date());
                iterationAttachMapper.updateByPrimaryKey(iterationAttach);
            } else {
                IterationAttach newIterationAttach = new IterationAttach(iterationAttach);
                newIterationAttach.setIterationId(iteration.getId());
                iterationAttachMapper.insert(newIterationAttach);
            }
        }
        return iteration;
    }

    @Override
    public Iteration addNewIterationForProject(Project project) {
        DateTime now = DateTime.now();
        Iteration iteration = new Iteration();
        iteration.setCompanyId(project.getCompanyId());
        iteration.setDeleted(false);
        iteration.setCreated(new Date());
        iteration.setUpdated(new Date());
        iteration.setCreatorId(-1);
        iteration.setEndTime(now.withTimeAtStartOfDay().plusDays(7).plusSeconds(-1).toDate());
        iteration.setProjectId(project.getId());
        iteration.setStartTime(now.withTimeAtStartOfDay().toDate());
        iteration.setStatus(IterationStatus.CREATED.getValue());
        iteration.setCreatorAvatar(sessionService.getCurrentUser().getAvatar());
        iteration.setCreatorId(sessionService.getCurrentUser().getId());
        iteration.setCreatorName(sessionService.getCurrentUser().getName());
        iterationMapper.insert(iteration);
        return iteration;
    }

    @Override
    public List<Iterable> getIterablesWithBoardablesByIteration(Integer iterationId) {
        IterationAttach sample = new IterationAttach();
        sample.setIterationId(iterationId);
        List<IterationAttach> iterationAttachs = iterationAttachMapper.selectByExample(new IterationAttachExample(sample));
        List<Iterable> iterables = Lists.newArrayList();
        for (IterationAttach iterationAttach : iterationAttachs) {
            BaseOperateItem identifiable = identifiableManager.getIdentifiableWithDetailByTypeAndId(
                    iterationAttach.getObjectType(), iterationAttach.getObjectId());
            if (identifiable != null && !identifiable.getDeleted()) {
                iterables.add((Iterable) identifiable);
            }
        }
        return iterables;
    }

    @Override
    public List<Iterable> getIterablesByIteration(Integer iterationId) {
        List<IterationAttach> iterationAttachs = getIterationAttachsByIteration(iterationId);
        List<Iterable> iterables = Lists.newArrayList();
        for (IterationAttach iterationAttach : iterationAttachs) {
            Iterable iterable = iterationAttach.getIterable();
            iterable.setIterationCompleted(iterationAttach.getCompleted());
            iterable.setIterationCompletedTime(iterationAttach.getCompletedTime());
            iterables.add(iterable);
        }
        return iterables;
    }

    private List<IterationAttach> getIterationAttachsByIteration(Integer iterationId) {
        IterationAttach sample = new IterationAttach();
        sample.setIterationId(iterationId);
        List<IterationAttach> iterationAttachs = iterationAttachMapper.selectByExample(new IterationAttachExample(sample));
        List<IterationAttach> result = Lists.newArrayList();
        for (IterationAttach iterationAttach : iterationAttachs) {
            BaseOperateItem identifiable = identifiableManager.getIdentifiableByTypeAndId(iterationAttach.getObjectType(),
                    iterationAttach.getObjectId());
            if (identifiable == null || identifiable.getDeleted()) {
                continue;
            }
            iterationAttach.setIterable((Iterable) identifiable);
            result.add(iterationAttach);
        }
        return result;
    }

    @Override
    public void addIterable(IterationAttach iterationAttach) {
        if (!iterationAttachMapper.selectByExample(new IterationAttachExample(iterationAttach)).isEmpty()) {
            return;
        }
        iterationAttach.setCompleted(false);
        iterationAttachMapper.insert(iterationAttach);
    }

    @Override
    public void removeIterable(IterationAttach iterationAttach) {
        iterationAttachMapper.deleteByExample(new IterationAttachExample(iterationAttach));
    }

    @Override
    public void removeIterable(Iterable iterable, Integer iterationId) {
        IterationAttach iterationAttach = new IterationAttach();
        iterationAttach.setIterationId(iterationId);
        iterationAttach.setObjectId(iterable.getId());
        iterationAttach.setObjectType(iterable.getType());
        removeIterable(iterationAttach);
    }

    @Override
    public void addIterable(Iterable iterable) {
        IterationAttach iterationAttach = new IterationAttach();
        iterationAttach.setCompleted(false);
        Iteration iteration = getCurrentIterationByProjectId(iterable.getProjectId());
        iterationAttach.setIterationId(iteration.getId());
        iterationAttach.setIterable(iterable);
        addIterable(iterationAttach);
    }

    @Override
    protected BaseMapper<Iteration, IterationExample> getBaseMapper() {
        return iterationMapper;
    }

    @Override
    public Iteration newItem() {
        return new Iteration();
    }

    @Override
    public IterationExample newExample() {
        return new IterationExample();
    }

    @Override
    public IterationExample newExample(Iteration item) {
        return new IterationExample(item);
    }

}
