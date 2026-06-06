/**
 * 鍦板潃瀹炰綋绫? * 瀵瑰簲鏁版嵁搴?address 琛紝瀛樺偍鐢ㄦ埛鏀惰揣鍦板潃淇℃伅
 */
package com.example.takeaway.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "address")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    /** 鍦板潃ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 鐢ㄦ埛ID */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 鏀惰揣浜哄鍚?*/
    @Column(nullable = false, length = 100)
    private String name;

    /** 鏀惰揣浜虹數璇?*/
    @Column(nullable = false, length = 20)
    private String phone;

    /** 鐪佷唤 */
    @Column(name = "province", length = 50)
    private String province;

    /** 鍩庡競 */
    @Column(name = "city", length = 50)
    private String city;

    /** 鍖哄幙 */
    @Column(name = "district", length = 50)
    private String district;

    /** 璇︾粏鍦板潃 */
    @Column(name = "detail", nullable = false, length = 500)
    private String detail;

    /** 鏄惁涓洪粯璁ゅ湴鍧€ */
    @Column(name = "is_default")
    private Boolean isDefault = false;

    /** 鍒涘缓鏃堕棿 */
    @Column(name = "created_at")
    private Long createdAt;

    /** 鏇存柊鏃堕棿 */
    @Column(name = "updated_at")
    private Long updatedAt;

    /** 鎸佷箙鍖栧墠鑷姩璁剧疆鍒涘缓鏃堕棿鍜屾洿鏂版椂闂?*/
    @PrePersist
    public void prePersist() {
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    /** 鏇存柊鍓嶈嚜鍔ㄨ缃洿鏂版椂闂?*/
    @PreUpdate
    public void preUpdate() {
        updatedAt = System.currentTimeMillis();
    }
}
