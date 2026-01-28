package com.rainer.cloudmall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.rainer.cloudmall.common.utils.FeignResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.rainer.cloudmall.ware.entity.WareSkuEntity;
import com.rainer.cloudmall.ware.service.WareSkuService;
import com.rainer.cloudmall.common.utils.PageUtils;
import com.rainer.cloudmall.common.utils.Result;



/**
 * 商品库存
 *
 * @author StarRainer
 * @email estarrainer@gmail.com
 * @date 2026-01-14 14:41:44
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    private final WareSkuService wareSkuService;

    public WareSkuController(WareSkuService wareSkuService) {
        this.wareSkuService = wareSkuService;
    }

    @PostMapping("/hasstock")
    public FeignResult<Map<Long, Boolean>> checkHasStock(@RequestBody List<Long> skuIds) {
        return FeignResult.success(wareSkuService.checkHasStockBySkuIds(skuIds));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);
        return Result.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
//    @RequiresPermissions("ware:waresku:info")
    public Result info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return Result.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
//    @RequiresPermissions("ware:waresku:save")
    public Result save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
//    @RequiresPermissions("ware:waresku:update")
    public Result update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
//    @RequiresPermissions("ware:waresku:delete")
    public Result delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
