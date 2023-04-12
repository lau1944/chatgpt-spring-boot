package com.vau.studio.chat.server.chatgpt.exceptions;

public class GptMessageException extends Exception {

    private String message;

    public GptMessageException(String m) {
        this.message = m;
    }

}
