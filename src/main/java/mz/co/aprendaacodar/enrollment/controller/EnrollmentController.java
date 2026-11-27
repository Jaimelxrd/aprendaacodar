package mz.co.aprendaacodar.enrollment.controller;
import mz.co.aprendaacodar.common.response.PageResponse;import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;import mz.co.aprendaacodar.enrollment.dto.EnrollmentDtos.*;import mz.co.aprendaacodar.enrollment.service.EnrollmentService;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController @RequestMapping("/api/v1") public class EnrollmentController {private final EnrollmentService service;public EnrollmentController(EnrollmentService s){service=s;}
 @PostMapping("/courses/{courseId}/enrollments") @PreAuthorize("hasRole('STUDENT')") public Response request(Authentication a,@PathVariable UUID courseId){return service.request((UUID)a.getPrincipal(),courseId);}
 @GetMapping("/enrollments/me") @PreAuthorize("hasRole('STUDENT')") public PageResponse<Response> mine(Authentication a,Pageable pageable){return service.mine((UUID)a.getPrincipal(),pageable);}
 @GetMapping("/enrollments/{id}") public Response get(Authentication a,@PathVariable UUID id){return service.get((UUID)a.getPrincipal(),id,a.getAuthorities().stream().anyMatch(x->x.getAuthority().equals("ROLE_ADMIN")));}
 @GetMapping("/admin/enrollments") @PreAuthorize("hasRole('ADMIN')") public PageResponse<Response> all(Pageable pageable){return service.all(pageable);}
 @GetMapping("/teacher/courses/{courseId}/students") @PreAuthorize("hasRole('TEACHER')") public PageResponse<CourseStudentResponse> courseStudents(Authentication a,@PathVariable UUID courseId,Pageable pageable){return service.courseStudents((UUID)a.getPrincipal(),courseId,pageable);}
 @GetMapping("/admin/users/{studentId}/enrollments") @PreAuthorize("hasRole('ADMIN')") public PageResponse<Response> byStudent(@PathVariable UUID studentId,Pageable pageable){return service.byStudent(studentId,pageable);}
 @PostMapping("/admin/enrollments/grant") @PreAuthorize("hasRole('ADMIN')") public Response grant(Authentication a,@Valid @RequestBody GrantRequest r){return service.grant((UUID)a.getPrincipal(),r.studentId(),r.courseId(),r.reason());}
 @PatchMapping("/admin/enrollments/{id}/revoke") @PreAuthorize("hasRole('ADMIN')") public void revoke(Authentication a,@PathVariable UUID id,@Valid @RequestBody RevokeRequest r){service.revoke((UUID)a.getPrincipal(),id,r.reason());}
}
