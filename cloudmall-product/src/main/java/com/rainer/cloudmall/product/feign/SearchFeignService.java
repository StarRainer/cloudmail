package com.rainer.cloudmall.product.feign;

import com.rainer.cloudmall.common.to.es.SkuEsModel;
import com.rainer.cloudmall.common.utils.FeignResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "cloudmall-search")
public interface SearchFeignService {
    @PostMapping("/search/product/publish")
    FeignResult<Void> publishProduct(@RequestBody List<SkuEsModel> skuEsModels);
}
