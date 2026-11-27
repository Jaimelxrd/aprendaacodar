package mz.co.aprendaacodar.config;

import mz.co.aprendaacodar.audit.dto.AuditLogResponse;
import mz.co.aprendaacodar.audit.service.AuditService;
import mz.co.aprendaacodar.common.enums.Enums.*;
import mz.co.aprendaacodar.common.response.PageResponse;
import mz.co.aprendaacodar.course.repository.CourseRepository;
import mz.co.aprendaacodar.enrollment.repository.EnrollmentRepository;
import mz.co.aprendaacodar.payment.repository.PaymentRepository;
import mz.co.aprendaacodar.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {
    private final UserRepository users;
    private final CourseRepository courses;
    private final EnrollmentRepository enrollments;
    private final PaymentRepository payments;
    private final AuditService audit;

    public AdminDashboardController(UserRepository users, CourseRepository courses, EnrollmentRepository enrollments,
                                    PaymentRepository payments, AuditService audit) {
        this.users = users; this.courses = courses; this.enrollments = enrollments; this.payments = payments; this.audit = audit;
    }

    public record DashboardResponse(long students, long teachers, long courses, long publishedCourses,
                                    long pendingCourses, long activeEnrollments, long pendingPayments,
                                    long approvedPayments, long rejectedPayments, BigDecimal approvedRevenue,
                                    List<AuditLogResponse> recentActivity) {}

    @GetMapping
    public DashboardResponse get() {
        PageResponse<AuditLogResponse> recent = audit.list(PageRequest.of(0, 10));
        return new DashboardResponse(users.countByRole(UserRole.STUDENT), users.countByRole(UserRole.TEACHER),
                courses.count(), courses.countByStatus(CourseStatus.PUBLISHED), courses.countByStatus(CourseStatus.PENDING_APPROVAL),
                enrollments.countByStatus(EnrollmentStatus.ACTIVE), payments.countByStatus(PaymentStatus.PENDING_REVIEW),
                payments.countByStatus(PaymentStatus.APPROVED), payments.countByStatus(PaymentStatus.REJECTED),
                payments.totalAmountByStatus(PaymentStatus.APPROVED), recent.content());
    }
}
