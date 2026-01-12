package com.project.splitwiseMirco.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ExpenseHistoryResponse(
        String expenseId,
        String description,
        BigDecimal amount,
        String paidByUserId,
        Instant createdAt,
        List<ExpenseSplitResponse> splits
) {}
