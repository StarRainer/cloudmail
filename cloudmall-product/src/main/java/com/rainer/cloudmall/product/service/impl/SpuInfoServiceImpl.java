package com.rainer.cloudmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rainer.cloudmall.common.constant.ProductConstant;
import com.rainer.cloudmall.common.exception.ProductPublishException;
import com.rainer.cloudmall.common.exception.code.CommonCode;
import com.rainer.cloudmall.common.to.SkuReductionTo;
import com.rainer.cloudmall.common.to.SpuBoundsTo;
import com.rainer.cloudmall.common.to.es.SkuEsModel;
import com.rainer.cloudmall.common.utils.FeignResult;
import com.rainer.cloudmall.common.utils.PageUtils;
import com.rainer.cloudmall.common.utils.Query;
import com.rainer.cloudmall.common.utils.Result;
import com.rainer.cloudmall.product.dao.SpuInfoDao;
import com.rainer.cloudmall.product.entity.*;
import com.rainer.cloudmall.product.feign.CouponFeignService;
import com.rainer.cloudmall.product.feign.SearchFeignService;
import com.rainer.cloudmall.product.feign.WareFeignService;
import com.rainer.cloudmall.product.service.*;
import com.rainer.cloudmall.product.utils.CouponMapper;
import com.rainer.cloudmall.product.utils.ProductMapper;
import com.rainer.cloudmall.product.utils.SearchMapper;
import com.rainer.cloudmall.product.vo.SpuSaveVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service("spuInfoService")
@Transactional(rollbackFor = Exception.class, readOnly = true)
@Slf4j
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    private final ProductMapper productMapper;
    private final CouponMapper couponMapper;
    private final SpuInfoDescService spuInfoDescService;
    private final SpuImagesService spuImagesService;
    private final AttrService attrService;
    private final ProductAttrValueService productAttrValueService;
    private final SkuInfoService skuInfoService;
    private final SkuImagesService skuImagesService;
    private final SkuSaleAttrValueService skuSaleAttrValueService;
    private final CouponFeignService couponFeignService;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final SearchMapper searchMapper;
    private final WareFeignService wareFeignService;
    private final SearchFeignService searchFeignService;

    public SpuInfoServiceImpl(
            ProductMapper productMapper,
            CouponMapper couponMapper,
            SpuInfoDescService spuInfoDescService,
            SpuImagesService spuImagesService,
            AttrService attrService,
            ProductAttrValueService productAttrValueService,
            SkuInfoService skuInfoService,
            SkuImagesService skuImagesService,
            SkuSaleAttrValueService skuSaleAttrValueService,
            CouponFeignService couponFeignService,
            BrandService brandService,
            CategoryService categoryService, SearchMapper searchMapper, WareFeignService wareFeignService, SearchFeignService searchFeignService) {
        this.productMapper = productMapper;
        this.couponMapper = couponMapper;
        this.spuInfoDescService = spuInfoDescService;
        this.spuImagesService = spuImagesService;
        this.attrService = attrService;
        this.productAttrValueService = productAttrValueService;
        this.skuInfoService = skuInfoService;
        this.skuImagesService = skuImagesService;
        this.skuSaleAttrValueService = skuSaleAttrValueService;
        this.couponFeignService = couponFeignService;
        this.brandService = brandService;
        this.categoryService = categoryService;
        this.searchMapper = searchMapper;
        this.wareFeignService = wareFeignService;
        this.searchFeignService = searchFeignService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");
        String catelogIdText = (String) params.get("catelogId");
        String brandIdText = (String) params.get("brandId");
        String status = (String) params.get("status");

        LambdaQueryWrapper<SpuInfoEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasLength(catelogIdText)) {
            long cateLogId = Long.parseLong(catelogIdText);
            wrapper.eq(cateLogId != 0, SpuInfoEntity::getCatalogId, cateLogId);
        }
        if (StringUtils.hasLength(brandIdText)) {
            long brandId = Long.parseLong(brandIdText);
            wrapper.eq(brandId != 0, SpuInfoEntity::getBrandId, brandId);
        }
        if (StringUtils.hasLength(status)) {
            wrapper.eq(SpuInfoEntity::getPublishStatus, Integer.parseInt(status));
        }
        wrapper.and(key != null, w -> {
            boolean isNumber = key != null && key.matches("^\\d+$");
            if (isNumber) {
                w.eq(SpuInfoEntity::getId, Long.parseLong(key));
            }
            w.or().like(SpuInfoEntity::getSpuName, key);
        });

        return new PageUtils(page(new Query<SpuInfoEntity>().getPage(params), wrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SpuSaveVo spuSaveVo) {
        log.debug("Handle SpuSaveVo...");
        // 1.保存SPU基本信息
        SpuInfoEntity spuInfoEntity = productMapper.spuSaveVoToSpuInfoEntity(spuSaveVo);
        spuInfoEntity.setCreateTime(LocalDateTime.now());
        spuInfoEntity.setUpdateTime(LocalDateTime.now());
        save(spuInfoEntity);
        log.debug("spuInfoEntity={}", spuInfoEntity);

        // 2.保存SPU描述图片
        Long spuId = spuInfoEntity.getId();
        SpuInfoDescEntity spuInfoDescEntity = productMapper.spuSaveVoToSpuInfoDescEntity(spuSaveVo, spuId);
        spuInfoDescService.save(spuInfoDescEntity);
        log.debug("spuInfoDescEntity={}", spuInfoDescEntity);

        // 3.保存SPU图片集
        List<SpuImagesEntity> spuImagesEntities = productMapper
                .imagesToSpuImagesEntities(spuSaveVo.getImages(), spuId)
                .stream()
                .filter(entity -> StringUtils.hasLength(entity.getImgUrl()))
                .toList();
        spuImagesService.saveBatch(spuImagesEntities);
        log.debug("spuImagesEntities={}", spuImagesEntities);

        // 4.保存SPU规格参数
        List<ProductAttrValueEntity> productAttrValueEntities = productMapper.baseAttrsVosToProductAttrValueEntities(spuSaveVo.getBaseAttrs(), spuId);
        List<Long> attrIds = productAttrValueEntities.stream().map(ProductAttrValueEntity::getAttrId).toList();
        Map<Long, String> attrIdToAttrName = attrService.list(new LambdaQueryWrapper<AttrEntity>()
                .select(AttrEntity::getAttrId, AttrEntity::getAttrName)
                .in(AttrEntity::getAttrId, attrIds)
        ).stream().collect(Collectors.toMap(AttrEntity::getAttrId, AttrEntity::getAttrName));

        productAttrValueEntities.forEach(productAttrValueEntity -> productAttrValueEntity.setAttrName(attrIdToAttrName.get(productAttrValueEntity.getAttrId())));
        productAttrValueService.saveBatch(productAttrValueEntities);
        log.debug("productAttrValueEntities={}", productAttrValueEntities);

        // 5.保存SPU积分信息
        SpuBoundsTo spuBoundsTo = couponMapper.boundsVoToSpuBoundsTo(spuSaveVo.getBounds(), spuId);
        log.debug("spuBoundsTo={}", spuBoundsTo);
        Result saveSpuBoundsResult = couponFeignService.saveSpuBounds(spuBoundsTo);
        if (saveSpuBoundsResult.getCode() != CommonCode.OK.getCode()) {
            log.error("远程调用保存SPU积分信息失败");
        }

        // 6.保存SPU对应的所有SKU信息
        // 6.1 SKU基本信息
        List<SpuSaveVo.SkusVo> skusVos = spuSaveVo.getSkus();
        List<SkuInfoEntity> skuInfoEntities = productMapper.skusVosAndSpuInfoEntityToSkuInfoEntities(skusVos, spuInfoEntity);
        log.debug("skuInfoEntities={}", skuInfoEntities);
        skuInfoService.saveBatch(skuInfoEntities);

        List<SkuImagesEntity> skuImagesEntities = new ArrayList<>();
        List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = new ArrayList<>();
        List<SkuReductionTo> skuReductionTos = new ArrayList<>();
        for (int i = 0; i < skusVos.size(); i++) {
            SpuSaveVo.SkusVo skusVo = skusVos.get(i);
            Long skuId = skuInfoEntities.get(i).getSkuId();
            List<SkuImagesEntity> currentSkuImagesEntities = productMapper
                    .imagesVosToSkuImagesEntities(skusVo.getImages(), skuId)
                    .stream()
                    .filter(entity -> StringUtils.hasLength(entity.getImgUrl()))
                    .toList();
            skuImagesEntities.addAll(currentSkuImagesEntities);
            skuSaleAttrValueEntities.addAll(productMapper.attrVosToSkuSaleAttrValueEntities(skusVo.getAttr(), skuId));
            skuReductionTos.add(couponMapper.skusVoToSkuReductionVo(skusVo, skuId));
        }

        // 6.2 SKU图片信息
        log.debug("skuImagesEntities={}", skuImagesEntities);
        skuImagesService.saveBatch(skuImagesEntities);

        // 6.3 SKU销售属性信息
        log.debug("skuSaleAttrValueEntities={}", skuSaleAttrValueEntities);
        skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

        // 6.4 SKU优惠满减信息
        log.debug("skuReductionTos={}", skuReductionTos);
        Result saveSkuReductionsResult = couponFeignService.saveSkuReductions(skuReductionTos);
        if (saveSkuReductionsResult.getCode() != CommonCode.OK.getCode()) {
            log.error("远程调用保存SKU优惠满减信息失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishProduct(Long spuId) {
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkuInfosBySpuId(spuId);
        if (CollectionUtils.isEmpty(skuInfoEntities)) {
            return;
        }

        List<Long> skuIds = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).toList();
        List<Long> brandIds = skuInfoEntities.stream().map(SkuInfoEntity::getBrandId).distinct().toList();
        List<Long> catalogIds = skuInfoEntities.stream().map(SkuInfoEntity::getCatalogId).distinct().toList();

        List<BrandEntity> brandEntities = brandService.listNameAndLogoByIds(brandIds);
        List<CategoryEntity> categoryEntities = categoryService.listNameByIds(catalogIds);
        List<Long> attrIds = productAttrValueService.listAttrIdsBySpuId(spuId);
        List<Long> searchableAttrIds = attrService.listSearchableIdsByIds(attrIds);

        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.listAttrByAttrIdsAndSpuId(searchableAttrIds, spuId);
        List<SkuEsModel.Attrs> attrs = searchMapper.toAttrs(productAttrValueEntities);

        FeignResult<Map<Long, Boolean>> feignResult = wareFeignService.checkHasStock(skuIds);
        Map<Long, Boolean> skuIdToHasStock = skuIds.stream().collect(Collectors.toMap(Function.identity(), skuId -> Boolean.FALSE));
        if (feignResult.getCode() != CommonCode.OK.getCode()) {
            log.error("远程调用获取库存信息失败:{}", feignResult.getMsg());
        } else {
            if (feignResult.getData() != null) {
                skuIdToHasStock.putAll(feignResult.getData());
            }
        }

        Map<Long, SkuInfoEntity> skuIdToEntity = skuInfoEntities.stream().collect(Collectors.toMap(SkuInfoEntity::getSkuId, Function.identity()));
        Map<Long, BrandEntity> brandIdToEntity = brandEntities.stream().collect(Collectors.toMap(BrandEntity::getBrandId, Function.identity()));
        Map<Long, CategoryEntity> categoryIdToEntity = categoryEntities.stream().collect(Collectors.toMap(CategoryEntity::getCatId, Function.identity()));

        List<SkuEsModel> skuEsModels = skuIds.stream().map(skuId -> {
            SkuInfoEntity skuInfoEntity = skuIdToEntity.get(skuId);
            return searchMapper.toSkuEsModel(
                    skuInfoEntity,
                    brandIdToEntity.get(skuInfoEntity.getBrandId()),
                    categoryIdToEntity.get(skuInfoEntity.getCatalogId()),
                    skuIdToHasStock.get(skuId),
                    attrs
            );
        }).toList();

        FeignResult<Void> elasticFeignResult = searchFeignService.publishProduct(skuEsModels);
        if (elasticFeignResult.getCode() == CommonCode.OK.getCode()) {
            SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
            spuInfoEntity.setId(spuId);
            spuInfoEntity.setPublishStatus(ProductConstant.PublishStatus.PUBLISHED.getCode());
            spuInfoEntity.setUpdateTime(LocalDateTime.now());
            updateById(spuInfoEntity);
        } else {
            log.error(elasticFeignResult.getMsg());
            throw new ProductPublishException();
        }
    }

}