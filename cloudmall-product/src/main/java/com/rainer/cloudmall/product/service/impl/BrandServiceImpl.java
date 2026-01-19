package com.rainer.cloudmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rainer.cloudmall.common.utils.PageUtils;
import com.rainer.cloudmall.common.utils.Query;
import com.rainer.cloudmall.product.dao.BrandDao;
import com.rainer.cloudmall.product.entity.BrandEntity;
import com.rainer.cloudmall.product.service.BrandService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");

        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                new LambdaQueryWrapper<BrandEntity>()
                        .eq(key.matches("^\\d+$"), BrandEntity::getBrandId, key)
                        .or()
                        .like(BrandEntity::getName, key)
        );

        return new PageUtils(page);
    }

}