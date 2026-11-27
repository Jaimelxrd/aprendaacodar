package mz.co.aprendaacodar.course.service;

import mz.co.aprendaacodar.audit.service.AuditService;
import mz.co.aprendaacodar.common.enums.Enums.CourseStatus;
import mz.co.aprendaacodar.common.enums.Enums.UserRole;
import mz.co.aprendaacodar.common.exception.ForbiddenOperationException;
import mz.co.aprendaacodar.common.exception.ResourceNotFoundException;
import mz.co.aprendaacodar.common.response.PageResponse;
import mz.co.aprendaacodar.course.dto.CourseDtos.CourseRequest;
import mz.co.aprendaacodar.course.dto.CourseDtos.CourseResponse;
import mz.co.aprendaacodar.course.dto.CourseDtos.TeacherSummary;
import mz.co.aprendaacodar.course.entity.Course;
import mz.co.aprendaacodar.course.repository.CourseRepository;
import mz.co.aprendaacodar.storage.StorageService;
import mz.co.aprendaacodar.user.entity.User;
import mz.co.aprendaacodar.user.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class CourseService {
    private final CourseRepository courses;
    private final UserRepository users;
    private final AuditService audit;
    private final StorageService storage;

    public CourseService(CourseRepository courses, UserRepository users, AuditService audit, StorageService storage) {
        this.courses = courses;
        this.users = users;
        this.audit = audit;
        this.storage = storage;
    }

    @Transactional(readOnly = true)
    public PageResponse<CourseResponse> published(Pageable pageable) {
        return PageResponse.from(courses.findAllByStatus(CourseStatus.PUBLISHED, pageable), this::map);
    }

    @Transactional(readOnly = true)
    public CourseResponse publicBySlug(String slug) {
        return map(courses.findBySlugAndStatus(slug, CourseStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado.")));
    }

    @Transactional(readOnly = true)
    public PageResponse<CourseResponse> mine(UUID teacher, Pageable pageable) {
        return PageResponse.from(courses.findAllByTeacherId(teacher, pageable), this::map);
    }

    @Transactional(readOnly = true)
    public PageResponse<CourseResponse> all(Pageable pageable) {
        return PageResponse.from(courses.findAll(pageable), this::map);
    }

    @Transactional(readOnly = true)
    public CourseResponse get(UUID id) {
        return map(find(id));
    }

    @Transactional(readOnly = true)
    public CourseResponse getMine(UUID teacherId, UUID id) {
        Course course = find(id);
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new ForbiddenOperationException("Não pode consultar curso de outro professor.");
        }
        return map(course);
    }

    @Transactional(readOnly = true)
    public void assertCanManage(UUID actor, UUID id) {
        Course course = find(id);
        if (!course.getTeacher().getId().equals(actor) && user(actor).getRole() != UserRole.ADMIN) {
            throw new ForbiddenOperationException("Não pode gerir este curso.");
        }
    }

    @Transactional
    public CourseResponse create(UUID actorId, CourseRequest request, boolean admin) {
        User actor = user(actorId);
        if (!admin && actor.getRole() != UserRole.TEACHER) {
            throw new ForbiddenOperationException("Apenas professores podem criar cursos.");
        }
        Course course = courses.save(new Course(request.title(), request.shortDescription(), request.description(),
                request.coverImageKey(), request.price(), request.durationHours(), request.prerequisites(),
                request.learningOutcomes(), request.curriculumMode(), actor));
        if (admin) audit.record(actor, "COURSE_CREATED", "Course", course.getId(), Map.of("slug", course.getSlug()));
        return map(course);
    }

    @Transactional
    public CourseResponse update(UUID actorId, UUID id, CourseRequest request, boolean admin) {
        Course course = find(id);
        if (!admin && !course.getTeacher().getId().equals(actorId)) {
            throw new ForbiddenOperationException("Não pode gerir cursos de outro professor.");
        }
        course.update(request.title(), request.shortDescription(), request.description(), request.coverImageKey(),
                request.price(), request.durationHours(), request.prerequisites(), request.learningOutcomes(),
                request.curriculumMode());
        if (admin) audit.record(user(actorId), "COURSE_UPDATED", "Course", id, Map.of("slug", course.getSlug()));
        return map(course);
    }

    @Transactional
    public void submit(UUID actorId, UUID id) {
        Course course = find(id);
        if (!course.getTeacher().getId().equals(actorId)) {
            throw new ForbiddenOperationException("Não pode gerir cursos de outro professor.");
        }
        if (course.getStatus() == CourseStatus.PUBLISHED || course.getStatus() == CourseStatus.ARCHIVED) {
            throw new mz.co.aprendaacodar.common.exception.BusinessException("Este curso não pode ser submetido neste estado.");
        }
        course.submit();
        audit.record(user(actorId), "COURSE_SUBMITTED", "Course", id, Map.of());
    }

    @Transactional
    public void approve(UUID actorId, UUID id) {
        User admin = user(actorId);
        find(id).approve(admin);
        audit.record(admin, "COURSE_APPROVED", "Course", id, Map.of());
    }

    @Transactional
    public void reject(UUID actorId, UUID id, String reason) {
        User admin = user(actorId);
        find(id).reject(admin, reason);
        audit.record(admin, "COURSE_REJECTED", "Course", id, Map.of("reason", reason));
    }

    @Transactional
    public void archive(UUID actorId, UUID id) {
        Course course = find(id);
        course.archive();
        audit.record(user(actorId), "COURSE_ARCHIVED", "Course", id, Map.of());
    }

    private User user(UUID id) {
        return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado."));
    }

    private Course find(UUID id) {
        return courses.findById(id).orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado."));
    }

    private CourseResponse map(Course course) {
        return new CourseResponse(course.getId(), course.getTitle(), course.getSlug(), course.getShortDescription(),
                course.getDescription(), course.getCoverImageKey() == null ? null : storage.url(course.getCoverImageKey(), true),
                course.getPrice(), course.getCurrency(), course.getDurationHours(),
                new TeacherSummary(course.getTeacher().getId(), course.getTeacher().getFirstName(),
                        course.getTeacher().getLastName(), null), course.getCurriculumMode(), course.getStatus());
    }
}
