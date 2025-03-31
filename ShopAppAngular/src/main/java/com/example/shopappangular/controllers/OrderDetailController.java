package com.example.shopappangular.controllers;

import com.example.shopappangular.components.LocalizationUtils;
import com.example.shopappangular.dtos.OrderDetailDTO;
import com.example.shopappangular.exceptions.DataNotFoundException;
import com.example.shopappangular.models.OrderDetail;
import com.example.shopappangular.responses.OrderDetailResponse;
import com.example.shopappangular.responses.OrderResponse;
import com.example.shopappangular.services.OrderDetailService;
import com.example.shopappangular.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/order_details")
public class OrderDetailController {
    private final OrderDetailService orderDetailService;
    private final LocalizationUtils localizationUtils;
    //Them moi 1 orderDetail
    @PostMapping("")
    public ResponseEntity<?> crateOrderDetail(
            @Valid @RequestBody OrderDetailDTO orderDetailDTO){
        try{
            OrderDetail newOrderDetail = orderDetailService.createOrderDetail(orderDetailDTO);
            return ResponseEntity.ok().body(OrderDetailResponse.fromOrderDetail(newOrderDetail));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Lay 1 orderDetail qua id
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(
            @Valid @PathVariable("id") Long id) throws Exception {
        OrderDetail orderDetail = orderDetailService.getOrderDetail(id);
        return ResponseEntity.ok().body(OrderDetailResponse.fromOrderDetail(orderDetail));
    }

    //Lay ds orderdetail cua 1 order nào đó
    @GetMapping("order/{orderId}")
    public ResponseEntity<?> getOrderDetails(
            @Valid @PathVariable("orderId") Long orderId){
        try {
            List<OrderDetail> orderDetails = orderDetailService.findByOrderId(orderId);
            List<OrderDetailResponse> orderDetailResponses = orderDetails
                    .stream()
                    .map(OrderDetailResponse::fromOrderDetail)
                    .toList();
            return ResponseEntity.ok(orderDetailResponses);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetails(
            @Valid @PathVariable("id") Long id,
            @RequestBody OrderDetailDTO orderDetailDTO){
        try {
            OrderDetail orderDetail = orderDetailService.updateOrderDetail(id, orderDetailDTO);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetail(
            @Valid @PathVariable("id") Long id) {
        orderDetailService.deleteById(id);
        return ResponseEntity.ok()
                .body(localizationUtils
                        .getLocalizedMessage(MessageKeys.DELETE_ORDER_DETAIL_SUCCESSFULLY));
    }

}
