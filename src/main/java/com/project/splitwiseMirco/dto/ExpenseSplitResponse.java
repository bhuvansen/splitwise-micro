package com.project.splitwiseMirco.dto;

import java.math.BigDecimal;

public record ExpenseSplitResponse(
        String userId,
        BigDecimal shareAmount
) {}
