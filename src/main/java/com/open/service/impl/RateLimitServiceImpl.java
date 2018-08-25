package com.open.service.impl;

import com.open.common.RedisProxy;
import com.open.constant.Constant;
import com.open.service.RateLimitService;

import java.util.List;

/**
 * @author Jing.Zhang
 * @date 2018/8/25
 */
public class RateLimitServiceImpl implements RateLimitService {

    private RedisProxy redisProxy;

    @Override
    public boolean exceedRateLimit(String appKey, String uri) {
        List<String> accessRecords = redisProxy.lrange(appKey + uri, 0, -1);
        if (null == accessRecords) {
            return false;
        }
        int size = accessRecords.size();
        return size > Constant.MAX_RATE;
    }

    @Override
    public void recordRequest(String appKey, String uri) {
        redisProxy.rpush(appKey + uri, String.valueOf(System.currentTimeMillis()));
        redisProxy.expire(appKey + uri, 1);
    }


}
