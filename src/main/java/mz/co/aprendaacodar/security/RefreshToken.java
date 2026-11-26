package mz.co.aprendaacodar.security;
import jakarta.persistence.*;import mz.co.aprendaacodar.common.entity.BaseEntity;import mz.co.aprendaacodar.user.entity.User;import java.time.Instant;
@Entity @Table(name="refresh_tokens")
public class RefreshToken extends BaseEntity {
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(nullable=false) private User user;
 @Column(nullable=false,unique=true,length=64) private String tokenHash;
 @Column(nullable=false) private Instant expiresAt;
 private Instant revokedAt;
 protected RefreshToken(){}
 public RefreshToken(User user,String hash,Instant expiry){this.user=user;this.tokenHash=hash;this.expiresAt=expiry;}
 public User getUser(){return user;}public Instant getExpiresAt(){return expiresAt;}public Instant getRevokedAt(){return revokedAt;}public void revoke(){revokedAt=Instant.now();}
}
