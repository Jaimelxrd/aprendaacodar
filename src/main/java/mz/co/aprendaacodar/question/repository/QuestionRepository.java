package mz.co.aprendaacodar.question.repository;

import mz.co.aprendaacodar.question.entity.LessonQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuestionRepository extends JpaRepository<LessonQuestion, UUID> {
    Page<LessonQuestion> findAllByLessonIdOrderByCreatedAt(UUID lessonId, Pageable pageable);
    @org.springframework.data.jpa.repository.Query("select q from LessonQuestion q where (:status is null or q.status = :status) and (:courseId is null or q.lesson.course.id = :courseId) and (:studentId is null or q.student.id = :studentId)")
    Page<LessonQuestion> searchAdmin(@org.springframework.data.repository.query.Param("status") mz.co.aprendaacodar.common.enums.Enums.QuestionStatus status,
                                     @org.springframework.data.repository.query.Param("courseId") UUID courseId,
                                     @org.springframework.data.repository.query.Param("studentId") UUID studentId,
                                     Pageable pageable);
}
