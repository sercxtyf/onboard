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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.onboard.service.file.ImageService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/service.common.xml" })
@Ignore
public class ImageServiceTest {

    @Autowired
    private ImageService imageSevice;

    private byte[] getTestImage() throws FileNotFoundException {
        File directory = new File("");
        String root = "";
        try {
            root = directory.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(root + "//src//test//resources//" + "test.jpg");
        FileInputStream fis = new FileInputStream(file);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                bos.write(buf, 0, readNum);
            }
        } catch (IOException ex) {
            Assert.assertTrue(false);
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                // ignore
            }
        }
        byte[] bytes = bos.toByteArray();

        return bytes;

    }

    @Test
    public void baseTest() {
        Assert.assertNotNull(imageSevice);
    }

    @Ignore
    public void readFile() throws IOException {
        byte[] image = getTestImage();
        String filename = "test_read.jpg";
        this.checkWriteFile(filename, image);
        this.checkReadFile(filename, image);
    }

    private void checkReadFile(String filename, byte[] image) {
        byte[] bytes = imageSevice.readFile(filename);
        Assert.assertNotNull(bytes);
        Assert.assertArrayEquals(bytes, image);

    }

    private void checkWriteFile(String path, byte[] image) {
        boolean result = imageSevice.writeFile(path, image);
        Assert.assertTrue(result);
    }

    @Ignore
    public void writeFile() throws IOException {
        this.checkWriteFile("test_write.jpg", this.getTestImage());
    }

    @Ignore
    public void writeFileEmpty() throws IOException {
        boolean result = imageSevice.writeFile("test_write.jpg", new byte[] {});
        Assert.assertFalse(result);

    }

    @Ignore
    public void deleteFile() throws IOException {
        byte[] image = getTestImage();
        String filename = File.separator + "test_delete.jpg";
        this.checkWriteFile(filename, image);

        boolean result = imageSevice.deleteFile(filename);
        Assert.assertTrue(result);
        byte[] bytes = imageSevice.readFile(filename);
        Assert.assertNull(bytes);
    }

    @Ignore
    public void renameFile() throws IOException {
        byte[] image = getTestImage();
        String oldFileName = "old.jpg";
        String newFileName = "new.jpg";
        String newFileNameInFolder = "new.jpg";
        this.checkWriteFile(oldFileName, image);
        checkRenameFile(oldFileName, newFileName, image);
        checkRenameFile(newFileName, newFileNameInFolder, image);
    }

    private void checkRenameFile(String oldFilename, String newFilename, byte[] image) {
        boolean result = imageSevice.writeFile(oldFilename, image);
        Assert.assertTrue(result);

        result = imageSevice.renameFile(File.separator + oldFilename, File.separator + newFilename, true);
        Assert.assertTrue(result);
        byte[] bytes = imageSevice.readFile(File.separator + oldFilename);
        Assert.assertNull(bytes);
        bytes = imageSevice.readFile(File.separator + newFilename);
        Assert.assertNotNull(bytes);
        Assert.assertArrayEquals(bytes, image);
    }

}
