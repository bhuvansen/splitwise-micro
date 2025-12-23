package com.project.splitwiseMirco.dto;

import java.util.List;

public record SettlementResponse(
        String groupId,
        List<SettlementDto> settlements
) {}
