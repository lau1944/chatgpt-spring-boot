package com.vau.studio.chat.server.chatgpt.chat;

import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.ChatGPTStream;
import com.vau.studio.chat.server.chatgpt.chat.proxy.GptProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;

/**
 * ChatGPT client
 */
@Component
public class ChatGptClient {

    @Value("${open_ai_key}")
    private String apiKey;

    @Value("${timeout}")
    private int timeout;

    @Value("${open_ai_host}")
    private String openAiHost;

    @Autowired
    private GptProxy proxy;

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ChatGPT getChatGPT() throws MalformedURLException {
        return ChatGPT.builder()
                .apiKey(apiKey)
                .proxy(proxy.getProxy())
                .timeout(timeout)
                .apiHost(openAiHost)
                .build()
                .init();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ChatGPTStream getChatGPTStream() throws MalformedURLException {
        return ChatGPTStream.builder()
                .apiKey(apiKey)
                .proxy(proxy.getProxy())
                .timeout(timeout)
                .apiHost(openAiHost)
                .build()
                .init();
    }
}
