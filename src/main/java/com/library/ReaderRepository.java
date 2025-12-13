package com.library;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, Long> {
    Optional<Reader> findByUserLogin(String login);
    Optional<Reader> findByUserId(Long userId);
    List<Reader> findByLastNameContainingIgnoreCase(String lastName);
}