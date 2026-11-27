package mz.co.aprendaacodar.config;

import mz.co.aprendaacodar.common.exception.ResourceNotFoundException;
import mz.co.aprendaacodar.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class PlatformSettingsService {
    private static final UUID SETTINGS_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final PlatformSettingsRepository settings;
    private final UserRepository users;

    public PlatformSettingsService(PlatformSettingsRepository settings, UserRepository users) {
        this.settings = settings;
        this.users = users;
    }

    public record SettingsResponse(String mpesaNumber, String emolaNumber, String accountHolderName,
                                   String paymentInstructions, String institutionalInformation) {}
    public record UpdateRequest(String mpesaNumber, String emolaNumber, String accountHolderName,
                                String paymentInstructions, String institutionalInformation) {}

    @Transactional(readOnly = true)
    public SettingsResponse get() { return map(entity()); }

    @Transactional
    public SettingsResponse update(UUID adminId, UpdateRequest request) {
        PlatformSettings entity = entity();
        entity.update(request.mpesaNumber(), request.emolaNumber(), request.accountHolderName(),
                request.paymentInstructions(), request.institutionalInformation(),
                users.findById(adminId).orElseThrow(() -> new ResourceNotFoundException("Administrador não encontrado.")));
        return map(entity);
    }

    private PlatformSettings entity() {
        return settings.findById(SETTINGS_ID).orElseThrow(() -> new ResourceNotFoundException("Configurações não encontradas."));
    }
    private SettingsResponse map(PlatformSettings s) {
        return new SettingsResponse(s.getMpesaNumber(), s.getEmolaNumber(), s.getAccountHolderName(),
                s.getPaymentInstructions(), s.getInstitutionalInformation());
    }
}
