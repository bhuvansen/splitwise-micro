package com.project.splitwiseMirco.dto;

import java.math.BigDecimal;
import java.util.List;

public record UpdateExpenseRequest(
        BigDecimal amount,
        String description,
        String paidByUserId,
        List<ExpenseSplitRequest> splits
) {}
