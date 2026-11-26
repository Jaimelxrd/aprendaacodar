package mz.co.aprendaacodar.payment.controller;
import jakarta.validation.Valid;import mz.co.aprendaacodar.payment.dto.PaymentDtos.*;import mz.co.aprendaacodar.payment.service.PaymentService;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController @RequestMapping("/api/v1") public class PaymentController {private final PaymentService service;public PaymentController(PaymentService s){service=s;}
 @PostMapping("/enrollments/{id}/payments") @PreAuthorize("hasRole('STUDENT')") public Response submit(Authentication a,@PathVariable UUID id,@Valid @RequestBody SubmitRequest r){return service.submit((UUID)a.getPrincipal(),id,r);}
 @GetMapping("/payments/me") @PreAuthorize("hasRole('STUDENT')") public List<Response> mine(Authentication a){return service.mine((UUID)a.getPrincipal());}
 @GetMapping("/admin/payments") @PreAuthorize("hasRole('ADMIN')") public List<Response> all(){return service.all();}
 @GetMapping("/admin/payments/{id}") @PreAuthorize("hasRole('ADMIN')") public Response get(@PathVariable UUID id){return service.get(id);}
 @PatchMapping("/admin/payments/{id}/approve") @PreAuthorize("hasRole('ADMIN')") public Response approve(Authentication a,@PathVariable UUID id){return service.approve((UUID)a.getPrincipal(),id);}
 @PatchMapping("/admin/payments/{id}/reject") @PreAuthorize("hasRole('ADMIN')") public Response reject(Authentication a,@PathVariable UUID id,@Valid @RequestBody ReviewRequest r){return service.reject((UUID)a.getPrincipal(),id,r.rejectionReason());}
}
