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
package com.onboard.service.file.impl.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.onboard.service.file.FileService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/service.common.xml" })
@ActiveProfiles("test")
@Ignore
public class FileServiceTest {

    public static final String ENCODING = "UTF-8";

    @Autowired
    private FileService fileService;

    @Value("${file.rootPathVariable}")
    private String rootPathVariable;

    @Value("${file.folderName}")
    private String folderName;

    private String rootPath;

    @PostConstruct
    void init() {
        rootPath = System.getProperty(rootPathVariable) + File.separator + folderName;
    }

    private void checkWriteFile(String path, String str) throws UnsupportedEncodingException {
        boolean result = fileService.writeFile(File.separator + path, str.getBytes(ENCODING));
        Assert.assertTrue(result);
    }

    private void checkReadFile(String filename, String str) throws UnsupportedEncodingException {

        byte[] bytes = fileService.readFile(File.separator + filename);
        Assert.assertNotNull(bytes);
        Assert.assertEquals(new String(bytes, ENCODING), str);

    }

    @Test
    public void baseTest() {
        Assert.assertNotNull(fileService);
    }

    @Ignore
    public void writeFile() throws IOException {
        this.checkWriteFile("test.txt", "测试文件");
    }

    @Ignore
    public void writeFileWithEmpty() throws IOException {
        this.checkWriteFile("test_empty_file.txt", "");
    }

    @Ignore
    public void writeFileWithFileNotFoundException() throws IOException {
        // FileNotFoundException
        boolean result = fileService.writeFile("./\\", "".getBytes(ENCODING));
        Assert.assertFalse(result);
    }

    @Ignore
    public void writeFileWithIOException() throws IOException {
        // IOException
        String path = "//IOException.txt";
        File file = new File(rootPath + path);
        FileOutputStream fos = new FileOutputStream(file);
        FileLock lock = fos.getChannel().tryLock();
        boolean result = fileService.writeFile(path, "test111".getBytes(ENCODING));
        lock.release();
        fos.close();
        Assert.assertFalse(result);
    }

    @Ignore
    public void readFile() throws UnsupportedEncodingException, IOException {
        String filename = "test_read.txt";
        String str = "测试读取文件";
        this.checkWriteFile(filename, str);
        this.checkReadFile(filename, str);
    }

    @Ignore
    public void readFileWithFileNotFoundException() throws UnsupportedEncodingException, IOException {
        // FileNotFoundException
        byte[] bytes = fileService.readFile(File.separator + "./\\");
        Assert.assertNull(bytes);
    }

    @Ignore
    public void readFileWithIOException() throws UnsupportedEncodingException, IOException {
        // IOException
        String filename = "test_read.txt";
        File file = new File(rootPath + File.separator + filename);
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        FileChannel fc = raf.getChannel();
        FileLock fl = fc.tryLock();
        byte[] bytes = fileService.readFile(filename);
        Assert.assertNull(bytes);
        fl.release();
        raf.close();
    }

    @Ignore
    public void deleteFile() throws IOException {
        String str = "测试删除文件";
        String filename = File.separator + "test_delete.txt";
        this.checkWriteFile(filename, str);

        boolean result = fileService.deleteFile(filename);
        Assert.assertTrue(result);
        byte[] bytes = fileService.readFile(filename);
        Assert.assertNull(bytes);
    }

    @Ignore
    public void deleteFileWithNull() throws IOException {
        String filename = File.separator + "test_delete.txt";
        // FileNotFoundException
        boolean result = fileService.deleteFile(filename);
        result = fileService.deleteFile(filename);
        Assert.assertFalse(result);
    }

    @Ignore
    public void deleteFileWithFileNotFoundException() throws IOException {
        // FileNotFoundException
        boolean result = fileService.deleteFile("./\\");
        Assert.assertFalse(result);
    }

    @Ignore
    public void deleteFileWithIOException() throws IOException {
        String str = "测试删除文件";
        String filename = File.separator + "test_delete.txt";

        // IOException
        this.checkWriteFile(filename, str);
        File file = new File(rootPath + filename);
        FileOutputStream fos = new FileOutputStream(file);
        FileLock lock = fos.getChannel().tryLock();
        boolean result = fileService.deleteFile(filename);
        Assert.assertFalse(result);
        lock.release();
        fos.close();
    }

    @Ignore
    public void renameFile() throws UnsupportedEncodingException, IOException {
        String str = UUID.randomUUID().toString();
        String filename = File.separator + UUID.randomUUID().toString() + ".txt";
        String newName = File.separator + UUID.randomUUID().toString() + ".txt";

        this.checkWriteFile(filename, str);
        boolean result = fileService.renameFile(filename, newName, true);
        Assert.assertTrue(result);
        byte[] bytes = fileService.readFile(filename);
        Assert.assertNull(bytes);
        this.checkReadFile(newName, str);
    }

    @Ignore
    public void renameFileWithFolder() throws UnsupportedEncodingException, IOException {
        String str = UUID.randomUUID().toString();
        String filename = File.separator + UUID.randomUUID().toString() + ".txt";
        String newFolderName = File.separator + UUID.randomUUID().toString();
        String newName = File.separator + UUID.randomUUID().toString() + ".txt";

        this.checkWriteFile(filename, str);
        boolean result = fileService.renameFile(filename, newFolderName + newName, true);
        Assert.assertTrue(result);
        byte[] bytes = fileService.readFile(filename);
        Assert.assertNull(bytes);
        this.checkReadFile(newFolderName + newName, str);
    }

    @Ignore
    public void renameFileWithFileNotFoundException() throws UnsupportedEncodingException, IOException {
        String filename = File.separator + UUID.randomUUID().toString() + ".txt";
        String newName = File.separator + UUID.randomUUID().toString() + ".txt";

        // FileNotFoundException
        fileService.deleteFile(filename);
        boolean result = fileService.renameFile(filename, newName, true);
        Assert.assertFalse(result);
        result = fileService.renameFile("./\\", newName, true);
        Assert.assertFalse(result);
        result = fileService.renameFile(newName, "./\\", true);
        Assert.assertFalse(result);
    }

    @Ignore
    public void renameFileWithIOException() throws UnsupportedEncodingException, IOException {
        String str = UUID.randomUUID().toString();
        String filename = File.separator + UUID.randomUUID().toString() + ".txt";
        String newName = File.separator + UUID.randomUUID().toString() + ".txt";

        // IOException
        this.checkWriteFile(filename, str);
        FileOutputStream fos = new FileOutputStream(filename);
        FileLock lock = fos.getChannel().tryLock();
        boolean result = fileService.renameFile(newName, "./\\", true);
        Assert.assertFalse(result);
        lock.release();

        // auto = false
        this.checkWriteFile(filename, str);
        fos.close();
        result = fileService.renameFile(filename, newName, false);
    }

    @Ignore
    public void renameFileWithAuto() throws UnsupportedEncodingException, IOException {
        String str = UUID.randomUUID().toString();
        String filename = File.separator + UUID.randomUUID().toString() + ".txt";
        String newFolderName = File.separator + UUID.randomUUID().toString();
        String newName = File.separator + UUID.randomUUID().toString() + ".txt";

        // auto = false
        this.checkWriteFile(filename, str);
        boolean result = fileService.renameFile(filename, newFolderName + newName, false);
        Assert.assertFalse(result);
    }
}
