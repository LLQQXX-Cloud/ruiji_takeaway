/**
 * Category控制器实现类
 * 实现 Category控制器 接口定义的方法
 */

package com.example.takeaway.controller.impl;

import com.example.takeaway.controller.Controller.CategoryController;
import com.example.takeaway.entity.Category;
import com.example.takeaway.mapper.impl.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryControllerImpl implements CategoryController {

    @Autowired
    private CategoryRepository categoryMapper;

    /**
     * 获取所有分类
     * @return 分类列表
     */
    @Override
    @GetMapping
    public ResponseEntity<List<Category>> findAll() {
        return ResponseEntity.ok(categoryMapper.findAll());
    }
}
