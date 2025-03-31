package com.example.shopappangular.services.Impl;

import com.example.shopappangular.dtos.OrderDetailDTO;
import com.example.shopappangular.exceptions.DataNotFoundException;
import com.example.shopappangular.models.Order;
import com.example.shopappangular.models.OrderDetail;
import com.example.shopappangular.models.Product;
import com.example.shopappangular.repositories.OrderDetailRepository;
import com.example.shopappangular.repositories.OrderRepository;
import com.example.shopappangular.repositories.ProductRepository;
import com.example.shopappangular.services.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    @Override
    @Transactional
    public OrderDetail createOrderDetail(OrderDetailDTO newOrderDetail) throws Exception {
        Order existingOrder = orderRepository.findById(newOrderDetail.getOrderId())
                .orElseThrow(() -> new DateTimeException("Cannot find order with id: + " + newOrderDetail.getOrderId()));
        Product existingProduct = productRepository.findById(newOrderDetail.getProductId())
                .orElseThrow(() -> new DateTimeException("Cannot find product with id: + " + newOrderDetail.getProductId()));
        OrderDetail orderDetail = OrderDetail.builder()
                .order(existingOrder)
                .product(existingProduct)
                .price(newOrderDetail.getPrice())
                .numberOfProducts(newOrderDetail.getNumberOfProducts())
                .totalMoney(newOrderDetail.getTotalMoney())
                .color(newOrderDetail.getColor())
                .build();
        //Lưu vào db
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetail getOrderDetail(Long id) throws Exception {
        return orderDetailRepository.findById(id).orElseThrow(() ->
                new DataNotFoundException("Cannot find orderDetail with id: + "+ id));
    }

    @Override
    @Transactional
    public OrderDetail updateOrderDetail(Long id, OrderDetailDTO newOrderDetailData) throws DataNotFoundException {
        //tìm xem order detail có tồn tại ko đã
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find order detail with id: "+id));
        Order existingOrder = orderRepository.findById(newOrderDetailData.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find order with id: "+id));
        Product existingProduct = productRepository.findById(newOrderDetailData.getProductId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find product with id: " + newOrderDetailData.getProductId()));
        existingOrderDetail.setPrice(newOrderDetailData.getPrice());
        existingOrderDetail.setNumberOfProducts(newOrderDetailData.getNumberOfProducts());
        existingOrderDetail.setTotalMoney(newOrderDetailData.getTotalMoney());
        existingOrderDetail.setColor(newOrderDetailData.getColor());
        existingOrderDetail.setOrder(existingOrder);
        existingOrderDetail.setProduct(existingProduct);
        return orderDetailRepository.save(existingOrderDetail);
    }

    @Override
    @Transactional
    public void deleteById(Long id){
        orderDetailRepository.deleteById(id);
    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}
