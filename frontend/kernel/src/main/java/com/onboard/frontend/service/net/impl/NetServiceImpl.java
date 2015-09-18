package com.onboard.frontend.service.net.impl;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.onboard.frontend.exception.BadRequestException;
import com.onboard.frontend.exception.InternalException;
import com.onboard.frontend.exception.NoLoginException;
import com.onboard.frontend.exception.NoPermissionException;
import com.onboard.frontend.exception.ResourceNotFoundException;
import com.onboard.frontend.model.User;
import com.onboard.frontend.service.net.NetService;
import com.onboard.frontend.service.web.SessionService;

/**
 * Created by XingLiang on 2015/4/23.
 */
@Service
public class NetServiceImpl implements NetService {

    public static final int TIME_OUT = 30000;
    public static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(NetServiceImpl.class);

    private final static String SCHEME = "http";

    @Value("${api.domain}")
    private String domain;

    @Value("${api.path}")
    private String apiUri;

    @Value("${api.protocol}")
    private String protocol;

    @Autowired
    private SessionService sessionService;

    private RequestConfig requestConfig;

    public RestTemplate restTemplate;

    public String getUrl(String uri) {
        return String.format("%s%s%s%s", protocol, domain, apiUri, uri);
    }

    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        restTemplate.getMessageConverters().add(new ResourceHttpMessageConverter());
        requestConfig = RequestConfig.custom().setSocketTimeout(TIME_OUT).setConnectTimeout(TIME_OUT).build();
    }

    // public <T> T getForObjectWithoutAuth(String uri, Class<T> returnType) throws RestClientException {
    // String url = getUrl(uri);
    // HttpHeaders headers = getHeader();
    // logger.info(url);
    // HttpEntity<T> httpEntity = new HttpEntity<T>(headers);
    // T data = restTemplate.exchange(url, HttpMethod.GET, httpEntity, returnType).getBody();
    // return data;
    // }

    public <K, V> V postForFormObject(String uri, K object, Class<V> returnType) throws RestClientException {
        String url = getUrl(uri);
        logger.info(url);
        HttpEntity<K> httpEntity = new HttpEntity<K>(object, getHeader());
        try {
            V data = restTemplate.exchange(url, HttpMethod.POST, httpEntity, returnType).getBody();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public <T> T getForObject(String uri, Class<T> returnType) throws RestClientException {
        String url = getUrl(uri);
        HttpEntity<T> httpEntity = new HttpEntity<T>(getHeader());
        T data = null;
        try {
            data = restTemplate.exchange(url, HttpMethod.GET, httpEntity, returnType).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public <T> HttpEntity<T> getForEntity(String uri, Class<T> returnType) throws RuntimeException, InternalException {
        String url = getUrl(uri);
        HttpEntity<T> httpEntity = new HttpEntity<T>(getHeader());
        HttpEntity<T> data = null;
        try {
            data = restTemplate.exchange(url, HttpMethod.GET, httpEntity, returnType);
        } catch (HttpStatusCodeException e) {
            e.printStackTrace();
        }
        return data;
    }

    public <T> T postForObject(String uri, T object, Class<T> returnType) throws RestClientException {
        String url = getUrl(uri);
        HttpEntity<T> httpEntity = new HttpEntity<T>(object, getHeader());
        T data = null;
        try {
            data = restTemplate.exchange(url, HttpMethod.POST, httpEntity, returnType).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public <T> T putForObject(String uri, T object, Class<T> returnType) throws RestClientException {
        HttpEntity<T> httpEntity = new HttpEntity<T>(object, getHeader());
        String url = getUrl(uri);
        return restTemplate.exchange(url, HttpMethod.PUT, httpEntity, returnType).getBody();
    }

    public <T> T deleteForObject(String uri, Class<T> returnType) throws RestClientException {
        HttpEntity<T> httpEntity = new HttpEntity<T>(getHeader());
        String url = getUrl(uri);
        return restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, returnType).getBody();
    }

    // private HttpHeaders getHeader() {
    // HttpHeaders headers = new HttpHeaders();
    // headers.setContentType(MediaType.APPLICATION_JSON);
    // return headers;
    // }

    private HttpHeaders getHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers = addAuthToHeader(headers);
        return headers;
    }

    private HttpHeaders addAuthToHeader(HttpHeaders headers) {
        String authHeader = getAuthorizationValue();
        if (authHeader != null) {
            return headers;
        }
        headers.add("Authorization", authHeader);
        return headers;
    }

    private String getAuthorizationValue() {
        User user = sessionService.getCurrentUser();

        if (user == null) {
            return null;
        }
        String auth = user.getEmail() + ":" + "";
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        return authHeader;
    }

    public String getRedirectUrl(String originUri) {
        String url = String.format("http://%s%s", domain, originUri);
        return url;
    }

    private URI getUriFromRequest(HttpServletRequest request) throws Exception {
        String originUri = request.getRequestURI();
        URIBuilder uriBuilder = new URIBuilder().setScheme(SCHEME).setHost(domain).setPath(originUri);
        Map<String, String[]> params = request.getParameterMap();
        for (String key : params.keySet()) {
            String[] paramValues = params.get(key);
            for (String value : paramValues) {
                uriBuilder.addParameter(key, value);
            }
        }
        return uriBuilder.build();
    }

    private StringEntity getRequestBodyEntity(HttpServletRequest request) throws IOException {
        Scanner scanner = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
        String result = scanner.hasNext() ? scanner.next() : "";
        return new StringEntity(result, ContentType.create(MediaType.APPLICATION_JSON_VALUE, "UTF-8"));
    }

    private void handleError(CloseableHttpClient client, CloseableHttpResponse response) throws InternalException,
            RuntimeException {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 404) {
            throw new ResourceNotFoundException();
        } else if (statusCode == 400) {
            throw new BadRequestException();
        } else if (statusCode == 401) {
            throw new NoLoginException();
        } else if (statusCode == 403) {
            throw new NoPermissionException();
        } else if (statusCode == 500) {
            throw new InternalException(processResponseToString(client, response));
        }
    }

    private CloseableHttpResponse sendRequest(CloseableHttpClient client, HttpRequestBase requestBase) throws RuntimeException,
            InternalException {
        CloseableHttpResponse response = null;
        try {
            response = client.execute(requestBase);
            handleError(client, response);

        } catch (ClientProtocolException e) {
            logger.info(e.getStackTrace().toString());
            e.printStackTrace();
        } catch (IOException e) {
            logger.info(e.getStackTrace().toString());
        }
        return response;
    }

    private String processResponseToString(CloseableHttpClient httpClient, CloseableHttpResponse response) {
        if (response == null) {
            return null;
        }
        try {
            org.apache.http.HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                return EntityUtils.toString(httpEntity);
            } else {
                return null;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        finally {
            try {

                response.close();
                httpClient.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private byte[] processResponseTobyte(CloseableHttpClient httpClient, CloseableHttpResponse response) {
        if (response == null) {
            return null;
        }
        try {
            org.apache.http.HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                return EntityUtils.toByteArray(httpEntity);
            } else {
                return null;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        finally {
            try {

                response.close();
                httpClient.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String excuteForString(HttpRequestBase requestBase) throws RuntimeException, InternalException {
        String authValue = getAuthorizationValue();
        if (authValue != null) {
            requestBase.addHeader("Authorization", authValue);
        }
        requestBase.setConfig(requestConfig);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = sendRequest(httpClient, requestBase);

        return processResponseToString(httpClient, response);
    }

    public String getForJson(HttpServletRequest request) throws RuntimeException, InternalException {
        HttpGet httpGet = null;
        URI uri = null;
        try {
            uri = getUriFromRequest(request);
            httpGet = new HttpGet(uri);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return excuteForString(httpGet);
    }

    public HttpEntity<byte[]> getForEntity(HttpServletRequest request) throws RuntimeException, InternalException {
        HttpGet httpGet = null;
        URI uri = null;
        try {
            uri = getUriFromRequest(request);
            httpGet = new HttpGet(uri);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        String authValue = getAuthorizationValue();
        if (authValue != null) {
            httpGet.addHeader("Authorization", authValue);
        }

        httpGet.setConfig(requestConfig);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = sendRequest(httpClient, httpGet);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        for (org.apache.http.Header header : response.getAllHeaders()) {
            map.add(header.getName(), header.getValue());
        }
        return new HttpEntity<byte[]>(processResponseTobyte(httpClient, response), map);
    }

    public String postForJson(HttpServletRequest request) throws RuntimeException, InternalException {
        HttpPost httpPost = null;
        URI uri = null;
        try {
            String originUri = request.getRequestURI();
            URIBuilder uriBuilder = new URIBuilder().setScheme(SCHEME).setHost(domain).setPath(originUri);
            uri = uriBuilder.build();
            httpPost = new HttpPost(uri);
            StringEntity entity = getRequestBodyEntity(request);
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

            if (0 == entity.getContentLength() && request.getContentType().contains("multipart")) {
                Collection<Part> parts = request.getParts();
                for (Part part : parts) {
                    multipartEntityBuilder
                            .addBinaryBody(part.getName(), part.getInputStream(), ContentType.create(part.getContentType()),
                                    part.getSubmittedFileName()).setCharset(Charset.forName("UTF-8"))
                            .setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    httpPost.setEntity(multipartEntityBuilder.build());
                }
            } else {
                httpPost.setEntity(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return excuteForString(httpPost);
    }

    public String putForJson(HttpServletRequest request) throws RuntimeException, InternalException {
        HttpPut httpPut = null;
        URI uri = null;
        try {
            String originUri = request.getRequestURI();
            URIBuilder uriBuilder = new URIBuilder().setScheme(SCHEME).setHost(domain).setPath(originUri);
            uri = uriBuilder.build();
            httpPut = new HttpPut(uri);
            httpPut.setEntity(getRequestBodyEntity(request));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return excuteForString(httpPut);
    }

    public String deleteForJson(HttpServletRequest request) throws RuntimeException, InternalException {
        HttpDelete httpDelete = null;
        URI uri = null;
        try {

            uri = getUriFromRequest(request);
            httpDelete = new HttpDelete(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return excuteForString(httpDelete);
    }

    public <T> T getForObject(String uri, Class<T> returnType, Object... urlVariables) throws RestClientException {
        // String url = getUrl(uri);
        // HttpEntity<T> httpEntity = new HttpEntity<T>(getAuthHeader());

        String url = getUrl(uri);
        HttpHeaders headers = getHeader();
        logger.info(url);
        HttpEntity<T> httpEntity = new HttpEntity<T>(headers);
        T data = null;
        try {
            data = restTemplate.exchange(url, HttpMethod.GET, httpEntity, returnType, urlVariables).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
