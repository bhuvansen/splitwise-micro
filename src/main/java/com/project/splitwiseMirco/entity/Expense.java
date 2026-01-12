package com.project.splitwiseMirco.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
@Entity
@Table(name = "expenses")
@Getter @Setter
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private ExpenseGroup expenseGroup;

    @ManyToOne
    @JoinColumn(name = "paid_by")
    private User paidBy;

    private BigDecimal amount;
    private String description;

    @Column(name = "created_at")
    private Instant createdAt;
}
