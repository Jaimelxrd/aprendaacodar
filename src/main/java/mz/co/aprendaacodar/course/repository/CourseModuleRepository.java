package mz.co.aprendaacodar.course.repository;

import mz.co.aprendaacodar.course.entity.CourseModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseModuleRepository extends JpaRepository<CourseModule, UUID> {
    List<CourseModule> findAllByCourseIdOrderByPosition(UUID id);
    Page<CourseModule> findAllByCourseIdOrderByPosition(UUID id, Pageable pageable);
}
