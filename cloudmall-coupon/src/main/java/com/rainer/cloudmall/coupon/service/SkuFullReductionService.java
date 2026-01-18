package com.rainer.cloudmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rainer.cloudmall.common.utils.PageUtils;
import com.rainer.cloudmall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author StarRainer
 * @email estarrainer@gmail.com
 * @date 2026-01-14 14:39:14
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

