package com.project.splitwiseMirco.repository;

import com.project.splitwiseMirco.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, String> {

    boolean existsByExpenseGroup_IdAndUser_Id(String groupId, String userId);

    Optional<GroupMember> findByExpenseGroup_IdAndUser_Id(String groupId, String userId);

    List<GroupMember> findByExpenseGroup_Id(String groupId);

    void deleteByExpenseGroup_Id(String groupId);
}
