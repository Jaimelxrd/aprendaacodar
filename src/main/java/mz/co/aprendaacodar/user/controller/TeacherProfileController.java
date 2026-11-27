package mz.co.aprendaacodar.user.controller;

import jakarta.validation.Valid;
import mz.co.aprendaacodar.user.dto.TeacherProfileDtos.*;
import mz.co.aprendaacodar.user.service.TeacherProfileService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teacher/profile")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherProfileController {
    private final TeacherProfileService service;
    public TeacherProfileController(TeacherProfileService service) { this.service = service; }
    @GetMapping public Response get(Authentication auth) { return service.get((UUID) auth.getPrincipal()); }
    @PutMapping public Response update(Authentication auth, @Valid @RequestBody UpdateRequest request) {
        return service.update((UUID) auth.getPrincipal(), request);
    }
}
