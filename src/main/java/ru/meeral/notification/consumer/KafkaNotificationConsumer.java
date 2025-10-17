package ru.meeral.notification.consumer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.meeral.notification.service.EmailService;
import ru.meeral.notification.dto.NotificationDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaNotificationConsumer {
    private final EmailService emailService;

    @KafkaListener(topics = "${spring.kafka.topic.card-expiry-notifications}", groupId = "notification-group")
    public void consumeNotification(NotificationDTO notification) {
        log.info("Получено уведомление: {}", notification);

        try {
            emailService.sendEmail(notification.getEmail(), "Напоминание", notification.getMessage());
            log.info("Email успешно отправлен на {}", notification.getEmail());
        } catch (Exception e) {
            log.error("Ошибка отправки email на {}: {}", notification.getEmail(), e.getMessage(), e);
        }
    }

}
