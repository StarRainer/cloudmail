package com.rainer.cloudmall.product.controller;

import com.rainer.cloudmall.product.entity.BrandEntity;
import com.rainer.cloudmall.product.service.BrandService;
import com.rainer.cloudmall.common.utils.PageUtils;
import com.rainer.cloudmall.common.utils.Result;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 品牌
 *
 * @author StarRainer
 * @email estarrainer@gmail.com
 * @date 2026-01-14 13:41:45
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
//    @RequiresPermissions("product:brand:list")
    public Result list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return Result.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
//    @RequiresPermissions("product:brand:info")
    public Result info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return Result.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
//    @RequiresPermissions("product:brand:save")
    public Result save(@RequestBody BrandEntity brand){
		brandService.save(brand);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody BrandEntity brand){
		brandService.updateById(brand);
        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
//    @RequiresPermissions("product:brand:delete")
    public Result delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return Result.ok();
    }

}
