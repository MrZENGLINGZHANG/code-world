package com.sh.fbs.basic.user.domain;

import com.sh.fbs.basic.commom.ecode.BasicAppErrorCode;
import com.sh.fbs.commom.result.BizException;
import com.sh.fbs.commom.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserMgmtService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private GlobalUserIDGenerator globalUserIDGenerator;

    public Long registerUser(UserEntity userEntity) throws Exception{
        if (userRepo.existsByUsername(userEntity.getUserName())){
            throw new BizException(BasicAppErrorCode.USER_REGISTER_ERROR.getCode(),String.format(BasicAppErrorCode.USER_REGISTER_ERROR.getMessage(),userEntity.getUserName()));
        }
        if (userRepo.existsByNickname(userEntity.getNickName())){
            throw new BizException(BasicAppErrorCode.USER_REGISTER_ERROR.getCode(),String.format(BasicAppErrorCode.USER_REGISTER_ERROR.getMessage(),userEntity.getNickName()));
        }
        if (userRepo.existsByPhone(userEntity.getPhone())){
            throw new BizException(BasicAppErrorCode.USER_REGISTER_ERROR.getCode(),String.format(BasicAppErrorCode.USER_REGISTER_ERROR.getMessage(),userEntity.getPhone()));
        }
        userEntity.setUserId(globalUserIDGenerator.nextID());
        userEntity.setPwd(MD5Utils.Encrypt(userEntity.getPwd()));
        userRepo.save(userEntity);
        return userEntity.getUserId();
    }

    public void updateUserById(UserEntity userEntity) throws Exception{
        if(userEntity==null || userEntity.getUserId() == null){
            throw new BizException(BasicAppErrorCode.PARAMS_ERROR.getCode(),String.format(BasicAppErrorCode.PARAMS_ERROR.getMessage(),"userId"));
        }
        userRepo.updateById(userEntity);
    }

    public UserEntity getUserById(Long userId) throws Exception{
        if(userId == null){
            throw new BizException(BasicAppErrorCode.PARAMS_ERROR.getCode(),String.format(BasicAppErrorCode.PARAMS_ERROR.getMessage(),"userId"));
        }
        return userRepo.findById(userId);
    }

    public UserEntity getUserByUsername(String username) throws Exception{
        if(username == null){
            throw new BizException(BasicAppErrorCode.PARAMS_ERROR.getCode(),String.format(BasicAppErrorCode.PARAMS_ERROR.getMessage(),"username"));
        }
        return userRepo.findByUsername(username);
    }

    public UserEntity getUserByPhone(String phone) throws Exception{
        if(phone == null){
            throw new BizException(BasicAppErrorCode.PARAMS_ERROR.getCode(),String.format(BasicAppErrorCode.PARAMS_ERROR.getMessage(),"phone"));
        }
        return userRepo.findByPhone(phone);
    }

}
