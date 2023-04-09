package com.vau.studio.chat.server.chatgpt.controller;

import com.vau.studio.chat.server.chatgpt.models.ChatMessage;
import com.vau.studio.chat.server.chatgpt.models.UserMessage;
import com.vau.studio.chat.server.chatgpt.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/chat")
    public ResponseEntity<ChatMessage> getChatMessage(
            @RequestParam(name = "text") String text,
            @RequestParam(name = "mid", required = false) String mid,
            @RequestParam(name = "cid", required = false) String cid
    ) {
        UserMessage userMessage = new UserMessage(text, mid, cid);
        ChatMessage res = chatService.getGptMessage(userMessage);
        return ResponseEntity.ok(res);
    }

}
