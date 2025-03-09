package com.sh.fbs.basic.user.domain;

import com.sh.fbs.basic.commom.ecode.BasicAppErrorCode;
import com.sh.fbs.commom.result.BizException;
import com.sh.fbs.commom.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public class UserMgmtService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private GlobalUserIDGenerator globalUserIDGenerator;

    public Long registerUser(UserEntity userEntity) throws Exception{
        if (userRepo.existsByUsername(userEntity.getUsername())){
            throw new BizException(BasicAppErrorCode.USER_REGISTER_ERROR.getCode(),String.format(BasicAppErrorCode.USER_REGISTER_ERROR.getMessage(),userEntity.getUsername()));
        }
        if (userRepo.existsByNickname(userEntity.getNickname())){
            throw new BizException(BasicAppErrorCode.USER_REGISTER_ERROR.getCode(),String.format(BasicAppErrorCode.USER_REGISTER_ERROR.getMessage(),userEntity.getNickname()));
        }
        if (userRepo.existsByPhone(userEntity.getPhone())){
            throw new BizException(BasicAppErrorCode.USER_REGISTER_ERROR.getCode(),String.format(BasicAppErrorCode.USER_REGISTER_ERROR.getMessage(),userEntity.getPhone()));
        }
        userEntity.setUserId(globalUserIDGenerator.nextID());
        String encryptPwd =MD5Utils.encrypt(userEntity.getPwd());
        userEntity.setPwd(encryptPwd);
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

    /**
     * 修改用户密码
     * @param userId 用户ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @throws Exception 修改密码过程中的异常
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) throws Exception {
        // 获取用户信息
        UserEntity userEntity = getUserById(userId);
        if (userEntity == null) {
            throw new BizException(BasicAppErrorCode.USER_NOT_EXIST);
        }

        // 验证原密码
        String encryptedOldPassword = MD5Utils.encrypt(oldPassword.trim());
        if (!encryptedOldPassword.equals(userEntity.getPwd())) {
            throw new BizException(BasicAppErrorCode.PASSWORD_ERROR);
        }

        // 更新新密码
        userEntity.setPwd(MD5Utils.encrypt(newPassword.trim()));
        userRepo.updateById(userEntity);
        
        log.info("Password changed successfully for user: {}", userId);
    }

    /**
     * Update user profile information
     *
     * @param userId user ID
     * @param nickname new nickname
     * @param phone new phone number
     * @param icon new icon URL
     * @param sex new sex value
     * @param birthDate new birth date
     * @param area new area
     * @throws Exception if update fails
     */
    public void updateUserProfile(Long userId, String nickname, String phone, 
                                String icon, int sex, Date birthDate, String area) throws Exception {
        // 获取用户信息
        UserEntity userEntity = getUserById(userId);
        if (userEntity == null) {
            throw new BizException(BasicAppErrorCode.USER_NOT_EXIST);
        }

        // 检查手机号是否被其他用户使用
        if (!phone.equals(userEntity.getPhone()) && userRepo.existsByPhone(phone)) {
            throw new BizException(BasicAppErrorCode.USER_REGISTER_ERROR.getCode(),
                    String.format(BasicAppErrorCode.USER_REGISTER_ERROR.getMessage(), phone));
        }

        // 检查昵称是否被其他用户使用
        if (!nickname.equals(userEntity.getNickname()) && userRepo.existsByNickname(nickname)) {
            throw new BizException(BasicAppErrorCode.USER_REGISTER_ERROR.getCode(),
                    String.format(BasicAppErrorCode.USER_REGISTER_ERROR.getMessage(), nickname));
        }

        // 更新用户信息
        userEntity.setNickname(nickname);
        userEntity.setPhone(phone);
        userEntity.setIcon(icon);
        userEntity.setSex(sex);
        userEntity.setBirthDate(birthDate);
        userEntity.setArea(area);
        userEntity.setUpdateTime(new Date());

        userRepo.updateById(userEntity);
        log.info("User profile updated successfully for user: {}", userId);
    }
}
