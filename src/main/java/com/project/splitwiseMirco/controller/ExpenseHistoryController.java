package com.project.splitwiseMirco.controller;

import com.project.splitwiseMirco.dto.ExpenseHistoryResponse;
import com.project.splitwiseMirco.service.ExpenseHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class ExpenseHistoryController {

    private final ExpenseHistoryService expenseHistoryService;

    @GetMapping("/{groupId}/expenses")
    public List<ExpenseHistoryResponse> getExpenseHistory(
            @PathVariable String groupId
    ) {
        return expenseHistoryService.getGroupExpenseHistory(groupId);
    }
}
