package com.rainer.cloudmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rainer.cloudmall.common.utils.PageUtils;
import com.rainer.cloudmall.order.entity.MqMessageEntity;

import java.util.Map;

/**
 * 
 *
 * @author StarRainer
 * @email estarrainer@gmail.com
 * @date 2026-01-14 14:37:12
 */
public interface MqMessageService extends IService<MqMessageEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

