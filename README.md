# java-explore-with-me
[Сcылка на PR](https://github.com/fidgo/java-explore-with-me/pull/1).

## Оглавление:
- 1.Задача
- 2.Сервисы
- 2.1.docker-compose.yaml
- 3.Основной сервис
- 3.1.ERM основного сервиса
- 3.2.Уровни доступа
- 3.3.Требования к уровням доступа
- 3.3.1.Public API
- 3.3.2.Private API
- 3.3.3.Admin API
- 3.4.Роль сущностей
- 3.4.1.User
- 3.4.2.Event
- 3.4.3.Request
- 3.4.4.Compilation
- 3.4.5.Category
- 3.5.StatClient
- 4.Сервис статистики
- 4.1.ERM сервиса статистики
- 4.2.Структура проекта
- 5.Обработка исключений
- 6.Feature
- 7.Общий план разработки
- 8.Приложение. Основной сервис. Эндпоинты
- 9.Приложение. Сервис статистики. Эндпоинты
- 10.Приложение. Основной сервис. schema.sql
- 11.Приложение. Сервис статистики. schema.sql

## 1. Задача
Афиша, где можно предложить какое-либо событие от выставки до похода в кино и набрать компанию для участия в нём.
<img src=".\assets\images\task.png" alt="Вид" width="800" height="400"/>

## 2. Сервисы

<img src=".\assets\images\architect.jpg" alt="Архитектура" width="800" height="300"/>

Описание:
* `ewm-service` —  **основной сервис**, связан с модулем `ewm-main-service`;
* `StatClient` — клиент внутри основного сервиса для связи с сервисом статистики;
* `ewm-stats` — **сервис статистики**, связан с модулем `ewm-stats-service`;
* `ewm-service-db` - сервис бд для основного сервиса, зависит от основного сервиса;
* `ewm-stats-db` - сервис бд для статистики, зависит от сервиса статистики.

### 2.1. docker-compose.yaml

<details>
<summary>Развернуть код</summary>

```yaml
version: '3.1'
services:
  stats-server:
    build: ./ewm-stats-service
    container_name: ewm-stats
    ports:
      - "9090:9090"
    expose:
      - 9090
    depends_on:
      - stats-db
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://ewm_stats_db:5433/ewm-stats
      - POSTGRES_USER=root
      - POSTGRES_PASSWORD=root
    command: -p 9090

  stats-db:
    image: postgres:14-alpine
    container_name: ewm_stats_db
    ports:
      - 5433:5433
    expose:
      - 5433
    volumes:
      - vol-stats-db:/var/lib/postgresql/data/
    environment:
      - POSTGRES_DB=ewm-stats
      - POSTGRES_USER=root
      - POSTGRES_PASSWORD=root
    command: -p 5433

  ewm-service:
    build: ./ewm-main-service
    container_name: ewm-service
    ports:
      - "8080:8080"
    expose:
      - 8080
    depends_on:
      - ewm-db
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://ewm_service_db:5434/main
      - POSTGRES_USER=root
      - POSTGRES_PASSWORD=root
      - STATSERV_URL=http://ewm-stats:9090
    command:
      - p 8080

  ewm-db:
    image: postgres:14-alpine
    container_name: ewm_service_db
    ports:
      - 5434:5434
    expose:
      - 5434
    volumes:
      - vol-ewm-db:/var/lib/postgresql/data/
    environment:
      - POSTGRES_DB=main
      - POSTGRES_USER=root
      - POSTGRES_PASSWORD=root
    command:
      -p 5434

volumes:
  vol-ewm-db:
  vol-stats-db:
```

</details>

## 3. Основной сервис

### 3.1. ERM основного сервиса

**ERM**(англ. _Entity–Relationship model_) — модель данных, позволяющая описывать концептуальные схемы предметной области для
проектирования баз данных, в которой устанавливаются ключевые сущности и их связи друг с другом.

<img src=".\assets\images\erm-service.png" alt="ERMServ" width="800" height="600"/>

Сущности:
* `users` характеризует пользователей.
* `compilations` характеризует подборки событий.
* `categories` характеризует категории.
* `events` характеризует события.
* `compilation_event` характеризует связь подборки событий с событиями.
* `requests` характеризует заявки на участия в событиях.

### 3.2. Уровни доступа
API основного сервиса можно разделить на три части. Первая — публичная(**Public**), доступна без регистрации любому
пользователю сети. Вторая — закрытая(**Private**), доступна только авторизованным пользователям.
Третья(**Admin**) — административная, для администраторов сервиса. К каждой из частей свои требования.

Предполагается, что сервисы `ExploreWithMe` уже находятся за системой аутентификации и авторизации.
Поэтому любой запрос к закрытой или административной части является валидным с точки зрения аутентификации
и авторизации.

### 3.3. Требования к уровням доступа
### 3.3.1. Public API
Публичный API должен предоставлять возможности поиска и фильтрации событий.
1. Сортировка списка событий должна быть организована либо по количеству просмотров, которое должно запрашиваться в
   сервисе статистики, либо по датам событий.
2. При просмотре списка событий возвращается только краткая информация о мероприятиях.
3. Просмотр подробной информации о конкретном событии нужно настроить отдельно (через отдельный эндпоинт).
4. Каждое событие должно относиться к какой-то из закреплённых в приложении категорий.
5. Должна быть настроена возможность получения всех имеющихся категорий и подборок событий (такие подборки будут
   составлять администраторы ресурса).
6. Каждый публичный запрос для получения списка событий или полной информации о мероприятии должен фиксироваться
   сервисом статистики.

### 3.3.2. Private API
Закрытая часть API призвана реализовать возможности зарегистрированных пользователей продукта.
1. Авторизованные пользователи должны иметь возможность добавлять в приложение новые мероприятия, редактировать их и
   просматривать после добавления.
2. Должна быть настроена подача заявок на участие в интересующих мероприятиях.
3. Создатель мероприятия должен иметь возможность подтверждать заявки, которые отправили другие пользователи сервиса.
4. При любых действиях внутри Private API в эндопинты передается userId — id зарегистрированного пользователя.

### 3.3.3. Admin API
Административная часть API должна предоставлять возможности настройки и поддержки работы сервиса.
1. Нужно настроить добавление, изменение и удаление категорий для событий.
2. Должна появиться возможность добавлять, удалять и закреплять на главной странице подборки мероприятий.
3. Требуется наладить модерацию событий, размещённых пользователями, — публикация или отклонение.
4. Также должно быть настроено управление пользователями — добавление, просмотр и удаление.

### 3.4 Роль сущностей
### 3.4.1. User
Сущность **User** (пользователь) может быть создана, изменена, удалена
и получена на уровне **Admin**.
Для создание и изменения этой сущности необходимо уникальное **name** (имя) и уникальное **email** (почта)
Учавствует в сущностях **Event**(событие), **Request** (заявка на участие в событии),
в userId на уровне Private Api. 

### 3.4.2. Event
Пользователи могут создавать события.

**Event** содержит в себе **User**, который является создателем события.
У события имеются разные **state**

``` java
public enum StateEvent {
    PENDING, PUBLISHED, CANCELED
}
```

Сущность **Event** (событие) может быть создана, измененно, отменено и получено на уровне **Private**. 

При создании события **creatorId** берется из **userId** зарегистрированного пользователя, задается
**state** ```StateEvent.PENDING``` (в ожидании модерации). Модерация может быть проведена
только на уровне **Admin**.

Изменить **Event** на уровне **Private** можно только, если **state** будет ```StateEvent.PENDING```
или ```StateEvent.CANCELED``` (событие отклонена). В этом случае после изменения
**state** станет ```StateEvent.PENDING```. При этом новый **event_date** (дата и время на которые намечено событие)
не может быть раньше, чем через два часа от текущего момента. 

Отменить событие на уровне **Private** можно только, если **state** будет ```StateEvent.PENDING```.

**Event** на уровне **Admin** может быть получено, отредактированно и 
пре-модерировано(отлконено ли событие или опубликовано).
Событие отлоклонено, если **state** станет ```StateEvent.CANCELED```.
Событие опубликованно, если **state** станет ```StateEvent.PUBLISHED```.

На уровне **Public** видны только опубликованные события, т.е. события  **Event** с ```StateEvent.PUBLISHED```,
причем при получении таким образом событий, информация об этом записывается на сервис статистики. Отсюда можно получать
информацию о количестве просмотров событий.

У **Event** поле **participant_limit** отвечает за ограничение по количеству людей, которые
могут посетить событие. Если **participant_limit** равно **0**, то посетить событие может неограниченное
количество участников. 

У **Event** поле **request_moderation** отвечает за необходимость модерации заявок. Если **true**,
то любая заявка на участие **Request** в конкретном событии **Event** должна быть одобрена на уровне **Private** 
с тем userId, который соотвествует создателю конкретного события **Event**. Иначе, любая заявка будет 
автоматически принята(если заявка принята, то пользователю разрешается посетить событие). При условии,
если имеется количество мест в **participant_limit** или количество мест неограниченно.

### 3.4.3. Request
Чтобы попасть на события, нужно подать заявку и дождаться когда заявку потвердят.
Пользователи создают заявки на участи в конкретном событии.

**Request** содержит в себе указание на событие **Event**, на которое подается заявка.
**Request** содержит в себе **User**, который подал заявку на участие в событии **Event**.

У **Request** (заявка на участия) имеет **status**.

``` java
public enum StateRequest {
    PENDING, CANCELED, REJECTED, CONFIRMED
}
```
По отношению к событию **Event** пользователи на уровне **Private** делится на одного создателя этого
события и всех остальных(не создателей).

Не создатель события на уровне **Private** с userId можно подать заявку на участи в событии(создать **Request**),
получение информации о заявках пользователя userId, отмена своего запроса на участии в событии.

На уровне **Private** с userId подать заявку на участии в событии  может только не создатель 
этого события, нельзя подать заявку на событие которое не опубликовано(```StateEvent.PUBLISHED```) ,
если достигнуть лимит на участие в событии, то будет ошибка,
если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти 
в состояние подтвержденного (```StateRequest.CONFIRMED```),
если **Request** уже такой есть(с такими же userId и eventId), то нельзя создать такую сущность
повторно. eventId — id события, на который подается запрос.

На уровне **Private** с userId при отмене своей заявки **Request** поле **status** 
будет ```StateRequest.CANCELED```.

Сам создатель события eventId на уровне **Private** с userId может подтвердить или отклонить заявку
на участия другого пользователя.

При отклонении создателем заявки, в этом случае, **Request** поле **status** будет ```StateRequest.REJECTED```.

При принятии создателем заявки на участие:
* при подтверждении заявки на участи, **Request** поле **status** будет ```StateRequest.CONFIRMED```;
* если для события лимит заявок **participant_limit** равен 0 или отключена пре-модерация заявок,
то подтверждение заявок не требуется
* нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие;
* если при подтверждении данной заявки, лимит заявок **participant_limit** для события **Event** исчерпан,
то ВСЕ неподтверждённые заявки(```StateRequest.PENDING```) необходимо отклонить.

### 3.4.4. Compilation
Удобно группировать события **Event** по определенным  характеристикам 
в различные множества(подборки событий), причем с возможностью закреплений на страницы. 
Например, по наибольшему числу просмотренных событий.
Причем каждому событию **Event** может располагаться в нескольких подборок.

**Compilation** (подборка событий) по смыслу должна в себе содержать список событий и название подборки.
У **Compilation** поле **pinned** отвечает за закрепление подборки на главной странице.

Создание подборки, изменение подборки, удаление подборки, добавление в подборку,
удаление из подборки происходит на уровне **Admin**.
Просмотр подборок на уровне **Public**.

### 3.4.5. Category
**Category** (категория) также группирует события **Event** и имеет название категории. 
Каждый **Event** содержит в себе **Category**. Поэтому каждый **Event** может располагаться только
в ОДНОЙ категории. При создании **Event** указывается **Category**.

Создание категории, изменение категории, удаление категории происходит на уровне **Admin**.
Причем при удалении категории нужно проверять, чтобы удаляемая категория 
не содержалась бы ни в одном событии **Event**.
Просмотр категорий на уровне **Public**.

### 3.5. StatClient
HTTP-клиент на основе ```org.springframework.web.reactive.function.client.WebClient```, который обеспечивает
взаимодействие между сервисом статистики и основным сервисом.
HTTP-клиент находится внутри основного сервиса `ewm-service`, который взаимодействует с сервисом статистики.

## 4. Сервис статистики
Сервис статистики призван собирать информацию:
1. О количестве обращений пользователей к спискам событий.
2. О количестве запросов к подробной информации о событии.

Функционал сервиса статистики должен содержать:
* запись информации о том, что был обработан запрос к эндпоинту API;
* предоставление статистики за выбранные даты по выбранному эндпоинту.

Эндпоинты в сервисе ```ewm-service``` для собирания статистики:
* `GET /events`, который отвечает за получение событий с возможностью фильтрации;
* `GET /events/{id}`, который позволяет получить подробную информацию об опубликованном событии по его идентификатору.

### 4.1. ERM сервиса статистики

<img src=".\assets\images\ewm-static.png" alt="Статистика" width="300" height="300"/>

Описание:
* ```app``` — ```ewm-main-service```;
* ```uri``` — путь эндпоинта;
* ```ip``` — адрес внешнего клиента, который отправил запрос на публичные эндпоинты по событиям; 
* ```date_create``` — дата и время отправления запроса на сервер статистики после обращение на публичные эндпоинты
по событиям.

### 4.2. Структура проекта
Расположение пакетов в проекте выбрано по сущностям (package by feature).

```ewm-main-service```
ru.practicum.ewm
* category
* compilation
* error
* event
* http.client
* request
* user
* dto
* util

```ewm-stats-service```
ru.practicum.ewm
* error
* stat
* util

## 5. Обработка исключений

Основные исключения:

* ```NoSuchElemException``` — не существует ожидаемого в существовании объекта;
* ```AlreadyExistException``` — уже существует такой же объект;
* ```StateElemException``` — внутреннее состояние объектов такое, что выполнять функционал дальше
без нарушения цельности данных нельзя; 
* ```IlLegalArgumentException``` — не переданы необходимые аргументы в эндпоинт.

```ApiError```
<details>
<summary>Развернуть код</summary>

```java
public class ApiError {
    private List<String> errors;
    private String message;
    private String reason;
    private HttpStatus status;
    private String timestamp;
}
```
</details>

```ErrorHandler```
<details>
<summary>Развернуть код</summary>

```java
@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler({IlLegalArgumentException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleExceptionReturn400(final RuntimeException e) {
        log.info("400 {} {}", e.getMessage(), e);

        return new ApiError(
                StackTraceToString.exec(e),
                e.getMessage(),
                "For the requested operation the conditions are not met.",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now().format(DateTimeFormat.formatter)
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleExceptionReturn403(final StateElemException e) {
        log.info("403 {} {}", e.getMessage(), e);

        return new ApiError(
                StackTraceToString.exec(e),
                e.getMessage(),
                "For the requested operation the conditions are not met",
                HttpStatus.FORBIDDEN,
                LocalDateTime.now().format(DateTimeFormat.formatter)
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleExceptionReturn404(final NoSuchElemException e) {
        log.info("404 {} {}", e.getMessage(), e);

        return new ApiError(
                StackTraceToString.exec(e),
                e.getMessage(),
                "The required object was not found.",
                HttpStatus.NOT_FOUND,
                LocalDateTime.now().format(DateTimeFormat.formatter)
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleExceptionReturn409(final AlreadyExistException e) {
        log.info("409 {} {}", e.getMessage(), e);

        return new ApiError(
                StackTraceToString.exec(e),
                e.getMessage(),
                "For the requested operation the conditions are not met",
                HttpStatus.CONFLICT,
                LocalDateTime.now().format(DateTimeFormat.formatter)
        );
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleExceptionReturn500(final Throwable e) {
        log.info("500 {} {}", e.getMessage(), e);

        return new ApiError(
                StackTraceToString.exec(e),
                e.getMessage(),
                "Error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now().format(DateTimeFormat.formatter)
        );
    }
}
```

</details>

## 6. Feature

-

## 7. Общий план разработки
- [X] **Проектирование и подготовка к запуску многомодульного проекта.**
- [X] **Добавление необходимых зависимостей в pom файле.**
- [X] **Подключение БД и проектирование ERM для статистики и основного сервиса.**
- [X] **Реализация функционала пользователей.**
- [X] **Реализация функционала категорий.**
- [X] **Реализация функционала событий.**
- [X] **Реализация функционала подборок событий.**
- [X] **Реализация функционала запросов участия в событиях.**
- [X] **Доработка клиента статистики.**
- [X] **Добавление учета статистики.**
- [ ] **Реализация выбранной дополнительной фичи.**

## 8. Приложение. Основной сервис. Эндпоинты

<details>
<summary>Развернуть эндпоинты</summary>

 **Public: События**
 **Публичный API для работы с событиями**
* GET /events Получение событий с возможностью фильтрации
* GET /events/{id} Получение подробной информации об опубликованном событии по его идентификатору

**Public: Подборки событий**
**Публичный API для работы с подборками событий**
* GET /compilations Получение подборок событий
* GET /compilations/{compId} Получение подборки событий по его id

**Public: Категории**
**Публичный API для работы с категориями**
* GET /categories Получение категорий
* GET /categories/{catId} Получение информации о категории по её идентификатору

**Private: События**
**Закрытый API для работы с событиями**
* GET /users/{userId}/events Получение событий, добавленных текущим пользователем
* PATCH /users/{userId}/events Изменение события добавленного текущим пользователем
* POST /users/{userId}/events Добавление нового события
* GET /users/{userId}/events/{eventId} Получение полной информации о событии добавленном текущим пользователем
* PATCH /users/{userId}/events/{eventId} Отмена события добавленного текущим пользователем.
* GET /users/{userId}/events/{eventId}/requests Получение информации о запросах на участие в событии текущего пользователя
* PATCH /users/{userId}/events/{eventId}/requests/{reqId}/confirm Подтверждение чужой заявки на участие в событии текущего пользователя
* PATCH /users/{userId}/events/{eventId}/requests/{reqId}/reject Отклонение чужой заявки на участие в событии текущего пользователя

**Private: Запросы на участие**
**Закрытый API для работы с запросами текущего пользователя на участие в событиях**
* GET /users/{userId}/requests Получение информации о заявках текущего пользователя на участие в чужих событиях
* POST /users/{userId}/requests Добавление запроса от текущего пользователя на участие в событии
* PATCH /users/{userId}/requests/{requestId}/cancel Отмена своего запроса на участие в событии

**Admin: События**
**API для работы с событиями**
* GET /admin/events Поиск событий
* PUT /admin/events/{eventId} Редактирование события
* PATCH /admin/events/{eventId}/publish Публикация события
* PATCH /admin/events/{eventId}/reject Отклонение события

**Admin: Категории**
**API для работы с категориями**
* PATCH /admin/categories Изменение категории
* POST /admin/categories Добавление новой категории
* DELETE /admin/categories/{catId} Удаление категории

**Admin: Пользователи**
**API для работы с пользователями**
* GET /admin/users Получение информации о пользователях
* POST /admin/users Добавление нового пользователя
* DELETE /admin/users/{userId} Удаление пользователя

**Admin: Подборки событий**
**API для работы с подборками событий**
* POST /admin/compilations Добавление новой подборки
* DELETE /admin/compilations/{compId} Удаление подборки
* DELETE /admin/compilations/{compId}/events/{eventId} Удалить событие из подборки
* PATCH /admin/compilations/{compId}/events/{eventId} Добавить событие в подборку
* DELETE /admin/compilations/{compId}/pin Открепить подборку на главной странице
* PATCH /admin/compilations/{compId}/pin Закрепить подборку на главной странице

## 9. Приложение. Сервис статистики. Эндпоинты
**API для работы со статистикой посещений**
* POST /hit Сохранение информации о том, что к эндпоинту был запрос
* GET /stats Получение статистики по посещениям.

</details>

## 10. Приложение. Основной сервис. schema.sql
<details>
<summary>Развернуть код</summary>

```postgresql
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS compilation_event CASCADE;
DROP TABLE IF EXISTS requests CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(512)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,

    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    pinned BOOLEAN                                 NOT NULL,
    title  VARCHAR(120)                           NOT NULL,

    CONSTRAINT pk_compilation PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255)                            NOT NULL,

    CONSTRAINT pk_category PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    category_id        BIGINT,
    creator_id         BIGINT                                  NOT NULL,
    description        VARCHAR(7000),
    annotation         VARCHAR(2000),
    title              VARCHAR(120),
    state              VARCHAR(20),
    event_date         TIMESTAMP WITHOUT TIME ZONE,
    date_create        TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    lat                float4,
    lon                float4,
    participant_limit  INTEGER,
    request_moderation BOOLEAN,
    paid               BOOLEAN,

    CONSTRAINT pk_event PRIMARY KEY (id),
    CONSTRAINT fk_creator FOREIGN KEY (creator_id) REFERENCES users (id),
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE IF NOT EXISTS compilation_event
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id       BIGINT                                  NOT NULL,
    compilation_id BIGINT                                  NOT NULL,

    CONSTRAINT pk_compilation_event PRIMARY KEY (id),
    CONSTRAINT compilation_fk FOREIGN KEY (compilation_id) REFERENCES compilations (id),
    CONSTRAINT compilation_event_fk FOREIGN KEY (event_id) REFERENCES events (id)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id     BIGINT                                  NOT NULL,
    requester_id BIGINT                                  NOT NULL,
    status       VARCHAR(20)                            NOT NULL,
    date_create  TIMESTAMP WITHOUT TIME ZONE             NOT NULL,

    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT requester_fk FOREIGN KEY (requester_id) REFERENCES users (id),
    CONSTRAINT event_fk FOREIGN KEY (event_id) REFERENCES events (id)
);
```
</details>

## 11. Приложение. Сервис статистики. schema.sql

<details>
<summary>Развернуть код</summary>

```postgresql
DROP TABLE IF EXISTS statistics CASCADE;

create sequence statistics_id_seq;

CREATE TABLE IF NOT EXISTS statistics
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,

    app         VARCHAR(256)                            NOT NULL,
    uri         VARCHAR(3000)                           NOT NULL,
    ip          VARCHAR(256)                            NOT NULL,
    date_create TIMESTAMP WITHOUT TIME ZONE             NOT NULL
);
```
</details>