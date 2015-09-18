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
package com.onboard.service.file.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * 又拍云官方API， 封装了又拍云图片云服务器的接口，用于图片和云服务器间的操作.
 * 
 * @author huangsz
 * 
 */
public class UpYun {

    /** 默认的编码格式 */
    private static final String UTF8 = "UTF-8";

    /** SKD版本号 */
    private final String VERSION = "2.0";

    /** 路径的分割符 */
    private final String SEPARATOR = "/";

    private final String AUTHORIZATION = "Authorization";
    private final String DATE = "Date";
    private final String CONTENT_LENGTH = "Content-Length";
    private final String CONTENT_MD5 = "Content-MD5";
    private final String CONTENT_SECRET = "Content-Secret";
    private final String MKDIR = "mkdir";

    private final String X_UPYUN_WIDTH = "x-upyun-width";
    private final String X_UPYUN_HEIGHT = "x-upyun-height";
    private final String X_UPYUN_FRAMES = "x-upyun-frames";
    private final String X_UPYUN_FILE_TYPE = "x-upyun-file-type";
    private final String X_UPYUN_FILE_SIZE = "x-upyun-file-size";
    private final String X_UPYUN_FILE_DATE = "x-upyun-file-date";

    private final String METHOD_HEAD = "HEAD";
    private final String METHOD_GET = "GET";
    private final String METHOD_PUT = "PUT";
    private final String METHOD_DELETE = "DELETE";

    /**
     * 根据网络条件自动选择接入点:v0.api.upyun.com
     */
    public static final String ED_AUTO = "v0.api.upyun.com";
    /**
     * 电信接入点:v1.api.upyun.com
     */
    public static final String ED_TELECOM = "v1.api.upyun.com";
    /**
     * 联通网通接入点:v2.api.upyun.com
     */
    public static final String ED_CNC = "v2.api.upyun.com";
    /**
     * 移动铁通接入点:v3.api.upyun.com
     */
    public static final String ED_CTT = "v3.api.upyun.com";

    // 默认不开启debug模式
    public boolean debug = false;
    // 默认的超时时间：30秒
    private int timeout = 30 * 1000;
    // 默认为自动识别接入点
    private String apiDomain = ED_CNC;
    // 待上传文件的 Content-MD5 值
    private String contentMD5 = null;
    // 待上传文件的"访问密钥"
    private String fileSecret = null;
    // 空间名
    protected String bucketName = null;
    // 操作员名
    protected String userName = null;
    // 操作员密码
    protected String password = null;

    // 图片信息的参数
    protected String picWidth = null;
    protected String picHeight = null;
    protected String picFrames = null;
    protected String picType = null;

    // 文件信息的参数
    protected String fileType = null;
    protected String fileSize = null;
    protected String fileDate = null;

    /**
     * 初始化 UpYun 存储接口
     * 
     * @param bucketName
     *            空间名称
     * @param userName
     *            操作员名称
     * @param password
     *            密码，不需要MD5加密
     * @return UpYun object
     */
    public UpYun(String bucketName, String userName, String password) {
        this.bucketName = bucketName;
        this.userName = userName;
        this.password = md5(password);
    }

    /**
     * 返回文件的URL前缀
     * 
     * @return
     */
    public String getUrlPrefix() {
        return "http://" + bucketName + ".b0.upaiyun.com";
    }

    /**
     * 切换 API 接口的域名接入点
     * <p>
     * 可选参数：<br>
     * 1) UpYun.ED_AUTO(v0.api.upyun.com)：默认，根据网络条件自动选择接入点 <br>
     * 2) UpYun.ED_TELECOM(v1.api.upyun.com)：电信接入点<br>
     * 3) UpYun.ED_CNC(v2.api.upyun.com)：联通网通接入点<br>
     * 4) UpYun.ED_CTT(v3.api.upyun.com)：移动铁通接入点
     * 
     * @param domain
     *            域名接入点
     */
    public void setApiDomain(String domain) {
        apiDomain = domain;
    }

    /**
     * 查看当前的域名接入点
     * 
     * @return
     */
    public String getApiDomain() {
        return apiDomain;
    }

