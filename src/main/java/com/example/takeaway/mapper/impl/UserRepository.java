/**
 * User数据映射实现类
 * 提供用户的数据访问操作
 */

package com.example.takeaway.mapper.impl;

import com.example.takeaway.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return 用户对象
     */
    Optional<User> findByPhone(String phone);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查手机号是否存在
     * @param phone 手机号
     * @return 是否存在
     */
    boolean existsByPhone(String phone);
}
