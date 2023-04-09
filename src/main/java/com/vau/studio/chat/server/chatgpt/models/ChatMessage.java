package com.vau.studio.chat.server.chatgpt.models;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.io.Serializable;

@Value
@AllArgsConstructor
public class ChatMessage implements Serializable {
    /**
     * Chat content
     */
    private String text;
    /**
     * Chat message unique id
     */
    private String messageId;
    /**
     * Message id that this message responds to
     */
    private String parentMessageId;
    /**
     * Conversation session id
     */
    private String conversationId;
}
