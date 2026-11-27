package mz.co.aprendaacodar.user.controller;
import mz.co.aprendaacodar.common.response.PageResponse;import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;import mz.co.aprendaacodar.auth.dto.AuthDtos.*;import mz.co.aprendaacodar.common.enums.Enums.UserStatus;import mz.co.aprendaacodar.user.service.UserService;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController @RequestMapping("/api/v1") public class UserController {
 private final UserService service;public UserController(UserService service){this.service=service;}
 @GetMapping("/users/me") public UserResponse me(Authentication a){return service.get((UUID)a.getPrincipal());}
 @PutMapping("/users/me") public UserResponse update(Authentication a,@Valid @RequestBody UpdateProfileRequest r){return service.update((UUID)a.getPrincipal(),r);}
 @PutMapping("/users/me/password") public void changePassword(Authentication a,@Valid @RequestBody mz.co.aprendaacodar.auth.dto.AuthDtos.ChangePasswordRequest r){service.changePassword((UUID)a.getPrincipal(),r);}
 @GetMapping("/admin/users") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public PageResponse<UserResponse> list(Pageable pageable){return service.list(pageable);}
 @GetMapping("/admin/users/{id}") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public UserResponse get(@PathVariable UUID id){return service.get(id);}
 @PutMapping("/admin/users/{id}") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public UserResponse updateUser(Authentication a,@PathVariable UUID id,@Valid @RequestBody UpdateProfileRequest r){return service.adminUpdate(id,r,(UUID)a.getPrincipal());}
 @PatchMapping("/admin/users/{id}/block") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public void block(Authentication a,@PathVariable UUID id){service.status(id,UserStatus.BLOCKED,(UUID)a.getPrincipal());}
 @PatchMapping("/admin/users/{id}/unblock") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public void unblock(Authentication a,@PathVariable UUID id){service.status(id,UserStatus.ACTIVE,(UUID)a.getPrincipal());}
}
