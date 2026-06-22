/**
 * Address数据映射实现类
 * 提供收货地址的数据访问操作
 */

package com.example.takeaway.mapper.impl;

import com.example.takeaway.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    /**
     * 根据用户ID查询收货地址列表，按是否默认降序、更新时间降序排列
     * @param userId 用户ID
     * @return 收货地址列表
     */
    List<Address> findByUserIdOrderByIsDefaultDescUpdatedAtDesc(Long userId);

    /**
     * 根据用户ID查询收货地址列表
     * @param userId 用户ID
     * @return 收货地址列表
     */
    List<Address> findByUserId(Long userId);

    /**
     * 根据用户ID查询默认收货地址
     * @param userId 用户ID
     * @return 默认收货地址，未找到返回null
     */
    Address findByUserIdAndIsDefaultTrue(Long userId);

    /**
     * 统计用户的收货地址数量
     * @param userId 用户ID
     * @return 地址数量
     */
    int countByUserId(Long userId);
}
