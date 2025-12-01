package org.example.rediscache.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheMessageListener {
    private final Cache<String, Object> cache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(1, java.util.concurrent.TimeUnit.MINUTES)
            .build();

    public void handleMessage(String key){
        log.info("Invalidating local cache for key: {}", key);
        cache.invalidate(key);
    }
}
