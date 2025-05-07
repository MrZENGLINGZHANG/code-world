package com.sh.fbs.basic.user.domain;

import com.alibaba.fastjson2.JSON;
import com.sh.fbs.basic.commom.constant.BasicAppConstant;
import com.sh.fbs.basic.commom.ecode.BasicAppErrorCode;
import com.sh.fbs.basic.commom.utils.DistributedReadWriteLock;
import com.sh.fbs.commom.result.BizException;
import com.sh.fbs.commom.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.hash;

@Slf4j
@Service
public class UserMgmtService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private GlobalUserIDGenerator globalUserIDGenerator;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String USER_CACHE_KEY = "fbs:user:info:%s";
    private static final int USER_CACHE_SHARD_NUM = 16;
    private static final long USER_CACHE_EXPIRE_HOURS = 24;
    
    // 使用ConcurrentHashMap存储用户缓存状态，true表示缓存可用
    private static final ConcurrentHashMap<Long, Boolean> userCacheStatus = new ConcurrentHashMap<>();

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

    public void updateUserById(UserEntity userEntity) throws Exception {
        if(userEntity == null || userEntity.getUserId() == null) {
            throw new BizException(BasicAppErrorCode.PARAMS_ERROR.getCode(),
                    String.format(BasicAppErrorCode.PARAMS_ERROR.getMessage(), "userId"));
        }

        // 更新本地缓存状态
        userCacheStatus.remove(userEntity.getUserId());

        // 第一次删除缓存
        String cacheKey = getHashedUserCacheKey(userEntity.getUserId());
        redisTemplate.opsForHash().delete(cacheKey, userEntity.getUserId().toString());
        log.debug("First delete user cache before update, userId: {}", userEntity.getUserId());

        // 更新数据库
        userRepo.updateById(userEntity);

        // 延迟双删
        try {
            Thread.sleep(BasicAppConstant.CACHE_DELETE_DELAY_MS);
            redisTemplate.opsForHash().delete(cacheKey, userEntity.getUserId().toString());
            log.debug("Second delete user cache after update, userId: {}", userEntity.getUserId());
        } catch (InterruptedException e) {
            log.error("Failed to perform second cache deletion for userId: {}", userEntity.getUserId(), e);
            Thread.currentThread().interrupt();
        }
    }

    public UserEntity getUserById(Long userId) throws Exception {
        if(userId == null) {
            throw new BizException(BasicAppErrorCode.PARAMS_ERROR.getCode(),
                    String.format(BasicAppErrorCode.PARAMS_ERROR.getMessage(), "userId"));
        }

        // 优先检查本地缓存状态
        Boolean localStatus = userCacheStatus.get(userId);
        String cacheKey = getHashedUserCacheKey(userId);
        
        // 如果本地状态为true，说明缓存一定可用
        if (Boolean.TRUE.equals(localStatus)) {
            String userJson = (String) redisTemplate.opsForHash().get(cacheKey, userId.toString());
            if (StringUtils.isNotBlank(userJson)) {
                log.debug("Cache hit with local status, userId: {}", userId);
                return JSON.parseObject(userJson, UserEntity.class);
            }
            // 缓存不存在，可能被删除了，清除本地状态
            userCacheStatus.remove(userId);
        }

        // 检查Redis缓存
        String userJson = (String) redisTemplate.opsForHash().get(cacheKey, userId.toString());
        if (StringUtils.isNotBlank(userJson)) {
            String lockKey = String.format(BasicAppConstant.USER_CACHE_LOCK_KEY, userId);
            DistributedReadWriteLock lock = new DistributedReadWriteLock(redisTemplate, lockKey, 
                    BasicAppConstant.USER_CACHE_LOCK_EXPIRE_SECONDS);
            
            // 尝试获取锁，检查状态
            int lockResult = lock.tryLock();
            if (lockResult == 0) {
                // 已经是共享状态，可以直接使用缓存
                userCacheStatus.put(userId, true);
                log.debug("Cache hit with shared state, userId: {}", userId);
                return JSON.parseObject(userJson, UserEntity.class);
            }
        }

        // 缓存不存在或非共享状态，走更新流程
        String lockKey = String.format(BasicAppConstant.USER_CACHE_LOCK_KEY, userId);
        DistributedReadWriteLock lock = new DistributedReadWriteLock(redisTemplate, lockKey, 
                BasicAppConstant.USER_CACHE_LOCK_EXPIRE_SECONDS);

        int lockResult = lock.tryLock();
        if (lockResult == 0) {
            // 已经是共享状态，重新获取缓存
            userJson = (String) redisTemplate.opsForHash().get(cacheKey, userId.toString());
            if (StringUtils.isNotBlank(userJson)) {
                userCacheStatus.put(userId, true);
                return JSON.parseObject(userJson, UserEntity.class);
            }
        } else if (lockResult == -1) {
            // 获取锁失败，说明其他线程正在更新，等待后重试
            Thread.sleep(100);
            return getUserById(userId);
        }

        try {
            // 获得独占锁，双重检查
            userJson = (String) redisTemplate.opsForHash().get(cacheKey, userId.toString());
            if (StringUtils.isNotBlank(userJson)) {
                lock.upgradeToShared();
                userCacheStatus.put(userId, true);
                return JSON.parseObject(userJson, UserEntity.class);
            }

            // 从数据库获取并更新缓存
            UserEntity userEntity = userRepo.findById(userId);
            if (userEntity != null) {
                redisTemplate.opsForHash().put(cacheKey, userId.toString(), JSON.toJSONString(userEntity));
                redisTemplate.expire(cacheKey, BasicAppConstant.USER_CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
                log.debug("Cache miss, updated cache for userId: {}", userId);
                
                lock.upgradeToShared();
                userCacheStatus.put(userId, true);
                return userEntity;
            }
            return null;
        } finally {
            lock.release();
        }
    }

    /**
     * 根据userId获取hash后的缓存key
     */
    private String getHashedUserCacheKey(Long userId) {
        int hashIndex = hash(userId) % BasicAppConstant.USER_CACHE_SHARD_NUM;
        return String.format(BasicAppConstant.USER_CACHE_KEY, hashIndex);
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
