package com.project.splitwiseMirco.service;

import com.project.splitwiseMirco.entity.ExpenseGroup;
import com.project.splitwiseMirco.entity.GroupMember;
import com.project.splitwiseMirco.entity.User;
import com.project.splitwiseMirco.repository.ExpenseGroupRepository;
import com.project.splitwiseMirco.repository.GroupMemberRepository;
import com.project.splitwiseMirco.repository.UserRepository;
import com.project.splitwiseMirco.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class ExpenseGroupService {

    private final ExpenseGroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    public ExpenseGroupService(
            ExpenseGroupRepository groupRepository,
            GroupMemberRepository groupMemberRepository,
            UserRepository userRepository
    ) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
    }

    // 1️⃣ Create Group
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

        // Creator becomes member
        GroupMember member = new GroupMember();
        member.setExpenseGroup(savedGroup);
        member.setUser(creator);
        member.setJoinedAt(Instant.now());

        groupMemberRepository.save(member);

        return savedGroup;
    }

    // 2️⃣ Add Member (CREATOR ONLY)
    @Transactional
    public void addMember(String groupId, String newUserId) {

        String currentUserId = SecurityUtil.currentUserId();

        ExpenseGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Authorization: only creator
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

    // 3️⃣ Membership check (used everywhere later)
    public void validateMembership(String groupId) {

        String userId = SecurityUtil.currentUserId();

        boolean isMember =
                groupMemberRepository.existsByExpenseGroup_IdAndUser_Id(groupId, userId);

        if (!isMember) {
            throw new RuntimeException("Access denied: not a group member");
        }
    }
}
