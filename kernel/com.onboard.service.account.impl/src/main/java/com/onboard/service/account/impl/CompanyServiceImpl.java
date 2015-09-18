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
package com.onboard.service.account.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onboard.domain.mapper.CompanyMapper;
import com.onboard.domain.mapper.ProjectMapper;
import com.onboard.domain.mapper.UserCompanyMapper;
import com.onboard.domain.mapper.UserProjectMapper;
import com.onboard.domain.mapper.base.BaseMapper;
import com.onboard.domain.mapper.model.CompanyExample;
import com.onboard.domain.mapper.model.UserCompanyExample;
import com.onboard.domain.mapper.model.UserProjectExample;
import com.onboard.domain.model.Company;
import com.onboard.domain.model.UserCompany;
import com.onboard.domain.model.UserProject;
import com.onboard.service.account.AccountService;
import com.onboard.service.account.CompanyService;
import com.onboard.service.account.UserService;
import com.onboard.service.base.AbstractBaseService;
import com.onboard.service.web.SessionService;

/**
 * 
 * {@link CompanyService}实现类，标注为bean
 * 
 * @author huangsz
 * 
 */
@Transactional
@Service("companyServiceBean")
public class CompanyServiceImpl extends AbstractBaseService<Company, CompanyExample> implements CompanyService {

    @Autowired
    CompanyMapper companyMapper;
    @Autowired
    UserCompanyMapper userCompanyMapper;
    @Autowired
    UserProjectMapper userProjectMapper;
    @Autowired
    ProjectMapper projectMapper;
    @Autowired
    UserService userService;
    @Autowired
    AccountService accountService;
    @Autowired
    SessionService session;

    @Override
    public List<Company> getCompaniesByUserId(int userId) {
        UserCompany sample = new UserCompany();
        sample.setUserId(userId);
        UserCompanyExample example = new UserCompanyExample(sample);

        List<UserCompany> userCompanyList = userCompanyMapper.selectByExample(example);

        List<Company> companies = new ArrayList<Company>();
        for (UserCompany userCompany : userCompanyList) {
            Company company = new Company(companyMapper.selectByPrimaryKey(userCompany.getCompanyId()));
            if (!company.getDeleted()) {
                companies.add(company);
            }
        }
        return companies;
    }

    private void addUserToCompany(int companyId, int userId) {
        UserCompany userCompany = new UserCompany();
        userCompany.setCompanyId(companyId);
        userCompany.setUserId(userId);
        userCompanyMapper.insert(userCompany);
    }

    @Override
    public Company create(Company company) {
        company.setDeleted(false);
        company.setCreated(new Date());
        company.setUpdated(company.getCreated());
        company.setCreatorId(session.getCurrentUser().getId());
        company.setCreatorAvatar(session.getCurrentUser().getAvatar());
        company.setCreatorName(session.getCurrentUser().getName());
        company.setPrivileged(false);
        companyMapper.insertSelective(company);
        addUserToCompany(company.getId(), company.getCreatorId());
        return company;
    }

    @Override
    public void removeUser(Integer companyId, Integer userId) {
        UserCompany sample = new UserCompany();
        sample.setCompanyId(companyId);
        sample.setUserId(userId);
        UserCompanyExample example = new UserCompanyExample(sample);
        userCompanyMapper.deleteByExample(example);

        UserProject up = new UserProject();
        up.setCompanyId(companyId);
        up.setUserId(userId);
        userProjectMapper.deleteByExample(new UserProjectExample(up));
    }

    @Override
    public boolean containsUser(Integer companyId, Integer userId) {
        UserCompany sample = new UserCompany();
        sample.setCompanyId(companyId);
        sample.setUserId(userId);
        return userCompanyMapper.countByExample(new UserCompanyExample(sample)) > 0;
    }

    @Override
    protected BaseMapper<Company, CompanyExample> getBaseMapper() {
        return companyMapper;
    }

    @Override
    public Company newItem() {
        return new Company();
    }

    @Override
    public CompanyExample newExample() {
        return new CompanyExample();
    }

    @Override
    public CompanyExample newExample(Company item) {
        return new CompanyExample(item);
    }

}
