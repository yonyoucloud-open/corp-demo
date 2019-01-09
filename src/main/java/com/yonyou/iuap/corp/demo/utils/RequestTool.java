package com.yonyou.iuap.corp.demo.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

public class RequestTool {

    private static final String HEADER_CONTENT_JSON = "application/json";

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static PoolingHttpClientConnectionManager cm = null;

    private static ObjectMapper mapper = new ObjectMapper();

    private static CloseableHttpClient httpClient;

    /**
     * 记录开放平台请求结果
     */
    public static class Response {
        /**
         * 该请求的 http 状态码
         * 200 为正常的返回结果
         */
        private int status;

        /**
         * 请求返回消息
         * 当 status == 200 时会返回 response body 中的字符串
         * 当 status !== 200 时会返回具体的错误信息
         */
        private String result;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }

    static{
        cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(500);
        cm.setDefaultMaxPerRoute(50);

        RequestConfig globalConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(1000)         // 连接池获取连接超时
                .setConnectTimeout(1000)                   // 连接建立超时
                .setSocketTimeout(5000)                    // 等待响应超时
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .build();

        httpClient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(globalConfig).build();
    }

    private static CloseableHttpClient getHttpClient(){
        return httpClient;
    }

    public static <T> T doGet(String requestUrl, Map<String, String> paramMap, Class<T> type) throws IOException {
        return mapper.readValue(doGet(requestUrl, paramMap), type);
    }

    public static <T> T doGet(String requestUrl, Map<String, String> paramMap, TypeReference<T> typeReference) throws IOException {
        return mapper.readValue(doGet(requestUrl, paramMap), typeReference);
    }

    public static String doGet(String requestUrl, Map<String, String> paramMap) throws IOException {
        CloseableHttpClient httpClient = getHttpClient();
        StringBuilder param = new StringBuilder("?");
        if (paramMap != null) {
            for(Map.Entry<String, String> entry: paramMap.entrySet()) {
                param.append(entry.getKey());
                param.append("=");
                param.append(entry.getValue());
                param.append("&");
            }
            param.deleteCharAt(param.length() - 1);
        }

        HttpGet get = new HttpGet(requestUrl + param);
        String responseString = httpClient.execute(get, response -> EntityUtils.toString(response.getEntity()));
        get.releaseConnection();
        return responseString;
    }
}
