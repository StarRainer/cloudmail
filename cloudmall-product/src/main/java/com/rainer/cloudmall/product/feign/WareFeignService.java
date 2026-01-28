package com.rainer.cloudmall.product.feign;

import com.rainer.cloudmall.common.utils.FeignResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "cloudmall-ware")
public interface WareFeignService {
    @PostMapping("/ware/waresku/hasstock")
    FeignResult<Map<Long, Boolean>> checkHasStock(@RequestBody List<Long> skuIds);
}
