package com.project.splitwiseMirco.dto;
import java.math.BigDecimal;

public record SettlementRequest(
        String fromUserId,
        String toUserId,
        BigDecimal amount
) {}