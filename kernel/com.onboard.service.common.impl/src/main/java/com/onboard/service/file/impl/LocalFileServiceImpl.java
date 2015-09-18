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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.onboard.service.file.FileService;

/**
 * 文件服务实现
 * 
 * @author yewei
 * 
 */
@Service("fileServiceBean")
public class LocalFileServiceImpl implements FileService {

    @Value("${file.rootPathVariable}")
    private String rootPathVariable;

    @Value("${file.folderName}")
    private String folderName;

    private String rootPath;

    @PostConstruct
    void init() {
        rootPath = System.getProperty(rootPathVariable) + File.separator
                + folderName;
    }

    @Override
    public boolean writeFile(String path, byte[] data) {
        File file = new File(rootPath + path);
        FileOutputStream fos = null;
        file.getParentFile().mkdirs();
        try {
            fos = new FileOutputStream(file);
            fos.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    @Override
    public byte[] readFile(String path) {
        FileInputStream fis = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(rootPath + path);
            for (int n; (n = fis.read(buffer)) != -1;) {
                os.write(buffer, 0, n);
            }
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public boolean deleteFile(String path) {
        File file = new File(rootPath + path);
        if (!file.exists() || file.isDirectory()) {
            return false;
        }
        return file.delete();
    }

    @Override
    public boolean renameFile(String path, String newPath, boolean auto) {
        File file = new File(rootPath + path);
        File newFile = new File(rootPath + newPath);
        if (!file.exists() || newFile.exists()) {
            return false;
        }
        if (auto) {
            newFile.getParentFile().mkdirs();
        }
        return file.renameTo(newFile);
    }

}
