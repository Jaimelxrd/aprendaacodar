package mz.co.aprendaacodar.enrollment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import mz.co.aprendaacodar.common.entity.BaseEntity;
import mz.co.aprendaacodar.course.entity.Lesson;

import java.time.Instant;

@Entity
@Table(name = "lesson_completions", uniqueConstraints = @UniqueConstraint(
        name = "uk_lesson_completions_enrollment_lesson",
        columnNames = {"enrollment_id", "lesson_id"}))
public class LessonCompletion extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CourseEnrollment enrollment;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Lesson lesson;

    @Column(nullable = false)
    private Instant completedAt;

    protected LessonCompletion() {}

    public LessonCompletion(CourseEnrollment enrollment, Lesson lesson) {
        this.enrollment = enrollment;
        this.lesson = lesson;
        this.completedAt = Instant.now();
    }

    public Lesson getLesson() {
        return lesson;
    }
}
