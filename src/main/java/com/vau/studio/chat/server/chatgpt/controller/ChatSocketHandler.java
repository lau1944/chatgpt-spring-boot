package com.vau.studio.chat.server.chatgpt.controller;

import com.vau.studio.chat.server.chatgpt.models.UserMessage;
import com.vau.studio.chat.server.chatgpt.service.ChatService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;

@Component
public class ChatSocketHandler extends AbstractWebSocketHandler {

    @Autowired
    private ChatService chatService;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        UserMessage userMessage = decode(message.getPayload());
        chatService.getChatMessageStream(userMessage, s -> {
            try {
                session.sendMessage(new TextMessage(s.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private UserMessage decode(String payload) {
        // string to json
        JSONObject json = new JSONObject(payload);
        String mid = null;
        String cid = null;
        if (json.has("mid")) {
            mid = json.getString("mid");
        }
        if (json.has("cid")) {
            cid = json.getString("cid");
        }
        // to user message object
        return new UserMessage(json.getString("text"), mid, cid);
    }
}
