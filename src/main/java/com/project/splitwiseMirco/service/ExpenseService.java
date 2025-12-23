package com.project.splitwiseMirco.service;

import com.project.splitwiseMirco.dto.CreateExpenseRequest;
import com.project.splitwiseMirco.entity.*;
import com.project.splitwiseMirco.repository.*;
import com.project.splitwiseMirco.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository splitRepository;
    private final ExpenseGroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            ExpenseSplitRepository splitRepository,
            ExpenseGroupRepository groupRepository,
            GroupMemberRepository groupMemberRepository,
            UserRepository userRepository
    ) {
        this.expenseRepository = expenseRepository;
        this.splitRepository = splitRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Expense createExpense(CreateExpenseRequest request) {

        String currentUserId = SecurityUtil.currentUserId();

        // 1️⃣ Validate group
        ExpenseGroup group = groupRepository.findById(request.groupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // 2️⃣ Requester must be member
        validateMember(group.getId(), currentUserId);

        // 3️⃣ Validate payer
        validateMember(group.getId(), request.paidByUserId());

        User payer = userRepository.findById(request.paidByUserId())
                .orElseThrow(() -> new RuntimeException("Payer not found"));

        // 4️⃣ Validate split users
        for (String userId : request.splits().keySet()) {
            validateMember(group.getId(), userId);
        }

        // 5️⃣ Validate split sum
        BigDecimal splitTotal = request.splits()
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (splitTotal.compareTo(request.amount()) != 0) {
            throw new RuntimeException("Split total does not match expense amount");
        }

        // 6️⃣ Create expense
        Expense expense = new Expense();
        expense.setExpenseGroup(group);
        expense.setPaidBy(payer);
        expense.setAmount(request.amount());
        expense.setDescription(request.description());
        expense.setCreatedAt(Instant.now());

        Expense savedExpense = expenseRepository.save(expense);

        // 7️⃣ Create splits
        for (Map.Entry<String, BigDecimal> entry : request.splits().entrySet()) {

            User user = userRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ExpenseSplit split = new ExpenseSplit();
            split.setExpense(savedExpense);
            split.setUser(user);
            split.setShareAmount(entry.getValue());

            splitRepository.save(split);
        }

        return savedExpense;
    }

    public void validateMember(String groupId, String userId) {
        if (!groupMemberRepository.existsByExpenseGroup_IdAndUser_Id(groupId, userId)) {
            throw new RuntimeException("User is not a group member");
        }
    }
}
