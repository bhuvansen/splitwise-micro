package com.project.splitwiseMirco.service;

import com.project.splitwiseMirco.dto.ExpenseHistoryResponse;
import com.project.splitwiseMirco.dto.ExpenseSplitResponse;
import com.project.splitwiseMirco.repository.ExpenseRepository;
import com.project.splitwiseMirco.repository.ExpenseSplitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ExpenseHistoryService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;

    public List<ExpenseHistoryResponse> getGroupExpenseHistory(String groupId) {

        return expenseRepository
                .findByExpenseGroup_IdOrderByCreatedAtDesc(groupId)
                .stream()
                .map(expense -> {
                    List<ExpenseSplitResponse> splits =
                            expenseSplitRepository.findByExpense_Id(expense.getId())
                                    .stream()
                                    .map(split -> new ExpenseSplitResponse(
                                            split.getUser().getId(),
                                            split.getShareAmount()
                                    ))
                                    .toList();

                    return new ExpenseHistoryResponse(
                            expense.getId(),
                            expense.getDescription(),
                            expense.getAmount(),
                            expense.getPaidBy().getId(),
                            expense.getCreatedAt(),
                            splits
                    );
                })
                .toList();
    }
}
