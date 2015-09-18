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
package com.onboard.service.index.model;

/**
 * 分页辅助类
 *
 * @author lvyiqiang, yewei
 */
public class Page {
    private long sum;
    private int currentPageNumber;
    private int pageSize;
    private boolean hasRead = false;

    public Page(int pageSize) {
        sum = 0;
        currentPageNumber = 0;
        this.pageSize = pageSize;
    }

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    public void setCurrentPageNumber(int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void nextPage() {
        hasRead = true;
        currentPageNumber++;
    }

    public boolean hasNext() {
        if (!hasRead) {
            return true;
        }
        if (currentPageNumber * pageSize >= sum) {
            return false;
        }

        return true;
    }
}
