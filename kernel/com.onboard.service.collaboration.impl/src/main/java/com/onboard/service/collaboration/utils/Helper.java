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
package com.onboard.service.collaboration.utils;

import java.io.IOException;

/**
 * 工具类
 * 
 * @author huangsz, yewei
 * 
 */
public class Helper {
    /**
     * 从本地文件系统中删除文件删除文件 与File.service的FileUtils重复，建议将file.service也归并到collaboration.service @huangsz
     * 
     * @param name
     * @param path
     * @throws IOException
     */
    public static void deleteFileFromDisk(String name, String path) throws IOException {
        java.io.File file = new java.io.File(path + name);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }
}
