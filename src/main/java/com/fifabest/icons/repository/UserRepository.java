package com.fifabest.icons.repository;

import com.fifabest.icons.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}