package com.project.splitwiseMirco.dto;

import java.util.List;

public record GroupBalanceResponse(
        String groupId,
        List<UserBalanceDto> balances
) {}
