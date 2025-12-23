package com.project.splitwiseMirco.controller;

import com.project.splitwiseMirco.dto.SettlementResponse;
import com.project.splitwiseMirco.service.SettlementService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
public class SettlementController {

    private final SettlementService service;

    public SettlementController(SettlementService service) {
        this.service = service;
    }

    @GetMapping("/{groupId}/settlements")
    public SettlementResponse getSettlements(@PathVariable String groupId) {
        return service.getSettlements(groupId);
    }
}
