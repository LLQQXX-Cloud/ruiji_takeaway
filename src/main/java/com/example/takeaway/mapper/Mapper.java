/**
 * 数据映射层接口汇总
 * 定义所有数据访问映射接口规范
 */

package com.example.takeaway.mapper;

import com.example.takeaway.entity.*;

import java.util.List;
import java.util.Optional;

public interface Mapper {

    interface AddressMapper {
        Address findById(Long id);
        List<Address> findByUserId(Long userId);
        Address findDefaultByUserId(Long userId);
        void insert(Address address);
        void update(Address address);
        void deleteById(Long id);
        int countByUserId(Long userId);
    }

    interface BusinessMapper {
        Business save(Business business);
        void deleteById(Long id);
        Optional<Business> findById(Long id);
        List<Business> findAll();
        List<Business> findByStatus(Integer status);
        List<Business> findByNameContaining(String name);
        Business findByUsername(String username);
        List<Business> findByCategoryId(Long categoryId);
        List<Business> findByStatusAndNameContaining(Integer status, String name);
        List<Business> findByStatusAndCategoryId(Integer status, Long categoryId);
        List<Business> findByStatusAndNameContainingAndCategoryId(Integer status, String name, Long categoryId);
        List<Business> findByNameContainingAndCategoryId(String name, Long categoryId);
    }

    interface CartMapper {
        Cart save(Cart cart);
        void deleteById(Long id);
        List<Cart> findByUserId(Long userId);
        List<Cart> findByUserIdAndBusinessId(Long userId, Long businessId);
        Optional<Cart> findByUserIdAndFoodId(Long userId, Long foodId);
        void deleteByUserId(Long userId);
        void deleteByUserIdAndFoodId(Long userId, Long foodId);
    }

    interface CategoryMapper {
        Category save(Category category);
        void deleteById(Long id);
        Optional<Category> findById(Long id);
        List<Category> findByBusinessIdOrderBySort(Long businessId);
        void deleteByBusinessId(Long businessId);
        List<Category> findAll();
    }

    interface FoodMapper {
        Food save(Food food);
        void deleteById(Long id);
        Optional<Food> findById(Long id);
        List<Food> findByBusinessIdAndStatus(Long businessId, Integer status);
        List<Food> findByCategoryId(Long categoryId);
        List<Food> findByBusinessId(Long businessId);
        void deleteByBusinessId(Long businessId);
    }

    interface OrderItemMapper {
        OrderItem save(OrderItem orderItem);
        void deleteById(Long id);
        Optional<OrderItem> findById(Long id);
        List<OrderItem> findByOrderId(Long orderId);
        void deleteByOrderId(Long orderId);
    }

    interface OrdersMapper {
        Orders save(Orders orders);
        void deleteById(Long id);
        Optional<Orders> findById(Long id);
        Optional<Orders> findByOrderNumber(String orderNumber);
        List<Orders> findByUserIdOrderByCreateTimeDesc(Long userId);
        List<Orders> findByBusinessIdOrderByCreateTimeDesc(Long businessId);
        List<Orders> findByStatusOrderByCreateTimeDesc(Integer status);

        List<Orders> findByUserIdAndNotDeletedByUser(Long userId);
        List<Orders> findByBusinessIdAndNotDeletedByBusiness(Long businessId);
        void softDeleteByUser(Long orderId);
        void softDeleteByBusiness(Long orderId);
    }

    interface ReviewMapper {
        Review findById(Long id);
        List<Review> findByBusinessId(Long businessId);
        Review findByOrderId(Long orderId);
        List<Review> findByUserId(Long userId);
        Double findAvgRating(Long businessId);
        int countByBusinessId(Long businessId);
        void insert(Review review);
        void update(Review review);
        void deleteById(Long id);
    }

    interface UserMapper {
        User save(User user);
        void deleteById(Long id);
        Optional<User> findById(Long id);
        Optional<User> findByUsername(String username);
        Optional<User> findByPhone(String phone);
        boolean existsByUsername(String username);
        boolean existsByPhone(String phone);
    }
}