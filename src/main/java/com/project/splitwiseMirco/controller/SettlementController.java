package com.project.splitwiseMirco.controller;

import com.project.splitwiseMirco.dto.SettlementRequest;
import com.project.splitwiseMirco.dto.SettlementResponse;
import com.project.splitwiseMirco.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups/{groupId}/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    // ---------------- CREATE SETTLEMENT ----------------
    @PostMapping
    public SettlementResponse createSettlement(
            @PathVariable String groupId,
            @RequestBody SettlementRequest request
    ) {
        return settlementService.createSettlement(groupId, request);
    }

    // ---------------- GET SETTLEMENTS ----------------
    @GetMapping
    public List<SettlementResponse> getSettlements(
            @PathVariable String groupId
    ) {
        return settlementService.getSettlements(groupId);
    }
}
