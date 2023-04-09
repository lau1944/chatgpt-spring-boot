package com.vau.studio.chat.server.chatgpt.service;

import com.vau.studio.chat.server.chatgpt.models.ChatMessage;
import com.vau.studio.chat.server.chatgpt.models.UserMessage;

public interface ChatService {

    ChatMessage getGptMessage(UserMessage message);

}
