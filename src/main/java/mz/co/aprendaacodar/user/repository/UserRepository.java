package mz.co.aprendaacodar.user.repository;
import mz.co.aprendaacodar.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
import mz.co.aprendaacodar.common.enums.Enums.UserRole;
public interface UserRepository extends JpaRepository<User, UUID> {
 Optional<User> findByEmailIgnoreCase(String email);
 boolean existsByEmailIgnoreCase(String email);
 boolean existsByRole(UserRole role);
 long countByRole(UserRole role);
}
