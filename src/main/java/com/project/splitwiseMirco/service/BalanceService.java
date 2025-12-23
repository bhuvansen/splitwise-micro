package com.project.splitwiseMirco.service;

import com.project.splitwiseMirco.dto.GroupBalanceResponse;
import com.project.splitwiseMirco.dto.UserBalanceDto;
import com.project.splitwiseMirco.entity.*;
import com.project.splitwiseMirco.repository.*;
import com.project.splitwiseMirco.security.SecurityUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class BalanceService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository splitRepository;
    private final GroupMemberRepository memberRepository;

    public BalanceService(
            ExpenseRepository expenseRepository,
            ExpenseSplitRepository splitRepository,
            GroupMemberRepository memberRepository
    ) {
        this.expenseRepository = expenseRepository;
        this.splitRepository = splitRepository;
        this.memberRepository = memberRepository;
    }

    public GroupBalanceResponse getGroupBalance(String groupId) {

        String currentUserId = SecurityUtil.currentUserId();

        // 1️⃣ Authorization
        if (!memberRepository.existsByExpenseGroup_IdAndUser_Id(groupId, currentUserId)) {
            throw new RuntimeException("Access denied");
        }

        Map<String, BigDecimal> balanceMap = new HashMap<>();

        // 2️⃣ Initialize members
        List<GroupMember> members = memberRepository.findByExpenseGroup_Id(groupId);
        for (GroupMember member : members) {
            balanceMap.put(member.getUser().getId(), BigDecimal.ZERO);
        }

        // 3️⃣ Add paid amounts
        List<Expense> expenses = expenseRepository.findByExpenseGroup_Id(groupId);
        for (Expense expense : expenses) {
            String payerId = expense.getPaidBy().getId();
            balanceMap.put(
                    payerId,
                    balanceMap.get(payerId).add(expense.getAmount())
            );
        }

        // 4️⃣ Subtract shares
        List<ExpenseSplit> splits = splitRepository.findByExpense_ExpenseGroup_Id(groupId);
        for (ExpenseSplit split : splits) {
            String userId = split.getUser().getId();
            balanceMap.put(
                    userId,
                    balanceMap.get(userId).subtract(split.getShareAmount())
            );
        }

        // 5️⃣ Build response
        List<UserBalanceDto> result = new ArrayList<>();
        for (GroupMember member : members) {
            User user = member.getUser();
            result.add(
                    new UserBalanceDto(
                            user.getId(),
                            user.getEmail(),
                            balanceMap.get(user.getId())
                    )
            );
        }

        return new GroupBalanceResponse(groupId, result);
    }
}
