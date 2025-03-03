package com.sh.fbs.basic.user.infra;


import com.sh.fbs.basic.user.domain.UserEntity;
import org.springframework.beans.BeanUtils;

public class UserConvertor {

    public static UserDO convert(UserEntity userEntity) {
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userEntity, userDO);
        return userDO;
    }
    public static UserEntity convert(UserDO userDO) {
        UserEntity userEntity = UserEntity.builder().build();
        BeanUtils.copyProperties(userDO, userEntity);
        return userEntity;
    }
}
