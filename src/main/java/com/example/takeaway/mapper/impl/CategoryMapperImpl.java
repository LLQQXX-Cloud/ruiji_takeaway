/**
 * Category数据映射实现类
 * 实现 Category数据映射 接口定义的方法
 */

package com.example.takeaway.mapper.impl;

import com.example.takeaway.entity.Category;
import com.example.takeaway.mapper.Mapper.CategoryMapper;
import com.example.takeaway.mapper.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryMapperImpl implements CategoryMapper {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public List<Category> findByBusinessIdOrderBySort(Long businessId) {
        return categoryRepository.findByBusinessIdOrderBySort(businessId);
    }

    @Override
    public void deleteByBusinessId(Long businessId) {
        categoryRepository.deleteByBusinessId(businessId);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
