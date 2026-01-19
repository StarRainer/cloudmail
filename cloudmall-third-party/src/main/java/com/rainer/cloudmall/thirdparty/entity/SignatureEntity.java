package com.rainer.cloudmall.thirdparty.entity;

import lombok.Data;

@Data
public class SignatureEntity {
    private String dir;

    private String host;

    private String policy;

    private String securityToken;

    private String signature;

    private String xOssCredential;

    private String xOssDate;

    private String xOssSignatureVersion;
}
