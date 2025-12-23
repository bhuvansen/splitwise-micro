package com.project.splitwiseMirco.service;

import com.project.splitwiseMirco.dto.*;
import com.project.splitwiseMirco.entity.GroupMember;
import com.project.splitwiseMirco.repository.GroupMemberRepository;
import com.project.splitwiseMirco.security.SecurityUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class SettlementService {

    private final BalanceService balanceService;
    private final GroupMemberRepository memberRepository;

    public SettlementService(
            BalanceService balanceService,
            GroupMemberRepository memberRepository
    ) {
        this.balanceService = balanceService;
        this.memberRepository = memberRepository;
    }

    public SettlementResponse getSettlements(String groupId) {

        String currentUserId = SecurityUtil.currentUserId();

        // 1️⃣ Authorization
        if (!memberRepository.existsByExpenseGroup_IdAndUser_Id(groupId, currentUserId)) {
            throw new RuntimeException("Access denied");
        }

        var balanceResponse = balanceService.getGroupBalance(groupId);

        Queue<UserBalanceDto> creditors = new ArrayDeque<>();
        Queue<UserBalanceDto> debtors = new ArrayDeque<>();

        for (UserBalanceDto ub : balanceResponse.balances()) {
            if (ub.balance().compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(ub);
            } else if (ub.balance().compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(
                        new UserBalanceDto(
                                ub.userId(),
                                ub.email(),
                                ub.balance().abs()
                        )
                );
            }
        }

        List<SettlementDto> result = new ArrayList<>();

        // 2️⃣ Greedy settlement
        while (!creditors.isEmpty() && !debtors.isEmpty()) {

            UserBalanceDto creditor = creditors.poll();
            UserBalanceDto debtor = debtors.poll();

            BigDecimal settleAmount =
                    creditor.balance().min(debtor.balance());

            result.add(
                    new SettlementDto(
                            debtor.userId(),
                            creditor.userId(),
                            settleAmount
                    )
            );

            BigDecimal creditorRemaining =
                    creditor.balance().subtract(settleAmount);

            BigDecimal debtorRemaining =
                    debtor.balance().subtract(settleAmount);

            if (creditorRemaining.compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(
                        new UserBalanceDto(
                                creditor.userId(),
                                creditor.email(),
                                creditorRemaining
                        )
                );
            }

            if (debtorRemaining.compareTo(BigDecimal.ZERO) > 0) {
                debtors.add(
                        new UserBalanceDto(
                                debtor.userId(),
                                debtor.email(),
                                debtorRemaining
                        )
                );
            }
        }

        return new SettlementResponse(groupId, result);
    }
}
