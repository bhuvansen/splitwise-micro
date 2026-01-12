package com.project.splitwiseMirco.controller;

import com.project.splitwiseMirco.dto.CreateExpenseRequest;
import com.project.splitwiseMirco.dto.UpdateExpenseRequest;
import com.project.splitwiseMirco.entity.Expense;
import com.project.splitwiseMirco.service.ExpenseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    @PostMapping
    public Expense create(@RequestBody CreateExpenseRequest request) {
        return service.createExpense(request);
    }

    @PutMapping("/{expenseId}")
    public void updateExpense(
            @PathVariable String expenseId,
            @RequestBody UpdateExpenseRequest request
    ) {
        service.updateExpense(expenseId, request);
    }

    @DeleteMapping("/{expenseId}")
    public void deleteExpense(@PathVariable String expenseId) {
        service.deleteExpense(expenseId);
    }
    @GetMapping("/{groupId}/expenses")
    public List<Expense> getGroupExpenses(@PathVariable String groupId) {
        return service.getGroupExpenses(groupId);
    }

}
