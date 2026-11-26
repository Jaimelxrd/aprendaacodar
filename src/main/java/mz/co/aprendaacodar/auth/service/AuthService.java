package mz.co.aprendaacodar.auth.service;
import mz.co.aprendaacodar.auth.dto.AuthDtos.*;import mz.co.aprendaacodar.common.exception.*;import mz.co.aprendaacodar.common.enums.Enums.*;import mz.co.aprendaacodar.security.*;import mz.co.aprendaacodar.user.entity.User;import mz.co.aprendaacodar.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;import org.springframework.security.crypto.password.PasswordEncoder;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;import java.security.*;import java.time.Instant;import java.util.Base64;import java.util.HexFormat;
@Service
public class AuthService {
 private final UserRepository users;private final RefreshTokenRepository refresh;private final PasswordEncoder passwords;private final JwtService jwt;private final long refreshMillis;
 public AuthService(UserRepository users,RefreshTokenRepository refresh,PasswordEncoder passwords,JwtService jwt,@Value("${app.jwt.refresh-expiration}") long refreshMillis){this.users=users;this.refresh=refresh;this.passwords=passwords;this.jwt=jwt;this.refreshMillis=refreshMillis;}
 @Transactional public AuthResponse register(RegisterRequest r){if(!r.password().equals(r.confirmPassword()))throw new BusinessException("As palavras-passe não coincidem.");if(users.existsByEmailIgnoreCase(r.email()))throw new BusinessException("Este e-mail já está registado.");User u=users.save(new User(r.firstName().trim(),r.lastName().trim(),r.email().trim(),r.phoneNumber(),passwords.encode(r.password()),UserRole.STUDENT));return response(u);}
 @Transactional public AuthResponse login(LoginRequest r){User u=users.findByEmailIgnoreCase(r.email()).orElseThrow(()->new org.springframework.security.authentication.BadCredentialsException("Credenciais inválidas"));if(u.getStatus()!=UserStatus.ACTIVE||!passwords.matches(r.password(),u.getPasswordHash()))throw new org.springframework.security.authentication.BadCredentialsException("Credenciais inválidas");u.setLastLoginAt(Instant.now());return response(u);}
 @Transactional public AuthResponse refresh(String raw){if(raw==null||raw.isBlank())throw new BusinessException("Refresh token ausente.");RefreshToken token=refresh.findByTokenHash(hash(raw)).orElseThrow(()->new BusinessException("Refresh token inválido."));if(token.getRevokedAt()!=null||token.getExpiresAt().isBefore(Instant.now())||token.getUser().getStatus()!=UserStatus.ACTIVE)throw new BusinessException("Refresh token expirado ou revogado.");token.revoke();return response(token.getUser());}
 @Transactional public void logout(String raw){if(raw!=null)refresh.findByTokenHash(hash(raw)).ifPresent(RefreshToken::revoke);}
 private AuthResponse response(User u){String raw=Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes());refresh.save(new RefreshToken(u,hash(raw),Instant.now().plusMillis(refreshMillis)));issuedRefresh.set(raw);return new AuthResponse(jwt.issue(u),"Bearer",jwt.getExpiration(),user(u));}
 private final ThreadLocal<String> issuedRefresh=new ThreadLocal<>();
 public String takeRefreshToken(){String value=issuedRefresh.get();issuedRefresh.remove();return value;}
 private byte[] randomBytes(){byte[] b=new byte[48];new SecureRandom().nextBytes(b);return b;}
 private String hash(String value){try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));}catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}}
 public static UserResponse user(User u){return new UserResponse(u.getId(),u.getFirstName(),u.getLastName(),u.getEmail(),u.getPhoneNumber(),null,u.getRole(),u.getStatus());}
}
