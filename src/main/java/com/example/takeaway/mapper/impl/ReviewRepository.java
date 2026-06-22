/**
 * Review数据映射实现类
 * 提供评价的数据访问操作
 */

package com.example.takeaway.mapper.impl;

import com.example.takeaway.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 根据商家ID查询评价列表，按创建时间降序排列
     * @param businessId 商家ID
     * @return 评价列表
     */
    List<Review> findByBusinessIdOrderByCreatedAtDesc(Long businessId);

    /**
     * 根据商家ID查询评价列表
     * @param businessId 商家ID
     * @return 评价列表
     */
    List<Review> findByBusinessId(Long businessId);

    /**
     * 根据订单ID查询评价
     * @param orderId 订单ID
     * @return 评价对象，未找到返回null
     */
    Review findByOrderId(Long orderId);

    /**
     * 根据用户ID查询评价列表，按创建时间降序排列
     * @param userId 用户ID
     * @return 评价列表
     */
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 根据用户ID查询评价列表
     * @param userId 用户ID
     * @return 评价列表
     */
    List<Review> findByUserId(Long userId);

    /**
     * 计算商家的平均评分
     * @param businessId 商家ID
     * @return 平均评分
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.businessId = :businessId")
    Double findAvgRatingByBusinessId(@Param("businessId") Long businessId);

    /**
     * 统计商家的评价数量
     * @param businessId 商家ID
     * @return 评价数量
     */
    int countByBusinessId(Long businessId);
}
