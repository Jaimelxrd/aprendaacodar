package mz.co.aprendaacodar.course.repository;

import mz.co.aprendaacodar.common.enums.Enums.LessonStatus;
import mz.co.aprendaacodar.course.entity.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    List<Lesson> findAllByCourseIdOrderByPosition(UUID id);
    Page<Lesson> findAllByCourseIdAndStatusOrderByPosition(UUID id, LessonStatus status, Pageable pageable);
    List<Lesson> findAllByModuleIdOrderByPosition(UUID id);
    boolean existsByModuleId(UUID id);
}
