package com.sh.fbs.basic.user.infra;

import com.alibaba.fastjson2.JSON;
import com.sh.fbs.basic.commom.constant.BasicAppConstant;
import com.sh.fbs.basic.commom.ecode.BasicAppErrorCode;
import com.sh.fbs.basic.user.domain.CaptchaCheckParam;
import com.sh.fbs.basic.user.domain.CaptchaGenParam;
import com.sh.fbs.basic.user.domain.CaptchaService;
import com.sh.fbs.commom.result.BizException;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.hash;

@Validated
@Slf4j
@Service
public class CaptchaMgmtServiceImpl implements CaptchaService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void generateCaptcha(@Valid CaptchaGenParam param) throws BizException {
        String captchaInfo = (String) redisTemplate.opsForHash().get(getHashKey(param.getUniqKey()),param.getUniqKey());
        if (!StringUtils.isBlank(captchaInfo)) {
            Map captchaMap = JSON.parseObject(captchaInfo,Map.class);
            if (Long.parseLong(captchaMap.get("expiration").toString()) >= System.currentTimeMillis()) {
                throw new BizException(BasicAppErrorCode.CAPTCHA_ALREADY_EXIST);
            }
        }
        Map<String,Long> captchaMap = new HashMap<>(2);
        String captcha = RandomStringUtils.randomNumeric(6);
        captchaMap.put("captcha",Long.valueOf(captcha));
        captchaMap.put("expiration",System.currentTimeMillis()+BasicAppConstant.USER_CAPTCHA_KEY_EXPIRE_TIME_MS);
        redisTemplate.opsForHash().put(getHashKey(param.getUniqKey()),param.getUniqKey(), JSON.toJSONString(captchaMap));
        log.info("generate captcha {} for user {}",captcha,param.getUniqKey());
        //TODO SEND TO USER by channel

    }

    @Override
    public boolean validateCaptcha(@Valid CaptchaCheckParam param) throws BizException {
        String captchaInfo = (String) redisTemplate.opsForHash().get(getHashKey(param.getUniqKey()),param.getUniqKey());
        if (StringUtils.isBlank(captchaInfo)) {
            throw new BizException(BasicAppErrorCode.CAPTCHA_NOT_EXIST);
        }
        Map captchaMap = JSON.parseObject(captchaInfo,Map.class);
        if (Long.parseLong(captchaMap.get("expiration").toString()) <= System.currentTimeMillis()) {
            redisTemplate.opsForHash().delete(getHashKey(param.getUniqKey()),param.getUniqKey());
            throw new BizException(BasicAppErrorCode.CAPTCHA_EXPIRED);
        }
        if (param.getCaptcha().equals(captchaMap.get("captcha").toString())) {
            redisTemplate.opsForHash().delete(getHashKey(param.getUniqKey()),param.getUniqKey());
            return true;
        }
        throw new BizException(BasicAppErrorCode.CAPTCHA_ERROR);
    }

    private String getHashKey(String uniqKey) {
        int index = hash(uniqKey)% BasicAppConstant.USER_CAPTCHA_NODE_KEY_NUM;
        return String.format(BasicAppConstant.USER_CAPTCHA_HASH_KEY, index);
    }
}
