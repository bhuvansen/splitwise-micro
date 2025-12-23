package com.project.splitwiseMirco.dto;

import java.math.BigDecimal;
import java.util.Map;

public record CreateExpenseRequest(
        String groupId,
        BigDecimal amount,
        String description,
        String paidByUserId,
        Map<String, BigDecimal> splits // userId -> share
) {}
