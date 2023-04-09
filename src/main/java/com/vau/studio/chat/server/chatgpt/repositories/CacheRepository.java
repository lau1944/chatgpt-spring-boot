package com.vau.studio.chat.server.chatgpt.repositories;

import com.vau.studio.chat.server.chatgpt.models.ChatMessage;

import java.util.List;

public interface CacheRepository {

    List<ChatMessage> getMessageByCid(String cid);

    void putMessage(ChatMessage message, String cid);

    void removeConversation(String cid);
}
