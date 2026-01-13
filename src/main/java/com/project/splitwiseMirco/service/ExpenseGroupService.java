package com.project.splitwiseMirco.service;

import com.project.splitwiseMirco.entity.ExpenseGroup;
import com.project.splitwiseMirco.entity.GroupMember;
import com.project.splitwiseMirco.entity.User;
import com.project.splitwiseMirco.repository.*;
import com.project.splitwiseMirco.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ExpenseGroupService {

    private final ExpenseGroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final SettlementRepository settlementRepository;
    public ExpenseGroupService(
            ExpenseGroupRepository groupRepository,
            GroupMemberRepository groupMemberRepository,
            UserRepository userRepository,
            ExpenseRepository expenseRepository,
            ExpenseSplitRepository expenseSplitRepository,
            SettlementRepository settlementRepository
    ) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.expenseSplitRepository = expenseSplitRepository;
        this.settlementRepository = settlementRepository;
    }

    @Transactional(readOnly = true)
    public List<ExpenseGroup> getMyGroups() {

        String userId = SecurityUtil.currentUserId();

        return groupRepository.findAllByUserId(userId);
    }

    @Transactional
    public ExpenseGroup createGroup(String groupName) {

        String userId = SecurityUtil.currentUserId();

        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ExpenseGroup group = new ExpenseGroup();
        group.setName(groupName);
        group.setCreatedBy(creator);
        group.setCreatedAt(Instant.now());

        ExpenseGroup savedGroup = groupRepository.save(group);

        GroupMember member = new GroupMember();
        member.setExpenseGroup(savedGroup);
        member.setUser(creator);
        member.setJoinedAt(Instant.now());

        groupMemberRepository.save(member);

        return savedGroup;
    }

    @Transactional
    public void addMember(String groupId, String newUserId) {

        String currentUserId = SecurityUtil.currentUserId();

        ExpenseGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!group.getCreatedBy().getId().equals(currentUserId)) {
            throw new RuntimeException("Only group creator can add members");
        }

        if (groupMemberRepository.existsByExpenseGroup_IdAndUser_Id(groupId, newUserId)) {
            throw new RuntimeException("User already a member");
        }

        User user = userRepository.findById(newUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GroupMember member = new GroupMember();
        member.setExpenseGroup(group);
        member.setUser(user);
        member.setJoinedAt(Instant.now());

        groupMemberRepository.save(member);
    }

    @Transactional
    public void addMemberByEmail(String groupId, String email) {

        String currentUserId = SecurityUtil.currentUserId();

        ExpenseGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (groupMemberRepository.existsByExpenseGroup_IdAndUser_Id(groupId, user.getId())) {
            throw new RuntimeException("User already a member");
        }

        GroupMember member = new GroupMember();
        member.setExpenseGroup(group);
        member.setUser(user);
        member.setJoinedAt(Instant.now());

        groupMemberRepository.save(member);
    }

    @Transactional
    public void deleteGroup(String groupId) {
        String currentUserId = SecurityUtil.currentUserId();

        ExpenseGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        expenseSplitRepository.deleteByExpense_ExpenseGroup_Id(groupId);
        expenseRepository.deleteByExpenseGroup_Id(groupId);
        groupMemberRepository.deleteByExpenseGroup_Id(groupId);
        settlementRepository.deleteByGroup_Id(groupId);
        groupRepository.delete(group);
    }

    @Transactional(readOnly = true)
    public List<GroupMember> getMembers(String groupId) {
        validateMembership(groupId);
        return groupMemberRepository.findByExpenseGroup_Id(groupId);
    }

    @Transactional
    public  void deleteGroupMember(String groupId, String userId) {

        ExpenseGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!groupMemberRepository.existsByExpenseGroup_IdAndUser_Id(groupId, userId)) {
            throw new RuntimeException("User is not a group member");
        }

        groupMemberRepository.deleteByExpenseGroup_IdAndUser_Id(groupId, userId);
    }
    // Membership check
    public void validateMembership(String groupId) {

        String userId = SecurityUtil.currentUserId();

        boolean isMember =
                groupMemberRepository.existsByExpenseGroup_IdAndUser_Id(groupId, userId);

        if (!isMember) {
            throw new RuntimeException("Access denied: not a group member");
        }
    }
}
