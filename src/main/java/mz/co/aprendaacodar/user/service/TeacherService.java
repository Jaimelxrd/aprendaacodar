package mz.co.aprendaacodar.user.service;

import mz.co.aprendaacodar.audit.service.AuditService;
import mz.co.aprendaacodar.common.enums.Enums.UserRole;
import mz.co.aprendaacodar.common.enums.Enums.UserStatus;
import mz.co.aprendaacodar.common.exception.BusinessException;
import mz.co.aprendaacodar.common.exception.ResourceNotFoundException;
import mz.co.aprendaacodar.common.response.PageResponse;
import mz.co.aprendaacodar.security.RefreshTokenRepository;
import mz.co.aprendaacodar.user.dto.TeacherDtos.CreateTeacherRequest;
import mz.co.aprendaacodar.user.dto.TeacherDtos.TeacherResponse;
import mz.co.aprendaacodar.user.dto.TeacherDtos.UpdateTeacherRequest;
import mz.co.aprendaacodar.user.entity.TeacherProfile;
import mz.co.aprendaacodar.user.entity.User;
import mz.co.aprendaacodar.user.repository.TeacherProfileRepository;
import mz.co.aprendaacodar.user.repository.UserRepository;
import mz.co.aprendaacodar.course.repository.CourseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class TeacherService {
    private final UserRepository users;
    private final TeacherProfileRepository profiles;
    private final PasswordEncoder encoder;
    private final RefreshTokenRepository tokens;
    private final AuditService audit;
    private final CourseRepository courses;

    public TeacherService(UserRepository users, TeacherProfileRepository profiles, PasswordEncoder encoder,
                          RefreshTokenRepository tokens, AuditService audit, CourseRepository courses) {
        this.users = users;
        this.profiles = profiles;
        this.encoder = encoder;
        this.tokens = tokens;
        this.audit = audit;
        this.courses = courses;
    }

    @Transactional
    public TeacherResponse create(UUID adminId, CreateTeacherRequest request) {
        if (users.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessException("Este e-mail já está registado.");
        }
        User teacher = users.save(new User(request.firstName(), request.lastName(), request.email(),
                request.phoneNumber(), encoder.encode(request.password()), UserRole.TEACHER));
        TeacherProfile profile = profiles.save(new TeacherProfile(teacher, request.biography(),
                request.specialization(), request.professionalTitle()));
        audit.record(admin(adminId), "TEACHER_CREATED", "User", teacher.getId(), Map.of("email", teacher.getEmail()));
        return map(profile);
    }

    @Transactional(readOnly = true)
    public PageResponse<TeacherResponse> list(Pageable pageable) {
        return PageResponse.from(profiles.findAll(pageable), this::map);
    }

    @Transactional(readOnly = true)
    public TeacherResponse get(UUID id) {
        return map(profiles.findByUserId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado.")));
    }

    @Transactional
    public TeacherResponse update(UUID adminId, UUID id, UpdateTeacherRequest request) {
        TeacherProfile profile = profiles.findByUserId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado."));
        User teacher = profile.getUser();
        if (!teacher.getEmail().equalsIgnoreCase(request.email()) && users.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessException("Este e-mail já está registado.");
        }
        teacher.updateProfile(request.firstName(), request.lastName(), request.phoneNumber());
        teacher.setEmail(request.email());
        profile.update(request.biography(), request.specialization(), request.professionalTitle());
        audit.record(admin(adminId), "TEACHER_UPDATED", "User", id, Map.of("email", teacher.getEmail()));
        return map(profile);
    }

    @Transactional
    public void deactivate(UUID adminId, UUID id) {
        TeacherProfile profile = profiles.findByUserId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado."));
        profile.getUser().setStatus(UserStatus.INACTIVE);
        tokens.revokeByUserId(id);
        audit.record(admin(adminId), "TEACHER_DEACTIVATED", "User", id, Map.of());
    }

    @Transactional
    public TeacherResponse activate(UUID adminId, UUID id) {
        TeacherProfile profile = profiles.findByUserId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado."));
        profile.getUser().setStatus(UserStatus.ACTIVE);
        audit.record(admin(adminId), "TEACHER_ACTIVATED", "User", id, Map.of());
        return map(profile);
    }

    public record TeacherCourseResponse(UUID id, String title, String slug, String status) {}

    @Transactional(readOnly = true)
    public PageResponse<TeacherCourseResponse> courses(UUID id, Pageable pageable) {
        if (profiles.findByUserId(id).isEmpty()) throw new ResourceNotFoundException("Professor não encontrado.");
        return PageResponse.from(courses.findAllByTeacherId(id, pageable),
                c -> new TeacherCourseResponse(c.getId(), c.getTitle(), c.getSlug(), c.getStatus().name()));
    }

    private User admin(UUID id) {
        return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("Administrador não encontrado."));
    }

    private TeacherResponse map(TeacherProfile profile) {
        User user = profile.getUser();
        return new TeacherResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getPhoneNumber(), profile.getBiography(), profile.getSpecialization(),
                profile.getProfessionalTitle(), user.getStatus().name());
    }
}
