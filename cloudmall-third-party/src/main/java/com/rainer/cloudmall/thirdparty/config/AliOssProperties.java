package com.rainer.cloudmall.thirdparty.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "alibaba.cloud")
public class AliOssProperties {
    private String accessKeyId;

    private String accessKeySecret;

    private String endpoint;

    private String roleArn;

    private String bucket;

    private String region;

    private Long expireTime;

    public String getHost() {
        return "https://" + bucket + "." + endpoint;
    }
}
