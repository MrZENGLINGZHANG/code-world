package com.sh.fbs.basic.user.infra;


import com.sh.fbs.basic.ObjectReflectFillUtils;
import com.sh.fbs.basic.user.domain.UserEntity;
import com.sh.fbs.commom.utils.MD5Utils;
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
       String str1="Yiran2023";
        String str2="Yiran2023";
        String st1= MD5Utils.encrypt(str1);
       String st2= MD5Utils.encrypt(str2);

       if (st1.equals(st2)) {
           System.out.println("true");
       }
    }

    @Test
    void findById() {
    }

    @Test
    void findByUsername() {
    }
}