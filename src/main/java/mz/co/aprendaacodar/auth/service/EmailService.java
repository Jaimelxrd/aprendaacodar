package mz.co.aprendaacodar.auth.service;

public interface EmailService {
    void sendPasswordReset(String recipient, String firstName, String resetUrl);
}
