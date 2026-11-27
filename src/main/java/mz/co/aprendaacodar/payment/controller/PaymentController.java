package mz.co.aprendaacodar.payment.controller;
import mz.co.aprendaacodar.common.response.PageResponse;import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;import mz.co.aprendaacodar.payment.dto.PaymentDtos.*;import mz.co.aprendaacodar.payment.service.PaymentService;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController @RequestMapping("/api/v1") public class PaymentController {private final PaymentService service;public PaymentController(PaymentService s){service=s;}
 @PostMapping("/enrollments/{id}/payments") @PreAuthorize("hasRole('STUDENT')") public Response submit(Authentication a,@PathVariable UUID id,@Valid @RequestBody SubmitRequest r){return service.submit((UUID)a.getPrincipal(),id,r);}
 @GetMapping("/payments/me") @PreAuthorize("hasRole('STUDENT')") public PageResponse<Response> mine(Authentication a,Pageable pageable){return service.mine((UUID)a.getPrincipal(),pageable);}
 @GetMapping("/admin/payments") @PreAuthorize("hasRole('ADMIN')") public PageResponse<Response> all(Pageable pageable){return service.all(pageable);}
 @GetMapping("/admin/payments/{id}") @PreAuthorize("hasRole('ADMIN')") public Response get(@PathVariable UUID id){return service.get(id);}
 @GetMapping("/admin/users/{studentId}/payments") @PreAuthorize("hasRole('ADMIN')") public PageResponse<Response> byStudent(@PathVariable UUID studentId,Pageable pageable){return service.byStudent(studentId,pageable);}
 @PatchMapping("/admin/payments/{id}/approve") @PreAuthorize("hasRole('ADMIN')") public Response approve(Authentication a,@PathVariable UUID id){return service.approve((UUID)a.getPrincipal(),id);}
 @PatchMapping("/admin/payments/{id}/reject") @PreAuthorize("hasRole('ADMIN')") public Response reject(Authentication a,@PathVariable UUID id,@Valid @RequestBody ReviewRequest r){return service.reject((UUID)a.getPrincipal(),id,r.rejectionReason());}
}
