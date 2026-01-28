package com.rainer.cloudmall.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ProductConstant {
    private ProductConstant() {

    }

    @Getter
    @AllArgsConstructor
    public enum AttrType {
        SALE(0, "销售属性"),
        BASE(1, "基本属性");
        private final int code;

        private final String msg;
    }

    @Getter
    @AllArgsConstructor
    public enum PublishStatus {
        UNPUBLISHED(0, "已下架"),
        PUBLISHED(1, "已上架");
        private final int code;

        private final String msg;
    }
}
