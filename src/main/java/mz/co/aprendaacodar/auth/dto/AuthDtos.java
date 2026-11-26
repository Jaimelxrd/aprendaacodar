package mz.co.aprendaacodar.auth.dto;
import jakarta.validation.constraints.*;import java.util.UUID;import mz.co.aprendaacodar.common.enums.Enums.*;
public final class AuthDtos {
 private AuthDtos(){}
 public record RegisterRequest(@NotBlank @Size(max=80) String firstName,@NotBlank @Size(max=80) String lastName,@NotBlank @Email String email,@Size(max=30) String phoneNumber,@NotBlank @Size(min=10,max=72) String password,@NotBlank String confirmPassword){}
 public record LoginRequest(@NotBlank @Email String email,@NotBlank String password){}
 public record UserResponse(UUID id,String firstName,String lastName,String email,String phoneNumber,String profilePhotoUrl,UserRole role,UserStatus status){}
 public record AuthResponse(String accessToken,String tokenType,long expiresIn,UserResponse user){}
 public record UpdateProfileRequest(@NotBlank @Size(max=80) String firstName,@NotBlank @Size(max=80) String lastName,@Size(max=30) String phoneNumber){}
 public record ForgotPasswordRequest(@NotBlank @Email String email){}
 public record ResetPasswordRequest(@NotBlank String token,@NotBlank @Size(min=10,max=72) String password,@NotBlank String confirmPassword){}
 public record MessageResponse(String message){}
}
