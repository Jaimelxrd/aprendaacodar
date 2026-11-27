package mz.co.aprendaacodar.question.repository;

import mz.co.aprendaacodar.question.entity.LessonQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuestionRepository extends JpaRepository<LessonQuestion, UUID> {
    Page<LessonQuestion> findAllByLessonIdOrderByCreatedAt(UUID lessonId, Pageable pageable);
}
