package com.sh.fbs.basic.user.infra;



import com.sh.fbs.basic.user.domain.GlobalUserIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class GlobalUserIDGeneratorImpl implements GlobalUserIDGenerator {

    private static final String GLOBAL_USER_ID_GEN_KEY = "GLOBAL_USER_ID_GEN_KEY";

    private static boolean  IS_KEY_EXIST = false;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public long nextID() throws Exception{
        if (!IS_KEY_EXIST) {
            initIfNotExists();
        }
        return redisTemplate.opsForValue().increment(GLOBAL_USER_ID_GEN_KEY);
    }

    public long[] nextID(int nums) throws Exception{
        if (!IS_KEY_EXIST) {
            initIfNotExists();
        }
        long max = redisTemplate.opsForValue().increment(GLOBAL_USER_ID_GEN_KEY, nums);
        long[] ids = new long[nums];
        for (int i = 0; i < nums; i++) {
            ids[i] = max - i;
        }
        return ids;

    }

    private void  initIfNotExists() {
        if (!redisTemplate.hasKey(GLOBAL_USER_ID_GEN_KEY)) {
            //TODO 分布式锁+查询最大Id
            redisTemplate.opsForValue().set(GLOBAL_USER_ID_GEN_KEY, "0");
        }
        IS_KEY_EXIST = true;
    }
}
