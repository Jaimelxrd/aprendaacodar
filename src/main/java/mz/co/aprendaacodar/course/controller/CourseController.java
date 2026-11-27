package mz.co.aprendaacodar.course.controller;
import jakarta.validation.Valid;import mz.co.aprendaacodar.course.dto.CourseDtos.*;import mz.co.aprendaacodar.course.service.CourseService;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;import java.util.*;
import mz.co.aprendaacodar.common.response.PageResponse;import org.springframework.data.domain.Pageable;
@RestController @RequestMapping("/api/v1") public class CourseController {
 private final CourseService service;public CourseController(CourseService s){service=s;}
 @GetMapping({"/public/courses", "/courses"}) public PageResponse<CourseResponse> publicCourses(@RequestParam(required=false) String q,Pageable pageable){return service.searchPublished(q,pageable);}
 @GetMapping({"/public/courses/{courseRef}", "/courses/{courseRef}"}) public CourseResponse publicCourse(@PathVariable String courseRef){
  try { return service.publicById(UUID.fromString(courseRef)); }
  catch (IllegalArgumentException ignored) { return service.publicBySlug(courseRef); }
 }
 @PostMapping("/teacher/courses") @org.springframework.security.access.prepost.PreAuthorize("hasRole('TEACHER')") public CourseResponse create(Authentication a,@Valid @RequestBody CourseRequest r){return service.create((UUID)a.getPrincipal(),r,false);}
 @GetMapping("/teacher/courses") @org.springframework.security.access.prepost.PreAuthorize("hasRole('TEACHER')") public PageResponse<CourseResponse> mine(Authentication a,Pageable pageable){return service.mine((UUID)a.getPrincipal(),pageable);}
 @GetMapping("/teacher/courses/{id}") @org.springframework.security.access.prepost.PreAuthorize("hasRole('TEACHER')") public CourseResponse getMine(Authentication a,@PathVariable UUID id){return service.getMine((UUID)a.getPrincipal(),id);}
 @PutMapping("/teacher/courses/{id}") @org.springframework.security.access.prepost.PreAuthorize("hasRole('TEACHER')") public CourseResponse update(Authentication a,@PathVariable UUID id,@Valid @RequestBody CourseRequest r){return service.update((UUID)a.getPrincipal(),id,r,false);}
 @PostMapping("/teacher/courses/{id}/submit") @org.springframework.security.access.prepost.PreAuthorize("hasRole('TEACHER')") public void submit(Authentication a,@PathVariable UUID id){service.submit((UUID)a.getPrincipal(),id);}
 @GetMapping("/admin/courses") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public PageResponse<CourseResponse> all(Pageable pageable){return service.all(pageable);}
 @PostMapping("/admin/courses") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public CourseResponse createAdmin(Authentication a,@Valid @RequestBody CourseRequest r){return service.create((UUID)a.getPrincipal(),r,true);}
 @GetMapping("/admin/courses/{id}") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public CourseResponse adminGet(@PathVariable UUID id){return service.get(id);}
 @PutMapping("/admin/courses/{id}") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public CourseResponse adminUpdate(Authentication a,@PathVariable UUID id,@Valid @RequestBody CourseRequest r){return service.update((UUID)a.getPrincipal(),id,r,true);}
 @PatchMapping("/admin/courses/{id}/approve") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public CourseResponse approve(Authentication a,@PathVariable UUID id){return service.approve((UUID)a.getPrincipal(),id);}
 @PatchMapping("/admin/courses/{id}/reject") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public CourseResponse reject(Authentication a,@PathVariable UUID id,@Valid @RequestBody RejectRequest r){return service.reject((UUID)a.getPrincipal(),id,r.rejectionReason());}
 @PatchMapping("/admin/courses/{id}/archive") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public CourseResponse archive(Authentication a,@PathVariable UUID id){return service.archive((UUID)a.getPrincipal(),id);}
}
