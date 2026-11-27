package mz.co.aprendaacodar.payment.repository;

import mz.co.aprendaacodar.common.enums.Enums.PaymentStatus;
import mz.co.aprendaacodar.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    @org.springframework.data.jpa.repository.Query("select coalesce(sum(p.amount), 0) from Payment p where p.status = :status")
    java.math.BigDecimal totalAmountByStatus(@org.springframework.data.repository.query.Param("status") PaymentStatus status);
    long countByStatus(PaymentStatus status);
    Page<Payment> findAllByEnrollmentStudentId(UUID studentId, Pageable pageable);
    Page<Payment> findAllByStatus(PaymentStatus status, Pageable pageable);
    boolean existsByTransactionReference(String ref);
    List<Payment> findAllByEnrollmentId(UUID enrollmentId);
}
