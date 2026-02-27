-- 创建数据库
CREATE DATABASE IF NOT EXISTS `pdd_order` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `pdd_order`;

-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_no` VARCHAR(32) UNIQUE NOT NULL,
  `user_id` BIGINT NOT NULL,
  `total_amount` DECIMAL(10,2) NOT NULL,
  `actual_amount` DECIMAL(10,2) NOT NULL,
  `payment_status` TINYINT DEFAULT 0,
  `order_status` TINYINT DEFAULT 0,
  `shipping_address` VARCHAR(255) NOT NULL,
  `receiver_name` VARCHAR(50) NOT NULL,
  `receiver_phone` VARCHAR(20) NOT NULL,
  `payment_time` TIMESTAMP,
  `shipping_time` TIMESTAMP,
  `confirm_time` TIMESTAMP,
  `cancel_time` TIMESTAMP,
  `tracking_number` VARCHAR(100),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 订单项表
CREATE TABLE IF NOT EXISTS `order_item` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `product_sku_id` BIGINT,
  `product_name` VARCHAR(200) NOT NULL,
  `product_sku` VARCHAR(255),
  `price` DECIMAL(10,2) NOT NULL,
  `quantity` INT NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`order_id`) REFERENCES `order`(`id`) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX idx_order_user_id ON `order`(`user_id`);
CREATE INDEX idx_order_order_no ON `order`(`order_no`);
CREATE INDEX idx_order_item_order_id ON `order_item`(`order_id`);
CREATE INDEX idx_order_item_product_id ON `order_item`(`product_id`);
