package com.vau.studio.chat.server.chatgpt.chat.listener;

import com.vau.studio.chat.server.chatgpt.models.UserMessage;

public interface MessageListener {
    void onMessage(UserMessage message);
}
