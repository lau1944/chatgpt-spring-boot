package com.vau.studio.chat.server.chatgpt.models;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.json.JSONObject;

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

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", text);
        jsonObject.put("messageId", messageId);
        jsonObject.put("parentMessageId", parentMessageId);
        jsonObject.put("conversationId", conversationId);
        return jsonObject.toString();
    }
}
