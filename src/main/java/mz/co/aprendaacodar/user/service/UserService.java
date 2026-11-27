package mz.co.aprendaacodar.user.service;
import mz.co.aprendaacodar.common.response.PageResponse;import org.springframework.data.domain.Pageable;
import mz.co.aprendaacodar.auth.dto.AuthDtos.*;import mz.co.aprendaacodar.auth.service.AuthService;import mz.co.aprendaacodar.audit.service.AuditService;import mz.co.aprendaacodar.common.enums.Enums.*;import mz.co.aprendaacodar.common.exception.ResourceNotFoundException;import mz.co.aprendaacodar.security.RefreshTokenRepository;import mz.co.aprendaacodar.user.entity.User;import mz.co.aprendaacodar.user.repository.UserRepository;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import java.util.*;
@Service public class UserService {
 private final UserRepository users;private final RefreshTokenRepository tokens;private final AuditService audit;
 public UserService(UserRepository users,RefreshTokenRepository tokens,AuditService audit){this.users=users;this.tokens=tokens;this.audit=audit;}
 public UserResponse get(UUID id){return AuthService.user(users.findById(id).orElseThrow(()->new ResourceNotFoundException("Utilizador não encontrado.")));}
 @Transactional public void updatePhoto(UUID id,String fileKey){User u=users.findById(id).orElseThrow(()->new ResourceNotFoundException("Utilizador não encontrado."));u.setProfilePhotoKey(fileKey);}
 @Transactional public UserResponse update(UUID id,UpdateProfileRequest r){User u=users.findById(id).orElseThrow(()->new ResourceNotFoundException("Utilizador não encontrado."));u.updateProfile(r.firstName(),r.lastName(),r.phoneNumber());return AuthService.user(u);}
 public PageResponse<UserResponse> list(Pageable pageable){return PageResponse.from(users.findAll(pageable),AuthService::user);}
 @Transactional public void status(UUID id,UserStatus status,UUID actorId){User u=users.findById(id).orElseThrow(()->new ResourceNotFoundException("Utilizador não encontrado."));u.setStatus(status);if(status!=UserStatus.ACTIVE)tokens.revokeByUserId(id);audit.record(users.findById(actorId).orElseThrow(),status==UserStatus.BLOCKED?"USER_BLOCKED":"USER_UNBLOCKED","User",id,Map.of("status",status.name()));}
}
