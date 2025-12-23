package com.project.splitwiseMirco.dto;

import java.math.BigDecimal;

public record UserBalanceDto(
        String userId,
        String email,
        BigDecimal balance
) {}
