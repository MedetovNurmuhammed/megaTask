package task.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import task.entities.Task;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.admin}")
    private String adminEmail;

    @Value("${app.email.enabled}")
    private boolean emailEnabled;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendTaskCreatedNotification(Task task) {
        if (!emailEnabled) {
            log.info("Email отправка отключена в настройках");
            return;
        }

        log.info("Отправка email уведомления о создании задачи: {}", task.getTitle());

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(adminEmail);
            message.setSubject("Новая задача создана: " + task.getTitle());
            message.setText(String.format(
                    "Создана новая задача:\n\n" +
                            "ID: %d\n" +
                            "Название: %s\n" +
                            "Описание: %s\n" +
                            "Дата создания: %s",
                    task.getId(),
                    task.getTitle(),
                    task.getDescription(),
                    task.getCreatedAt()
            ));

            mailSender.send(message);
            log.info("Email уведомление отправлено на: {}", adminEmail);
        } catch (Exception e) {
            log.error("Ошибка отправки email уведомления: {}", e.getMessage());
        }
    }
}