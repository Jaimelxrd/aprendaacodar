package mz.co.aprendaacodar.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String hash);

    @Modifying
    @Query("update RefreshToken t set t.revokedAt = :revokedAt where t.user.id = :userId and t.revokedAt is null")
    void revokeByUserIdAt(@Param("userId") UUID userId, @Param("revokedAt") Instant revokedAt);

    default void revokeByUserId(UUID userId) {
        revokeByUserIdAt(userId, Instant.now());
    }
}
