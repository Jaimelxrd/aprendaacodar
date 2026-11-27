package mz.co.aprendaacodar.config;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class PlatformSettingsController {
    private final PlatformSettingsService service;
    public PlatformSettingsController(PlatformSettingsService service) { this.service = service; }

    @GetMapping({"/public/settings", "/settings"})
    public PlatformSettingsService.SettingsResponse get() { return service.get(); }

    @PutMapping("/admin/settings")
    @PreAuthorize("hasRole('ADMIN')")
    public PlatformSettingsService.SettingsResponse update(Authentication auth,
            @RequestBody PlatformSettingsService.UpdateRequest request) {
        return service.update((UUID) auth.getPrincipal(), request);
    }
}
