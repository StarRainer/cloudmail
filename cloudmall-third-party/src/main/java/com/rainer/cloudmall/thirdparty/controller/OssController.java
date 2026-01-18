package com.rainer.cloudmall.thirdparty.controller;

import com.rainer.cloudmall.common.utils.Result;
import com.rainer.cloudmall.thirdparty.service.OssService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("oss")
public class OssController {

    private final OssService ossService;

    public OssController(OssService ossService) {
        this.ossService = ossService;
    }

    @GetMapping("/signature")
    public Result getPostSignatureForOssUpload() throws Exception {
        return Result.ok().put("data", ossService.getPostSignature());
    }
}
