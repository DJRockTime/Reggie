package com.app.service;

import com.app.entiry.Category;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CategoryService extends IService<Category> {

    public void remove(Long id);
}
