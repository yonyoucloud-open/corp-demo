# 开放平台自建应用接入 Demo

## 获取 `access_token`

调用接口令牌 `access_token` 是自建应用调用开放平台业务接口的凭证，有效期为2小时。

**接口地址:** `https://open.yonyoucloud.com/open-auth/selfAppAuth/getAccessToken`

**请求方式:** `GET`

**请求参数:**

|字段|类型|说明|
|---|---|:---|
|appKey|string|应用 appKey|
|timestamp|number long|unix timestamp, 毫秒时间戳|
|signature|string|校验签名，HmacSHA256，加签方式看下文|

其中，签名字段 `signature` 计算使用 `HmacSHA256`，具体计算方式如下：

```
URLEncode( Base64( HmacSHA256( parameterMap ) ) )
```

其中，`parameterMap` 按照参数名称排序，参数名称与参数值依次拼接(signature字段除外)，形成待计算签名的字符串。之后对 `parameterMap`
使用 `HmacSHA256` 计算签名，`Hmac` 的 `key` 为自建应用的 `appSecret` 。计算出的二进制签名先进行 `base64`，之后进行 `urlEncode`，
即得到 `signatrue` 字段的值。

**示例请求:**

```
GET https://open.yonyoucloud.com/open-auth/selfAppAuth/getAccessToken?appKey=fbb5f5b6-21fb-4156-8b73-3ec3ac389ab7&timestamp=1547192727928&signature=7OzDhux%2FyJQt%2B9K2GK4E8YuX%2Fl30NYcnsbEO8D%2F0jCc%3D
```

**返回参数:**

返回数据格式为 `json`，字段说明如下：

|字段|类型|说明|
|---|---|:---|
|code|string|结果码，正确返回 "00000"|
|message|string|结果信息，若有错误该字段会返回具体错误信息|
|data.access_token|string|调用接口令牌 `access_token` |
|data.expire|number int|有效期，单位秒 |


**示例返回:**

```json
{
  "code": "00000",
  "message": "成功！",
  "data": {
    "access_token": "2215d3c77b684876980ff15f9a0df6c7",
    "expire": 7200
  }
}
```

**示例 Demo:** [AppController](src/main/java/com/yonyou/iuap/corp/demo/web/AppController.java)。

## 事件推送接收

开发者需要实现事件推送接口，在开启事件订阅后，开放平台在订阅数据变更后会调用开发者的事件推送接口来推送事件。为了保证用户信息的安全性，开放平台推送的数据是经过加密和加签的，开发者需要实现相关的加解密算法。开发者在收到事件后，可以根据事件变更id进行相关业务操作（推荐异步处理，推送超时5秒）。确认收到事件后，需返回加密加签后的“success”，通知事件处理成功。否则开放平台会尝试重试推送，对于超过24小时的推送失败的事件，开放平台将不再推送。

### 推送格式

开放平台推送的事件会进行加密、加签后进行投递，加密加签后以 `json` 形式 `POST` 到自建应用的 `事件订阅回调地址`，投递格式如下：

|字段|类型|说明|必填|
|----|----|----|----|
|msgSignature|string|签名|Y|
|timestamp|number|unix timestamp|Y|
|nonce|string|随机值|Y|
|encrypt|string|加密消息体|Y|

**示例请求:**

```json
{
  "msgSignature": "2ff5a94ca2dd9376c8dcebde690b1b8e94741ec5",
  "timestamp": 1530862251583,
  "nonce": "uM48M4qajlEtVCz4",
  "encrypt": "9Mo8oaTFKJAdK3wnM2gS9RJxt0febE/fFJF1vhKbcPmdljs44OwHlW96qj2hkOcHQ7gneqsyx8VN4HSdbwu5z/ibhZqTjY/RkjfE+lB1LjlPDTQNl1hh+VimCIl4W4m1RRySeRxSbLikvimfswkJ6fkBj97eRRjeS26079lK8oRke5vmMh5NjWY4Rq5iuvHwInfz8qCalcgJTT/2C37wJA=="
}
```

* "encrypt" 字段解密之后即为投递的事件 `json` 消息
* 解密验签算法与授权流程中数据推送的解密验签算法完全一致
* 加密方式 `encrypt = Base64 ( AES ( message ))`
* 签名计算方式 `msgSignature = SHA1( sort (appSecret, timestamp, nonce, encrypt))`，其中 `SHA1` 计算后取 16 进制字符串，全小写。
* 加密加签用到的 `appSecret`、`AES key` 同授权数据
* **注意：** AES 过程中，由于密钥长度限制，需要替换 `[JRE_HOME]/lib/security/` 下的部分包，详见 `http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html`


### 事件格式

上述 "encrypt" 字段解密之后即为投递的事件消息，事件以 `json` 格式发送，定义如下：

|字段|类型|说明|必填|
|----|----|----|----|
|type|string|事件类型|Y|
|eventId|string|事件 uuid|Y|
|timestamp|number|unix timestamp|Y|
|tenantId|string|租户 id|Y|
|staffId|string array|事件变更的 staffId list|N|
|deptId|string array|事件变更的 deptId list|N|
|userId|string array|事件变更的 userId list|N|

示例事件如下
```json
{
  "type":"STAFF_ADD",
  "timestamp":1529999656469,
  "tenantId":"abcde859",
  "eventId":"033af2b1-96c0-4cc2-8991-3abe42aa3d0b",
  "staffId":["abcde859-d853-4f57-896c-6658c5920e25"]
}
```

* 非必填项若为空 `json` 中会无此字段，而非 `null`
* `type` 事件类型请参考如下事件类型

### 事件类型

|事件类型 | 说明| id 字段|
|:---|:--- |:---|
|CHECK_URL| 检查回调地址||
|STAFF_ADD | 员工增加|staffId|
|STAFF_UPDATE | 员工更改|staffId|
|STAFF_ENABLE | 员工启用 |staffId|
|STAFF_DISABLE | 员工停用 |staffId|
|DEPT_ADD | 部门创建|deptId|
|DEPT_UPDATE | 部门修改|deptId
|DEPT_ENABLE | 部门启用 |deptId|
|DEPT_DISABLE | 部门停用 |deptId|
|DEPT_DELETE | 部门删除 |deptId|
|USER_ADD | 用户增加 |userId|
|USER_DELETE | 用户删除 |userId|

* `CHECK_URL` 是开放平台在进行事件订阅时确定开发者填写的回调地址有效性时推送的
* 不同的事件推送的变更 id 列表字段是不同的，请参考上表的 `id 字段`

**示例 Demo:** [PushController](src/main/java/com/yonyou/iuap/corp/demo/web/PushController.java)。