-- 插入测试订单数据
INSERT INTO `order` (order_no, user_id, total_amount, actual_amount, payment_status, order_status, shipping_address, receiver_name, receiver_phone)
VALUES 
('20260226000001', 1, 200.00, 200.00, 0, 0, '北京市朝阳区', '张三', '13800138000'),
('20260226000002', 1, 150.00, 150.00, 1, 1, '北京市海淀区', '李四', '13900139000');

-- 插入测试订单项数据
INSERT INTO `order_item` (order_id, product_id, product_name, price, quantity)
VALUES 
(1, 1, '测试商品1', 100.00, 2),
(2, 2, '测试商品2', 150.00, 1);
