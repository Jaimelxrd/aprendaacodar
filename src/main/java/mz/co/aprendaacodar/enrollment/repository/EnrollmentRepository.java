package mz.co.aprendaacodar.enrollment.repository;

import mz.co.aprendaacodar.common.enums.Enums.EnrollmentStatus;
import mz.co.aprendaacodar.enrollment.entity.CourseEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<CourseEnrollment, UUID> {
    Optional<CourseEnrollment> findByStudentIdAndCourseId(UUID student, UUID course);
    Page<CourseEnrollment> findAllByStudentId(UUID student, Pageable pageable);
    boolean existsByStudentIdAndCourseIdAndStatus(UUID student, UUID course, EnrollmentStatus status);
    Page<CourseEnrollment> findAllByStatus(EnrollmentStatus status, Pageable pageable);
}
