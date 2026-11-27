package mz.co.aprendaacodar.audit.service;
import mz.co.aprendaacodar.audit.dto.AuditLogResponse;
import mz.co.aprendaacodar.audit.entity.AuditLog;
import mz.co.aprendaacodar.audit.repository.AuditLogRepository;
import mz.co.aprendaacodar.common.response.PageResponse;
import mz.co.aprendaacodar.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class AuditService {
    private final AuditLogRepository logs;

    public AuditService(AuditLogRepository logs) {
        this.logs = logs;
    }

    public void record(User actor, String action, String type, UUID id, Map<String, Object> details) {
        logs.save(new AuditLog(actor, action, type, id, details == null ? Map.of() : details));
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditLogResponse> list(Pageable pageable) {
        return PageResponse.from(logs.findAllByOrderByCreatedAtDesc(pageable), AuditService::response);
    }

    private static AuditLogResponse response(AuditLog log) {
        return new AuditLogResponse(log.getId(), log.getActor() == null ? null : log.getActor().getId(),
                log.getAction(), log.getEntityType(), log.getEntityId(), log.getDetails(), log.getCreatedAt());
    }
}
