/**
 * Business数据映射实现类
 * 提供商家的数据访问操作
 */

package com.example.takeaway.mapper.impl;

import com.example.takeaway.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {

    /**
     * 根据状态查询商家列表
     * @param status 商家状态（0:禁用, 1:正常）
     * @return 商家列表
     */
    List<Business> findByStatus(Integer status);

    /**
     * 根据商家名称模糊查询商家列表
     * @param name 商家名称关键词
     * @return 商家列表
     */
    List<Business> findByNameContaining(String name);

    /**
     * 根据用户名查询商家
     * @param username 用户名
     * @return 商家对象，未找到返回null
     */
    Business findByUsername(String username);

    /**
     * 根据分类ID查询商家列表
     * @param categoryId 分类ID
     * @return 商家列表
     */
    List<Business> findByCategoryId(Long categoryId);

    /**
     * 根据状态和名称查询商家列表
     * @param status 商家状态
     * @param name 商家名称关键词
     * @return 商家列表
     */
    List<Business> findByStatusAndNameContaining(Integer status, String name);

    /**
     * 根据状态和分类ID查询商家列表
     * @param status 商家状态
     * @param categoryId 分类ID
     * @return 商家列表
     */
    List<Business> findByStatusAndCategoryId(Integer status, Long categoryId);

    /**
     * 根据状态、名称和分类ID查询商家列表
     * @param status 商家状态
     * @param name 商家名称关键词
     * @param categoryId 分类ID
     * @return 商家列表
     */
    List<Business> findByStatusAndNameContainingAndCategoryId(Integer status, String name, Long categoryId);

    /**
     * 根据名称和分类ID查询商家列表
     * @param name 商家名称关键词
     * @param categoryId 分类ID
     * @return 商家列表
     */
    List<Business> findByNameContainingAndCategoryId(String name, Long categoryId);
}
