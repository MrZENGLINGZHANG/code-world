package com.sh.fbs.oms.orders.infra.impl.db;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@SpringBootTest
class FulfilOrderDOMapperTest {

    @Autowired
    private FulfilOrderDOMapper fulfilOrderDOMapper;

    @Test
    void insert() {
        for (int i = 0; i < 10000; i++) {
            FulfilOrderDO entity = new FulfilOrderDO();
            entity.setOrderId("OR202511245" + i);
            entity.setShopId(100001121);
            entity.setShopName("Hello Kitty Official Shop");
            entity.setUserId(10000000 + i);
            entity.setUserName("Kevin" + i + "MOUSE");
            entity.setOrderStatus((short) 1);
            entity.setOrderAmount(BigDecimal.valueOf(i));
            entity.setDiscountAmount(BigDecimal.valueOf(i));
            entity.setPaidAmount(BigDecimal.valueOf(i));
            entity.setItemLines(4);
            entity.setPaymentMethod("Wechat");
            entity.setShippingAddress("SHENZHEN CITY 10086,CHINA");

            entity.setCreateTime(new Date());
            entity.setUpdateTime(new Date());
            fulfilOrderDOMapper.insert(entity);
        }
    }

    @Test
    void selectOne() {
        for (int i = 0; i < 10000; i++) {
            Wrapper<FulfilOrderDO> queryWrapper = Wrappers.<FulfilOrderDO>lambdaQuery()
                    .eq(FulfilOrderDO::getOrderId, "OR202511245" + i)
                    .eq(FulfilOrderDO::getShopId, 100001121);
            FulfilOrderDO orderDO = fulfilOrderDOMapper.selectOne(queryWrapper);
            log.info(orderDO.toString());
        }
    }
}