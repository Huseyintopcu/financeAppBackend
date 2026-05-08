package com.example.financeapp.entity;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
@Data
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true,nullable = false)
    private String email;

    @Column(nullable = false)
    private  String password;
}
