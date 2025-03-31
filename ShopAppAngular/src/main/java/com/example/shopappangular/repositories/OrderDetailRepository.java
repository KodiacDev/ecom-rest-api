package com.example.shopappangular.repositories;

import com.example.shopappangular.models.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> { //Tham số thứ hai <Long> chỉ ra kiểu dữ liệu của khóa chính của entity
    List<OrderDetail> findByOrderId(Long orderId);
}
