package com.sh.fbs.basic.user.domain;

public interface UserRepo {

    void save(UserEntity userEntity) throws Exception;

    void updateById(UserEntity userEntity) throws Exception;

    UserEntity findById(Long userId) throws Exception;

    UserEntity findByUsername(String username) throws Exception;

    UserEntity findByPhone(String phone) throws Exception;

    UserEntity findByNickname(String nickname) throws Exception;


    boolean existsByUsername(String username) throws Exception;
    boolean existsByPhone(String phone) throws Exception;
    boolean existsByNickname(String nickname) throws Exception;


}
