package mz.co.aprendaacodar.user.service;

import mz.co.aprendaacodar.common.exception.*;
import mz.co.aprendaacodar.storage.StorageService;
import mz.co.aprendaacodar.user.dto.TeacherProfileDtos.*;
import mz.co.aprendaacodar.user.entity.TeacherProfile;
import mz.co.aprendaacodar.user.entity.User;
import mz.co.aprendaacodar.user.repository.TeacherProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class TeacherProfileService {
    private final TeacherProfileRepository profiles;
    private final StorageService storage;
    public TeacherProfileService(TeacherProfileRepository profiles, StorageService storage) { this.profiles = profiles; this.storage = storage; }
    @Transactional(readOnly = true)
    public Response get(UUID id) { return map(profile(id)); }
    @Transactional
    public Response update(UUID id, UpdateRequest request) {
        TeacherProfile profile = profile(id);
        User user = profile.getUser();
        if (request.profilePhotoKey() != null && !request.profilePhotoKey().startsWith("profiles/" + id + "/"))
            throw new BusinessException("A fotografia deve pertencer ao próprio professor.");
        if (request.profilePhotoKey() != null) user.setProfilePhotoKey(request.profilePhotoKey());
        user.updateProfile(user.getFirstName(), user.getLastName(), request.phoneNumber());
        profile.update(request.biography(), request.specialization(), request.professionalTitle());
        return map(profile);
    }
    private TeacherProfile profile(UUID id) { return profiles.findByUserId(id).orElseThrow(() -> new ResourceNotFoundException("Perfil de professor não encontrado.")); }
    private Response map(TeacherProfile p) {
        User u = p.getUser();
        return new Response(u.getId(), u.getFirstName(), u.getLastName(), u.getEmail(), u.getPhoneNumber(),
                u.getProfilePhotoKey() == null ? null : storage.url(u.getProfilePhotoKey(), true),
                p.getBiography(), p.getSpecialization(), p.getProfessionalTitle());
    }
}
