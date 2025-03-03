package com.sh.fbs.basic.user.infra;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.sh.fbs.basic.user.domain.UserEntity;
import com.sh.fbs.basic.user.domain.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepoImplDB implements UserRepo {
    @Autowired
    private UserMapper userMapper;

    @Override
    public void save(UserEntity userEntity) throws Exception {
        userMapper.insert(UserConvertor.convert(userEntity));
    }

    @Override
    public void updateById(UserEntity userEntity) {
        userMapper.updateById(UserConvertor.convert(userEntity));
    }

    @Override
    public UserEntity findById(Long userId) throws Exception {
        UserDO userDO =  userMapper.selectById(userId);
        return UserConvertor.convert(userDO);
    }
    @Override
    public UserEntity findByUsername(String username) {
        UserDO userDO = userMapper.selectOne(Wrappers.<UserDO>lambdaQuery().eq(UserDO::getUserName, username));
        return UserConvertor.convert(userDO);
    }

    @Override
    public UserEntity  findByPhone(String phone) {
        UserDO userDO = userMapper.selectOne(Wrappers.<UserDO>lambdaQuery().eq(UserDO::getPhone, phone));
        return UserConvertor.convert(userDO);
    }

    @Override
    public UserEntity findByNickname(String nickname) {
        // TODO 非sharding Key的点查，全部要基于Redis全局映射转成UserId查询
        UserDO userDO = userMapper.selectOne(Wrappers.<UserDO>lambdaQuery().eq(UserDO::getNickName, nickname));
        return UserConvertor.convert(userDO);
    }

    @Override
    public boolean existsByUsername(String username) {
        // TODO 因为这里是UserId分片，这里的关于非UserID查询全部走基于Redis的bloomFilter
        return userMapper.exists(Wrappers.<UserDO>lambdaQuery().eq(UserDO::getUserName, username));

    }

    @Override
    public boolean existsByPhone(String phone) {
        return userMapper.exists(Wrappers.<UserDO>lambdaQuery().eq(UserDO::getPhone, phone));
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return userMapper.exists(Wrappers.<UserDO>lambdaQuery().eq(UserDO::getNickName, nickname));
    }

}
