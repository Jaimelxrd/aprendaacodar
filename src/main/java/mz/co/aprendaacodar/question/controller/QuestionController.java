package mz.co.aprendaacodar.question.controller;
import mz.co.aprendaacodar.common.response.PageResponse;import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;import mz.co.aprendaacodar.question.dto.QuestionDtos.*;import mz.co.aprendaacodar.question.service.QuestionService;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController @RequestMapping("/api/v1") public class QuestionController {private final QuestionService service;public QuestionController(QuestionService s){service=s;}
 @PostMapping("/lessons/{lessonId}/questions") @PreAuthorize("hasRole('STUDENT')") public Response ask(Authentication a,@PathVariable UUID lessonId,@Valid @RequestBody ContentRequest r){return service.ask((UUID)a.getPrincipal(),lessonId,r.content());}
 @GetMapping("/lessons/{lessonId}/questions") public PageResponse<Response> list(Authentication a,@PathVariable UUID lessonId,Pageable pageable){return service.byLesson((UUID)a.getPrincipal(),lessonId,pageable);}
 @GetMapping("/questions/{id}") public Response get(Authentication a,@PathVariable UUID id){return service.get((UUID)a.getPrincipal(),id);}
 @PostMapping("/questions/{id}/replies") @PreAuthorize("hasAnyRole('TEACHER','ADMIN')") public Response reply(Authentication a,@PathVariable UUID id,@Valid @RequestBody ContentRequest r){return service.reply((UUID)a.getPrincipal(),id,r.content());}
 @PatchMapping("/questions/{id}/close") @PreAuthorize("hasAnyRole('TEACHER','ADMIN')") public void close(Authentication a,@PathVariable UUID id){service.close((UUID)a.getPrincipal(),id);}
}
