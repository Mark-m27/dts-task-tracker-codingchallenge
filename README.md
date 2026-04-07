# dts-task-tracker-codingchallenge

Simple caseworker task tracker built for the DTS developer technical test. The application allows users to create, view, update and delete tasks, along with tracking their status and due dates.

It is implemented as a single Spring Boot application using Java 17, with a lightweight Thymeleaf frontend, REST API, and H2 database for persistence.

For full transparency, I used Codex by OpenAI to assist with some of the initial structure and syntax. All code has been reviewed and adjusted to ensure it behaves correctly and meets the requirements. Happy to go through and answer any questions on any aspects. 

I also built a very similar application to this 5 yearss ago for University, which was a contacts service but same basic requirements from the API, this is where I intially learned Spring Boot and the reason I also used it within this application.

## Requirements

- Java **17**
- Maven **3.9+**

## Run

```bash
mvn spring-boot:run
```

- **Thymeleaf UI:** [http://localhost:8080/tasks](http://localhost:8080/tasks)
- **REST API base:** `http://localhost:8080/api/tasks`
- **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **H2 console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL matches `spring.datasource.url` in `application.properties`)

## Data storage

The app uses a **file-based H2** database under `./data/` (created when the app starts). That directory is gitignored.

## Tech Stack

- Java 17
- Spring Boot
- Spring Web + MVC
- Spring Data JPA
- Thymeleaf
- H2 Database (file-based)
- Maven
- springdoc OpenAPI (Swagger)
- JUnit / MockMvc

## Features

### Backend API
- Create a task
- Retrieve all tasks
- Retrieve a task by ID
- Update task status
- Delete a task

### Frontend
- Create tasks
- View task list
- Edit tasks
- Update task status
- Delete tasks
- Search Tasks by ID Title Description

## Tests

```bash
mvn verify
```

Runs unit tests (`TaskServiceTest`) and API slice tests (`TaskRestControllerTest`).