    /**
     * 设置连接超时时间，默认为30秒
     * 
     * @param second
     *            秒数，60即为一分钟超时
     */
    public void setTimeout(int second) {
        timeout = second * 1000;
    }

    /**
     * 查看当前的超时时间
     * 
     * @return
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * 查看当前是否是debug模式
     * 
     * @return
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * 设置是否开启debug模式
     * 
     * @param debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * 设置待上传文件的 Content-MD5 值
     * <p>
     * 如果又拍云服务端收到的文件MD5值与用户设置的不一致，将回报 406 Not Acceptable 错误
     * 
     * @param md5Value
     *            文件 MD5 校验后的内容
     */
    public void setContentMD5(String md5Value) {
        contentMD5 = md5Value;
    }

    /**
     * 设置待上传文件的"访问密钥"
     * <p>
     * 注意：<br>
     * 仅支持图片空！设置密钥后，无法根据原文件URL直接访问，需带 URL 后面加上 （缩略图间隔标志符+密钥） 进行访问
     * <p>
     * 举例:<br>
     * 如果缩略图间隔标志符为"!"，密钥为"bac"，上传文件路径为"/folder/test.jpg"，<br>
     * 那么该图片的对外访问地址为：http://空间域名 /folder/test.jpg!bac
     * 
     * @param secret
     *            密钥字符串
     */
    public void setFileSecret(String secret) {
        fileSecret = secret;
    }

    public String getPicWidth() {
        return picWidth;
    }

    public String getPicHeight() {
        return picHeight;
    }

    public String getPicFrames() {
        return picFrames;
    }

    public String getPicType() {
        return picType;
    }

    /**
     * 获取当前SDK的版本号
     * 
     * @return SDK版本号
     */
    public String version() {
        return VERSION;
    }

    /**
     * 获取总体空间的占用量
     * 
     * @param path
     *            目标路径
     * @return 空间占用量，失败时返回 -1
     */
    public long getBucketUsage() {
        return getFolderUsage("/");
    }

    /**
     * 获取某个子目录的占用量
     * 
     * @param path
     *            目标路径
     * @return 空间占用量，失败时返回 -1
     */
    public long getFolderUsage(String path) {

        long usage = -1;

        String result = HttpAction(METHOD_GET, formatPath(path) + "/?usage");

        if (!isEmpty(result)) {

            try {
                usage = Long.parseLong(result.trim());
            } catch (NumberFormatException e) {
            }
        }

        return usage;
    }

    /**
     * 上传文件
     * 
     * @param filePath
     *            文件路径（包含文件名）
     * @param datas
     *            文件内容
     * 
     * @return true or false
     */
    public boolean writeFile(String filePath, byte[] datas) {
        return writeFile(filePath, datas, false, null);
    }

    /**
     * 上传文件
     * 
     * @param filePath
     *            文件路径（包含文件名）
     * @param datas
     *            文件内容
     * @param auto
     *            是否自动创建父级目录(最多10级)
     * 
     * @return true or false
     */
    public boolean writeFile(String filePath, byte[] datas, boolean auto) {
        return writeFile(filePath, datas, auto, null);
    }

    /**
     * 上传文件
     * 
     * @param filePath
     *            文件路径（包含文件名）
     * @param datas
     *            文件内容
     * @param auto
     *            是否自动创建父级目录(最多10级)
     * @param params
     *            额外参数
     * 
     * @return true or false
     */
    public boolean writeFile(String filePath, byte[] datas, boolean auto, Map<String, String> params) {

        return HttpAction(METHOD_PUT, formatPath(filePath), datas, null, auto, params) != null;
    }

    /**
     * 上传文件
     * 
     * @param filePath
     *            文件路径（包含文件名）
     * @param String
     *            datas 文件内容
     * 
     * @return true or false
     */
    public boolean writeFile(String filePath, String datas) {
        return writeFile(filePath, datas, false, null);
    }

    /**
     * 上传文件
     * 
     * @param filePath
     *            文件路径（包含文件名）
     * @param String
     *            datas 文件内容
     * @param auto
     *            是否自动创建父级目录(最多10级)
     * 
     * @return true or false
     */
    public boolean writeFile(String filePath, String datas, boolean auto) {
        return writeFile(filePath, datas, auto, null);
    }

