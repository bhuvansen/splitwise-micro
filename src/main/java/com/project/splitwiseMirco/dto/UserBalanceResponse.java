package com.project.splitwiseMirco.dto;

import java.math.BigDecimal;

public record UserBalanceResponse(
        String userId,
        String userName,
        BigDecimal amount
) {}
