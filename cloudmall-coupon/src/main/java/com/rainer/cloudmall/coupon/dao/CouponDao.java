package com.rainer.cloudmall.coupon.dao;

import com.rainer.cloudmall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author StarRainer
 * @email estarrainer@gmail.com
 * @date 2026-01-14 14:39:15
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
