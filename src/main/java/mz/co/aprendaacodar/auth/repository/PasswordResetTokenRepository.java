package mz.co.aprendaacodar.auth.repository;

import mz.co.aprendaacodar.auth.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
    @Modifying
    @Query("update PasswordResetToken t set t.usedAt = CURRENT_TIMESTAMP where t.user.id = :userId and t.usedAt is null")
    void invalidateAllForUser(@Param("userId") UUID userId);
}
