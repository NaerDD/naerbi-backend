package com.naer.springbootinit.manager;

import com.naer.springbootinit.common.ErrorCode;
import com.naer.springbootinit.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author NaerDD
 * @version 1.0
 * 专门提供redis基础服务的(提供了通用的能力)
 **/
@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 限流操作
     */
    public void doRateLimit(String key) {
        //创建一个限流器，每秒最多访问2次
        RRateLimiter rRateLimiter = redissonClient.getRateLimiter(key);
        rRateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);

        //每当一个操作来了后，请求一个令牌
        boolean b = rRateLimiter.tryAcquire(1);
        if (!b) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }
}
