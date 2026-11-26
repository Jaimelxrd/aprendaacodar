package mz.co.aprendaacodar.user.controller;
import jakarta.validation.Valid;import mz.co.aprendaacodar.auth.dto.AuthDtos.*;import mz.co.aprendaacodar.common.enums.Enums.UserStatus;import mz.co.aprendaacodar.user.service.UserService;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController @RequestMapping("/api/v1") public class UserController {
 private final UserService service;public UserController(UserService service){this.service=service;}
 @GetMapping("/users/me") public UserResponse me(Authentication a){return service.get((UUID)a.getPrincipal());}
 @PutMapping("/users/me") public UserResponse update(Authentication a,@Valid @RequestBody UpdateProfileRequest r){return service.update((UUID)a.getPrincipal(),r);}
 @GetMapping("/admin/users") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public List<UserResponse> list(){return service.list();}
 @GetMapping("/admin/users/{id}") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public UserResponse get(@PathVariable UUID id){return service.get(id);}
 @PatchMapping("/admin/users/{id}/block") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public void block(Authentication a,@PathVariable UUID id){service.status(id,UserStatus.BLOCKED,(UUID)a.getPrincipal());}
 @PatchMapping("/admin/users/{id}/unblock") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public void unblock(Authentication a,@PathVariable UUID id){service.status(id,UserStatus.ACTIVE,(UUID)a.getPrincipal());}
}
