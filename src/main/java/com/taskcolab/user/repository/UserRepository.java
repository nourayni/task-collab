package com.taskcolab.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskcolab.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    
}
