package com.vau.studio.chat.server.chatgpt.service.impl;

import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.ChatGPTStream;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import com.vau.studio.chat.server.chatgpt.common.DateUtils;
import com.vau.studio.chat.server.chatgpt.models.ChatMessage;
import com.vau.studio.chat.server.chatgpt.models.UserMessage;
import com.vau.studio.chat.server.chatgpt.repositories.CacheRepository;
import com.vau.studio.chat.server.chatgpt.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ChatServiceImpl implements ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);

    @Autowired
    private ChatGPT chatGPT;

    @Autowired
    private ChatGPTStream chatGPTStream;

    @Value("${open_ai_model}")
    private String modelName;

    @Value("${max_token}")
    private int maxToken;

    @Autowired
    private CacheRepository cacheRepository;

    @Override
    public ChatMessage getGptMessage(UserMessage message) {
        Objects.requireNonNull(message.getText());

        List<ChatMessage> messages = cacheRepository.getMessageByCid(message.getConversationId());

        // filter message session
        messages = getParentMessages(messages, message.getMessageId());

        String currentDate = DateUtils.format(LocalDateTime.now());
        // system instruction prompt (modify it if you have specific needs)
        Message system = Message.ofSystem("Instructions:\n You are ChatGPT, " +
                "a large language model trained by OpenAI. Respond conversationally.\n" +
                "Current date: " + currentDate + "\n");
        // user message
        Message newMessage = Message.of(message.getText());

        logger.info("New Message received " + newMessage.getContent());

        List<Message> oldMessages = messages
                .stream()
                .map(m -> Message.of(m.getText() + "\n"))
                .collect(Collectors.toList());
        oldMessages.add(0, system);
        oldMessages.add(newMessage);

        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model(modelName)
                .messages(oldMessages)
                .maxTokens(maxToken)
                .build();

        // save user message to cache
        String mid = UUID.randomUUID().toString();
        String cid = message.getConversationId();
        if (Objects.isNull(cid)) {
            cid = UUID.randomUUID().toString();
        }
        ChatMessage userMessage = new ChatMessage(message.getText(), mid, message.getMessageId(), cid);
        cacheRepository.putMessage(userMessage, cid);

        ChatCompletionResponse response = chatGPT.chatCompletion(chatCompletion);
        Message res = response.getChoices().get(0).getMessage();

        // save response to cache
        ChatMessage botMessage = new ChatMessage(res.getContent(), UUID.randomUUID().toString(), mid, cid);
        cacheRepository.putMessage(botMessage, cid);
        return botMessage;
    }

    private List<ChatMessage> getParentMessages(List<ChatMessage> messages, String mid) {
        List<ChatMessage> sessions = new ArrayList<>();
        if (Objects.isNull(mid)) {
            return sessions;
        }

        for (int i = messages.size() - 1; i >= 0; --i) {
            if (Objects.isNull(mid)) {
                break;
            }

            ChatMessage current = messages.get(i);
            if (current.getMessageId().equals(mid)) {
                sessions.add(0, current);
                mid = current.getParentMessageId();
            }
        }
        return sessions;
    }
}
