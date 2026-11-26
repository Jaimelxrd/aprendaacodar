package mz.co.aprendaacodar.auth.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "app.mail", name = "enabled", havingValue = "true")
public class SmtpEmailService implements EmailService {
    private final JavaMailSender mailSender;
    private final String from;

    public SmtpEmailService(JavaMailSender mailSender, @Value("${app.mail.from:}") String from) {
        if (from.isBlank()) throw new IllegalStateException("MAIL_FROM deve estar definido quando MAIL_ENABLED=true.");
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void sendPasswordReset(String recipient, String firstName, String resetUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(recipient);
        message.setSubject("Recuperação da palavra-passe — Aprenda a Codar");
        message.setText("Olá, " + firstName + ".\n\n" +
                "Recebemos um pedido para redefinir a palavra-passe da sua conta. " +
                "Use o link abaixo nos próximos 30 minutos:\n\n" + resetUrl +
                "\n\nSe não fez este pedido, ignore esta mensagem.");
        mailSender.send(message);
    }
}
