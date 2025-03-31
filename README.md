# Notification Service

## Описание
`notification-service` – это микросервис, отвечающий за отправку email-уведомлений клиентам о скором истечении срока действия их банковских карт.

## Стек технологий
- Java 17
- Spring Boot
- Spring Kafka
- Spring Mail
- Apache Kafka
- Docker

## Конфигурация

Сервис использует переменные окружения для настройки:

```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS}
    consumer:
      group-id: notification-group
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"
    topic:
      card-expiry-notifications: card-expiry-topic
  mail:
    host: smtp.example.com
    port: 587
    username: ${EMAIL_ADDRESS}
    password: ${EMAIL_PASSWORD}
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true
```

### Переменные окружения

| Переменная               | Описание                           |
|---------------------------|------------------------------------|
| `KAFKA_BOOTSTRAP_SERVERS` | Адрес Kafka-брокера               |
| `EMAIL_ADDRESS`           | Email для отправки уведомлений    |
| `EMAIL_PASSWORD`          | Пароль от email                   |

## Запуск

### Через Docker

```sh
docker-compose up -d
```

### Локально

```sh
mvn clean package
java -jar target/notification-service.jar
```

## Kafka Consumer

Сервис подписан на топик `card-expiry-topic` и обрабатывает уведомления:

```java
@KafkaListener(topics = "${spring.kafka.topic.card-expiry-notifications}", groupId = "${spring.kafka.consumer.group-id}")
public void consumeNotification(ConsumerRecord<String, NotificationDTO> record, Acknowledgment ack) {
    try {
        emailService.sendEmail(record.value().getEmail(), "Напоминание", record.value().getMessage());
        ack.acknowledge();
    } catch (Exception e) {
        log.error("Ошибка отправки email: {}", e.getMessage(), e);
    }
}
```
