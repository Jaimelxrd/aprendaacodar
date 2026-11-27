package mz.co.aprendaacodar.enrollment.repository;

import mz.co.aprendaacodar.enrollment.entity.LessonCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LessonCompletionRepository extends JpaRepository<LessonCompletion, UUID> {
    boolean existsByEnrollmentIdAndLessonId(UUID enrollmentId, UUID lessonId);
    List<LessonCompletion> findAllByEnrollmentIdOrderByCompletedAtAsc(UUID enrollmentId);
}
