package com.project.splitwiseMirco.repository;

import com.project.splitwiseMirco.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement, String> {
    List<Settlement> findByGroup_IdOrderBySettledAtDesc(String groupId);
}
