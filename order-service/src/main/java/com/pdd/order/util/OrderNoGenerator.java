package com.pdd.order.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class OrderNoGenerator {

    /**
     * 生成订单号
     */
    public static String generate() {
        // 订单号格式：年月日时分秒 + 6位随机数
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String datePart = sdf.format(new Date());
        String randomPart = String.format("%06d", new Random().nextInt(1000000));
        return datePart + randomPart;
    }
}
