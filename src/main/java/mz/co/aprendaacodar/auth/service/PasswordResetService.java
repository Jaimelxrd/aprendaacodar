package mz.co.aprendaacodar.auth.service;

import mz.co.aprendaacodar.auth.entity.PasswordResetToken;
import mz.co.aprendaacodar.auth.repository.PasswordResetTokenRepository;
import mz.co.aprendaacodar.auth.dto.AuthDtos.ForgotPasswordRequest;
import mz.co.aprendaacodar.auth.dto.AuthDtos.ResetPasswordRequest;
import mz.co.aprendaacodar.common.enums.Enums.UserStatus;
import mz.co.aprendaacodar.common.exception.BusinessException;
import mz.co.aprendaacodar.common.exception.ServiceUnavailableException;
import mz.co.aprendaacodar.security.RefreshTokenRepository;
import mz.co.aprendaacodar.user.entity.User;
import mz.co.aprendaacodar.user.repository.UserRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class PasswordResetService {
    private final PasswordResetTokenRepository tokens;
    private final UserRepository users;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<EmailService> emailService;
    private final String frontendUrl;

    public PasswordResetService(PasswordResetTokenRepository tokens, UserRepository users,
            RefreshTokenRepository refreshTokens, PasswordEncoder passwordEncoder,
            ObjectProvider<EmailService> emailService,
            @Value("${app.frontend-url}") String frontendUrl) {
        this.tokens=tokens; this.users=users; this.refreshTokens=refreshTokens;
        this.passwordEncoder=passwordEncoder; this.emailService=emailService; this.frontendUrl=frontendUrl;
    }

    @Transactional
    public void requestReset(ForgotPasswordRequest request) {
        EmailService mailer=emailService.getIfAvailable();
        if(mailer==null) throw new ServiceUnavailableException("A recuperação de palavra-passe ainda não está configurada.");
        users.findByEmailIgnoreCase(request.email()).filter(u->u.getStatus()==UserStatus.ACTIVE).ifPresent(user->{
            String raw=Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes());
            tokens.invalidateAllForUser(user.getId());
            tokens.save(new PasswordResetToken(user,hash(raw),Instant.now().plusSeconds(1800)));
            String separator=frontendUrl.contains("?")?"&":"?";
            String link=frontendUrl.replaceAll("/$", "")+"/reset-password"+separator+"token="+URLEncoder.encode(raw,StandardCharsets.UTF_8);
            try { mailer.sendPasswordReset(user.getEmail(),user.getFirstName(),link); }
            catch(RuntimeException ex){throw new ServiceUnavailableException("Não foi possível enviar o e-mail de recuperação.");}
        });
    }

    @Transactional
    public void reset(ResetPasswordRequest request) {
        if(!request.password().equals(request.confirmPassword())) throw new BusinessException("As palavras-passe não coincidem.");
        PasswordResetToken token=tokens.findByTokenHash(hash(request.token())).orElseThrow(()->new BusinessException("Token inválido ou expirado."));
        if(token.getUsedAt()!=null||token.getExpiresAt().isBefore(Instant.now())||token.getUser().getStatus()!=UserStatus.ACTIVE) throw new BusinessException("Token inválido ou expirado.");
        token.getUser().setPasswordHash(passwordEncoder.encode(request.password()));
        token.markUsed();
        refreshTokens.revokeByUserId(token.getUser().getId());
    }

    private byte[] randomBytes(){byte[] bytes=new byte[32];new SecureRandom().nextBytes(bytes);return bytes;}
    private String hash(String value){try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));}catch(Exception e){throw new IllegalStateException(e);}}
}
