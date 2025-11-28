package org.example.rediscache.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicRedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private int getCurrentVersion(String key){
        String versionKey = "version_" + key;
        Object versionObj = redisTemplate.opsForValue().get(versionKey);
        if(versionObj == null){
            return 1;
        }
        return (Integer) versionObj;
    }

    public <T> void set(String key , T value , Long timeout , TimeUnit timeUnit) {
        String jsonValue = null;
        try {
            jsonValue = objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String versionKey = "version_" + key;
        int currentVersion = getCurrentVersion(key);
        redisTemplate.opsForValue().set(versionKey, currentVersion + 1);
        key = key + "_v" + currentVersion;
        redisTemplate.opsForValue().set(key, jsonValue, timeout, timeUnit);
    }

    public <T> T get(String key , Class<T> clazz){
        int currentVersion = getCurrentVersion(key);
        key = key + "_v" + currentVersion;
        Object value = redisTemplate.opsForValue().get(key);
        if(value == null){
            return null;
        }
        try {
            return objectMapper.readValue((String) value, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String key){
        int currentVersion = getCurrentVersion(key);
        key = key + "_v" + currentVersion;
        redisTemplate.delete(key);
    }

    public <T> java.util.List<T> getList(String key , Class<T> clazz){
        int currentVersion = getCurrentVersion(key);
        key = key + "_v" + currentVersion;
        Object value = redisTemplate.opsForValue().get(key);
        if(value == null){
            return null;
        }
        try {
            return objectMapper.readValue((String) value, objectMapper.getTypeFactory().constructCollectionType(java.util.List.class, clazz));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
