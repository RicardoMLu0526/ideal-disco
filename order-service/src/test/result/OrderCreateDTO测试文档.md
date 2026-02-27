```bash
F:\Program\service>type order-service\src\main\java\com\pdd\order\dto\OrderCreateDTO.java
package com.pdd.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderCreateDTO {
    private Long userId;
    private List<OrderItemDTO> items;
    private Long addressId;
    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;
}

F:\Program\service>cd order-service

F:\Program\service\order-service>./gradlew test --tests "com.pdd.order.dto.OrderCreateDTOTest"

F:\Program\service\order-service>cd ..

F:\Program\service>
```