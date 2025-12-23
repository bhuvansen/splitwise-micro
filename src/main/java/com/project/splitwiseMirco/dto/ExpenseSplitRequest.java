package com.project.splitwiseMirco.dto;

import java.math.BigDecimal;

public record ExpenseSplitRequest(
        String userId,
        BigDecimal shareAmount
) {}
