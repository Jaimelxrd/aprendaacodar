package mz.co.aprendaacodar.course.controller;
import jakarta.validation.Valid;import mz.co.aprendaacodar.course.dto.CourseDtos.*;import mz.co.aprendaacodar.course.service.CourseService;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController @RequestMapping("/api/v1") public class CourseController {
 private final CourseService service;public CourseController(CourseService s){service=s;}
 @GetMapping("/public/courses") public List<CourseResponse> publicCourses(){return service.published();}
 @GetMapping("/public/courses/{slug}") public CourseResponse publicCourse(@PathVariable String slug){return service.publicBySlug(slug);}
 @PostMapping("/teacher/courses") @org.springframework.security.access.prepost.PreAuthorize("hasRole('TEACHER')") public CourseResponse create(Authentication a,@Valid @RequestBody CourseRequest r){return service.create((UUID)a.getPrincipal(),r,false);}
 @GetMapping("/teacher/courses") @org.springframework.security.access.prepost.PreAuthorize("hasRole('TEACHER')") public List<CourseResponse> mine(Authentication a){return service.mine((UUID)a.getPrincipal());}
 @GetMapping("/teacher/courses/{id}") @org.springframework.security.access.prepost.PreAuthorize("hasRole('TEACHER')") public CourseResponse getMine(Authentication a,@PathVariable UUID id){CourseResponse c=service.get(id);if(!service.mine((UUID)a.getPrincipal()).stream().anyMatch(x->x.id().equals(id)))throw new mz.co.aprendaacodar.common.exception.ForbiddenOperationException("Não pode consultar curso de outro professor.");return c;}
 @PutMapping("/teacher/courses/{id}") @org.springframework.security.access.prepost.PreAuthorize("hasRole('TEACHER')") public CourseResponse update(Authentication a,@PathVariable UUID id,@Valid @RequestBody CourseRequest r){return service.update((UUID)a.getPrincipal(),id,r,false);}
 @PostMapping("/teacher/courses/{id}/submit") @org.springframework.security.access.prepost.PreAuthorize("hasRole('TEACHER')") public void submit(Authentication a,@PathVariable UUID id){service.submit((UUID)a.getPrincipal(),id);}
 @GetMapping("/admin/courses") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public List<CourseResponse> all(){return service.all();}
 @PostMapping("/admin/courses") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public CourseResponse createAdmin(Authentication a,@Valid @RequestBody CourseRequest r){return service.create((UUID)a.getPrincipal(),r,true);}
 @GetMapping("/admin/courses/{id}") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public CourseResponse adminGet(@PathVariable UUID id){return service.get(id);}
 @PutMapping("/admin/courses/{id}") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public CourseResponse adminUpdate(Authentication a,@PathVariable UUID id,@Valid @RequestBody CourseRequest r){return service.update((UUID)a.getPrincipal(),id,r,true);}
 @PatchMapping("/admin/courses/{id}/approve") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public void approve(Authentication a,@PathVariable UUID id){service.approve((UUID)a.getPrincipal(),id);}
 @PatchMapping("/admin/courses/{id}/reject") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public void reject(Authentication a,@PathVariable UUID id,@Valid @RequestBody RejectRequest r){service.reject((UUID)a.getPrincipal(),id,r.rejectionReason());}
 @PatchMapping("/admin/courses/{id}/archive") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public void archive(Authentication a,@PathVariable UUID id){service.archive((UUID)a.getPrincipal(),id);}
}
