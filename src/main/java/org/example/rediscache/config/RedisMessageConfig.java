package org.example.rediscache.config;

import lombok.RequiredArgsConstructor;
import org.example.rediscache.service.CacheMessageListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@RequiredArgsConstructor
public class RedisMessageConfig {
    private final CacheMessageListener cacheMessageListener;
    private static final String INVALIDATE_TOPIC = "cache:invalidate";

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(@Lazy RedisConnectionFactory redisConnectionFactory){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);

        // subscribe to the invalidate topic
        container.addMessageListener(messageListenerAdapter(), new ChannelTopic(INVALIDATE_TOPIC));
        return container;
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter(){
        return new MessageListenerAdapter(cacheMessageListener);
    }
}
