package mz.co.aprendaacodar.user.controller;

import jakarta.validation.Valid;
import mz.co.aprendaacodar.common.response.PageResponse;
import mz.co.aprendaacodar.user.dto.TeacherDtos.CreateTeacherRequest;
import mz.co.aprendaacodar.user.dto.TeacherDtos.TeacherResponse;
import mz.co.aprendaacodar.user.dto.TeacherDtos.UpdateTeacherRequest;
import mz.co.aprendaacodar.user.service.TeacherService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/teachers")
@PreAuthorize("hasRole('ADMIN')")
public class TeacherController {
    private final TeacherService service;

    public TeacherController(TeacherService service) {
        this.service = service;
    }

    @PostMapping
    public TeacherResponse create(Authentication authentication, @Valid @RequestBody CreateTeacherRequest request) {
        return service.create((UUID) authentication.getPrincipal(), request);
    }

    @GetMapping
    public PageResponse<TeacherResponse> list(Pageable pageable) {
        return service.list(pageable);
    }

    @GetMapping("/{id}")
    public TeacherResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public TeacherResponse update(Authentication authentication, @PathVariable UUID id,
                                  @Valid @RequestBody UpdateTeacherRequest request) {
        return service.update((UUID) authentication.getPrincipal(), id, request);
    }

    @PatchMapping("/{id}/deactivate")
    public void deactivate(Authentication authentication, @PathVariable UUID id) {
        service.deactivate((UUID) authentication.getPrincipal(), id);
    }
}
