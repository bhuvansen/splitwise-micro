package com.project.splitwiseMirco.repository;

import com.project.splitwiseMirco.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, String> {
    List<Expense> findByExpenseGroup_Id(String groupId);
    List<Expense> findByExpenseGroup_IdOrderByCreatedAtDesc(String groupId);
    void deleteByExpenseGroup_Id(String groupId);

//    @Query("""
//        select distinct e
//        from Expense e
//        join e.splits s
//        where e.paidBy.id = :userId
//           or s.user.id = :userId
//    """)
//    List<Expense> findAllByUserInvolved(@Param("userId") String userId);


        @Query("""
        select distinct e
        from Expense e
        where e.paidBy.id = :userId
           or e.id in (
                select s.expense.id
                from ExpenseSplit s
                where s.user.id = :userId
           )
    """)
        List<Expense> findAllByUserInvolved(@Param("userId") String userId);

}
