package com.vau.studio.chat.server.chatgpt.repositories.impl;

import com.vau.studio.chat.server.chatgpt.models.ChatMessage;
import com.vau.studio.chat.server.chatgpt.repositories.CacheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class CacheRepositotyImpl implements CacheRepository {

    /**
     * the maximum messages size in a conversation
     */
    private static final int MAX_CACHE_SIZE = 20;
    private static final Logger logger = LoggerFactory.getLogger(CacheRepository.class);

    @Autowired
    private RedisTemplate<String, ChatMessage> redisTemplate;

    @Override
    public List<ChatMessage> getMessageByCid(String cid) {
        ListOperations operations = redisTemplate.opsForList();
        if (Objects.isNull(cid) || !redisTemplate.hasKey(cid)) {
            return new ArrayList<>();
        }
        return operations.range(cid, 0, -1);
    }

    @Override
    public void putMessage(ChatMessage message, String cid) {
        Objects.requireNonNull(cid);
        Objects.requireNonNull(message);

        ListOperations operations = redisTemplate.opsForList();

        long size = operations.size(cid);
        // LRU cache
        if (size > MAX_CACHE_SIZE) {
            operations.leftPop(cid, 1);
        }
        operations.rightPush(cid, message);
    }

    @Override
    public void removeConversation(String cid) {
        Objects.requireNonNull(cid);

        ListOperations operations = redisTemplate.opsForList();
        long size = operations.size(cid);
        operations.trim(cid, size, 0);
    }
}
