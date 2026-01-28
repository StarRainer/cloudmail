package com.rainer.cloudmall.ware.dao;

import com.rainer.cloudmall.ware.dto.StockDTO;
import com.rainer.cloudmall.ware.entity.PurchaseDetailEntity;
import com.rainer.cloudmall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 * 
 * @author StarRainer
 * @email estarrainer@gmail.com
 * @date 2026-01-14 14:41:44
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("wareSkuEntities") List<WareSkuEntity> wareSkuEntities);

    List<StockDTO> selectStockBySkuIds(@Param("skuIds") List<Long> skuIds);
}
