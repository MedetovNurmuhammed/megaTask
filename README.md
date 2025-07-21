Task Manager REST API
Полнофункциональный REST API для управления задачами.
 REST API для управления задачами - Полный CRUD функционал
 H2 Database - In-memory база данных настроена
 Логирование запросов и ответов - Реализовано через custom interceptor

 HTTP запрос на внешний API - Сервис для запроса к https://api.restful-api.dev/objects
 Unit-тесты - Полное покрытие CRUD методов TaskService
 Email уведомления - Отправка Gmail при создании задач
 Basic Authentication - Защита всех API endpoints
 Кэширование - Simple Cache для оптимизации производительности
 Dockerfile - Готовая контейнеризация приложения

🛠 Технологический стек
Backend:        Java 17 + Spring Boot 3.2.0
Database:       H2 Database (in-memory)
Security:       Spring Security (Basic Auth)
Caching:        Spring Cache (Simple Cache)  
Email:          Spring Mail (Gmail SMTP)
Testing:        JUnit 5 + Mockito
Build:          Maven 3.6+
Containerization: Docker + Docker Compose
