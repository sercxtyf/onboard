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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Created by luoruici on 13-12-12.
 */
@RunWith(JUnit4.class)
public class PasswordUtilsTest {

    private static final String EMPTY = new String();
    private static final String STRING = "onboard.cn";
    private static final String ANOTHERSTRING = "onboard.nc";
    private static final String DATESTRING = "Mon Jul 21 17:09:00 CST 2014";
    private static final String SALTSTRING = "17:09:00";
    private static final String SALTEDPW = STRING + "00:90-71";

    @Test(expected = NullPointerException.class)
    public void createPasswordWithNull() {
        PasswordUtils.createPassword(null, null);
    }

    @Test
    public void createPasswordWithEmptyValue() {
        assertNotNull("empty string should have md5 value!", PasswordUtils.createPassword(EMPTY, DATESTRING));
    }

    @Test
    public void testDigestResultChange() {
        BCryptPasswordEncoder digester = PasswordUtils.getBcryptDigestByLevel(0);
        String result1 = digester.encode(STRING);
        String result2 = digester.encode(STRING);
        assertTrue("Digest two times should produce different result", result1 != result2);
    }

    @Test
    public void testDigestResultsMatch() {
        BCryptPasswordEncoder digester = PasswordUtils.getBcryptDigestByLevel(0);
        String result1 = digester.encode(STRING);
        String result2 = digester.encode(STRING);
        assertTrue("Digest once should match", digester.matches(STRING, result1));
        assertTrue("Digest twice should match", digester.matches(STRING, result2));
    }

    @Test
    public void testSimilarPWNotMatch() {
        BCryptPasswordEncoder digester = PasswordUtils.getBcryptDigestByLevel(0);
        String result1 = digester.encode(STRING);
        String result2 = digester.encode(ANOTHERSTRING);
        assertTrue("Similar PW shouldn't match", !digester.matches(STRING, result2));
        assertTrue("Similar PW shouldn't match", !digester.matches(ANOTHERSTRING, result1));
    }

    @Test
    public void testOldPWMigrate() {
        PasswordEncoder encoder = new Md5PasswordEncoder();
        String oldPW = encoder.encodePassword(STRING, null).toUpperCase();
        String newPW = PasswordUtils.updateOldEncPass(oldPW, DATESTRING);
        assertTrue("Old PW should match", PasswordUtils.isPasswordValid(newPW, STRING, DATESTRING));
    }

    @Test
    public void testNewPWSetup() {
        String newPW = PasswordUtils.createPassword(STRING, DATESTRING);
        assertTrue("New PW should match", PasswordUtils.isPasswordValid(newPW, STRING, DATESTRING));
    }

    @Test
    public void testDifferentStrengthNotMatch() {
        BCryptPasswordEncoder digester = PasswordUtils.getBcryptDigestByLevel(10);
        BCryptPasswordEncoder digesterStronger = PasswordUtils.getBcryptDigestByLevel(20);
        String enPW = digester.encode(STRING);
        boolean isMatch = digesterStronger.matches(STRING, enPW);
        assertTrue("Digesters with different strength should not match", isMatch);
    }

    @Test
    public void testSaltExtraction() {
        Pattern p = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");
        Matcher m = p.matcher(DATESTRING);
        assertTrue("String extracted should match", m.find() && m.group().equals(SALTSTRING));
    }

    @Test
    public void testAddSalt() {
        String result = PasswordUtils.addSalt(STRING, DATESTRING);
        assertTrue(result + " should match", result.equals(SALTEDPW));
    }
}
