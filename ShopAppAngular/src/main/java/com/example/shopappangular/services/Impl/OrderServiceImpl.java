package com.example.shopappangular.services.Impl;

import com.example.shopappangular.dtos.CartItemDTO;
import com.example.shopappangular.dtos.OrderDTO;
import com.example.shopappangular.exceptions.DataNotFoundException;
import com.example.shopappangular.models.*;
import com.example.shopappangular.repositories.OrderDetailRepository;
import com.example.shopappangular.repositories.OrderRepository;
import com.example.shopappangular.repositories.ProductRepository;
import com.example.shopappangular.repositories.UserRepository;
import com.example.shopappangular.services.OrderService;
import jdk.jshell.Snippet;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;

    private final ModelMapper modelMapper;
    @Override
    @Transactional
    public Order createOrder(OrderDTO orderDTO) throws Exception {
        //Tìm xem userId có tồn tại không
        User user = userRepository
                .findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find user with id: "+orderDTO.getUserId()));
        //convert orderDTO => Order
        //dùng thư viện Model Mapper
        // Tạo một luồng bảng ánh xạ riêng để kiểm soát việc ánh xạ
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));//skip qua trường id
        // Cập nhật các trường của đơn hàng từ orderDTO
        Order order = new Order();
        modelMapper.map(orderDTO, order); //khúc này đẩy dữ liệu từ orderDTO sang order
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());//lấy thời điểm hiện tại
        //Kiểm tra shipping date phải >= ngày hôm nay
        //Phương thức isBefore() kiểm tra ngày đầu tiên có nhỏ hơn ngày thứ hai hay không.
        LocalDate shippingDate = orderDTO.getShippingDate() == null
                ? LocalDate.now() : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Date must be at least today !");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);//đoạn này nên set sẵn trong sql
        order.setTotalMoney(orderDTO.getTotalMoney());
        orderRepository.save(order);
        //Tạo danh sách các đối tượng OrderDetail từ cartItems
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
            // Tạo một đối tượng OrderDetail từ CartItemDTO
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            // Lấy thông tin sản phẩm từ cartItemDTO
            Long productId = cartItemDTO.getProductId();
            int quantity = cartItemDTO.getQuantity();

            // Tìm thông tin sản phẩm từ cơ sở dữ liệu (hoặc sử dụng cache nếu cần)
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new DataNotFoundException("Product not found with id: " + productId));

            // Đặt thông tin cho OrderDetail
            orderDetail.setProduct(product);
            orderDetail.setNumberOfProducts(quantity);
            // Các trường khác của OrderDetail nếu cần
            orderDetail.setPrice(product.getPrice());

            // Thêm OrderDetail vào danh sách
            orderDetails.add(orderDetail);
        }
        // Lưu danh sách OrderDetail vào cơ sở dữ liệu
        orderDetailRepository.saveAll(orderDetails);
        return order;
    }

    @Override
    public Order getOrder(Long id) {
        Order selectedOrder = orderRepository.findById(id).orElse(null); //mếu k thấy thì trả về null
        return selectedOrder;
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException{
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new DateTimeException("Cannot find order with id: + " + id));
        User existingUser = userRepository.findById(orderDTO.getUserId()).orElseThrow(()->
                new DateTimeException("Cannot find user with id: + " + id));
        // Tạo một luồng bảng ánh xạ riêng để kiểm soát việc ánh xạ
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        // Cập nhật các trường của đơn hàng từ orderDTO
        modelMapper.map(orderDTO, order);
        order.setUser(existingUser);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void deletedOrder(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        //no hard-delete, => please soft-delete
        if(order != null){
            order.setActive(false);
            orderRepository.save(order);
        }
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findAll();
    }

    @Override
    public Page<Order> getOrdersByKeyword(String keyword, Pageable pageable) {
        return orderRepository.findByKeyword(keyword, pageable);
    }
}
