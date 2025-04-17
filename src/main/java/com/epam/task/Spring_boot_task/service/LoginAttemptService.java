package com.epam.task.Spring_boot_task.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private final int MAX_ATTEMPTS = 3;
    private final long BLOCK_DURATION_MINUTES = 5;

    private static class AttemptInfo {
        int attempts;
        LocalDateTime lastAttempt;
        boolean isBlockedUntil(LocalDateTime now) {
            return attempts >= 3 && lastAttempt.plusMinutes(5).isAfter(now);
        }
    }

    private final Map<String, AttemptInfo> attemptsCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
    }

    public void loginFailed(String username) {
        AttemptInfo info = attemptsCache.getOrDefault(username, new AttemptInfo());
        info.attempts++;
        info.lastAttempt = LocalDateTime.now();
        attemptsCache.put(username, info);
    }

    public boolean isBlocked(String username) {
        AttemptInfo info = attemptsCache.get(username);
        if (info == null) return false;
        if (info.isBlockedUntil(LocalDateTime.now())) return true;
        if (info.attempts >= MAX_ATTEMPTS && !info.isBlockedUntil(LocalDateTime.now())) {
            // Reset after block expires
            attemptsCache.remove(username);
        }
        return false;
    }
}
