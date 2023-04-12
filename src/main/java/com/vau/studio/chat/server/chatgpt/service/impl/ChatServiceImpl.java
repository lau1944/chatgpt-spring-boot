package com.vau.studio.chat.server.chatgpt.service.impl;

import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.ChatGPTStream;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import com.plexpt.chatgpt.listener.AbstractStreamListener;
import com.vau.studio.chat.server.chatgpt.chat.listener.MessageListener;
import com.vau.studio.chat.server.chatgpt.common.DateUtils;
import com.vau.studio.chat.server.chatgpt.exceptions.GptMessageException;
import com.vau.studio.chat.server.chatgpt.models.ChatMessage;
import com.vau.studio.chat.server.chatgpt.models.UserMessage;
import com.vau.studio.chat.server.chatgpt.repositories.CacheRepository;
import com.vau.studio.chat.server.chatgpt.service.ChatService;
import lombok.SneakyThrows;
import okhttp3.sse.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
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
    public UserMessage getGptMessage(UserMessage message) {
        ChatCompletion chatCompletion = createChatCompletion(message);

        // save user message to cache
        String mid = UUID.randomUUID().toString();
        String cid = message.getConversationId();
        if (Objects.isNull(cid)) {
            cid = UUID.randomUUID().toString();
        }
        ChatMessage userMessage = new ChatMessage(message.getText(), mid, message.getMessageId(), cid);
        saveMessage(userMessage);

        ChatCompletionResponse response = chatGPT.chatCompletion(chatCompletion);
        Message res = response.getChoices().get(0).getMessage();

        // save response to cache
        String botMessageId = UUID.randomUUID().toString();
        ChatMessage botMessage = new ChatMessage(res.getContent(), botMessageId, mid, cid);
        saveMessage(botMessage);

        return new UserMessage(res.getContent(), botMessageId, cid);
    }

    @Override
    public void getChatMessageStream(UserMessage message, MessageListener listener) {
        ChatCompletion chatCompletion = createChatCompletion(message);

        // save user message to cache
        String mid = UUID.randomUUID().toString();
        String cid = message.getConversationId();
        if (Objects.isNull(cid)) {
            cid = UUID.randomUUID().toString();
        }
        ChatMessage userMessage = new ChatMessage(message.getText(), mid, message.getMessageId(), cid);
        saveMessage(userMessage);

        String finalCid = cid;
        StringBuffer buffer = new StringBuffer();
        String botMid = UUID.randomUUID().toString();
        chatGPTStream.streamChatCompletion(chatCompletion, new AbstractStreamListener() {
            @Override
            public void onMsg(String s) {
                buffer.append(s);
                listener.onMessage(new UserMessage(buffer.toString(), botMid, finalCid));
            }

            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                if ("[DONE]".equals(data)) {
                    ChatMessage botMessage = new ChatMessage(buffer.toString(), botMid, mid, finalCid);
                    saveMessage(botMessage);
                }
                super.onEvent(eventSource, id, type, data);
            }

            @SneakyThrows
            @Override
            public void onError(Throwable throwable, String s) {
                logger.error(s);
                throw new GptMessageException(s);
            }
        });
    }

    private ChatCompletion createChatCompletion(UserMessage message) {
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
        return ChatCompletion.builder()
                .model(modelName)
                .messages(oldMessages)
                .maxTokens(maxToken)
                .build();
    }

    private void saveMessage(ChatMessage message) {
        logger.info("Message saved " + message.toString());
        cacheRepository.putMessage(message, message.getConversationId());
    }

    private List<ChatMessage> getParentMessages(List<ChatMessage> messages, String mid) {
        List<ChatMessage> sessions = new ArrayList<>();
        if (Objects.isNull(mid)) {
            return sessions;
        }

        for (int i = messages.size() - 1; i >= 0; --i) {
            System.out.println(messages.get(i).toString());
            System.out.println(mid);
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
