package com.example.shopappangular.services;

import com.example.shopappangular.dtos.OrderDTO;
import com.example.shopappangular.exceptions.DataNotFoundException;
import com.example.shopappangular.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(OrderDTO orderDTO) throws Exception;
    Order getOrder(Long id);
    Order updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException;
    void deletedOrder(Long id);
    List<Order> findByUserId(Long userId);
    Page<Order> getOrdersByKeyword(String keyword, Pageable pageable);
}
