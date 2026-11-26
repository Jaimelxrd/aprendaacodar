package mz.co.aprendaacodar.common.enums;

public final class Enums {
    private Enums() {}
    public enum UserRole { STUDENT, TEACHER, ADMIN }
    public enum UserStatus { ACTIVE, BLOCKED, INACTIVE }
    public enum CourseStatus { DRAFT, PENDING_APPROVAL, PUBLISHED, REJECTED, ARCHIVED }
    public enum CurriculumMode { MODULES, DIRECT_LESSONS }
    public enum LessonStatus { DRAFT, PUBLISHED, ARCHIVED }
    public enum EnrollmentStatus { PENDING_PAYMENT, UNDER_REVIEW, ACTIVE, REJECTED, REVOKED }
    public enum EnrollmentSource { PAYMENT, ADMIN }
    public enum PaymentMethod { MPESA, EMOLA }
    public enum PaymentStatus { PENDING_REVIEW, APPROVED, REJECTED }
    public enum QuestionStatus { OPEN, ANSWERED, CLOSED }
    public enum BlogPostStatus { DRAFT, PUBLISHED, ARCHIVED }
    public enum CommentStatus { VISIBLE, HIDDEN }
}
