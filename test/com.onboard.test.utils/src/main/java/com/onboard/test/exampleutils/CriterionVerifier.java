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

import java.util.List;

import com.onboard.domain.mapper.model.common.BaseCriteria;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.mapper.model.common.Criterion;

/**
 * Criterion检查的辅助函数
 * <p>
 * 注意: 目前只适用于只有一个or criteria的情况
 * </p>
 * 
 * @author yewei
 * 
 */
public class CriterionVerifier {

    public static boolean verifyDistinct(BaseExample baseExample, boolean distinct) {
        return distinct == baseExample.isDistinct();
    }

    public static boolean verifyStart(BaseExample baseExample, int start) {
        return start == baseExample.getStart();
    }

    public static boolean verifyLimit(BaseExample baseExample, int limit) {
        return limit == baseExample.getLimit();
    }

    public static boolean verifyOrderByClause(BaseExample baseExample, String orderbyclause) {
        return orderbyclause.equals(baseExample.getOrderByClause());
    }

    public static boolean verifyIsNull(BaseExample baseExample, String field) {
        Criterion criterion = getCriterionByCondition(baseExample, field + CriteriaType.IS_NULL);
        return checkCriterion(criterion);
    }

    public static boolean verifyIsNotNull(BaseExample baseExample, String field) {
        Criterion criterion = getCriterionByCondition(baseExample, field + CriteriaType.IS_NOT_NULL);
        return checkCriterion(criterion);
    }

    public static boolean verifyBetween(BaseExample baseExample, String field, Object value, Object secondValue) {
        Criterion criterion = getCriterionByCondition(baseExample, field + CriteriaType.BETWEEN);
        return checkCriterion(criterion, value, secondValue);
    }

    public static boolean verifyNotBetween(BaseExample baseExample, String field, Object value, Object secondValue) {
        Criterion criterion = getCriterionByCondition(baseExample, field + CriteriaType.NOT_BETWEEN);
        return checkCriterion(criterion, value, secondValue);
    }

    public static boolean verifyIn(BaseExample baseExample, String field, Object value) {
        Criterion criterion = getCriterionByCondition(baseExample, field + CriteriaType.IN);
        return checkCriterion(criterion, value);
    }

    public static boolean verifyNotIn(BaseExample baseExample, String field, Object value) {
        Criterion criterion = getCriterionByCondition(baseExample, field + CriteriaType.NOT_IN);
        return checkCriterion(criterion, value);
    }

    public static boolean verifyEqualTo(BaseExample baseExample, String field, Object value) {
        Criterion criterion = getCriterionByCondition(baseExample, field + CriteriaType.EQUAL_TO);
        return checkCriterion(criterion, value);
    }

    public static boolean verifyNotEqualTo(BaseExample baseExample, String field, Object value) {
        Criterion criterion = getCriterionByCondition(baseExample, field + CriteriaType.NOT_EQUAL_TO);
        return checkCriterion(criterion, value);
    }

    public static boolean verifyGraterThan(BaseExample baseExample, String field, Object value) {
        Criterion criterion = getCriterionByCondition(baseExample, field + CriteriaType.GREATER_THAN);
        return checkCriterion(criterion, value);
    }

    public static boolean verifyGraterThanOrEqualTo(BaseExample baseExample, String field, Object value) {
        Criterion criterion = getCriterionByCondition(baseExample, field + CriteriaType.GREATER_THAN_OR_EQUAL_TO);
        return checkCriterion(criterion, value);
    }

    public static boolean verifyLessThan(BaseExample baseExample, String field, Object value) {
        Criterion criterion = getCriterionByCondition(baseExample, field + CriteriaType.LESS_THAN);
        return checkCriterion(criterion, value);
    }

    public static boolean verifyLessThanOrEqualTo(BaseExample baseExample, String field, Object value) {
        Criterion criterion = getCriterionByCondition(baseExample, field + CriteriaType.LESS_THAN_OR_EQUAL_TO);
        return checkCriterion(criterion, value);
    }

    private static boolean checkCriterion(Criterion c) {
        return c != null;
    }

    private static boolean checkCriterion(Criterion c, Object value) {
        if (c != null) {
            // 如果value为list, list中不能包含null元素
            if (value != null && c.getValue() != null && value instanceof List && c.getValue() instanceof List) {
                List<?> list = (List<?>) value;
                List<?> secondList = (List<?>) c.getValue();
                if (list.size() == secondList.size()) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i) != null && !list.get(i).equals(secondList.get(i))) {
                            return false;
                        }
                    }
                    return true;
                }
            } else {
                return c.getValue() != null && c.getValue().equals(value);
            }
        }

        return false;
    }

    private static boolean checkCriterion(Criterion c, Object value, Object secondValue) {
        return c != null && c.getValue() != null && c.getValue().equals(value) && c.getSecondValue() != null
                && c.getSecondValue().equals(secondValue);
    }

    /**
     * 根据condition找到baseExample中的criterion
     * 
     * @param baseExample
     * @param condition
     * @return
     */
    private static Criterion getCriterionByCondition(BaseExample baseExample, String condition) {
        List<BaseCriteria> criterias = baseExample.getOredBaseCriteria();
        if (criterias != null && !criterias.isEmpty()) {
            for (Criterion c : criterias.get(0).getCriteria()) {
                if (c.getCondition().equals(condition)) {
                    return c;
                }
            }
        }
        return null;
    }

    private static class CriteriaType {
        public static final String IS_NULL = " is null";
        public static final String IS_NOT_NULL = " is not null";
        public static final String BETWEEN = " between";
        public static final String NOT_BETWEEN = " not between";
        public static final String IN = " in";
        public static final String NOT_IN = " not in";
        public static final String EQUAL_TO = " =";
        public static final String NOT_EQUAL_TO = " <>";
        public static final String GREATER_THAN = " >";
        public static final String GREATER_THAN_OR_EQUAL_TO = " >=";
        public static final String LESS_THAN = " <";
        public static final String LESS_THAN_OR_EQUAL_TO = " <=";
    }
}
