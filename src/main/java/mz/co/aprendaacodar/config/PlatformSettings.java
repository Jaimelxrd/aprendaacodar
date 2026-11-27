package mz.co.aprendaacodar.config;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import mz.co.aprendaacodar.user.entity.User;
import java.time.Instant;

import java.util.UUID;

@Entity
@Table(name = "platform_settings")
public class PlatformSettings {
    @Id
    private UUID id;
    @Column(nullable = false, updatable = false) private Instant createdAt;
    @Column(nullable = false) private Instant updatedAt;
    @Column(length = 50) private String mpesaNumber;
    @Column(length = 50) private String emolaNumber;
    @Column(length = 180) private String accountHolderName;
    @Column(columnDefinition = "TEXT") private String paymentInstructions;
    @Column(columnDefinition = "TEXT") private String institutionalInformation;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    private User updatedBy;

    protected PlatformSettings() {}

    public PlatformSettings(UUID id) { this.id = id; this.createdAt = Instant.now(); this.updatedAt = this.createdAt; }
    public void update(String mpesa, String emola, String holder, String instructions, String information, User actor) {
        this.mpesaNumber = mpesa;
        this.emolaNumber = emola;
        this.accountHolderName = holder;
        this.paymentInstructions = instructions;
        this.institutionalInformation = information;
        this.updatedBy = actor;
        this.updatedAt = Instant.now();
    }
    public UUID getId() { return id; }
    public String getMpesaNumber() { return mpesaNumber; }
    public String getEmolaNumber() { return emolaNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public String getPaymentInstructions() { return paymentInstructions; }
    public String getInstitutionalInformation() { return institutionalInformation; }
}
