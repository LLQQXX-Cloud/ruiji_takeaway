/**
 * 控制器接口汇总
 * 定义所有 REST API 接口规范
 */

package com.example.takeaway.controller;

import com.example.takeaway.entity.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

public interface Controller {

    interface AddressController {
        @GetMapping("/api/address")
        ResponseEntity<List<Address>> getAddresses(@RequestParam Long userId);

        @GetMapping("/api/address/default")
        ResponseEntity<Address> getDefaultAddress(@RequestParam Long userId);

        @GetMapping("/api/address/{id}")
        ResponseEntity<Address> getAddress(@PathVariable Long id);

        @PostMapping("/api/address")
        ResponseEntity<Address> addAddress(@RequestBody Address address);

        @PutMapping("/api/address/{id}")
        ResponseEntity<Address> updateAddress(@PathVariable Long id, @RequestBody Address address);

        @DeleteMapping("/api/address/{id}")
        ResponseEntity<Void> deleteAddress(@PathVariable Long id);
    }

    interface BusinessController {
        @PostMapping("/api/businesses")
        ResponseEntity<Map<String, Object>> create(@RequestBody Business business);

        @GetMapping("/api/businesses")
        ResponseEntity<Map<String, Object>> list(@RequestParam(required = false) String name);

        @GetMapping("/api/businesses/search")
        ResponseEntity<Map<String, Object>> search(
                @RequestParam(required = false) String keyword,
                @RequestParam(required = false) Long categoryId,
                @RequestParam(required = false) Integer status);

        @GetMapping("/api/businesses/{id}")
        ResponseEntity<Map<String, Object>> getById(@PathVariable Long id);

        @PutMapping("/api/businesses/{id}")
        ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Business business);

        @DeleteMapping("/api/businesses/{id}")
        ResponseEntity<Map<String, Object>> delete(@PathVariable Long id);

        @PostMapping("/api/businesses/login")
        ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData);

        @PostMapping("/api/businesses/register")
        ResponseEntity<Map<String, Object>> register(@RequestBody Business business);
    }

    interface CartController {
        @PostMapping("/api/cart")
        ResponseEntity<Map<String, Object>> add(@RequestBody Map<String, Object> data);

        @PutMapping("/api/cart")
        ResponseEntity<Map<String, Object>> update(@RequestBody Map<String, Object> data);

        @DeleteMapping("/api/cart")
        ResponseEntity<Map<String, Object>> remove(
                @RequestParam Long userId,
                @RequestParam Long foodId);

        @DeleteMapping("/api/cart/clear")
        ResponseEntity<Map<String, Object>> clear(@RequestParam Long userId);

        @GetMapping("/api/cart/user/{userId}")
        ResponseEntity<Map<String, Object>> getByUser(@PathVariable Long userId);

        @GetMapping("/api/cart/user/{userId}/business/{businessId}")
        ResponseEntity<Map<String, Object>> getByUserAndBusiness(
                @PathVariable Long userId,
                @PathVariable Long businessId);
    }

    interface CategoryController {
        @GetMapping("/api/categories")
        ResponseEntity<List<Category>> findAll();
    }

    interface FoodController {
        @PostMapping("/api/foods")
        ResponseEntity<Map<String, Object>> create(@RequestBody Food food);

        @GetMapping("/api/foods/business/{businessId}")
        ResponseEntity<Map<String, Object>> getByBusiness(@PathVariable Long businessId);

        @GetMapping("/api/foods/category/{categoryId}")
        ResponseEntity<Map<String, Object>> getByCategory(@PathVariable Long categoryId);

        @GetMapping("/api/foods/{id}")
        ResponseEntity<Map<String, Object>> getById(@PathVariable Long id);

        @PutMapping("/api/foods/{id}")
        ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Food food);

        @DeleteMapping("/api/foods/{id}")
        ResponseEntity<Map<String, Object>> delete(@PathVariable Long id);
    }

    interface OrderController {
        @PostMapping("/api/orders")
        ResponseEntity<Map<String, Object>> create(@RequestBody Orders order);

        @GetMapping("/api/orders/{id}")
        ResponseEntity<Map<String, Object>> getById(@PathVariable Long id);

        @GetMapping("/api/orders/number/{orderNumber}")
        ResponseEntity<Map<String, Object>> getByOrderNumber(@PathVariable String orderNumber);

        @GetMapping("/api/orders/user/{userId}")
        ResponseEntity<Map<String, Object>> getByUser(@PathVariable Long userId);

        @GetMapping("/api/orders/business/{businessId}")
        ResponseEntity<Map<String, Object>> getByBusiness(@PathVariable Long businessId);

        @PutMapping("/api/orders/{id}/status")
        ResponseEntity<Map<String, Object>> updateStatus(
                @PathVariable Long id,
                @RequestBody Map<String, Integer> data);

        @PutMapping("/api/orders/{id}")
        ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Orders order);

        @DeleteMapping("/api/orders/{id}")
        ResponseEntity<Map<String, Object>> delete(@PathVariable Long id);

        @DeleteMapping("/api/orders/{id}/user")
        ResponseEntity<Map<String, Object>> deleteByUser(@PathVariable Long orderId);

        @DeleteMapping("/api/orders/{id}/business")
        ResponseEntity<Map<String, Object>> deleteByBusiness(@PathVariable Long orderId);
    }

    interface ReviewController {
        @GetMapping("/api/review/business/{businessId}")
        ResponseEntity<List<Review>> getReviewsByBusiness(@PathVariable Long businessId);

        @GetMapping("/api/review/order/{orderId}")
        ResponseEntity<Review> getReviewByOrder(@PathVariable Long orderId);

        @GetMapping("/api/review/user/{userId}")
        ResponseEntity<List<Review>> getReviewsByUser(@PathVariable Long userId);

        @GetMapping("/api/review/rating/{businessId}")
        ResponseEntity<Map<String, Object>> getBusinessRating(@PathVariable Long businessId);

        @PostMapping("/api/review")
        ResponseEntity<Review> addReview(@RequestBody Review review);

        @PutMapping("/api/review/{id}")
        ResponseEntity<Review> updateReview(@PathVariable Long id, @RequestBody Review review);

        @DeleteMapping("/api/review/{id}")
        ResponseEntity<Void> deleteReview(@PathVariable Long id);
    }

    interface UserController {
        @PostMapping("/api/users/register")
        ResponseEntity<Map<String, Object>> register(@RequestBody User user);

        @PostMapping("/api/users/login")
        ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData);

        @GetMapping("/api/users/{id}")
        ResponseEntity<Map<String, Object>> getUser(@PathVariable Long id);

        @PutMapping("/api/users/{id}")
        ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody User user);

        @DeleteMapping("/api/users/{id}")
        ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id);
    }
}