package mz.co.aprendaacodar.common.exception;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
 private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
 @ExceptionHandler(ResourceNotFoundException.class) ResponseEntity<?> notFound(Exception e,HttpServletRequest r){return body(404,"RESOURCE_NOT_FOUND",e.getMessage(),r);}
 @ExceptionHandler(ForbiddenOperationException.class) ResponseEntity<?> forbidden(Exception e,HttpServletRequest r){return body(403,"FORBIDDEN",e.getMessage(),r);}
 @ExceptionHandler(ServiceUnavailableException.class) ResponseEntity<?> unavailable(Exception e,HttpServletRequest r){return body(503,"SERVICE_UNAVAILABLE",e.getMessage(),r);}
 @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class) ResponseEntity<?> denied(Exception e,HttpServletRequest r){return body(403,"FORBIDDEN","Não tem permissão para executar esta operação.",r);}
 @ExceptionHandler({BusinessException.class,IllegalArgumentException.class}) ResponseEntity<?> business(Exception e,HttpServletRequest r){return body(400,"BUSINESS_RULE_VIOLATION",e.getMessage(),r);}
 @ExceptionHandler(AuthenticationException.class) ResponseEntity<?> auth(AuthenticationException e,HttpServletRequest r){return body(401,"UNAUTHORIZED","Credenciais inválidas ou sessão não autorizada.",r);}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> invalid(MethodArgumentNotValidException e,HttpServletRequest r){String message=e.getBindingResult().getFieldErrors().stream().findFirst().map(x->x.getDefaultMessage()).orElse("Dados inválidos.");return body(400,"VALIDATION_ERROR",message,r);}
 @ExceptionHandler(HttpMessageNotReadableException.class) ResponseEntity<?> unreadable(HttpMessageNotReadableException e,HttpServletRequest r){return body(400,"INVALID_REQUEST_BODY","O corpo da requisição está malformado ou contém dados inválidos.",r);}
 @ExceptionHandler(DataIntegrityViolationException.class) ResponseEntity<?> conflict(Exception e,HttpServletRequest r){return body(409,"DATA_CONFLICT","Já existe um registo com os dados fornecidos.",r);}
 @ExceptionHandler(Exception.class) ResponseEntity<?> unexpected(Exception e,HttpServletRequest r){log.error("Unhandled exception for {} {}",r.getMethod(),r.getRequestURI(),e);return body(500,"INTERNAL_ERROR","Ocorreu um erro interno.",r);}
 private ResponseEntity<?> body(int status,String error,String message,HttpServletRequest r){return ResponseEntity.status(status).body(Map.of("timestamp",Instant.now(),"status",status,"error",error,"message",message,"path",r.getRequestURI()));}
}
