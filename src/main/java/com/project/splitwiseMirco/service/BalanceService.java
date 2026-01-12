package com.project.splitwiseMirco.service;

import com.project.splitwiseMirco.dto.GroupBalanceResponse;
import com.project.splitwiseMirco.dto.UserBalanceDto;
import com.project.splitwiseMirco.dto.UserBalanceResponse;
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
    private final SettlementRepository settlementRepository;
    private final UserRepository userRepository;
    private final ExpenseSplitRepository expenseSplitRepository;

    public BalanceService(
            ExpenseRepository expenseRepository,
            ExpenseSplitRepository splitRepository,
            GroupMemberRepository memberRepository,
            SettlementRepository settlementRepository,
            UserRepository userRepository,
            ExpenseSplitRepository expenseSplitRepository
    ) {
        this.expenseRepository = expenseRepository;
        this.splitRepository = splitRepository;
        this.memberRepository = memberRepository;
        this.settlementRepository = settlementRepository;
        this.userRepository = userRepository;
        this.expenseSplitRepository = expenseSplitRepository;
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

    public List<UserBalanceResponse> getOverallBalances() {

        String currentUserId = SecurityUtil.currentUserId();

        Map<String, BigDecimal> balanceMap = new HashMap<>();


        expenseRepository.findAllByUserInvolved(currentUserId)
                .forEach(expense -> {

                    String paidBy = expense.getPaidBy().getId();

                    List<ExpenseSplit> splits =
                            expenseSplitRepository.findAllByExpense_Id(expense.getId());

                    splits.forEach(split -> {
                        String splitUserId = split.getUser().getId();
                        BigDecimal amount = split.getShareAmount();

                        // I paid → other user owes me
                        if (paidBy.equals(currentUserId)
                                && !splitUserId.equals(currentUserId)) {

                            balanceMap.merge(
                                    splitUserId,
                                    amount,
                                    BigDecimal::add
                            );
                        }

                        // Someone else paid → I owe them
                        if (splitUserId.equals(currentUserId)
                                && !paidBy.equals(currentUserId)) {

                            balanceMap.merge(
                                    paidBy,
                                    amount.negate(),
                                    BigDecimal::add
                            );
                        }
                    });
                });


        settlementRepository.findAllByUserInvolved(currentUserId)
                .forEach(settlement -> {

                    String from = settlement.getFromUser().getId();
                    String to = settlement.getToUser().getId();
                    BigDecimal amount = settlement.getAmount();

                    // I paid someone → reduce my debt
                    if (from.equals(currentUserId)) {
                        balanceMap.merge(
                                to,
                                amount,
                                BigDecimal::add
                        );
                    }

                    // Someone paid me → reduce their debt
                    if (to.equals(currentUserId)) {
                        balanceMap.merge(
                                from,
                                amount.negate(),
                                BigDecimal::add
                        );
                    }
                });


        return balanceMap.entrySet().stream()
                .filter(e -> e.getValue().compareTo(BigDecimal.ZERO) != 0)
                .map(entry -> {
                    var user = userRepository.findById(entry.getKey())
                            .orElseThrow();

                    return new UserBalanceResponse(
                            user.getId(),
                            user.getName(),
                            entry.getValue()
                    );
                })
                .toList();
    }
}
