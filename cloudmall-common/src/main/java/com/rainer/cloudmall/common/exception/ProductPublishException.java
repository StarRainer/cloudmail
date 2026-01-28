package com.rainer.cloudmall.common.exception;

import com.rainer.cloudmall.common.exception.code.ProductResponseCode;

public class ProductPublishException extends BaseException {
    public ProductPublishException() {
        super(ProductResponseCode.PRODUCT_PUBLISH_EXCEPTION.getCode(), ProductResponseCode.PRODUCT_PUBLISH_EXCEPTION.getMessage());
    }
}
