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
package com.onboard.service.email.impl;

import com.onboard.service.email.TemplateEngineService;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring3.SpringTemplateEngine;

import java.util.Locale;
import java.util.Map;

public class OSGiTemplateEngine extends SpringTemplateEngine implements TemplateEngineService {

    @Override
    public String process(Class<?> clazz, String templateName, Map<String, ?> model) {
        final Context ctx = new Context(Locale.getDefault(), model);
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(clazz.getClassLoader());
            return super.process(templateName, ctx);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }
}
