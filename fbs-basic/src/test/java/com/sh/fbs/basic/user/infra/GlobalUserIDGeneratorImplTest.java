package com.sh.fbs.basic.user.infra;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GlobalUserIDGeneratorImplTest {

    @Autowired
    private GlobalUserIDGeneratorImpl globalUserIDGenerator;

    @Test
    void nextID() throws Exception {
       long userId =  globalUserIDGenerator.nextID();
       System.out.println(userId);
    }

    @Test
    void testNextID() throws Exception {
        long[] ids = globalUserIDGenerator.nextID(5);
        System.out.println(ids);
    }
}