# Task Manager API

Простое RESTful API для управления задачами с аутентификацией через JWT. Реализовано на **Java**, **Spring Boot**.

---

## Возможности

- Регистрация и логин пользователей
- Создание, получение, обновление и удаление задач
- Фильтрация задач по статусу
- Безопасная аутентификация с JWT
- DTO для возврата данных

---

## Безопасность

JWT-токен создается после логина и передается клиентом в заголовке Authorization.
Spring Security проверяет подлинность токена и связывает пользователя с задачами.

---

## Используемые технологии

| Технология | Назначение |
|------------|------------|
| Java 17+ | Основной язык разработки |
| Spring Boot | Фреймворк для создания приложений |
| Spring Web | REST API |
| Spring Data JPA | Работа с базой данных |
| Spring Security | Безопасность и аутентификация |
| JWT | Авторизация через токены |
| Lombok | Уменьшение шаблонного кода |
| H2/PostgreSQL/MySQL | База данных |
| Hibernate | ORM для JPA |

---

## Запуск проекта

Перед запуском создайте или укажите свою бд в application.properties

Пример `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/task_db
spring.datasource.username=postgres
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```
### Запуск 
./mvnw spring-boot:run Либо через IDE — запусти TaskManagerApplication.java.

---

## Работа с токенами (Postman)

### Регистрация

POST /api/auth/register

{
"username": "user1",
"password": "123456"
}

### Логин

POST /api/auth/login

{
"username": "user1",
"password": "123456"
}

Ответ:
{
"token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

### Доступ к задачам

Добавь заголовок: Authorization: Bearer <твой_токен>

#### Примеры:

GET /api/tasks — получить список задач

GET /api/tasks?status=IN_PROGRESS — по статусу

POST /api/tasks — создать

PUT /api/tasks/{id} — обновить

DELETE /api/tasks/{id} — удалить


