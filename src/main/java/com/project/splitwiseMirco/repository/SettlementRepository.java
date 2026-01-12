package com.project.splitwiseMirco.repository;

import com.project.splitwiseMirco.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement, String> {
    List<Settlement> findByGroup_IdOrderBySettledAtDesc(String groupId);
    void deleteByGroup_Id(String groupId);

    @Query("""
        select s
        from Settlement s
        where s.fromUser.id = :userId
           or s.toUser.id = :userId
    """)
    List<Settlement> findAllByUserInvolved(@Param("userId") String userId);
}
