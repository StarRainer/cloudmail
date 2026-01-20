package com.rainer.cloudmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rainer.cloudmall.product.entity.AttrAttrgroupRelationEntity;
import com.rainer.cloudmall.product.mapper.AttrMapper;
import com.rainer.cloudmall.product.service.AttrAttrgroupRelationService;
import com.rainer.cloudmall.product.service.AttrGroupService;
import com.rainer.cloudmall.product.service.CategoryService;
import com.rainer.cloudmall.product.vo.AttrShowVo;
import com.rainer.cloudmall.product.vo.AttrVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rainer.cloudmall.common.utils.PageUtils;
import com.rainer.cloudmall.common.utils.Query;

import com.rainer.cloudmall.product.dao.AttrDao;
import com.rainer.cloudmall.product.entity.AttrEntity;
import com.rainer.cloudmall.product.service.AttrService;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    private final AttrGroupService attrGroupService;

    private final CategoryService categoryService;

    private final AttrAttrgroupRelationService attrAttrgroupRelationService;

    private final AttrMapper attrMapper;

    public AttrServiceImpl(AttrGroupService attrGroupService,
                           CategoryService categoryService,
                           AttrAttrgroupRelationService attrAttrgroupRelationService,
                           AttrMapper attrMapper) {
        this.attrGroupService = attrGroupService;
        this.categoryService = categoryService;
        this.attrAttrgroupRelationService = attrAttrgroupRelationService;
        this.attrMapper = attrMapper;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long cateLogId) {
        String key = (String) params.get("key");
        boolean isNumber = key != null && key.matches("^\\d+$");
        IPage<AttrEntity> page = page(
                new Query<AttrEntity>().getPage(params),
                new LambdaQueryWrapper<AttrEntity>()
                        .eq(cateLogId != 0, AttrEntity::getCatelogId, cateLogId)
                        .and(StringUtils.hasLength(key), object ->
                                object.eq(isNumber, AttrEntity::getAttrId, isNumber ? Long.parseLong(key) : null)
                                        .or().like(AttrEntity::getAttrName, key)
                        )
        );
        PageUtils pageUtils = new PageUtils(page);
        List<AttrShowVo> records = page.getRecords()
                .stream()
                .map(attrEntity -> {
                    // 映射到 VO
                    AttrShowVo attrShowVo = attrMapper.attrEntityToAttrShowVo(attrEntity);

                    // 获取所属分类
                    attrShowVo.setCatelogName(categoryService.getById(attrEntity.getCatelogId()).getName());

                    // 获取所属分组
                    AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationService
                            .getOne(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                                    .eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId())
                            );
                    if (attrAttrgroupRelationEntity != null) {
                        Long attrGroupId = attrAttrgroupRelationEntity.getAttrGroupId();
                        attrShowVo.setGroupName(attrGroupService.getById(attrGroupId).getAttrGroupName());
                    }
                    return attrShowVo;
                })
                .toList();
        pageUtils.setList(records);
        return pageUtils;
    }

    @Override
    public void saveAttr(AttrVo attrVo) {
        AttrEntity attrEntity = attrMapper.attrVoToAttrEntity(attrVo);
        save(attrEntity);
        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
        attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
        attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
        attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
    }

}