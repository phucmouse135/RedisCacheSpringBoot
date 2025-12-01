package org.example.rediscache.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class MultiLevelCacheService {
    private final RedisTemplate<String , Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String INVALIDATE_TOPIC = "cache:invalidate";

    private final Cache<String , Object > localCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(1 , TimeUnit.MINUTES)
            .build();

    public <T> void set(String key , T value , Long timeout , TimeUnit timeUnit) {
        String jsonValue = null;
        try {
            jsonValue = objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        // Set in local cache
        localCache.put(key, jsonValue);

        long timeoutMillis = timeUnit.toMillis(timeout);
        long maxJitter = Math.min(timeoutMillis, 60_000L); // jitter at most 60s or timeoutMillis
        long jitterMillis = ThreadLocalRandom.current().nextLong(0, maxJitter + 1);
        long ttlMillis = timeoutMillis + jitterMillis;
        if (ttlMillis > 0) {
            redisTemplate.opsForValue().set(key, jsonValue, ttlMillis, TimeUnit.MILLISECONDS);
            return;
        }
    }

    public <T> T get(String key , Class<T> clazz){
        // try to get from local cache
        Object value = localCache.getIfPresent(key);
        if(value != null){
            log.info("Fetching key {} from local cache", key);
            try {
                return  objectMapper.readValue(value.toString(), clazz);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        // if not present in local cache, get from Redis
        value = redisTemplate.opsForValue().get(key);
        if(value != null){
            log.info("Fetching key {} from Redis cache", key);
            // store in local cache for future requests
            localCache.put(key, value);
            try {
                return (T) objectMapper.readValue(value.toString(), clazz);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

    public void delete(String key){
        // Remove from local cache
        localCache.invalidate(key);
        // Remove from Redis
        redisTemplate.delete(key);

        redisTemplate.convertAndSend(INVALIDATE_TOPIC, key);
    }

    public <T> List<T> getList(String key , Class<T> clazz){
        // try to get from local cache
        Object value = localCache.getIfPresent(key);
        if(value != null){
            log.info("Fetching list key {} from local cache", key);
            try {
                return objectMapper.readValue(value.toString(), objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        // if not present in local cache, get from Redis
        value = redisTemplate.opsForValue().get(key);
        if(value != null){
            log.info("Fetching list key {} from Redis cache", key);
            // store in local cache for future requests
            localCache.put(key, value);
            try {
                return objectMapper.readValue(value.toString(), objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }





}
