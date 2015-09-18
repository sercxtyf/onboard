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
package com.onboard.test.exampleutils;

import com.onboard.domain.mapper.model.common.BaseExample;

/**
 * Example Matcher for {@link BaseExample}
 * 
 * @author Dongdong Du
 * 
 */
public abstract class ObjectMatcher<T> extends AbstractMatcher<T> {

    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(Object arg0) {
        return verifymatches((T) arg0);
    }

    public abstract boolean verifymatches(T item);
}
