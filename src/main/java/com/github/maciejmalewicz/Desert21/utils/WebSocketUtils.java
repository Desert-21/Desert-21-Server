package com.github.maciejmalewicz.Desert21.utils;

import static com.github.maciejmalewicz.Desert21.config.Constants.WEB_SOCKET_TOPICS_PATH;

public class WebSocketUtils {

    public static String getTopicPath(String relativePath) {
        return WEB_SOCKET_TOPICS_PATH + relativePath;
    }
}
