package com.project.splitwiseMirco.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record SettlementResponse(
        String id,
        String fromUserId,
        String toUserId,
        BigDecimal amount,
        LocalDateTime settledAt
) {}