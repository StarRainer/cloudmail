package com.rainer.cloudmall.order.controller;

import java.util.Arrays;
import java.util.Map;

import com.rainer.cloudmall.common.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rainer.cloudmall.order.entity.OrderOperateHistoryEntity;
import com.rainer.cloudmall.order.service.OrderOperateHistoryService;
import com.rainer.cloudmall.common.utils.PageUtils;


/**
 * 订单操作历史记录
 *
 * @author StarRainer
 * @email estarrainer@gmail.com
 * @date 2026-01-14 14:37:13
 */
@RestController
@RequestMapping("order/orderoperatehistory")
public class OrderOperateHistoryController {
    @Autowired
    private OrderOperateHistoryService orderOperateHistoryService;

    /**
     * 列表
     */
    @RequestMapping("/list")
//    @RequiresPermissions("order:orderoperatehistory:list")
    public Result list(@RequestParam Map<String, Object> params){
        PageUtils page = orderOperateHistoryService.queryPage(params);

        return Result.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
//    @RequiresPermissions("order:orderoperatehistory:info")
    public Result info(@PathVariable("id") Long id){
		OrderOperateHistoryEntity orderOperateHistory = orderOperateHistoryService.getById(id);

        return Result.ok().put("orderOperateHistory", orderOperateHistory);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
//    @RequiresPermissions("order:orderoperatehistory:save")
    public Result save(@RequestBody OrderOperateHistoryEntity orderOperateHistory){
		orderOperateHistoryService.save(orderOperateHistory);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
//    @RequiresPermissions("order:orderoperatehistory:update")
    public Result update(@RequestBody OrderOperateHistoryEntity orderOperateHistory){
		orderOperateHistoryService.updateById(orderOperateHistory);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
//    @RequiresPermissions("order:orderoperatehistory:delete")
    public Result delete(@RequestBody Long[] ids){
		orderOperateHistoryService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
