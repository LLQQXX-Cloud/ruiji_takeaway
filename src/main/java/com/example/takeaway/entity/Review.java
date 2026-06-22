/**
 * 评价实体类
 * 对应数据库 review 表，存储用户对商家的评价信息
 */
package com.example.takeaway.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "review")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    /** 评价ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 订单ID */
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    /** 用户ID */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 商家ID */
    @Column(name = "business_id", nullable = false)
    private Long businessId;

    /** 评分(1-5) */
    @Column(nullable = false)
    private Integer rating;

    /** 评价内容 */
    @Column(length = 500)
    private String content;

    /** 评价图片URL */
    @Column(name = "images", length = 1000)
    private String images;

    /** 创建时间 */
    @Column(name = "created_at")
    private Long createdAt;

    /** 持久化前自动设置创建时间 */
    @PrePersist
    public void prePersist() {
        createdAt = System.currentTimeMillis();
    }
}
