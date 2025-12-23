package com.project.splitwiseMirco.dto;

import java.math.BigDecimal;

public record SettlementDto(
        String fromUserId,
        String toUserId,
        BigDecimal amount
) {}
