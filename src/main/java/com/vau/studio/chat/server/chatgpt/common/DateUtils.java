package com.vau.studio.chat.server.chatgpt.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    /**
     * Get date in format
     */
    public static String format(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return formatter.format(time);
    }
}
