package com.app.contorller;


import com.app.common.R;
import com.app.entiry.Category;
import com.app.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增食品分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {

        log.info("category: {}", category);
        categoryService.save(category);

        return R.success("保存成功！");
    }

    /**
     * 查询菜品分类列表
     * @param id
     * @return
     */
    @GetMapping
    public R<Page> getCategoryList(int pageNumber, int pageSize, String name) {
        Page pageInfo = new Page(pageNumber, pageSize);

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.isNotEmpty(name), Category::getName, name);

        // 添加排序条件
        queryWrapper.orderByAsc(Category::getSort);

        categoryService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> deleteById(Long id) {
        log.info("id: {}", id);
        categoryService.remove(id);

        return R.success("删除成功！");
    }


    /**
     * 根据id修改分类信息
     * @param category 分类实体
     * @return R String
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        log.info("修改分类信息：{}", category);

        categoryService.updateById(category);
        return R.success("修改分类信息成功！");
    }

    /**
     * 根据条件查询分类数据
     * @param category 菜品实体
     * @return 返回类型列表
     */
    @GetMapping("/list")
    public R<List<Category>> getCategoryTypeList(Category category) {

        // 条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 添加条件
        lambdaQueryWrapper.eq(category.getType() != null, Category::getType, category.getType());

        // 添加排序条件  // 优先使用sort排序，sort相同在使用更新事件降序排序
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(lambdaQueryWrapper);

        return R.success(list);
    }


}
