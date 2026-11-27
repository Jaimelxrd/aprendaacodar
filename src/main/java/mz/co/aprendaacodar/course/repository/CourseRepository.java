package mz.co.aprendaacodar.course.repository;

import mz.co.aprendaacodar.common.enums.Enums.CourseStatus;
import mz.co.aprendaacodar.course.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    long countByStatus(CourseStatus status);
    @org.springframework.data.jpa.repository.Query("select c from Course c where c.status = :status and (:q is null or lower(c.title) like lower(concat('%', :q, '%')) or lower(c.shortDescription) like lower(concat('%', :q, '%')))")
    Page<Course> searchPublished(@org.springframework.data.repository.query.Param("status") CourseStatus status, @org.springframework.data.repository.query.Param("q") String q, Pageable pageable);
    Optional<Course> findBySlugAndStatus(String slug, CourseStatus status);
    Optional<Course> findByIdAndStatus(UUID id, CourseStatus status);
    Page<Course> findAllByStatus(CourseStatus status, Pageable pageable);
    Page<Course> findAllByTeacherId(UUID teacherId, Pageable pageable);
}
