package com.rainer.cloudmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rainer.cloudmall.common.utils.PageUtils;
import com.rainer.cloudmall.common.utils.Query;
import com.rainer.cloudmall.product.dao.CategoryDao;
import com.rainer.cloudmall.product.entity.CategoryEntity;
import com.rainer.cloudmall.product.service.CategoryBrandRelationService;
import com.rainer.cloudmall.product.service.CategoryService;
import com.rainer.cloudmall.product.utils.ProductMapper;
import com.rainer.cloudmall.product.vo.Catelog2Vo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service("categoryService")
@Transactional(rollbackFor = Exception.class, readOnly = true)
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    private final CategoryBrandRelationService categoryBrandRelationService;
    private final ProductMapper productMapper;

    public CategoryServiceImpl(CategoryBrandRelationService categoryBrandRelationService, ProductMapper productMapper) {
        this.categoryBrandRelationService = categoryBrandRelationService;
        this.productMapper = productMapper;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 1.查出所有分类
        List<CategoryEntity> categoryEntities = list();

        // 2.组装成父子树形结构
        return categoryEntities
                .stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .map(menu -> {
                    // 递归设置子菜单
                    menu.setChildren(getChildrenCategory(menu, categoryEntities));
                    return menu;
                })
                // 第一个参数指定比较哪个字段，第二个参数指定排序规则
                .sorted(Comparator.comparing(CategoryEntity::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMenusByIds(List<Long> catIds) {
        // TODO: 如果有子节点没有被删除，不能删除当前节点
        removeByIds(catIds);
    }

    @Override
    public List<Long> getPathLink(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        long currentCatelogId = catelogId;
        do {
            paths.addFirst(currentCatelogId);
            currentCatelogId = getById(currentCatelogId).getParentCid();
        } while (currentCatelogId != 0);
        return paths;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCascade(CategoryEntity category) {
        updateById(category);
        if (StringUtils.hasLength(category.getName())) {
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
        }
    }

    @Override
    public List<CategoryEntity> listNameByIds(List<Long> catalogIds) {
        if (CollectionUtils.isEmpty(catalogIds)) {
            return Collections.emptyList();
        }
        return list(new LambdaQueryWrapper<CategoryEntity>()
                .select(CategoryEntity::getName)
                .in(CategoryEntity::getCatId, catalogIds)
        );
    }

    @Override
    public List<CategoryEntity> getFirstLevel() {
        return list(new LambdaQueryWrapper<CategoryEntity>()
                .eq(CategoryEntity::getCatLevel, 1)
        );
    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        List<CategoryEntity> categoryEntities = list();
        Map<Long, List<CategoryEntity>> parentToChildren = categoryEntities
                .stream()
                .sorted(Comparator.comparing(CategoryEntity::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.groupingBy(
                        CategoryEntity::getParentCid,
                        Collectors.toList()
                ));
        return categoryEntities.stream().filter(entity -> entity.getCatLevel() == 1)
                .collect(Collectors.toMap(
                        entity -> entity.getCatId().toString(),
                        entity -> parentToChildren
                                .getOrDefault(entity.getCatId(), Collections.emptyList())
                                .stream()
                                .map(catelog2Entity -> productMapper.toCateLog2Vo(
                                        catelog2Entity,
                                        parentToChildren
                                                .getOrDefault(catelog2Entity.getCatId(), Collections.emptyList())
                                                .stream()
                                                .map(productMapper::toCatelog3Vo)
                                                .toList()
                                ))
                                .toList()
                ));
    }

    /**
     * 获取根菜单对应的菜单树
     */
    private List<CategoryEntity> getChildrenCategory(CategoryEntity currentMenu, List<CategoryEntity> menuList) {
        return menuList
                .stream()
                .filter(menu -> menu.getParentCid().equals(currentMenu.getCatId()))
                .map(menu -> {
                    menu.setChildren(getChildrenCategory(menu, menuList));
                    return menu;
                })
                .sorted(Comparator.comparing(CategoryEntity::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }
}