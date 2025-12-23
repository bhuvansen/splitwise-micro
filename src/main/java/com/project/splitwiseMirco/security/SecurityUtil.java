package com.project.splitwiseMirco.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {

    private SecurityUtil() {}

    public static String currentUserId() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("Unauthenticated access");
        }

        return auth.getName(); // JWT subject
    }
}
