package com.rainer.cloudmall.product.web;

import com.rainer.cloudmall.product.entity.CategoryEntity;
import com.rainer.cloudmall.product.service.CategoryService;
import com.rainer.cloudmall.product.service.impl.CategoryServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {

    private final CategoryService categoryService;

    public IndexController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {
        List<CategoryEntity> categoryEntities = categoryService.getFirstLevel();
        model.addAttribute("categories", categoryEntities);
        return "index";
    }
}
