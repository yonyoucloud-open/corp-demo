package com.yonyou.iuap.corp.demo.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yonyou.iuap.corp.demo.crypto.SignHelper;
import com.yonyou.iuap.corp.demo.model.AccessTokenResponse;
import com.yonyou.iuap.corp.demo.model.GenericResponse;
import com.yonyou.iuap.corp.demo.utils.RequestTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AppController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppController.class);

    @Value("${app.key}")
    private String appKey;

    @Value("${app.secret}")
    private String appSecret;

    @Value("${open-api.url}")
    private String openApiUrl;

    @GetMapping("/getAccessToken")
    public AccessTokenResponse getAccessToken() throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        Map<String, String> params = new HashMap<>();
        // 除签名外的其他参数
        params.put("appKey", appKey);
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        // 计算签名
        String signature = SignHelper.sign(params, appSecret);
        params.put("signature", signature);

        // 请求
        String requestUrl = openApiUrl + "/open-auth/selfAppAuth/getAccessToken";
        GenericResponse<AccessTokenResponse> response = RequestTool.doGet(requestUrl, params, new TypeReference<GenericResponse<AccessTokenResponse>>() {});

        if (response.isSuccess()) {
            return response.getData();
        }

        LOGGER.error("请求开放平台接口失败，code: {}, message: {}", response.getCode(), response.getMessage());
        throw new RuntimeException("请求开放平台接口失败, code: " + response.getCode() + ", message: " + response.getMessage());
    }
}
