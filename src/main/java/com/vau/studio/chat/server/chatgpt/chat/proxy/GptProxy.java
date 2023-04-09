package com.vau.studio.chat.server.chatgpt.chat.proxy;

import com.plexpt.chatgpt.util.Proxys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GptProxy {

    @Value("${proxy:#{null}}")
    private String url;

    /**
     * Get http proxy, define in the application.properties file
     * @return Proxy object
     */
    public Proxy getProxy() throws MalformedURLException {
        if (url == null || url.isEmpty()) {
            return null;
        }

        URL formatted = new URL(url);
        String schema = formatted.getProtocol();
        if (isHttp(schema)) {
            return Proxys.http(formatted.getHost(), formatted.getPort());
        } else if (isSock5(schema)) {
            return Proxys.socks5(formatted.getHost(), formatted.getPort());
        } else {
            throw new IllegalStateException(formatted.getProtocol() + " is not supported protocol on proxy");
        }
    }

    private boolean isSock5(String schema) {
        return schema.equals("sock5");
    }

    private boolean isHttp(String schema) {
        return schema.equals("http") || schema.equals("https");
    }
}
