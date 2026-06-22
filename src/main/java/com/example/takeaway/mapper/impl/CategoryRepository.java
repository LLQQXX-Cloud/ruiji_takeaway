/**
 * Category数据映射实现类
 * 提供食品分类的数据访问操作
 */

package com.example.takeaway.mapper.impl;

import com.example.takeaway.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * 根据商家ID查询分类列表，按排序字段升序排列
     * @param businessId 商家ID
     * @return 分类列表
     */
    List<Category> findByBusinessIdOrderBySort(Long businessId);

    /**
     * 根据商家ID删除所有分类
     * @param businessId 商家ID
     */
    void deleteByBusinessId(Long businessId);
}
