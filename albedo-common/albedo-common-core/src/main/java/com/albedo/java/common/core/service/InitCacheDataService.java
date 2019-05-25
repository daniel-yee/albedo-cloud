package com.albedo.java.common.core.service;


import com.albedo.java.common.core.util.CollUtil;
import com.albedo.java.common.core.constant.CommonConstants;
import com.albedo.java.common.core.util.SpringContextHolder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.CacheManager;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class InitCacheDataService implements InitializingBean {

	private final Environment environment;
	private final CacheManager cacheManager;
	private final RedisTemplate redisTemplate;

	@Override
	public void afterPropertiesSet(){
		Collection<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
		if(activeProfiles.contains(CommonConstants.SPRING_PROFILE_DEVELOPMENT)){
			Collection<String> cacheNames = cacheManager.getCacheNames();
			if(CollUtil.isNotEmpty(cacheNames)){
				for(String cacheName : cacheNames){
					cacheManager.getCache(cacheName).clear();
				}
			}
			Set keys = redisTemplate.keys("*details*");
			redisTemplate.delete(keys);
		}
	}
}