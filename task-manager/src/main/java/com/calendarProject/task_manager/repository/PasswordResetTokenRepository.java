// Utwórz plik w paczce: com.calendarProject.task_manager.repository
package com.calendarProject.task_manager.repository;

import com.calendarProject.task_manager.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
}