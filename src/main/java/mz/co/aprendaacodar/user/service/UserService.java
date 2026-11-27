package mz.co.aprendaacodar.user.service;

import mz.co.aprendaacodar.auth.dto.AuthDtos;
import mz.co.aprendaacodar.auth.dto.AuthDtos.UserResponse;
import mz.co.aprendaacodar.audit.service.AuditService;
import mz.co.aprendaacodar.common.enums.Enums.UserRole;
import mz.co.aprendaacodar.common.enums.Enums.UserStatus;
import mz.co.aprendaacodar.common.exception.BusinessException;
import mz.co.aprendaacodar.common.exception.ResourceNotFoundException;
import mz.co.aprendaacodar.common.response.PageResponse;
import mz.co.aprendaacodar.security.RefreshTokenRepository;
import mz.co.aprendaacodar.storage.StorageService;
import mz.co.aprendaacodar.user.entity.User;
import mz.co.aprendaacodar.user.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class UserService {
 private final UserRepository users; private final RefreshTokenRepository tokens; private final AuditService audit;
 private final StorageService storage; private final PasswordEncoder passwords;
 public UserService(UserRepository users, RefreshTokenRepository tokens, AuditService audit, StorageService storage, PasswordEncoder passwords){this.users=users;this.tokens=tokens;this.audit=audit;this.storage=storage;this.passwords=passwords;}
 public UserResponse get(UUID id){return map(find(id));}
 @Transactional public void updatePhoto(UUID id,String fileKey){User u=find(id);u.setProfilePhotoKey(fileKey);}
 @Transactional public UserResponse update(UUID id,AuthDtos.UpdateProfileRequest r){User u=find(id);u.updateProfile(r.firstName(),r.lastName(),r.phoneNumber());if(r.profilePhotoKey()!=null){if(!r.profilePhotoKey().startsWith("profiles/"+id+"/"))throw new BusinessException("A fotografia não pertence a este perfil.");u.setProfilePhotoKey(r.profilePhotoKey());}return map(u);}
 @Transactional public UserResponse adminUpdate(UUID id,AuthDtos.UpdateProfileRequest r,UUID actor){User u=find(id);if(u.getRole()!=UserRole.STUDENT)throw new BusinessException("Esta rota permite editar apenas estudantes.");u.updateProfile(r.firstName(),r.lastName(),r.phoneNumber());audit.record(find(actor),"USER_UPDATED","User",id,Map.of());return map(u);}
 @Transactional public void changePassword(UUID id,AuthDtos.ChangePasswordRequest r){User u=find(id);if(!passwords.matches(r.currentPassword(),u.getPasswordHash()))throw new BusinessException("A palavra-passe atual está incorreta.");if(!r.newPassword().equals(r.confirmPassword()))throw new BusinessException("As palavras-passe não coincidem.");u.setPasswordHash(passwords.encode(r.newPassword()));tokens.revokeByUserId(id);}
 public PageResponse<UserResponse> list(Pageable pageable){return PageResponse.from(users.findAll(pageable),this::map);}
 @Transactional public void status(UUID id,UserStatus status,UUID actorId){User u=find(id);u.setStatus(status);if(status!=UserStatus.ACTIVE)tokens.revokeByUserId(id);audit.record(find(actorId),status==UserStatus.BLOCKED?"USER_BLOCKED":"USER_UNBLOCKED","User",id,Map.of("status",status.name()));}
 private User find(UUID id){return users.findById(id).orElseThrow(()->new ResourceNotFoundException("Utilizador não encontrado."));}
 private UserResponse map(User u){return new UserResponse(u.getId(),u.getFirstName(),u.getLastName(),u.getEmail(),u.getPhoneNumber(),u.getProfilePhotoKey()==null?null:storage.url(u.getProfilePhotoKey(),true),u.getRole(),u.getStatus());}
}
