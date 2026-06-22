/**
 * 服务层接口汇总
 * 定义所有业务逻辑接口规范
 */

package com.example.takeaway.service;

import com.example.takeaway.entity.*;

import java.util.List;
import java.util.Map;

public interface Service {

    interface AddressService {
        Address findById(Long id);
        List<Address> findByUserId(Long userId);
        Address findDefaultByUserId(Long userId);
        Address addAddress(Address address);
        Address updateAddress(Address address);
        void deleteAddress(Long id);
    }

    interface BusinessService {
        Business create(Business business);
        Business update(Business business);
        void delete(Long id);
        Business findById(Long id);
        List<Business> findAll();
        List<Business> findByStatus(Integer status);
        List<Business> searchByName(String name);
        List<Business> search(String keyword, Long categoryId, Integer status);
        Business login(String username, String password);
        Business register(Business business);
    }

    interface CartService {
        Cart add(Long userId, Long businessId, Long foodId, Integer quantity);
        Cart update(Long userId, Long foodId, Integer quantity);
        void remove(Long userId, Long foodId);
        void clear(Long userId);
        List<Cart> findByUserId(Long userId);
        List<Cart> findByUserIdAndBusinessId(Long userId, Long businessId);
    }

    interface FoodService {
        Food create(Food food);
        Food update(Food food);
        void delete(Long id);
        Food findById(Long id);
        List<Food> findByBusinessId(Long businessId);
        List<Food> findByBusinessIdAndStatus(Long businessId, Integer status);
        List<Food> findByCategoryId(Long categoryId);
    }

    interface OrderService {
        Orders create(Orders order);
        Orders update(Orders order);
        Orders findById(Long id);
        Orders findByOrderNumber(String orderNumber);
        List<Orders> findByUserId(Long userId);
        List<Orders> findByBusinessId(Long businessId);
        List<Orders> findByStatus(Integer status);
        Orders updateStatus(Long id, Integer status);
        void delete(Long id);

        List<Orders> findByUserIdActive(Long userId);
        List<Orders> findByBusinessIdActive(Long businessId);
        void deleteByUser(Long orderId);
        void deleteByBusiness(Long orderId);

        Orders applyCancelByUser(Long id);
        Orders approveCancelByBusiness(Long id);
        Orders rejectCancelByBusiness(Long id);
        Orders cancelByBusiness(Long id);
    }

    interface ReviewService {
        Review findById(Long id);
        List<Review> findByBusinessId(Long businessId);
        Review findByOrderId(Long orderId);
        List<Review> findByUserId(Long userId);
        Map<String, Object> getBusinessRating(Long businessId);
        Review addReview(Review review);
        Review updateReview(Review review);
        void deleteReview(Long id);
        void deleteAllReviews();
    }

    interface UserService {
        User register(User user);
        User login(String username, String password);
        User findById(Long id);
        User findByUsername(String username);
        User update(User user);
        void delete(Long id);
    }
}