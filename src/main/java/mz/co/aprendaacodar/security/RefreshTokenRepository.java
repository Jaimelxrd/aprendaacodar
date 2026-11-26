package mz.co.aprendaacodar.security;
import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.data.jpa.repository.Modifying;import org.springframework.data.jpa.repository.Query;import org.springframework.data.repository.query.Param;import java.util.*;
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,UUID>{Optional<RefreshToken> findByTokenHash(String hash);@Modifying @Query("update RefreshToken t set t.revokedAt = CURRENT_TIMESTAMP where t.user.id = :userId and t.revokedAt is null") void revokeByUserId(@Param("userId") UUID userId);}
