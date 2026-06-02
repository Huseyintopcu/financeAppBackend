package com.example.financeapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "bill")
@Data
public class Bill
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDate finalPaymentDate;

    private boolean isPaid = false;

    @Column(unique = true,nullable = false)
    private String userEmail;
}
