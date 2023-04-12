package com.vau.studio.chat.server.chatgpt.service;

import com.vau.studio.chat.server.chatgpt.chat.listener.MessageListener;
import com.vau.studio.chat.server.chatgpt.models.UserMessage;

public interface ChatService {

    UserMessage getGptMessage(UserMessage message);

    void getChatMessageStream(UserMessage message, MessageListener listener);
}
