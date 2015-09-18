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
package com.onboard.service.account.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.google.common.base.Preconditions;

/**
 * Created by luoruici on 13-12-12.
 */
public class PasswordUtils {

    public static final PasswordEncoder encoder = new Md5PasswordEncoder();
    public static final BCryptPasswordEncoder digester = new BCryptPasswordEncoder();

    /**
     * MD5加密密码后再用bcrypt加密
     * 
     * @param password
     * @return
     */
    public static String createPassword(String rawPass, String salt) {
        Preconditions.checkNotNull(rawPass);
        String oldPW = createMD5Password(rawPass);
        return updateOldEncPass(oldPW, salt);
    }

    /**
     * 验证密码是否有效
     * 
     * @param encPass
     *            加密密码
     * @param rawPass
     *            原始密码
     * @return
     */
    public static boolean isPasswordValid(String encPass, String rawPass, String salt) {
        Preconditions.checkNotNull(encPass);
        Preconditions.checkNotNull(rawPass);
        String oldPW = createMD5Password(rawPass);
        return digester.matches(addSalt(oldPW, salt), encPass);
        // return encoder.isPasswordValid(encPass.toLowerCase(), rawPass, null);
    }

    /**
     * 原有加密策略,MD5 hash后大写
     * 
     * @param rawPass
     * @return
     */
    private static String createMD5Password(String rawPass) {
        return encoder.encodePassword(rawPass, null).toUpperCase();
    }

    /**
     * 在原有密码基础上加密
     * 
     * @param oldPW
     * @param salt
     * @return
     */
    public static String updateOldEncPass(String oldPW, String salt) {
        return digester.encode(addSalt(oldPW, salt));
    }

    /**
     * 
     * @param level
     *            加密强度
     * @return
     */
    public static BCryptPasswordEncoder getBcryptDigestByLevel(int level) {
        if (level <= 0)
            return new BCryptPasswordEncoder();
        else
            return new BCryptPasswordEncoder(level);
    }

    /**
     * implement our own salt adding using created time
     * 
     * @param origin
     * @param salt
     * @return
     */
    public static String addSalt(String origin, String salt) {
        Pattern p = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");
        Matcher m = p.matcher(salt);
        if (m.find() != true)
            assert (false);
        return origin + revertString(m.group().replaceFirst(":", "-"));
    }

    private static String revertString(String s) {
        if (s == null || s.length() == 0)
            return "";
        else {
            return (new StringBuffer(s)).reverse().toString();
        }
    }
}
