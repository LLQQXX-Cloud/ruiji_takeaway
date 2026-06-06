/**
 * 璇勪环瀹炰綋绫? * 瀵瑰簲鏁版嵁搴?review 琛紝瀛樺偍鐢ㄦ埛瀵瑰晢瀹剁殑璇勪环淇℃伅
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

    /** 璇勪环ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 璁㈠崟ID */
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    /** 鐢ㄦ埛ID */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 鍟嗗ID */
    @Column(name = "business_id", nullable = false)
    private Long businessId;

    /** 璇勫垎锛?-5锛?*/
    @Column(nullable = false)
    private Integer rating;

    /** 璇勪环鍐呭 */
    @Column(length = 500)
    private String content;

    /** 璇勪环鍥剧墖URL */
    @Column(name = "images", length = 1000)
    private String images;

    /** 鍒涘缓鏃堕棿 */
    @Column(name = "created_at")
    private Long createdAt;

    /** 鎸佷箙鍖栧墠鑷姩璁剧疆鍒涘缓鏃堕棿 */
    @PrePersist
    public void prePersist() {
        createdAt = System.currentTimeMillis();
    }
}
