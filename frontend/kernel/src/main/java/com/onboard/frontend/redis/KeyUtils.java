package com.onboard.frontend.redis;

import com.google.common.base.Preconditions;

public class KeyUtils {

    public static String userToken(String type, Integer uid) {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(uid);
        return String.format("account:%s:%d", type, uid);
    }

    public static String sessionCurrentUserId(String sessionId) {
        Preconditions.checkNotNull(sessionId);
        return String.format("session-current-user:%s", sessionId);
    }

    public static String sessionCurrentCompanyId(String sessionId) {
        Preconditions.checkNotNull(sessionId);
        return String.format("session-current-company:%s", sessionId);
    }

    public static String sessionCurrentProjectId(String sessionId) {
        Preconditions.checkNotNull(sessionId);
        return String.format("session-current-project:%s", sessionId);
    }

}
