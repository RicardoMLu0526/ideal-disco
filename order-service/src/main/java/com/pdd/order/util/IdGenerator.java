package com.pdd.order.util;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.stereotype.Component;

@Component
public class IdGenerator {

    /**
     * 生成分布式ID
     */
    public Long nextId() {
        return IdWorker.getId();
    }
}
