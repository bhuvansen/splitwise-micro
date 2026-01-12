package com.project.splitwiseMirco.repository;

import com.project.splitwiseMirco.entity.ExpenseSplit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, String> {
    List<ExpenseSplit> findByExpense_Id(String expenseId);
    List<ExpenseSplit> findByExpense_ExpenseGroup_Id(String groupId);
    void deleteByExpense_Id(String expenseId);
    void deleteByExpense_ExpenseGroup_Id(String groupId);

        List<ExpenseSplit> findAllByExpense_Id(String expenseId);

}
