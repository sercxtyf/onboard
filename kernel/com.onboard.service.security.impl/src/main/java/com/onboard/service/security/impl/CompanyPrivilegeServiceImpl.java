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
package com.onboard.service.security.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onboard.domain.mapper.CompanyPrivilegeMapper;
import com.onboard.domain.mapper.model.CompanyPrivilegeExample;
import com.onboard.domain.model.CompanyPrivilege;
import com.onboard.service.security.CompanyPrivilegeService;

/**
 * {@link com.onboard.service.collaboration.CompanyPrivilegeService} Service
 * implementation
 * 
 * @author XR
 * 
 */
@Transactional
@Service("companyPrivilegeServiceBean")
public class CompanyPrivilegeServiceImpl implements CompanyPrivilegeService {

    public static final Logger logger = LoggerFactory.getLogger(CompanyPrivilegeServiceImpl.class);

    @Autowired
    private CompanyPrivilegeMapper companyPrivilegeMapper;

    @Override
    public CompanyPrivilege getCompanyPrivilegeById(int id) {
        return companyPrivilegeMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<CompanyPrivilege> getCompanyPrivileges(int start, int limit) {
        CompanyPrivilegeExample example = new CompanyPrivilegeExample(new CompanyPrivilege());
        example.setLimit(start, limit);
        return companyPrivilegeMapper.selectByExample(example);
    }

    @Override
    public List<CompanyPrivilege> getCompanyPrivilegesByExample(CompanyPrivilege item, int start, int limit) {
        CompanyPrivilegeExample example = new CompanyPrivilegeExample(item);
        example.setLimit(start, limit);
        return companyPrivilegeMapper.selectByExample(example);
    }

    @Override
    public int countByExample(CompanyPrivilege item) {
        CompanyPrivilegeExample example = new CompanyPrivilegeExample(item);
        return companyPrivilegeMapper.countByExample(example);
    }

    @Override
    public CompanyPrivilege createCompanyPrivilege(CompanyPrivilege item) {
        companyPrivilegeMapper.insert(item);
        return item;
    }

    @Override
    public CompanyPrivilege updateCompanyPrivilege(CompanyPrivilege item) {
        companyPrivilegeMapper.updateByPrimaryKey(item);
        return item;
    }

    @Override
    public void deleteCompanyPrivilege(int id) {
        companyPrivilegeMapper.deleteByPrimaryKey(id);
    }

    @Override
    public CompanyPrivilege getOrCreateCompanyPrivilegeByUserId(int companyId, int userId) {
        CompanyPrivilege sample = new CompanyPrivilege();
        sample.setCompanyId(companyId);
        sample.setUserId(userId);
        List<CompanyPrivilege> ps = companyPrivilegeMapper.selectByExample(new CompanyPrivilegeExample(sample));
        if (ps == null || ps.size() == 0) {
            CompanyPrivilege p = new CompanyPrivilege();
            p.setCompanyId(companyId);
            p.setUserId(userId);
            p.setIsAdmin(false);
            p.setCanCreateProject(false);
            return createCompanyPrivilege(p);
        }

        return ps.get(0);
    }

    @Override
    public CompanyPrivilege setCompanyPrivilege(CompanyPrivilege item) {
        try {
            companyPrivilegeMapper.updateByPrimaryKeySelective(item);
        } catch (Exception e) {
            logger.debug("error occurs update privilege.", e);
        }
        return item;
    }
}
