CREATE DATABASE ShopAppAngular;
use ShopAppAngular;

CREATE TABLE users(
    id int primary key AUTO_INCREMENT,
    fullname VARCHAR(100) default '',
    phone_number VARCHAR(10) not null,
    address VARCHAR(200) default '',
    password VARCHAR(100) not null default '',
    create_at datetime,
    updated_at datetime,
    is_active tinyint(1) default 1,
    date_of_birth date,
    facebook_account_id int default 0,
    google_account_id int default 0,
    role_id int not null default 1
    foreign key (role_id) references roles(id)
);

CREATE TABLE product_image(
    id int primary key AUTO_INCREMENT,
    product_id int,
    foreign key (product_id) references products(id),
    constraint fk_product_images_product_id
    foreign key (product_id) references products(id)  on delete cascade //xóa ảnh trog đây bay luôn trong product
);

CREATE TABLE roles(
    id int primary key not null,
    name VARCHAR(20) not null
)

CREATE TABLE tokens(
    id int primary key AUTO_INCREMENT,
    token VARCHAR(255) unique not null,
    token_type VARCHAR(50) not null,
    expiration_date datetime,
    revoked tinyint(1) not null,
    expired tinyint(1) not null,
    user_id int,
    foreign key (user_id) references users(id)
); 
--Hỗ trợ đăng nhập từ fb and gg
CREATE TABLE social_accounts(
    id int primary key AUTO_INCREMENT,
    provider VARCHAR(20) not null COMMENT 'Tên nhà social network',
    provider_id VARCHAR(50) not null,
    email VARCHAR(150) not null COMMENT 'Email tài khoản',
    name VARCHAR(100) not null COMMENT 'Tên người dùng',
    user_id int,
    foreign key (user_id) references users(id)
);

CREATE TABLE categories(
    id int primary key AUTO_INCREMENT,
    name VARCHAR(100) not null default '' COMMENT 'Tên danh mục'
);

CREATE TABLE products(
    id int primary key AUTO_INCREMENT,
    name VARCHAR(350) COMMENT 'Tên sản phẩm',
    price float not null check(price >= 0),
    thumbnail VARCHAR(300) default '',
    description LONGTEXT default '',
    create_at datetime,
    updated_at datetime,
    category_id int,
    foreign key (category_id) references categories(id)
);

CREATE TABLE orders(
    id int primary key AUTO_INCREMENT,
    user_id int,
    fullname VARCHAR(100) default '',
    email VARCHAR(100) default '',
    phone_number VARCHAR(20) not null,
    address VARCHAR(200) not null, --dia chi noi gui
    note VARCHAR(100) default '',
    order_date datetime default current_timestamp,
    shipping_method VARCHAR(100),
    shipping_address VARCHAR(200), --dia chi noi toi
    shipping_date date, --ngay nao den
    tracking_numer VARCHAR(100), --so van don
    payment_method VARCHAR(100), --phuong thuc thanh toan
    is_active tinyint(1),
    status VARCHAR(20),
    total_money float check(total_money >= 0),
    foreign key (user_id) references users(id),
    MODIFY status enum('pending','processing','shipped','delivered','cancelled') COMMENT 'Trạng thái đơn hàng',
);

CREATE TABLE order_details(
    id int primary key AUTO_INCREMENT,
    order_id int,
    product_id int,
    price float check(price >= 0),
    number_of_products int check(number_of_products > 0), -- tong san pham
    total_money float check(total_money >= 0),
    color VARCHAR(20) default '',
    foreign key (order_id) references orders(id),
    foreign key (product_id) references products(id)
);
--truy van

update products --gan thumbnail cho bang products
set thumbnail = (
    select img_url
    from product_images
    where product_id = product_images.product_id
    limit 1
    );

update products
set price = round(price / 1000000, 1) where price > 1000000 -- lệnh chỉnh giá cho đẹp