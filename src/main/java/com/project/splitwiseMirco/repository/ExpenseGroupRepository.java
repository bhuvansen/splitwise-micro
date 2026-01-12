package com.project.splitwiseMirco.repository;

import com.project.splitwiseMirco.entity.ExpenseGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExpenseGroupRepository extends JpaRepository<ExpenseGroup, String> {

    @Query("""
        select gm.expenseGroup
        from GroupMember gm
        where gm.user.id = :userId
    """)
    List<ExpenseGroup> findAllByUserId(String userId);
}
