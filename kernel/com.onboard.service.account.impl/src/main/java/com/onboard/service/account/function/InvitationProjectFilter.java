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
package com.onboard.service.account.function;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.onboard.domain.model.InvitationProjects;
import com.onboard.domain.model.Project;

/**
 * 用于过滤一个Project集合中的元素
 *
 * Created by luoruici on 13-12-12.
 */
public class InvitationProjectFilter implements Predicate<Project> {

    private Set<Integer> existingProjectIds;

    public InvitationProjectFilter(Collection<InvitationProjects> existings) {
        this.existingProjectIds = ImmutableSet.copyOf(Iterables.transform(existings, new Function<InvitationProjects, Integer>() {
            @Override
            public Integer apply(InvitationProjects input) {
                return input.getProjectId();
            }
        }));
    }

    @Override
    public boolean apply(Project input) {
        return !this.existingProjectIds.contains(input.getId());
    }
}
