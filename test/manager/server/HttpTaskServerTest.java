package manager.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exception.NotFoundException;
import manager.history.InMemoryHistoryManager;
import manager.task.InMemoryTaskManager;
import manager.task.TaskManager;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Сервер")
class HttpTaskServerTest {

    TaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager());
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubTasks();
        manager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    @DisplayName("Должен создать задачу, когда приходит запрос POST/tasks и у задачи нет id")
    public void shouldCreateTask_POSTTasksRequestArrivesAndTaskDoesNotHaveId() throws IOException, InterruptedException {
        Task taskExpected = new Task("test name", "test description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60));
        String taskJson = gson.toJson(taskExpected);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasksList();
        Task taskActual = tasksFromManager.getFirst();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");

        assertNotEquals(taskExpected.getId(), taskActual.getId(), "id не сгенерировано");
        assertEquals(taskExpected.getName(), taskActual.getName(), "не совпадает name");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "не совпадают description");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "не совпадают status");

        assertEquals(taskExpected.getStartDateTime(), taskActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(taskExpected.getDuration(), taskActual.getDuration(), "не совпадает duration");
        assertEquals(taskExpected.getEndDateTime(), taskActual.getEndDateTime(), "не совпадает endTime");
    }

    @Test
    @DisplayName("Должен возвращать код ошибки, когда приходит запрос POST/tasks и задачи пересекаются по времени")
    public void shouldReturnErrorCode_POSTTasksRequestArrivesAndTasksIntersectByTime() throws IOException, InterruptedException {
        Task task1 = manager.createTask(new Task("test name", "test description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 20), Duration.ofMinutes(60)));

        Task task2 = new Task("test name", "test description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60));
        String taskJson = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }


    @Test
    @DisplayName("Должен обновить задачу, когда приходит запрос POST/tasks и у задачи есть id")
    public void shouldUpdateTask_POSTTasksRequestArrivesAndTaskHaveId() throws IOException, InterruptedException {

        Task taskForChange = manager.createTask(new Task("test name", "test description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));

        Task taskExpected = new Task(1, "test name NEW", Status.DONE, "test description NEW",
                LocalDateTime.of(2024, 7, 8, 15, 13), Duration.ofMinutes(50));
        String taskExpectedJson = gson.toJson(taskExpected);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest requestUpdate = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskExpectedJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> responseUpdate = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseUpdate.statusCode());

        List<Task> tasksFromManager = manager.getTasksList();
        Task taskActual = tasksFromManager.getFirst();


        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");

        assertEquals(taskExpected.getId(), taskActual.getId(), "id не совпадают");
        assertEquals(taskExpected.getName(), taskActual.getName(), "не совпадает name");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "не совпадают description");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "не совпадают status");

        assertEquals(taskExpected.getStartDateTime(), taskActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(taskExpected.getDuration(), taskActual.getDuration(), "не совпадает duration");
        assertEquals(taskExpected.getEndDateTime(), taskActual.getEndDateTime(), "не совпадает endTime");
    }


    @Test
    @DisplayName("Должен вернуть список задач, когда приходит запрос GET/tasks")
    public void shouldGetTasksList_GETTasksRequestArrives() throws IOException, InterruptedException {

        Task taskExpected1 = manager.createTask(new Task("test name1", "test description1", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 15, 17), Duration.ofMinutes(30)));
        Task taskExpected2 = manager.createTask(new Task("test name2", "test description2", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());


        List<Task> tasksFromManager = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        Task taskActual1 = tasksFromManager.getFirst();
        Task taskActual2 = tasksFromManager.getLast();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");

        assertEquals(taskExpected1.getId(), taskActual1.getId(), "id не совпадает");
        assertEquals(taskExpected1.getName(), taskActual1.getName(), "не совпадает name");
        assertEquals(taskExpected1.getDescription(), taskActual1.getDescription(), "не совпадают description");
        assertEquals(taskExpected1.getStatus(), taskActual1.getStatus(), "не совпадают status");
        assertEquals(taskExpected1.getStartDateTime(), taskActual1.getStartDateTime(), "не совпадает startTime");
        assertEquals(taskExpected1.getDuration(), taskActual1.getDuration(), "не совпадает duration");
        assertEquals(taskExpected1.getEndDateTime(), taskActual1.getEndDateTime(), "не совпадает endTime");

        assertEquals(taskExpected2.getId(), taskActual2.getId(), "id не совпадает");
        assertEquals(taskExpected2.getName(), taskActual2.getName(), "не совпадает name");
        assertEquals(taskExpected2.getDescription(), taskActual2.getDescription(), "не совпадают description");
        assertEquals(taskExpected2.getStatus(), taskActual2.getStatus(), "не совпадают status");
        assertEquals(taskExpected2.getStartDateTime(), taskActual2.getStartDateTime(), "не совпадает startTime");
        assertEquals(taskExpected2.getDuration(), taskActual2.getDuration(), "не совпадает duration");
        assertEquals(taskExpected2.getEndDateTime(), taskActual2.getEndDateTime(), "не совпадает endTime");

    }


    @Test
    @DisplayName("Должен вернуть задачу, когда приходит запрос GET/tasks/{id}")
    public void shouldGetTaskById_GETTasksIdRequestArrives() throws IOException, InterruptedException {

        Task taskExpected = manager.createTask(new Task("test name1", "test description1", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 15, 17), Duration.ofMinutes(30)));
        int taskExpectedId = taskExpected.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + taskExpectedId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String responseBody = response.body();
        Task taskActual = gson.fromJson(responseBody, Task.class);

        assertEquals(taskExpected.getId(), taskActual.getId(), "id не совпадает");
        assertEquals(taskExpected.getName(), taskActual.getName(), "не совпадает name");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "не совпадают description");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "не совпадают status");
        assertEquals(taskExpected.getStartDateTime(), taskActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(taskExpected.getDuration(), taskActual.getDuration(), "не совпадает duration");
        assertEquals(taskExpected.getEndDateTime(), taskActual.getEndDateTime(), "не совпадает endTime");

    }

    @Test
    @DisplayName("Должен вернуть код ошибки, когда приходит запрос GET/tasks/{id} и задача не найдена")
    public void shouldReturnErrorCode_GETTasksIdRequestArrivesAndTaskNotFound() throws IOException, InterruptedException {

        Task task = manager.createTask(new Task("test name1", "test description1", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 15, 17), Duration.ofMinutes(30)));
        int taskId = task.getId();
        manager.deleteTaskById(taskId);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }


    @Test
    @DisplayName("Должен удалить задачу, когда приходит запрос DELETE/task/{id}")
    public void shouldDeleteTask_DELETETasksIdRequestArrives() throws IOException, InterruptedException {

        Task taskExpected = manager.createTask(new Task("test name1", "test description1", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 15, 17), Duration.ofMinutes(30)));
        int taskExpectedId = taskExpected.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + taskExpectedId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());

        List<Task> tasksFromManager = manager.getTasksList();
        assertEquals(0, tasksFromManager.size(), "Задача не удалилась, список не пуст");
        assertThrows(NotFoundException.class, () -> manager.getTaskById(1),
                "задача не удалилась, исключение не выброшено");
    }

    @Test
    @DisplayName("Должен вернуть код ошибки, когда приходит запрос DELETE/tasks/{id} и задача не найдена")
    public void shouldReturnErrorCode_DELETETasksIdRequestArrivesAndTaskNotFound() throws IOException, InterruptedException {

        Task task = manager.createTask(new Task("test name1", "test description1", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 15, 17), Duration.ofMinutes(30)));
        int taskId = task.getId();
        manager.deleteTaskById(taskId);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    @DisplayName("Должен создать подзадачу, когда приходит запрос POST/subtasks и у подзадачи нет id")
    public void shouldCreateSubTask_POSTSubTasksRequestArrivesAndSubTaskDoesNotHaveId() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("test name", "test description"));
        SubTask subtaskExpected = new SubTask("test name", Status.IN_PROGRESS, "test description",
                epic.getId(), LocalDateTime.of(2024, 7, 19, 14, 17), Duration.ofMinutes(60));
        String subtaskJson = gson.toJson(subtaskExpected);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<SubTask> subtasksFromManager = manager.getSubTasksList();
        SubTask subtaskActual = subtasksFromManager.getFirst();


        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");

        assertNotEquals(subtaskExpected.getId(), subtaskActual.getId(), "id не сгенерировано");
        assertEquals(subtaskExpected.getName(), subtaskActual.getName(), "не совпадает name");
        assertEquals(subtaskExpected.getDescription(), subtaskActual.getDescription(), "не совпадают description");
        assertEquals(subtaskExpected.getStatus(), subtaskActual.getStatus(), "не совпадают status");

        assertEquals(subtaskExpected.getStartDateTime(), subtaskActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(subtaskExpected.getDuration(), subtaskActual.getDuration(), "не совпадает duration");
        assertEquals(subtaskExpected.getEndDateTime(), subtaskActual.getEndDateTime(), "не совпадает endTime");
    }

    @Test
    @DisplayName("Должен возвращать код ошибки, когда приходит запрос POST/subtasks и задачи пересекаются по времени")
    public void shouldReturnErrorCode_POSTSubTasksRequestArrivesAndSubTasksIntersectByTime() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("test name", "test description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("test name", Status.IN_PROGRESS, "test description",
                epic.getId(), LocalDateTime.of(2024, 7, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subtask2 = new SubTask("test name", Status.IN_PROGRESS, "test description",
                epic.getId(), LocalDateTime.of(2024, 7, 19, 14, 30), Duration.ofMinutes(60));

        String subtaskJson = gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    @DisplayName("Должен обновить подзадачу, когда приходит запрос POST/subtasks и у подзадачи есть id")
    public void shouldUpdateSubTask_POSTSubTasksRequestArrivesAndSubTaskHaveId() throws IOException, InterruptedException {

        Epic epic = manager.createEpic(new Epic("test name", "test description"));
        SubTask subtaskForChange = manager.createSubTask(new SubTask("test name", Status.IN_PROGRESS,
                "test description", epic.getId(),
                LocalDateTime.of(2024, 7, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask subtaskExpected = new SubTask(2, "test name New", Status.IN_PROGRESS, "test description New",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(30));
        String subtaskExpectedJson = gson.toJson(subtaskExpected);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest requestUpdate = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskExpectedJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> responseUpdate = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseUpdate.statusCode());

        List<SubTask> tasksFromManager = manager.getSubTasksList();
        SubTask subtaskActual = tasksFromManager.getFirst();


        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");

        assertEquals(subtaskExpected.getId(), subtaskActual.getId(), "id не совпадают");
        assertEquals(subtaskExpected.getName(), subtaskActual.getName(), "не совпадает name");
        assertEquals(subtaskExpected.getDescription(), subtaskActual.getDescription(), "не совпадают description");
        assertEquals(subtaskExpected.getStatus(), subtaskActual.getStatus(), "не совпадают status");

        assertEquals(subtaskExpected.getStartDateTime(), subtaskActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(subtaskExpected.getDuration(), subtaskActual.getDuration(), "не совпадает duration");
        assertEquals(subtaskExpected.getEndDateTime(), subtaskActual.getEndDateTime(), "не совпадает endTime");
    }

    @Test
    @DisplayName("Должен вернуть список подзадач, когда приходит запрос GET/subtasks")
    public void shouldGetSubTasksList_GETSubTasksRequestArrives() throws IOException, InterruptedException {

        Epic epic = manager.createEpic(new Epic("test name", "test description"));
        SubTask subtaskExpected1 = manager.createSubTask(new SubTask("test name", Status.IN_PROGRESS,
                "test description", epic.getId(),
                LocalDateTime.of(2024, 7, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subtaskExpected2 = manager.createSubTask(new SubTask("test name", Status.IN_PROGRESS,
                "test description", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());


        List<SubTask> subtasksFromManager = gson.fromJson(response.body(), new TypeToken<List<SubTask>>() {
        }.getType());
        SubTask subtaskActual1 = subtasksFromManager.getFirst();
        SubTask subtaskActual2 = subtasksFromManager.getLast();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(2, subtasksFromManager.size(), "Некорректное количество задач");

        assertEquals(subtaskExpected1.getId(), subtaskActual1.getId(), "id не совпадает");
        assertEquals(subtaskExpected1.getName(), subtaskActual1.getName(), "не совпадает name");
        assertEquals(subtaskExpected1.getDescription(), subtaskActual1.getDescription(), "не совпадают description");
        assertEquals(subtaskExpected1.getStatus(), subtaskActual1.getStatus(), "не совпадают status");
        assertEquals(subtaskExpected1.getStartDateTime(), subtaskActual1.getStartDateTime(), "не совпадает startTime");
        assertEquals(subtaskExpected1.getDuration(), subtaskActual1.getDuration(), "не совпадает duration");
        assertEquals(subtaskExpected1.getEndDateTime(), subtaskActual1.getEndDateTime(), "не совпадает endTime");

        assertEquals(subtaskExpected2.getId(), subtaskActual2.getId(), "id не совпадает");
        assertEquals(subtaskExpected2.getName(), subtaskActual2.getName(), "не совпадает name");
        assertEquals(subtaskExpected2.getDescription(), subtaskActual2.getDescription(), "не совпадают description");
        assertEquals(subtaskExpected2.getStatus(), subtaskActual2.getStatus(), "не совпадают status");
        assertEquals(subtaskExpected2.getStartDateTime(), subtaskActual2.getStartDateTime(), "не совпадает startTime");
        assertEquals(subtaskExpected2.getDuration(), subtaskActual2.getDuration(), "не совпадает duration");
        assertEquals(subtaskExpected2.getEndDateTime(), subtaskActual2.getEndDateTime(), "не совпадает endTime");

    }

    @Test
    @DisplayName("Должен вернуть подзадачу, когда приходит запрос GET/subtasks/{id}")
    public void shouldGetSubTaskById_GETSubTaskIdRequestArrives() throws IOException, InterruptedException {

        Epic epic = manager.createEpic(new Epic("test name", "test description"));
        SubTask subtaskExpected = manager.createSubTask(new SubTask("test name", Status.IN_PROGRESS,
                "test description", epic.getId(),
                LocalDateTime.of(2024, 7, 19, 14, 17), Duration.ofMinutes(60)));
        int subtaskExpectedId = subtaskExpected.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtaskExpectedId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String responseBody = response.body();
        SubTask subtaskActual = gson.fromJson(responseBody, SubTask.class);

        assertEquals(subtaskExpected.getId(), subtaskActual.getId(), "id не совпадает");
        assertEquals(subtaskExpected.getName(), subtaskActual.getName(), "не совпадает name");
        assertEquals(subtaskExpected.getDescription(), subtaskActual.getDescription(), "не совпадают description");
        assertEquals(subtaskExpected.getStatus(), subtaskActual.getStatus(), "не совпадают status");
        assertEquals(subtaskExpected.getStartDateTime(), subtaskActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(subtaskExpected.getDuration(), subtaskActual.getDuration(), "не совпадает duration");
        assertEquals(subtaskExpected.getEndDateTime(), subtaskActual.getEndDateTime(), "не совпадает endTime");

    }


    @Test
    @DisplayName("Должен вернуть код ошибки, когда приходит запрос GET/subtasks/{id} и задача не найдена")
    public void shouldReturnErrorCode_GETSubTasksIdRequestArrivesAndSubTaskNotFound() throws IOException, InterruptedException {

        Epic epic = manager.createEpic(new Epic("test name", "test description"));
        SubTask subtask = manager.createSubTask(new SubTask("test name", Status.IN_PROGRESS,
                "test description", epic.getId(),
                LocalDateTime.of(2024, 7, 19, 14, 17), Duration.ofMinutes(60)));
        int subtaskId = subtask.getId();
        manager.deleteSubTaskById(subtaskId);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtaskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    @DisplayName("Должен удалить подзадачу, когда приходит запрос DELETE/subtasks/{id}")
    public void shouldDeleteSubTask_DELETESubTasksIdRequestArrives() throws IOException, InterruptedException {

        Epic epic = manager.createEpic(new Epic("test name", "test description"));
        SubTask subtaskExpected = manager.createSubTask(new SubTask("test name", Status.IN_PROGRESS,
                "test description", epic.getId(),
                LocalDateTime.of(2024, 7, 19, 14, 17), Duration.ofMinutes(60)));
        int subtaskExpectedId = subtaskExpected.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtaskExpectedId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());

        List<SubTask> subtasksFromManager = manager.getSubTasksList();
        assertEquals(0, subtasksFromManager.size(), "Задача не удалилась, список не пуст");
        assertThrows(NotFoundException.class, () -> manager.getSubTaskById(2),
                "задача не удалилась, исключение не выброшено");
    }

    @Test
    @DisplayName("Должен вернуть код ошибки, когда приходит запрос DELETE/subtasks/{id} и задача не найдена")
    public void shouldReturnErrorCode_DELETESubTasksIdRequestArrivesAndTaskNotFound() throws IOException, InterruptedException {

        Epic epic = manager.createEpic(new Epic("test name", "test description"));
        SubTask subtask = manager.createSubTask(new SubTask("test name", Status.IN_PROGRESS,
                "test description", epic.getId(),
                LocalDateTime.of(2024, 7, 19, 14, 17), Duration.ofMinutes(60)));
        int subtaskId = subtask.getId();
        manager.deleteSubTaskById(subtaskId);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtaskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    @DisplayName("Должен создать эпик, когда приходит запрос POST/epics и у эпика нет id")
    public void shouldCreateEpic_POSTEpicsRequestArrivesAndEpicDoesNotHaveId() throws IOException, InterruptedException {
        Epic epicExpected = new Epic("test name", "test description");

        String epicJson = gson.toJson(epicExpected);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpicList();
        Epic epicActual = epicsFromManager.getFirst();


        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");

        assertNotEquals(epicExpected.getId(), epicActual.getId(), "id не сгенерировано");
        assertEquals(epicExpected.getName(), epicActual.getName(), "не совпадает name");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(), "не совпадают description");
        assertEquals(Status.NEW, epicActual.getStatus(), "не совпадают status");

        assertEquals(epicExpected.getStartDateTime(), epicActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(epicExpected.getDuration(), epicActual.getDuration(), "не совпадает duration");
        assertEquals(epicExpected.getEndDateTime(), epicActual.getEndDateTime(), "не совпадает endTime");
    }


    @Test
    @DisplayName("Должен обновить эпик, когда приходит запрос POST/epics и у эпика есть id")
    public void shouldUpdateEpic_POSTEpicsRequestArrivesAndEpicHaveId() throws IOException, InterruptedException {
        Epic epicForChange = manager.createEpic(new Epic("test name", "test description"));

        Epic epicExpected = new Epic(1, "test name NEW", Status.NEW, "test description NEW",
                null, null);

        String epicJson = gson.toJson(epicExpected);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpicList();
        Epic epicActual = epicsFromManager.getFirst();


        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");

        assertEquals(epicExpected.getId(), epicActual.getId(), "id не совпадает");
        assertEquals(epicExpected.getName(), epicActual.getName(), "не совпадает name");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(), "не совпадают description");
        assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "не совпадают status");

        assertEquals(epicExpected.getStartDateTime(), epicActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(epicExpected.getDuration(), epicActual.getDuration(), "не совпадает duration");
        assertEquals(epicExpected.getEndDateTime(), epicActual.getEndDateTime(), "не совпадает endTime");
    }


    @Test
    @DisplayName("Должен вернуть список эпиков, когда приходит запрос GET/epics")
    public void shouldGetEpicsList_GETEpicsRequestArrives() throws IOException, InterruptedException {

        Epic epicExpected1 = manager.createEpic(new Epic("test name", "test description"));
        Epic epicExpected2 = manager.createEpic(new Epic("test name", "test description"));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());


        List<Epic> epicFromManager = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());
        Epic epicActual1 = epicFromManager.getFirst();
        Epic epicActual2 = epicFromManager.getLast();

        assertNotNull(epicFromManager, "Задачи не возвращаются");
        assertEquals(2, epicFromManager.size(), "Некорректное количество задач");

        assertEquals(epicExpected1.getId(), epicActual1.getId(), "id не совпадает");
        assertEquals(epicExpected1.getName(), epicActual1.getName(), "не совпадает name");
        assertEquals(epicExpected1.getDescription(), epicActual1.getDescription(), "не совпадают description");
        assertEquals(epicExpected1.getStatus(), epicActual1.getStatus(), "не совпадают status");
        assertEquals(epicExpected1.getStartDateTime(), epicActual1.getStartDateTime(), "не совпадает startTime");
        assertEquals(epicExpected1.getDuration(), epicActual1.getDuration(), "не совпадает duration");
        assertEquals(epicExpected1.getEndDateTime(), epicActual1.getEndDateTime(), "не совпадает endTime");

        assertEquals(epicExpected2.getId(), epicActual2.getId(), "id не совпадает");
        assertEquals(epicExpected2.getName(), epicActual2.getName(), "не совпадает name");
        assertEquals(epicExpected2.getDescription(), epicActual2.getDescription(), "не совпадают description");
        assertEquals(epicExpected2.getStatus(), epicActual2.getStatus(), "не совпадают status");
        assertEquals(epicExpected2.getStartDateTime(), epicActual2.getStartDateTime(), "не совпадает startTime");
        assertEquals(epicExpected2.getDuration(), epicActual2.getDuration(), "не совпадает duration");
        assertEquals(epicExpected2.getEndDateTime(), epicActual2.getEndDateTime(), "не совпадает endTime");

    }

    @Test
    @DisplayName("Должен вернуть эпик, когда приходит запрос GET/epics/{id}")
    public void shouldGetEpicById_GETEpicsIdRequestArrives() throws IOException, InterruptedException {

        Epic epicExpected = manager.createEpic(new Epic("test name", "test description"));
        int epicExpectedId = epicExpected.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epicExpectedId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String responseBody = response.body();
        Epic epicActual = gson.fromJson(responseBody, Epic.class);

        assertEquals(epicExpected.getId(), epicActual.getId(), "id не совпадает");
        assertEquals(epicExpected.getName(), epicActual.getName(), "не совпадает name");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(), "не совпадают description");
        assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "не совпадают status");
        assertEquals(epicExpected.getStartDateTime(), epicActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(epicExpected.getDuration(), epicActual.getDuration(), "не совпадает duration");
        assertEquals(epicExpected.getEndDateTime(), epicActual.getEndDateTime(), "не совпадает endTime");

    }


    @Test
    @DisplayName("Должен вернуть код ошибки, когда приходит запрос GET/epics/{id} и задача не найдена")
    public void shouldReturnErrorCode_GETEpicsIdRequestArrivesAndEpicNotFound() throws IOException, InterruptedException {

        Epic epic = manager.createEpic(new Epic("test name", "test description"));

        int epicId = epic.getId();
        manager.deleteEpicById(epicId);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epicId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }


    @Test
    @DisplayName("Должен вернуть список подзадач эпика по его id, когда приходит запрос GET/epics/{id}/subtasks")
    public void shouldGetEpicsSubTasks_GETSubTasksRequestArrives() throws IOException, InterruptedException {

        Epic epic = manager.createEpic(new Epic("test name", "test description"));
        SubTask subtaskExpected1 = manager.createSubTask(new SubTask("test name", Status.IN_PROGRESS,
                "test description", epic.getId(),
                LocalDateTime.of(2024, 7, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subtaskExpected2 = manager.createSubTask(new SubTask("test name", Status.IN_PROGRESS,
                "test description", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());


        List<SubTask> subtasksFromManager = gson.fromJson(response.body(), new TypeToken<List<SubTask>>() {
        }.getType());
        SubTask subtaskActual1 = subtasksFromManager.getFirst();
        SubTask subtaskActual2 = subtasksFromManager.getLast();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(2, subtasksFromManager.size(), "Некорректное количество задач");

        assertEquals(subtaskExpected1.getId(), subtaskActual1.getId(), "id не совпадает");
        assertEquals(subtaskExpected1.getName(), subtaskActual1.getName(), "не совпадает name");
        assertEquals(subtaskExpected1.getDescription(), subtaskActual1.getDescription(), "не совпадают description");
        assertEquals(subtaskExpected1.getStatus(), subtaskActual1.getStatus(), "не совпадают status");
        assertEquals(subtaskExpected1.getStartDateTime(), subtaskActual1.getStartDateTime(), "не совпадает startTime");
        assertEquals(subtaskExpected1.getDuration(), subtaskActual1.getDuration(), "не совпадает duration");
        assertEquals(subtaskExpected1.getEndDateTime(), subtaskActual1.getEndDateTime(), "не совпадает endTime");

        assertEquals(subtaskExpected2.getId(), subtaskActual2.getId(), "id не совпадает");
        assertEquals(subtaskExpected2.getName(), subtaskActual2.getName(), "не совпадает name");
        assertEquals(subtaskExpected2.getDescription(), subtaskActual2.getDescription(), "не совпадают description");
        assertEquals(subtaskExpected2.getStatus(), subtaskActual2.getStatus(), "не совпадают status");
        assertEquals(subtaskExpected2.getStartDateTime(), subtaskActual2.getStartDateTime(), "не совпадает startTime");
        assertEquals(subtaskExpected2.getDuration(), subtaskActual2.getDuration(), "не совпадает duration");
        assertEquals(subtaskExpected2.getEndDateTime(), subtaskActual2.getEndDateTime(), "не совпадает endTime");

    }

    @Test
    @DisplayName("Должен вернуть код ошибки, когда приходит запрос GET/epics/{id}/subtasks и задача не найдена")
    public void shouldReturnErrorCode_GETEpicsIdSubTasksRequestArrivesAndEpicNotFound() throws IOException, InterruptedException {

        Epic epic = manager.createEpic(new Epic("test name", "test description"));

        int epicId = epic.getId();
        manager.deleteEpicById(epicId);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epicId + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }


    @Test
    @DisplayName("Должен удалить эпик, когда приходит запрос DELETE/epics/{id}")
    public void shouldDeleteEpic_DELETEEpicsIdRequestArrives() throws IOException, InterruptedException {

        Epic epicExpected = manager.createEpic(new Epic("test name", "test description"));
        int epicExpectedId = epicExpected.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epicExpectedId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpicList();
        assertEquals(0, epicsFromManager.size(), "Задача не удалилась, список не пуст");
        assertThrows(NotFoundException.class, () -> manager.getEpicById(1),
                "задача не удалилась, исключение не выброшено");
    }

    @Test
    @DisplayName("Должен вернуть код ошибки, когда приходит запрос DELETE/epics/{id} и эпик не найден")
    public void shouldReturnErrorCode_DELETEEpicsIdRequestArrivesAndEpicNotFound() throws IOException, InterruptedException {

        Epic epic = manager.createEpic(new Epic("test name", "test description"));

        int epickId = epic.getId();
        manager.deleteEpicById(epickId);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epickId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }


    @Test
    @DisplayName("Должен вернуть лист с историей, когда приходит запрос GET/history")
    public void shouldGetHistoryList_GETHistoryRequestArrives() throws IOException, InterruptedException {

        Epic epic = manager.createEpic(new Epic("test EPIC name", "test EPIC description"));
        SubTask subtask = manager.createSubTask(new SubTask("test SUBTASK name", Status.NEW,
                "test SUBTASK description", epic.getId(), null, null));
        Task task = manager.createTask(new Task("test TASK name", "test TASK description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 15, 17), Duration.ofMinutes(30)));
        manager.getEpicById(1);
        manager.getSubTaskById(2);
        manager.getTaskById(3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String responseBody = response.body();
        List<Task> historyActual = gson.fromJson(responseBody, new TypeToken<List<Task>>() {
        }.getType());
        List<Task> historyExpected = manager.getHistory();


        assertEquals(historyExpected.size(), historyActual.size(), "списки разного размера");

        for (Task taskExpected : historyExpected) {
            Task taskActual = historyActual.get(historyExpected.indexOf(taskExpected));

            assertEquals(taskExpected.getId(), taskActual.getId(), "не совпадают id");
            assertEquals(taskExpected.getName(), taskActual.getName(), "не совпадает name");
            assertEquals(taskExpected.getDescription(), taskActual.getDescription(),
                    "не совпадают description");
            assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "не совпадают status");
            assertEquals(taskExpected.getStartDateTime(), taskActual.getStartDateTime(), "не совпадает startTime");
            assertEquals(taskExpected.getDuration(), taskActual.getDuration(), "не совпадает duration");
            assertEquals(taskExpected.getEndDateTime(), taskActual.getEndDateTime(), "не совпадает endTime");
        }
    }

    @Test
    @DisplayName("Должен вернуть лист с приоритетными задачами, когда приходит запрос GET/prioritized")
    public void shouldGetPrioritizedList_GETPrioritizedRequestArrives() throws IOException, InterruptedException {

        Epic epic = manager.createEpic(new Epic("test EPIC name", "test EPIC description"));
        SubTask subtask = manager.createSubTask(new SubTask("test SUBTASK name", Status.NEW,
                "test SUBTASK description", epic.getId(),
                LocalDateTime.of(2024, 7, 18, 15, 17), Duration.ofMinutes(30)));
        Task task = manager.createTask(new Task("test TASK name", "test TASK description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 15, 17), Duration.ofMinutes(60)));


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String responseBody = response.body();
        List<Task> prioritizedActual = gson.fromJson(responseBody, new TypeToken<List<Task>>() {
        }.getType());
        List<Task> prioritizedExpected = manager.getPrioritizedTasks();


        assertEquals(prioritizedExpected.size(), prioritizedActual.size(), "списки разного размера");

        for (Task taskExpected : prioritizedExpected) {
            Task taskActual = prioritizedActual.get(prioritizedExpected.indexOf(taskExpected));

            assertEquals(taskExpected.getId(), taskActual.getId(), "не совпадают id");
            assertEquals(taskExpected.getName(), taskActual.getName(), "не совпадает name");
            assertEquals(taskExpected.getDescription(), taskActual.getDescription(),
                    "не совпадают description");
            assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "не совпадают status");
            assertEquals(taskExpected.getStartDateTime(), taskActual.getStartDateTime(), "не совпадает startTime");
            assertEquals(taskExpected.getDuration(), taskActual.getDuration(), "не совпадает duration");
            assertEquals(taskExpected.getEndDateTime(), taskActual.getEndDateTime(), "не совпадает endTime");
        }
    }

}

