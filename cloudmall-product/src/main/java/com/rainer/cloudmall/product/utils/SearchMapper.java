package com.rainer.cloudmall.product.utils;

import com.rainer.cloudmall.common.to.es.SkuEsModel;
import com.rainer.cloudmall.product.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SearchMapper {
    SkuEsModel.Attrs toAttr(ProductAttrValueEntity attrValueEntity);

    List<SkuEsModel.Attrs> toAttrs(List<ProductAttrValueEntity> attrValueEntities);

    @Mapping(target = "hasStock", source = "hasStock")
    @Mapping(target = "hotScore", constant = "0L")
    @Mapping(target = "catalogName", source = "categoryEntity.name")
    @Mapping(target = "catalogId", source = "skuInfoEntity.catalogId")
    @Mapping(target = "brandId", source = "skuInfoEntity.brandId")
    @Mapping(target = "brandImg", source = "brandEntity.logo")
    @Mapping(target = "brandName", source = "brandEntity.name")
    @Mapping(target = "skuPrice", source = "skuInfoEntity.price")
    @Mapping(target = "skuImg", source = "skuInfoEntity.skuDefaultImg")
    SkuEsModel toSkuEsModel(SkuInfoEntity skuInfoEntity, BrandEntity brandEntity, CategoryEntity categoryEntity, Boolean hasStock, List<SkuEsModel.Attrs> attrs);
}
