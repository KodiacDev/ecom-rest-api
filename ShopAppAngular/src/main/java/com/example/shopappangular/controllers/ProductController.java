package com.example.shopappangular.controllers;

import com.example.shopappangular.components.LocalizationUtils;
import com.example.shopappangular.dtos.ProductDTO;
import com.example.shopappangular.dtos.ProductImageDTO;
import com.example.shopappangular.exceptions.DataNotFoundException;
import com.example.shopappangular.models.Product;
import com.example.shopappangular.models.ProductImage;
import com.example.shopappangular.responses.ProductListResponse;
import com.example.shopappangular.responses.ProductResponse;
import com.example.shopappangular.services.ProductService;
import com.example.shopappangular.utils.MessageKeys;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/products")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    private final LocalizationUtils localizationUtils;

    //POST http://localhost:8088/api/v1/products
    @PostMapping("")
    //POST http://localhost:8088/v1/api/products
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult result
    ) {
        try {
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Product newProduct = productService.createProduct(productDTO);
            return ResponseEntity.ok(newProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "uploads/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //POST http://localhost:8088/v1/api/products
    public ResponseEntity<?> uploadImages(
            @PathVariable("id") Long productId,
            @ModelAttribute("files") List<MultipartFile> files
    ){
        try {
            Product existingProduct = productService.getProductById(productId);
            files = files == null ? new ArrayList<MultipartFile>() : files;
            if(files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
                return ResponseEntity.badRequest().body(localizationUtils
                        .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_MAX_5));
            }
            List<ProductImage> productImages = new ArrayList<>();
            for (MultipartFile file : files) {
                if(file.getSize() == 0) {
                    continue;
                }
                // Kiểm tra kích thước file và định dạng
                if(file.getSize() > 10 * 1024 * 1024) { // Kích thước > 10MB
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body(localizationUtils
                                    .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_LARGE));
                }
                String contentType = file.getContentType();
                if(contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE));
                }
                // Lưu file và cập nhật thumbnail trong DTO
                String filename = storeFile(file); // Thay thế hàm này với code của bạn để lưu file
                //lưu vào đối tượng productImage trong DB
                ProductImage productImage = productService.createProductImage(
                        existingProduct.getId(),
                        ProductImageDTO.builder()
                                .imageUrl(filename)
                                .build()
                );
                //Sau cùng danh sách này được trả về cho API response.
                productImages.add(productImage);
            }
            return ResponseEntity.ok().body(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
            java.nio.file.Path imagePath = Paths.get("uploads/"+imageName); //đường dẫn chi tiết
            UrlResource resource = new UrlResource(imagePath.toUri());
            //resource = URL [file:///C:/Users/a/IdeaProjects/ShopApp/shopapp-backend/uploads/06f0b1f9-1799-46c8-a4ae-49c7fe05a4a8_xuc.jpg]
            //dùng cửa sổ run là hiện ra ngay
            //URI là định dạng chuẩn để xác định và truy cập tài nguyên trên mạng,
            // bao gồm các thông tin như giao thức, host, đường dẫn url...

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpeg").toUri()));
                //return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Chú ý nếu 2 user upload lên 1 ảnh trùng tên sẽ bị ghi đè mặc dù khác nội dung ảnh
    private String storeFile(MultipartFile file) throws IOException {
        if (!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Invalid image format");
        }
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())); //StringUtils.cleanPath() xử lý tên file bằng cách:Loại bỏ đường dẫn trong tên file
        // Thêm UUID vào trước tên file để đảm bảo tên file là duy nhất
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        // Đường dẫn đến thư mục mà bạn muốn lưu file
        java.nio.file.Path uploadDir = Paths.get("uploads");
        // Kiểm tra và tạo thư mục nếu nó không tồn tại
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        // Đường dẫn đầy đủ đến file
        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        // Sao chép file vào thư mục đích
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }
    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/"); //tránh trường hợp upload các file khác như PDF, DOC, Excel lên.
    }

    @GetMapping("")
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ){
        //Tạo pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page,limit,
                //Sort.by("createdAt").descending());
                Sort.by("id").ascending());
        logger.info(String.format("keyword = %s, category_id = %d, page = %d, limit = %d",
                keyword, categoryId, page, limit));
        Page<ProductResponse> productPage = productService.getAllProducts(keyword, categoryId, pageRequest);
        //Lấy tổng số trang
        int totalPages = productPage.getTotalPages();
        List<ProductResponse> products = productPage.getContent();
        return ResponseEntity.ok(ProductListResponse.builder()
                .products(products)
                .totalPages(totalPages)
                .build());
    }

//    {
//        "name":"ipad pro 2023",0e06fbe5-89bd-429e-b647-19d57b25861c_65cf789d-ed86-4fd7-8030-2a79af81a88b_070
//            "price":82.23,
//            "thumbnail":"",
//            "description":"1234",
//            "category_id": 1
//    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long ProductId){
        try {
            Product existingProduct = productService.getProductById(ProductId);
            return ResponseEntity.ok(ProductResponse.fromProduct(existingProduct));
        }catch (StackOverflowError | Exception e){
            StackTraceElement[] stackTrace = e.getStackTrace();
            for(StackTraceElement ste : stackTrace) {
                System.out.println(ste);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/by-ids")
    public ResponseEntity<?> getProductsByIds(@RequestParam("ids") String ids) {
        //eg: 1,3,5,7
        try {
            // Tách chuỗi ids thành một mảng các số nguyên
            List<Long> productIds = Arrays.stream(ids.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<Product> products = productService.findProductByIds(productIds);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletedProductById(@PathVariable("id") Long ProductId){
        try {
            productService.deleteProduct(ProductId);
            return ResponseEntity.ok(String.format("Product with id = %d deleted successfully.", ProductId));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //update a product
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable long id,
            @RequestBody ProductDTO productDTO) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDTO);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

//    @PostMapping("/generateFakeProducts")
//    private ResponseEntity<String> generateFakeProducts() {
//        Faker faker = new Faker();
//        for (int i = 0; i < 1_000_000; i++) {
//            String productName = faker.commerce().productName();
//            if(productService.existsByName(productName)) {
//                continue;
//            }
//            ProductDTO productDTO = ProductDTO.builder()
//                    .name(productName) //fakerName
//                    .price((float)faker.number().numberBetween(10, 90_000_000))
//                    .description(faker.lorem().sentence())
//                    .thumbnail("")
//                    .categoryId((long)faker.number().numberBetween(2, 5))
//                    .build();
//            try {
//                productService.createProduct(productDTO);
//            } catch (Exception e) {
//                return ResponseEntity.badRequest().body(e.getMessage());
//            }
//        }
//        return ResponseEntity.ok("Fake Products created successfully");
//    }

}
