package mz.co.aprendaacodar.audit.service;
import mz.co.aprendaacodar.audit.entity.AuditLog;import mz.co.aprendaacodar.audit.repository.AuditLogRepository;import mz.co.aprendaacodar.user.entity.User;import org.springframework.stereotype.Service;import java.util.*;
@Service public class AuditService {private final AuditLogRepository logs;public AuditService(AuditLogRepository r){logs=r;}public void record(User actor,String action,String type,UUID id,Map<String,Object> details){logs.save(new AuditLog(actor,action,type,id,details==null?Map.of():details));}}
