package com.rainer.cloudmall.common.constant;

import lombok.Getter;

public class ProductConstant {
    private ProductConstant() {

    }

    @Getter
    public enum AttrType {
        SALE(0, "销售属性"),
        BASE(1, "基本属性");
        private final int code;

        private final String msg;

        AttrType(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }
}
