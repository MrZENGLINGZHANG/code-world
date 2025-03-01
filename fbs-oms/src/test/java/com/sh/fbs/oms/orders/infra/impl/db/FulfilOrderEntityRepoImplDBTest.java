package com.sh.fbs.oms.orders.infra.impl.db;

import com.sh.fbs.oms.orders.domain.model.FulfilOrderEntity;
import com.sh.fbs.oms.orders.domain.repo.FulfilOrderEntityRepo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class FulfilOrderEntityRepoImplDBTest {

    private static final Logger log = LoggerFactory.getLogger(FulfilOrderEntityRepoImplDBTest.class);

    @Autowired
    private FulfilOrderEntityRepoImplDB fulfilOrderEntityRepo;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findById() {
        String id = "1";
        FulfilOrderEntity entity = fulfilOrderEntityRepo.findById(id,1);
        log.info(entity.toString());
    }

    @Test
    void save() {
        for (int i = 100001; i < 1000000; i++) {
            FulfilOrderEntity entity = new FulfilOrderEntity();
            entity.setOrderId("OR202511245"+ i);
            entity.setShopId(100001121);
            entity.setShopName("Hello Kitty Official Shop");
            entity.setUserId(10000000+i);
            entity.setUserName("Kevin"+i+"MOUSE");
            entity.setOrderStatus((short)1);
            entity.setOrderAmount(BigDecimal.valueOf(i));
            entity.setDiscountAmount(BigDecimal.valueOf(i));
            entity.setPaidAmount(BigDecimal.valueOf(i));
            entity.setItemLines(4);
            entity.setPaymentMethod("Wechat");
            entity.setShippingAddress("SHENZHEN CITY 10086,CHINA");

            entity.setCreateTime(new Date());
            entity.setUpdateTime(new Date());
            fulfilOrderEntityRepo.save(entity);
        }


    }
}