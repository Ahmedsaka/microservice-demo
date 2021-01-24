package io.medalytics.demo.repository;

import io.medalytics.demo.model.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    Optional<ConfirmationToken> findByToken(String token);

    @Modifying
    @Query("UPDATE confirmation_token ct SET ct.confirmedAt = :confirmation_time WHERE ct.token = :token")
    void setConfirmed(@Param(value = "token") String token, @Param(value = "confirmation_time") LocalDateTime confirmationTime);
}
