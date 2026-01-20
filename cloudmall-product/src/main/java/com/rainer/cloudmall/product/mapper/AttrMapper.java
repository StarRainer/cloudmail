package com.rainer.cloudmall.product.mapper;

import com.rainer.cloudmall.product.entity.AttrEntity;
import com.rainer.cloudmall.product.vo.AttrShowVo;
import com.rainer.cloudmall.product.vo.AttrVo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttrMapper {

    AttrEntity attrVoToAttrEntity(AttrVo attrVo);

    AttrShowVo attrEntityToAttrShowVo(AttrEntity attrVo);
}
