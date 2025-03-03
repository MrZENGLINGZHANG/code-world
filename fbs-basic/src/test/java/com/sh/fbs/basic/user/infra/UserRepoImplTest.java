package com.sh.fbs.basic.user.infra;


import com.sh.fbs.basic.ObjectReflectFillUtils;
import com.sh.fbs.basic.user.domain.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserRepoImplTest {

    @Autowired
    private UserRepoImplDB userRepo;

    @Autowired
    private GlobalUserIDGeneratorImpl globalUserIDGenerator ;

    @Test
    void save() throws Exception {
        for (int i = 0; i < 10; i++) {
            UserEntity user = UserEntity.builder().build();
            ObjectReflectFillUtils.fillObject(user);
            user.setUserId(globalUserIDGenerator.nextID());
            System.out.println(user);
            userRepo.save(user);
        }
    }

    @Test
    void updateById() {
    }

    @Test
    void findById() {
    }

    @Test
    void findByUsername() {
    }
}