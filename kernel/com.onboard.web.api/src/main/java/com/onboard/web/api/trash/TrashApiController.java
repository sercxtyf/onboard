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
package com.onboard.web.api.trash;

import java.util.ArrayList;
import java.util.List;

import org.elevenframework.web.interceptor.Interceptors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Trash;
import com.onboard.domain.model.type.BaseOperateItem;
import com.onboard.service.base.BaseService;
import com.onboard.service.collaboration.TrashService;
import com.onboard.service.common.identifiable.IdentifiableManager;
import com.onboard.service.security.interceptors.CompanyMemberRequired;
import com.onboard.service.web.SessionService;

@RequestMapping(value = "/{companyId}")
@Controller
public class TrashApiController {

    @Autowired
    TrashService trashService;

    @Autowired
    SessionService sessionService;

    @Autowired
    IdentifiableManager identifiableManager;

    @RequestMapping(value = "/trashes", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class })
    @ResponseBody
    public List<Trash> getTrashs(@PathVariable int companyId, @RequestParam(required = true) Integer start,
            @RequestParam(required = true) Integer limit) {
        List<Trash> trashs = new ArrayList<Trash>();
        Trash trash = new Trash();
        trash.setCompanyId(companyId);
        trashs = trashService.getTrashesByExample(trash, start, limit);
        return trashs;
    }

    @RequestMapping(value = "/trashes/{trashId}", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class })
    @ResponseBody
    public Trash getTrashById(@PathVariable int companyId, @PathVariable int trashId) {
        return trashService.getTrashById(trashId);
    }

    @RequestMapping(value = "/trashes/{trashId}", method = RequestMethod.DELETE)
    @Interceptors({ CompanyMemberRequired.class })
    @ResponseStatus(HttpStatus.OK)
    public void deleteTrash(@PathVariable int companyId, @PathVariable int trashId) {
        Trash trash = trashService.getTrashById(trashId);
        BaseService<? extends BaseOperateItem, ? extends BaseExample> identifiableService = identifiableManager.getIdentifiableService(trash.getAttachType());
        identifiableService.recover(trash.getAttachId());
        trashService.deleteTrash(trashId);
    }

}
