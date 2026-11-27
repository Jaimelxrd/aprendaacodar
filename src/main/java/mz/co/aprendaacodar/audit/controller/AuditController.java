package mz.co.aprendaacodar.audit.controller;

import mz.co.aprendaacodar.audit.dto.AuditLogResponse;
import mz.co.aprendaacodar.audit.service.AuditService;
import mz.co.aprendaacodar.common.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {
    private final AuditService service;

    public AuditController(AuditService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<AuditLogResponse> list(Pageable pageable) {
        return service.list(pageable);
    }
}
