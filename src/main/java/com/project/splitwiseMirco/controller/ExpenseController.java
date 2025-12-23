package com.project.splitwiseMirco.controller;

import com.project.splitwiseMirco.dto.CreateExpenseRequest;
import com.project.splitwiseMirco.entity.Expense;
import com.project.splitwiseMirco.service.ExpenseService;
import org.springframework.web.bind.annotation.*;

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
}
