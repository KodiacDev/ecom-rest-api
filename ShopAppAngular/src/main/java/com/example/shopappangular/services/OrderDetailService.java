package com.example.shopappangular.services;

import com.example.shopappangular.dtos.OrderDetailDTO;
import com.example.shopappangular.exceptions.DataNotFoundException;
import com.example.shopappangular.models.OrderDetail;
import java.util.List;

public interface OrderDetailService {
    OrderDetail createOrderDetail(OrderDetailDTO newOrderDetail) throws Exception;
    OrderDetail getOrderDetail(Long id) throws Exception;
    OrderDetail updateOrderDetail(Long id, OrderDetailDTO newOrderDetailData)
            throws DataNotFoundException;
    void deleteById(Long id);
    List<OrderDetail> findByOrderId(Long orderId);


}
