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
package com.onboard.domain.model.utils;

import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.util.ParserException;

/**
 * 将HTML转化为纯文本的辅助类
 * 
 * @author lvyiqiang, yewei
 * 
 */
public class HtmlTextParser {
    public static String getPlainText(String htmlStr) {
        Parser parser = new Parser();
        String plainText = "";
        try {
            parser.setInputHTML(htmlStr);

            StringBean stringBean = new StringBean();
            // 设置不需要得到页面所包含的链接信息
            stringBean.setLinks(false);
            // 设置将不间断空格由正规空格所替代
            stringBean.setReplaceNonBreakingSpaces(true);
            // 设置将一序列空格由单一空格替代
            stringBean.setCollapse(true);

            parser.visitAllNodesWith(stringBean);
            plainText = stringBean.getStrings();

        } catch (ParserException e) {
            e.printStackTrace();
        }

        return plainText;
    }
}
