package mz.co.aprendaacodar.user.dto;

import jakarta.validation.constraints.Size;
import java.util.UUID;

public final class TeacherProfileDtos {
    private TeacherProfileDtos() {}
    public record UpdateRequest(@Size(max = 500) String profilePhotoKey,
                                @Size(max = 30) String phoneNumber,
                                @Size(max = 10000) String biography,
                                @Size(max = 255) String specialization,
                                @Size(max = 255) String professionalTitle) {}
    public record Response(UUID id, String firstName, String lastName, String email, String phoneNumber,
                           String profilePhotoUrl, String biography, String specialization, String professionalTitle) {}
}
