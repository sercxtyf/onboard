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
package com.onboard.service.file.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.onboard.service.file.ImageService;
import com.onboard.service.file.utils.UpYun;

@Service("upyunImageServiceBean")
public class UpyunImageServiceImpl implements ImageService {

    @Value("${upyun.bucketName}")
    private String bucketName;

    @Value("${upyun.username}")
    private String username;

    @Value("${upyun.password}")
    private String password;

    private UpYun upyun;

    @PostConstruct
    public void init() {
        upyun = new UpYun(bucketName, username, password);
    }

    @Override
    public byte[] readFile(String path) {
        return null;
    }

    @Override
    public boolean writeFile(String path, byte[] data) {
        return upyun.writeFile(path, data, true);
    }

    @Override
    public boolean deleteFile(String path) {
        return upyun.deleteFile(path);
    }

    @Override
    public boolean renameFile(String path, String newPath, boolean auto) {

        return false;
    }

}
