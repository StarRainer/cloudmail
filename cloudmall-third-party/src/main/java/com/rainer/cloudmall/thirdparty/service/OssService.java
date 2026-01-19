package com.rainer.cloudmall.thirdparty.service;

import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.sts20150401.Client;
import com.aliyun.sts20150401.models.AssumeRoleRequest;
import com.aliyun.sts20150401.models.AssumeRoleResponse;
import com.aliyun.sts20150401.models.AssumeRoleResponseBody;
import com.aliyun.teautil.models.RuntimeOptions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rainer.cloudmall.common.utils.TimeUtils;
import com.rainer.cloudmall.thirdparty.config.AliOssProperties;
import com.rainer.cloudmall.thirdparty.entity.SignatureEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OssService {
    private final AliOssProperties aliOssProperties;

    private final Client client;

    public OssService(AliOssProperties aliOssProperties, Client client) {
        this.aliOssProperties = aliOssProperties;
        this.client = client;
    }

    public SignatureEntity getPostSignature() throws Exception {
        AssumeRoleResponseBody.AssumeRoleResponseBodyCredentials credentials = getCredential();

        String accessKeyId =  credentials.accessKeyId;
        String accessKeySecret =  credentials.accessKeySecret;
        String securityToken =  credentials.securityToken;
        String region = aliOssProperties.getRegion();

        // TODO: 改进文件前缀的生成逻辑
        String uploadDir = LocalDate.now().toString() + "/";

        //获取 x-oss-credential 里的date，当前日期，格式为yyyyMMdd
        ZonedDateTime today = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = today.format(formatter);

        //获取x-oss-date
        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC);
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
        String xOssDate = now.format(formatter2);

        // 1. 创建policy。
        String xOssCredential = accessKeyId + "/" + date + "/" + region + "/oss/aliyun_v4_request";

        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> policy = new HashMap<>();
        policy.put("expiration", TimeUtils.generateExpiration(aliOssProperties.getExpireTime()));

        List<Object> conditions = new ArrayList<>();

        Map<String, String> bucketCondition = new HashMap<>();
        bucketCondition.put("bucket", aliOssProperties.getBucket());
        conditions.add(bucketCondition);

        Map<String, String> securityTokenCondition = new HashMap<>();
        securityTokenCondition.put("x-oss-security-token", securityToken);
        conditions.add(securityTokenCondition);

        Map<String, String> signatureVersionCondition = new HashMap<>();
        signatureVersionCondition.put("x-oss-signature-version", "OSS4-HMAC-SHA256");
        conditions.add(signatureVersionCondition);

        Map<String, String> credentialCondition = new HashMap<>();
        credentialCondition.put("x-oss-credential", xOssCredential);
        conditions.add(credentialCondition);

        Map<String, String> dateCondition = new HashMap<>();
        dateCondition.put("x-oss-date", xOssDate);
        conditions.add(dateCondition);

        conditions.add(Arrays.asList("content-length-range", 1, 10240000));
        conditions.add(Arrays.asList("eq", "$success_action_status", "200"));
        conditions.add(Arrays.asList("starts-with", "$key", uploadDir));

        policy.put("conditions", conditions);

        String jsonPolicy = mapper.writeValueAsString(policy);

        // 2. 构造待签名字符串（StringToSign）。
        String stringToSign = Base64.getEncoder().encodeToString(jsonPolicy.getBytes(StandardCharsets.UTF_8));

        // 3. 计算SigningKey。
        byte[] dateKey = hmacsha256(("aliyun_v4" + accessKeySecret).getBytes(), date);
        byte[] dateRegionKey = hmacsha256(dateKey, region);
        byte[] dateRegionServiceKey = hmacsha256(dateRegionKey, "oss");
        byte[] signingKey = hmacsha256(dateRegionServiceKey, "aliyun_v4_request");

        // 4. 计算Signature。
        byte[] result = hmacsha256(signingKey, stringToSign);
        String signature = BinaryUtil.toHex(result);

        SignatureEntity signatureEntity = new SignatureEntity();
        signatureEntity.setDir(uploadDir);
        signatureEntity.setHost(aliOssProperties.getHost());
        signatureEntity.setPolicy(stringToSign);
        signatureEntity.setSecurityToken(securityToken);
        signatureEntity.setSignature(signature);
        signatureEntity.setXOssCredential(xOssCredential);
        signatureEntity.setXOssDate(xOssDate);
        signatureEntity.setXOssSignatureVersion("OSS4-HMAC-SHA256");
        return signatureEntity;
    }

    /**
     * 获取STS临时凭证
     * @return AssumeRoleResponseBodyCredentials 对象
     */
    private AssumeRoleResponseBody.AssumeRoleResponseBodyCredentials getCredential() throws Exception {
        AssumeRoleRequest assumeRoleRequest = new AssumeRoleRequest()
                .setRoleArn(aliOssProperties.getRoleArn())
                .setRoleSessionName("oss-upload");
        RuntimeOptions runtime = new RuntimeOptions();

        // 复制代码运行请自行打印 API 的返回值
        AssumeRoleResponse response = client.assumeRoleWithOptions(assumeRoleRequest, runtime);

        // credentials里包含了后续要用到的AccessKeyId、AccessKeySecret和SecurityToken
        return response.body.credentials;
    }

    private byte[] hmacsha256(byte[] key, String data) throws InvalidKeyException, NoSuchAlgorithmException {
        // 初始化HMAC密钥规格，指定算法为HMAC-SHA256并使用提供的密钥。
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");

        // 获取Mac实例，并通过getInstance方法指定使用HMAC-SHA256算法。
        Mac mac = Mac.getInstance("HmacSHA256");

        // 使用密钥初始化Mac对象。
        mac.init(secretKeySpec);

        // 执行HMAC计算，通过doFinal方法接收需要计算的数据并返回计算结果的数组。
        return mac.doFinal(data.getBytes());
    }
}
