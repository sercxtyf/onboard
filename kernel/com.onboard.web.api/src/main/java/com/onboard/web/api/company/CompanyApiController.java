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

import java.util.List;

import org.elevenframework.web.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.onboard.domain.model.Company;
import com.onboard.domain.model.Project;
import com.onboard.domain.transform.CompanyTransform;
import com.onboard.dto.CompanyDTO;
import com.onboard.dto.ProjectDTO;
import com.onboard.service.account.CompanyService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.sampleProject.SampleProjectService;
import com.onboard.service.security.RoleService;
import com.onboard.service.security.interceptors.CompanyAdminRequired;
import com.onboard.service.security.interceptors.CompanyChecking;
import com.onboard.service.security.interceptors.CompanyMemberRequired;
import com.onboard.service.security.interceptors.LoginRequired;
import com.onboard.service.web.SessionService;

@Controller
@RequestMapping("/")
public class CompanyApiController {

    private static final int ITEM_LIMIT = -1;

    public static final Logger logger = LoggerFactory.getLogger(CompanyApiController.class);

    @Autowired
    private SessionService session;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SampleProjectService sampleProjectService;

    @Autowired
    private RoleService roleService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @Interceptors({ LoginRequired.class })
    @ResponseBody
    public List<CompanyDTO> getAll() {
        return Lists.transform(companyService.getCompaniesByUserId(session.getCurrentUser().getId()),
                new Function<Company, CompanyDTO>() {
                    @Override
                    public CompanyDTO apply(Company input) {
                        return CompanyTransform.companyToCompanyDTO(input);
                    }
                });
    }

    /**
     * 创建团队
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @Interceptors({ LoginRequired.class })
    @ResponseBody
    public CompanyDTO newCompany(@RequestBody CompanyDTO form) {
        form.setMoney(0);
        Company company = companyService.create(CompanyTransform.companyDTOtoCompany(form));

        sampleProjectService.createSampleProjectByCompanyId(company.getId(), session.getCurrentUser());

        return CompanyTransform.companyToCompanyDTO(company);
    }

    /**
     * 浏览某个Company
     */
    @RequestMapping(value = "/{companyId}", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class, CompanyChecking.class })
    @ResponseBody
    public ResponseEntity<CompanyDTO> getOne(@PathVariable int companyId) {
        Company company = companyService.getById(companyId);

        int userId = session.getCurrentUser().getId();
        List<Project> activeProjects = projectService.getActiveProjectListByUserByCompany(userId, companyId, 0, ITEM_LIMIT);

        CompanyDTO companyDTO = CompanyTransform.companyAndProjectsToCompanyDTO(company, activeProjects);
        for (ProjectDTO projectDTO : companyDTO.getProjects()) {
            int projectId = projectDTO.getId();
            projectDTO.setTopicCount(projectService.getTopicCount(projectId));
            projectDTO.setTodoCount(projectService.getTodoCount(projectId));
            projectDTO.setUserCount(projectService.getUserCount(projectId));
            projectDTO.setAttachmentCount(projectService.getAttachmentCount(projectId));
            projectDTO.setIsCurrentUserAdmin(roleService.projectAdmin(userId, companyId, projectId));
        }
        return new ResponseEntity<CompanyDTO>(companyDTO, HttpStatus.OK);
    }

    /**
     * 更新公司信息
     * 
     * @param companyId
     * @return
     */
    @RequestMapping(value = "/{companyId}", method = { RequestMethod.PUT })
    @Interceptors({ CompanyAdminRequired.class, CompanyChecking.class })
    @ResponseBody
    public CompanyDTO updateCompany(@PathVariable int companyId, @RequestBody CompanyDTO form) {
        form.setId(companyId);
        companyService.updateSelective(CompanyTransform.companyDTOtoCompany(form));
        return form;
    }

    /**
     * 删除一个公司
     * 
     * @param companyId
     * @return
     */
    @RequestMapping(value = "/{companyId}", method = { RequestMethod.DELETE })
    @Interceptors({ CompanyAdminRequired.class, CompanyChecking.class })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void deleteCompany(@PathVariable int companyId) {
        Company company = companyService.getById(companyId);
        company.setDeleted(true);
        companyService.updateSelective(company);
    }

}
