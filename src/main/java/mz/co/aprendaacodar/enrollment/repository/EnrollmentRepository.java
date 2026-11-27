package mz.co.aprendaacodar.enrollment.repository;

import mz.co.aprendaacodar.common.enums.Enums.EnrollmentStatus;
import mz.co.aprendaacodar.enrollment.entity.CourseEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<CourseEnrollment, UUID> {
    long countByStatus(EnrollmentStatus status);
    Optional<CourseEnrollment> findByStudentIdAndCourseId(UUID student, UUID course);
    Page<CourseEnrollment> findAllByStudentId(UUID student, Pageable pageable);
    Page<CourseEnrollment> findAllByCourseId(UUID courseId, Pageable pageable);
    boolean existsByStudentIdAndCourseIdAndStatus(UUID student, UUID course, EnrollmentStatus status);
    Page<CourseEnrollment> findAllByStatus(EnrollmentStatus status, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from CourseEnrollment e where e.id = :id")
    Optional<CourseEnrollment> findByIdForUpdate(@Param("id") UUID id);
}
