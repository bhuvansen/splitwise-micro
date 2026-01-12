package com.project.splitwiseMirco.controller;

import com.project.splitwiseMirco.dto.GroupBalanceResponse;
import com.project.splitwiseMirco.dto.UserBalanceResponse;
import com.project.splitwiseMirco.service.BalanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class BalanceController {

    private final BalanceService service;

    public BalanceController(BalanceService service) {
        this.service = service;
    }

    @GetMapping("/{groupId}/balances")
    public GroupBalanceResponse getBalances(@PathVariable String groupId) {
        return service.getGroupBalance(groupId);
    }
    //Overall balance of user
    @GetMapping("/overall/balances")
    public List<UserBalanceResponse> getOverallBalances() {
        return service.getOverallBalances();
    }

}
