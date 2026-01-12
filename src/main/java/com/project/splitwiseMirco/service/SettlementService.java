package com.project.splitwiseMirco.service;

import com.project.splitwiseMirco.dto.SettlementRequest;
import com.project.splitwiseMirco.dto.SettlementResponse;
import com.project.splitwiseMirco.entity.ExpenseGroup;
import com.project.splitwiseMirco.entity.Settlement;
import com.project.splitwiseMirco.entity.User;
import com.project.splitwiseMirco.repository.ExpenseGroupRepository;
import com.project.splitwiseMirco.repository.SettlementRepository;
import com.project.splitwiseMirco.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final UserRepository userRepository;
    private final ExpenseGroupRepository expenseGroupRepository;

    public SettlementResponse createSettlement(
            String groupId,
            SettlementRequest request
    ) {
        ExpenseGroup group = expenseGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User fromUser = userRepository.findById(request.fromUserId())
                .orElseThrow(() -> new RuntimeException("From user not found"));
        User toUser = userRepository.findById(request.toUserId())
                .orElseThrow(() -> new RuntimeException("To user not found"));
        Settlement settlement = new Settlement();
        settlement.setGroup(group);
        settlement.setFromUser(fromUser);
        settlement.setToUser(toUser);
        settlement.setAmount(request.amount());
        settlement.setSettledAt(LocalDateTime.now());

        Settlement saved = settlementRepository.save(settlement);

        return new SettlementResponse(
                saved.getId(),
                saved.getFromUser().getId(),
                saved.getToUser().getId(),
                saved.getAmount(),
                saved.getSettledAt()
        );
    }

    public List<SettlementResponse> getSettlements(String groupId) {

        return settlementRepository
                .findByGroup_IdOrderBySettledAtDesc(groupId)
                .stream()
                .map(settlement -> new SettlementResponse(
                        settlement.getId(),
                        settlement.getFromUser().getId(),
                        settlement.getToUser().getId(),
                        settlement.getAmount(),
                        settlement.getSettledAt()
                ))
                .toList();
    }
}
