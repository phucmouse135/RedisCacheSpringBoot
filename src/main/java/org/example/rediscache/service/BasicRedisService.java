package org.example.rediscache.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicRedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public <T> void set(String key , T value , Long timeout , java.util.concurrent.TimeUnit timeUnit){
        String jsonValue = objectMapper.writeValueAsString(value);
        redisTemplate.opsForValue().set(key, jsonValue, timeout, timeUnit);
    }

    public <T> T get(String key , Class<T> clazz){
        Object value = redisTemplate.opsForValue().get(key);
        if(value == null){
            return null;
        }
        return objectMapper.readValue((String) value, clazz);
    }

    public void delete(String key){
        redisTemplate.delete(key);
    }

    public <T> java.util.List<T> getList(String key , Class<T> clazz){
        Object value = redisTemplate.opsForValue().get(key);
        if(value == null){
            return null;
        }
        return objectMapper.readValue((String) value, objectMapper.getTypeFactory().constructCollectionType(java.util.List.class, clazz));
    }


}
