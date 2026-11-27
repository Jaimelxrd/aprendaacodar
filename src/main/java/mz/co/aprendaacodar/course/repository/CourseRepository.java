package mz.co.aprendaacodar.course.repository;

import mz.co.aprendaacodar.common.enums.Enums.CourseStatus;
import mz.co.aprendaacodar.course.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    Optional<Course> findBySlugAndStatus(String slug, CourseStatus status);
    Page<Course> findAllByStatus(CourseStatus status, Pageable pageable);
    Page<Course> findAllByTeacherId(UUID teacherId, Pageable pageable);
}
