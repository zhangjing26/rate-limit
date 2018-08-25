package com.open.service;

/**
 * @author Jing.Zhang
 * @date 2018/8/25
 */
public interface RateLimitService {

    boolean exceedRateLimit(String appKey, String uri);

    void recordRequest(String appKey, String uri);
}
