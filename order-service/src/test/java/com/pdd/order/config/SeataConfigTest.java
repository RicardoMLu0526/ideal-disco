package com.pdd.order.config;

import io.seata.spring.annotation.GlobalTransactionScanner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SeataConfigTest {

    @Autowired
    private GlobalTransactionScanner globalTransactionScanner;

    @Test
    void testGlobalTransactionScannerBean() {
        assertNotNull(globalTransactionScanner, "GlobalTransactionScanner bean should be created");
    }
}
