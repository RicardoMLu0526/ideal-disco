```bash
F:\Program\service>type order-service\src\main\java\com\pdd\order\dto\OrderItemDTO.java
package com.pdd.order.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Long productId;
    private Integer quantity;
}

F:\Program\service>cd order-service

F:\Program\service\order-service>./gradlew test --tests "com.pdd.order.dto.OrderItemDTOTest"

F:\Program\service\order-service>cd ..

F:\Program\service>
```