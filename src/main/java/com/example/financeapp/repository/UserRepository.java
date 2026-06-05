package com.example.financeapp.repository;

import com.example.financeapp.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>
{
    Optional<User> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("delete from User u where u.email = :email")
    void deleteByEmail(@Param("email") String email);
}
