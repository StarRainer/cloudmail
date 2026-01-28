package com.rainer.cloudmall.search.controller;

import com.rainer.cloudmall.common.to.es.SkuEsModel;
import com.rainer.cloudmall.common.utils.FeignResult;
import com.rainer.cloudmall.search.service.ProductService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/search/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/publish")
    public FeignResult<Void> publishProduct(@RequestBody List<SkuEsModel> skuEsModels) throws IOException {
        productService.publishProduct(skuEsModels);
        return FeignResult.success();
    }
}