    /**
     * 上传文件
     * 
     * @param filePath
     *            文件路径（包含文件名）
     * @param String
     *            datas 文件内容
     * @param auto
     *            是否自动创建父级目录(最多10级)
     * @param params
     *            额外参数
     * 
     * @return true or false
     */
    public boolean writeFile(String filePath, String datas, boolean auto, Map<String, String> params) {

        boolean result = false;

        try {
            result = writeFile(filePath, datas.getBytes(UTF8), auto, params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 上传文件
     * 
     * @param filePath
     *            文件路径（包含文件名）
     * @param file
     *            待上传的文件
     * 
     * @return true or false
     * @throws IOException
     */
    public boolean writeFile(String filePath, File file) throws IOException {
        return writeFile(filePath, file, false, null);
    }

    /**
     * 上传文件
     * 
     * @param filePath
     *            文件路径（包含文件名）
     * @param file
     *            待上传的文件
     * @param auto
     *            是否自动创建父级目录(最多10级)
     * 
     * @return true or false
     * @throws IOException
     */
    public boolean writeFile(String filePath, File file, boolean auto) throws IOException {
        return writeFile(filePath, file, auto, null);
    }

    /**
     * 上传文件
     * 
     * @param filePath
     *            文件路径（包含文件名）
     * @param file
     *            待上传的文件
     * @param auto
     *            是否自动创建父级目录(最多10级)
     * @param params
     *            额外参数
     * 
     * @return true or false
     * @throws IOException
     */
    public boolean writeFile(String filePath, File file, boolean auto, Map<String, String> params) throws IOException {

        filePath = formatPath(filePath);

        InputStream is = null;
        OutputStream os = null;
        HttpURLConnection conn = null;

        try {
            // 读取待上传的文件
            is = new FileInputStream(file);

            // 获取链接
            URL url = new URL("http://" + apiDomain + filePath);
            conn = (HttpURLConnection) url.openConnection();

            // 设置必要参数
            conn.setConnectTimeout(timeout);
            conn.setRequestMethod(METHOD_PUT);
            conn.setUseCaches(false);
            conn.setDoOutput(true);

            // 设置时间
            conn.setRequestProperty(DATE, getGMTDate());
            // 设置签名
            conn.setRequestProperty(AUTHORIZATION, sign(conn, filePath, is.available()));

            // 设置文件的 MD5 参数
            if (!isEmpty(contentMD5)) {
                conn.setRequestProperty(CONTENT_MD5, contentMD5);
                contentMD5 = null;
            }

            // 设置文件的访问密钥
            if (!isEmpty(fileSecret)) {
                conn.setRequestProperty(CONTENT_SECRET, fileSecret);
                fileSecret = null;
            }

            // 是否自动创建父级目录
            if (auto) {
                conn.setRequestProperty(MKDIR, "true");
            }

            // 设置额外的参数，如图片缩略图等
            if (params != null && !params.isEmpty()) {

                for (Map.Entry<String, String> param : params.entrySet()) {
                    conn.setRequestProperty(param.getKey(), param.getValue());
                }
            }

            // 创建链接
            conn.connect();

            os = conn.getOutputStream();
            byte[] data = new byte[4096];
            int temp = 0;

            // 上传文件内容
            while ((temp = is.read(data)) != -1) {
                os.write(data, 0, temp);
            }

            // 获取返回的信息
            getText(conn, false);

            // 上传成功
            return true;

        } catch (IOException e) {
            if (debug) {
                e.printStackTrace();
            }

            // 上传失败
            return false;

        } finally {

            if (os != null) {
                os.close();
                os = null;
            }
            if (is != null) {
                is.close();
                is = null;
            }
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
    }

    /**
     * 读取文件
     * 
     * @param filePath
     *            文件路径（包含文件名）
     * 
     * @return 文件内容 或 null
     */
    public String readFile(String filePath) {
        return HttpAction(METHOD_GET, formatPath(filePath));
    }

    /**
     * 读取文件
     * 
     * @param filePath
     *            文件路径（包含文件名）
     * @param file
     *            临时文件
     * 
     * @return true or false
     */
    public boolean readFile(String filePath, File file) {

        String result = HttpAction(METHOD_GET, formatPath(filePath), null, file, false);

        return "".equals(result);
    }

    /**
     * 获取文件信息
     * 
     * @param filePath
     *            文件路径（包含文件名）
     * 
     * @return 文件信息 或 null
     */
    public Map<String, String> getFileInfo(String filePath) {

        HttpAction(METHOD_HEAD, formatPath(filePath));

        // 判断是否存在文件信息
        if (isEmpty(fileType) && isEmpty(fileSize) && isEmpty(fileDate)) {
            return null;
        }

        Map<String, String> mp = new HashMap<String, String>();
        mp.put("type", fileType);
        mp.put("size", fileSize);
        mp.put("date", fileDate);

        return mp;
    }

    /**
     * 删除文件
     * 
     * @param filePath
     *            文件路径（包含文件名）
     * 
     * @return true or false
     */
    public boolean deleteFile(String filePath) {

        return HttpAction(METHOD_DELETE, formatPath(filePath)) != null;
    }

    /**
     * 创建目录
     * 
     * @param path
     *            目录路径
     * 
     * @return true or false
     */
    public boolean mkDir(String path) {
        return mkDir(path, false);
    }

    /**
     * 创建目录
     * 
     * @param path
     *            目录路径
     * @param auto
     *            是否自动创建父级目录(最多10级)
     * 
     * @return true or false
     */
    public boolean mkDir(String path, boolean auto) {

        Map<String, String> params = new HashMap<String, String>(1);
        params.put(PARAMS.KEY_MAKE_DIR.getValue(), "true");

        String result = HttpAction(METHOD_PUT, formatPath(path), null, null, auto, params);

        return result != null;
    }

    /**
     * 读取目录列表
     * 
     * @param path
     *            目录路径
     * 
     * @return List<FolderItem> 或 null
     */
    public List<FolderItem> readDir(String path) {

        String result = HttpAction(METHOD_GET, formatPath(path) + SEPARATOR);

        if (isEmpty(result)) {
            return null;
        }

        List<FolderItem> list = new LinkedList<FolderItem>();

        String[] datas = result.split("\n");

        for (String data : datas) {
            if (data.indexOf("\t") > 0) {
                list.add(new FolderItem(data));
            }
        }
        return list;
    }

    /**
     * 删除目录
     * 
     * @param path
     *            目录路径
     * 
     * @return true or false
     */
    public boolean rmDir(String path) {
        return HttpAction(METHOD_DELETE, formatPath(path)) != null;
    }

    /**
     * 获取上传文件后的信息（仅图片空间有返回数据）
     * 
     * @param key
     *            信息字段名（x-upyun-width、x-upyun-height、x-upyun-frames、x-upyun-file
     *            -type）
     * 
     * @return value or NULL
     * @deprecated
     */
    @Deprecated
    public String getWritedFileInfo(String key) {

        if (isEmpty(picWidth)) {
            return null;
        }

        if (X_UPYUN_WIDTH.equals(key)) {
            return picWidth;
        }
        if (X_UPYUN_HEIGHT.equals(key)) {
            return picHeight;
        }
        if (X_UPYUN_FRAMES.equals(key)) {
            return picFrames;
        }
        if (X_UPYUN_FILE_TYPE.equals(key)) {
            return picType;
        }

        return null;
    }

    /**
     * 对字符串进行 MD5 加密
     * 
     * @param str
     *            待加密字符串
     * 
     * @return 加密后字符串
     */
    public static String md5(String str) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes(UTF8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        byte[] encodedValue = md5.digest();
        int j = encodedValue.length;
        char finalValue[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte encoded = encodedValue[i];
            finalValue[k++] = hexDigits[encoded >> 4 & 0xf];
            finalValue[k++] = hexDigits[encoded & 0xf];
        }

        return new String(finalValue);
    }

    /**
     * 对文件进行 MD5 加密
     * 
     * @param file
     *            待加密的文件
     * 
     * @return 文件加密后的 MD5 值
     * @throws IOException
     */
    public static String md5(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            int n = 0;
            byte[] buffer = new byte[1024];
            do {
                n = is.read(buffer);
                if (n > 0) {
                    md5.update(buffer, 0, n);
                }
            } while (n != -1);
            is.skip(0);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            is.close();
        }

        byte[] encodedValue = md5.digest();

        int j = encodedValue.length;
        char finalValue[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte encoded = encodedValue[i];
            finalValue[k++] = hexDigits[encoded >> 4 & 0xf];
            finalValue[k++] = hexDigits[encoded & 0xf];
        }

        return new String(finalValue);
    }

    /**
     * 获取 GMT 格式时间戳
     * 
     * @return GMT 格式时间戳
     */
    private String getGMTDate() {
        SimpleDateFormat formater = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        formater.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formater.format(new Date());
    }

    /**
     * 计算签名
     * 
     * @param conn
     *            连接
     * @param uri
     *            请求地址
     * @param length
     *            请求所发Body数据长度
     * 
     * @return 签名字符串
     */
    private String sign(HttpURLConnection conn, String uri, long length) {
        String sign = conn.getRequestMethod() + "&" + uri + "&" + conn.getRequestProperty(DATE) + "&" + length + "&"
                + password;
        return "UpYun " + userName + ":" + md5(sign);
    }

    /**
     * 连接处理逻辑
     * 
     * @param method
     *            请求方式 {GET, POST, PUT, DELETE}
     * @param uri
     *            请求地址
     * 
     * @return 请求结果（字符串）或 null
     */
    private String HttpAction(String method, String uri) {
        return HttpAction(method, uri, null, null, false);
    }

    /**
     * 连接处理逻辑
     * 
     * @param method
     *            请求方式 {GET, POST, PUT, DELETE}
     * @param uri
     *            请求地址
     * @param datas
     *            该请求所需发送数据（可为 null）
     * @param outFile
     *            文件描述符（可为 null）
     * @param auto
     *            自动创建父级目录(最多10级)
     * 
     * @return 请求结果（字符串）或 null
     */
    private String HttpAction(String method, String uri, byte[] datas, File outFile, boolean auto) {

        return HttpAction(method, uri, datas, outFile, auto, null);
    }

    /**
     * 连接处理逻辑
     * 
     * @param method
     *            请求方式 {GET, POST, PUT, DELETE}
     * @param uri
     *            请求地址
     * @param datas
     *            该请求所需发送数据（可为 null）
     * @param outFile
     *            文件描述符（可为 null）
     * @param auto
     *            自动创建父级目录(最多10级)
     * @param params
     *            额外参数
     * 
     * @return 请求结果（字符串）或 null
     */
    @SuppressWarnings("resource")
    private String HttpAction(String method, String uri, byte[] datas, File outFile, boolean auto,
            Map<String, String> params) {

        String result = null;

        HttpURLConnection conn = null;
        OutputStream os = null;
        InputStream is = null;

        try {
            // 获取链接
            URL url = new URL("http://" + apiDomain + uri);
            conn = (HttpURLConnection) url.openConnection();

            // 设置必要参数
            conn.setConnectTimeout(timeout);
            conn.setRequestMethod(method);
            conn.setUseCaches(false);
            conn.setDoOutput(true);

            // 设置时间
            conn.setRequestProperty(DATE, getGMTDate());

            // 是否自动创建父级目录
            if (auto) {
                conn.setRequestProperty(MKDIR, "true");
            }

            long contentLength = 0;
            if (datas == null) {
                conn.setRequestProperty(CONTENT_LENGTH, "0");
            } else {
                contentLength = datas.length;
                conn.setRequestProperty(CONTENT_LENGTH, String.valueOf(datas.length));

                // 设置文件的 MD5 参数
                if (!isEmpty(contentMD5)) {
                    conn.setRequestProperty(CONTENT_MD5, contentMD5);
                    contentMD5 = null;
                }
                // 设置文件的访问密钥
                if (!isEmpty(fileSecret)) {
                    conn.setRequestProperty(CONTENT_SECRET, fileSecret);
                    fileSecret = null;
                }
            }

            // 设置签名
            conn.setRequestProperty(AUTHORIZATION, sign(conn, uri, contentLength));

            // 是否是创建文件目录
            boolean isFolder = false;

            // 设置额外的参数，如图片缩略图等
            if (params != null && !params.isEmpty()) {

                isFolder = !isEmpty(params.get(PARAMS.KEY_MAKE_DIR.getValue()));

                for (Map.Entry<String, String> param : params.entrySet()) {
                    conn.setRequestProperty(param.getKey(), param.getValue());
                }
            }

            // 创建链接
            conn.connect();

            if (datas != null) {
                os = conn.getOutputStream();
                os.write(datas);
                os.flush();
            }

            if (isFolder) {
                os = conn.getOutputStream();
                os.flush();
            }

            if (outFile == null) {

                result = getText(conn, METHOD_HEAD.equals(method));

            } else {
                result = "";

                os = new FileOutputStream(outFile);
                byte[] data = new byte[4096];
                int temp = 0;

                is = conn.getInputStream();

                while ((temp = is.read(data)) != -1) {
                    os.write(data, 0, temp);
                }
            }
        } catch (IOException e) {
            if (debug) {
                e.printStackTrace();
            }

            // 操作失败
            return null;

        } finally {

            try {
                if (os != null) {
                    os.close();
                    os = null;
                }
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }

        return result;
    }

    /**
     * 获得连接请求的返回数据
     * 
     * @param conn
     * 
     * @return 字符串
     */
    private String getText(HttpURLConnection conn, boolean isHeadMethod) throws IOException {

        StringBuilder text = new StringBuilder();
        fileType = null;

        InputStream is = null;
        InputStreamReader sr = null;
        BufferedReader br = null;

        int code = conn.getResponseCode();

        try {
            is = code >= 400 ? conn.getErrorStream() : conn.getInputStream();

            if (!isHeadMethod) {
                sr = new InputStreamReader(is);
                br = new BufferedReader(sr);

                char[] chars = new char[4096];
                int length = 0;

                while ((length = br.read(chars)) != -1) {
                    text.append(chars, 0, length);
                }
            }
            if (200 == code && conn.getHeaderField(X_UPYUN_WIDTH) != null) {
                picWidth = conn.getHeaderField(X_UPYUN_WIDTH);
                picHeight = conn.getHeaderField(X_UPYUN_HEIGHT);
                picFrames = conn.getHeaderField(X_UPYUN_FRAMES);
                picType = conn.getHeaderField(X_UPYUN_FILE_TYPE);
            } else {
                picWidth = picHeight = picFrames = picType = null;
            }

            if (200 == code && conn.getHeaderField(X_UPYUN_FILE_TYPE) != null) {
                fileType = conn.getHeaderField(X_UPYUN_FILE_TYPE);
                fileSize = conn.getHeaderField(X_UPYUN_FILE_SIZE);
                fileDate = conn.getHeaderField(X_UPYUN_FILE_DATE);
            } else {
                fileType = fileSize = fileDate = null;
            }
        } finally {
            if (br != null) {
                br.close();
                br = null;
            }
            if (sr != null) {
                sr.close();
                sr = null;
            }
            if (is != null) {
                is.close();
                is = null;
            }
        }

        if (isHeadMethod) {
            if (code >= 400) {
                return null;
            }
            return "";
        }

        if (code >= 400) {
            throw new IOException(text.toString());
        }

        return text.toString();
    }

    /**
     * 判断字符串是否为空
     * 
     * @param str
     * @return 是否为空
     */
    private boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 格式化路径参数，去除前后的空格并确保以"/"开头，最后添加"/空间名"
     * <p>
     * 最终构成的格式："/空间名/文件路径"
     * 
     * @param path
     *            目录路径或文件路径
     * @return 格式化后的路径
     */
    private String formatPath(String path) {

        if (!isEmpty(path)) {

            // 去除前后的空格
            path = path.trim();

            // 确保路径以"/"开头
            if (!path.startsWith(SEPARATOR)) {
                return SEPARATOR + bucketName + SEPARATOR + path;
            }
        }

        return SEPARATOR + bucketName + path;
    }

    public class FolderItem {
        // 文件名
        public String name;

        // 文件类型 {file, folder}
        public String type;

        // 文件大小
        public long size;

        // 文件日期
        public Date date;

        public FolderItem(String data) {
            String[] a = data.split("\t");
            if (a.length == 4) {
                name = a[0];
                type = "N".equals(a[1]) ? "File" : "Folder";
                try {
                    size = Long.parseLong(a[2].trim());
                } catch (NumberFormatException e) {
                    size = -1;
                }
                long da = 0;
                try {
                    da = Long.parseLong(a[3].trim());
                } catch (NumberFormatException e) {
                }
                date = new Date(da * 1000);
            }
        }

        @Override
        public String toString() {
            return "time = " + date + "  size = " + size + "  type = " + type + "  name = " + name;
        }
    }

    /**
     * 其他额外参数的键值和参数值
     */
    public enum PARAMS {

        /**
         * 缩略图类型
         * <p>
         * 可选参数：<br>
         * 1)VALUE_FIX_MAX("fix_max")："限定最长边，短边自适应"<br>
         * 2)VALUE_FIX_WIDTH_OR_HEIGHT("fix_width_or_height")："限定宽度和高度"<br>
         * 3)VALUE_FIX_WIDTH("fix_width")："限定宽度，高度自适应"<br>
         * 4)VALUE_FIX_HEIGHT("fix_height")："限定高度，宽度自适应"<br>
         * 5)VALUE_SQUARE("square")："方块图，固定高固定宽"<br>
         * 6)VALUE_FIX_BOTH("fix_both")："固定宽度和高度"<br>
         */
        KEY_X_GMKERL_TYPE("x-gmkerl-type"),
        /**
         * 缩略图参数值
         */
        KEY_X_GMKERL_VALUE("x-gmkerl-value"),
        /**
         * 缩略图质量：图片压缩质量，默认 95
         */
        KEY_X_GMKERL_QUALITY("x-gmkerl-quality"),
        /**
         * 图片锐化：默认锐化（true）
         */
        KEY_X_GMKERL_UNSHARP("x-gmkerl-unsharp"),
        /**
         * 缩略图版本名称
         */
        KEY_X_GMKERL_THUMBNAIL("x-gmkerl-thumbnail"),
        /**
         * 图片旋转：只接受"auto"，"90"，"180"，"270"四种参数
         */
        KEY_X_GMKERL_ROTATE("x-gmkerl-rotate"),
        /**
         * 图片裁剪
         */
        KEY_X_GMKERL_CROP("x-gmkerl-crop"),

        /**
         * 创建目录
         */
        KEY_MAKE_DIR("folder"),

        /**
         * 缩略图类型之 "限定最长边，短边自适应"，参数为像素值，如: 150
         */
        VALUE_FIX_MAX("fix_max"),
        /**
         * 缩略图类型之 "限定宽度和高度"，参数为像素值，如: 150x130
         */
        VALUE_FIX_WIDTH_OR_HEIGHT("fix_width_or_height"),
        /**
         * 缩略图类型之 "限定宽度，高度自适应"，参数为像素值，如: 150
         */
        VALUE_FIX_WIDTH("fix_width"),
        /**
         * 缩略图类型之 "限定高度，宽度自适应"，参数为像素值，如: 150
         */
        VALUE_FIX_HEIGHT("fix_height"),
        /**
         * 缩略图类型之 "方块图，固定高固定宽"，参数为像素值，如: 150
         */
        VALUE_SQUARE("square"),
        /**
         * 缩略图类型之 "固定宽度和高度"，参数为像素值，如: 150x130
         */
        VALUE_FIX_BOTH("fix_both"),

        /**
         * 图片旋转之 "自动扶正"
         */
        VALUE_ROTATE_AUTO("auto"),
        /**
         * 图片旋转之 "旋转90度"
         */
        VALUE_ROTATE_90("90"),
        /**
         * 图片旋转之 "旋转180度"
         */
        VALUE_ROTATE_180("180"),
        /**
         * 图片旋转之 "旋转270度"
         */
        VALUE_ROTATE_270("270");

        private final String value;

        private PARAMS(String val) {
            value = val;
        }

        public String getValue() {
            return value;
        }

    }
}
