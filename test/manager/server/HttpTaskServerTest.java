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

@DisplayName("Server")
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
    @DisplayName("Should create a task when a POST /tasks request is received and the task has no ID")
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

        assertNotNull(tasksFromManager, "Tasks are not returned");
        assertEquals(1, tasksFromManager.size(), "Incorrect number of tasks");

        assertNotEquals(taskExpected.getId(), taskActual.getId(), "ID was not generated");
        assertEquals(taskExpected.getName(), taskActual.getName(), "Names do not match");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "Descriptions do not match");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "Statuses do not match");

        assertEquals(taskExpected.getStartDateTime(), taskActual.getStartDateTime(), "Start time does not match");
        assertEquals(taskExpected.getDuration(), taskActual.getDuration(), "Duration does not match");
        assertEquals(taskExpected.getEndDateTime(), taskActual.getEndDateTime(), "End time does not match");
    }

    @Test
    @DisplayName("Should return an error status code when a POST /tasks request is received and tasks overlap in time")
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
    @DisplayName("Should update a task when a POST /tasks request is received and the task has an ID")
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


        assertNotNull(tasksFromManager, "Tasks are not returned");
        assertEquals(1, tasksFromManager.size(), "Incorrect number of tasks");

        assertEquals(taskExpected.getId(), taskActual.getId(), "IDs do not match");
        assertEquals(taskExpected.getName(), taskActual.getName(), "Names do not match");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "Descriptions do not match");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "Statuses do not match");

        assertEquals(taskExpected.getStartDateTime(), taskActual.getStartDateTime(), "Start time does not match");
        assertEquals(taskExpected.getDuration(), taskActual.getDuration(), "Duration does not match");
        assertEquals(taskExpected.getEndDateTime(), taskActual.getEndDateTime(), "End time does not match");
    }


    @Test
    @DisplayName("Should return a list of tasks when a GET /tasks request is received")
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

        assertNotNull(tasksFromManager, "Tasks are not returned");
        assertEquals(2, tasksFromManager.size(), "Incorrect number of tasks");

        assertEquals(taskExpected1.getId(), taskActual1.getId(), "IDs do not match");
        assertEquals(taskExpected1.getName(), taskActual1.getName(), "Names do not match");
        assertEquals(taskExpected1.getDescription(), taskActual1.getDescription(), "Descriptions do not match");
        assertEquals(taskExpected1.getStatus(), taskActual1.getStatus(), "Statuses do not match");
        assertEquals(taskExpected1.getStartDateTime(), taskActual1.getStartDateTime(), "Start time does not match");
        assertEquals(taskExpected1.getDuration(), taskActual1.getDuration(), "Duration does not match");
        assertEquals(taskExpected1.getEndDateTime(), taskActual1.getEndDateTime(), "End time does not match");

        assertEquals(taskExpected2.getId(), taskActual2.getId(), "IDs do not match");
        assertEquals(taskExpected2.getName(), taskActual2.getName(), "Names do not match");
        assertEquals(taskExpected2.getDescription(), taskActual2.getDescription(), "Descriptions do not match");
        assertEquals(taskExpected2.getStatus(), taskActual2.getStatus(), "Statuses do not match");
        assertEquals(taskExpected2.getStartDateTime(), taskActual2.getStartDateTime(), "Start time does not match");
        assertEquals(taskExpected2.getDuration(), taskActual2.getDuration(), "Duration does not match");
        assertEquals(taskExpected2.getEndDateTime(), taskActual2.getEndDateTime(), "End time does not match");

    }


    @Test
    @DisplayName("Should return a task when a GET /tasks/{id} request is received")
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

        assertEquals(taskExpected.getId(), taskActual.getId(), "IDs do not match");
        assertEquals(taskExpected.getName(), taskActual.getName(), "Names do not match");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "Descriptions do not match");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "Statuses do not match");
        assertEquals(taskExpected.getStartDateTime(), taskActual.getStartDateTime(), "Start time does not match");
        assertEquals(taskExpected.getDuration(), taskActual.getDuration(), "Duration does not match");
        assertEquals(taskExpected.getEndDateTime(), taskActual.getEndDateTime(), "End time does not match");

    }

    @Test
    @DisplayName("Should return an error status code when a GET /tasks/{id} request is received and the task is not found")
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
    @DisplayName("Should delete a task when a DELETE /task/{id} request is received")
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
        assertEquals(0, tasksFromManager.size(), "Task was not deleted, the list is not empty");
        assertThrows(NotFoundException.class, () -> manager.getTaskById(1),
                "Task was not deleted, no exception was thrown");
    }

    @Test
    @DisplayName("Should return an error code for DELETE /tasks/{id} when the task does not exist")
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
    @DisplayName("Should create a subtask when a POST /subtasks request is received and the subtask has no ID")
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


        assertNotNull(subtasksFromManager, "Tasks are not returned");
        assertEquals(1, subtasksFromManager.size(), "Incorrect number of tasks");

        assertNotEquals(subtaskExpected.getId(), subtaskActual.getId(), "ID was not generated");
        assertEquals(subtaskExpected.getName(), subtaskActual.getName(), "Names do not match");
        assertEquals(subtaskExpected.getDescription(), subtaskActual.getDescription(), "Descriptions do not match");
        assertEquals(subtaskExpected.getStatus(), subtaskActual.getStatus(), "Statuses do not match");

        assertEquals(subtaskExpected.getStartDateTime(), subtaskActual.getStartDateTime(), "Start time does not match");
        assertEquals(subtaskExpected.getDuration(), subtaskActual.getDuration(), "Duration does not match");
        assertEquals(subtaskExpected.getEndDateTime(), subtaskActual.getEndDateTime(), "End time does not match");
    }

    @Test
    @DisplayName("Should return an error code for POST /subtasks when there is a time conflict")
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
    @DisplayName("Should update a subtask when a POST /subtasks request is received and the subtask has an ID")
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


        assertNotNull(tasksFromManager, "Tasks are not returned");
        assertEquals(1, tasksFromManager.size(), "Incorrect number of tasks");

        assertEquals(subtaskExpected.getId(), subtaskActual.getId(), "IDs do not match");
        assertEquals(subtaskExpected.getName(), subtaskActual.getName(), "Names do not match");
        assertEquals(subtaskExpected.getDescription(), subtaskActual.getDescription(), "Descriptions do not match");
        assertEquals(subtaskExpected.getStatus(), subtaskActual.getStatus(), "Statuses do not match");

        assertEquals(subtaskExpected.getStartDateTime(), subtaskActual.getStartDateTime(), "Start time does not match");
        assertEquals(subtaskExpected.getDuration(), subtaskActual.getDuration(), "Duration does not match");
        assertEquals(subtaskExpected.getEndDateTime(), subtaskActual.getEndDateTime(), "End time does not match");
    }

    @Test
    @DisplayName("Should return a list of subtasks when a GET /subtasks request is received")
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

        assertNotNull(subtasksFromManager, "Tasks are not returned");
        assertEquals(2, subtasksFromManager.size(), "Incorrect number of tasks");

        assertEquals(subtaskExpected1.getId(), subtaskActual1.getId(), "IDs do not match");
        assertEquals(subtaskExpected1.getName(), subtaskActual1.getName(), "Names do not match");
        assertEquals(subtaskExpected1.getDescription(), subtaskActual1.getDescription(), "Descriptions do not match");
        assertEquals(subtaskExpected1.getStatus(), subtaskActual1.getStatus(), "Statuses do not match");
        assertEquals(subtaskExpected1.getStartDateTime(), subtaskActual1.getStartDateTime(), "Start time does not match");
        assertEquals(subtaskExpected1.getDuration(), subtaskActual1.getDuration(), "Duration does not match");
        assertEquals(subtaskExpected1.getEndDateTime(), subtaskActual1.getEndDateTime(), "End time does not match");

        assertEquals(subtaskExpected2.getId(), subtaskActual2.getId(), "IDs do not match");
        assertEquals(subtaskExpected2.getName(), subtaskActual2.getName(), "Names do not match");
        assertEquals(subtaskExpected2.getDescription(), subtaskActual2.getDescription(), "Descriptions do not match");
        assertEquals(subtaskExpected2.getStatus(), subtaskActual2.getStatus(), "Statuses do not match");
        assertEquals(subtaskExpected2.getStartDateTime(), subtaskActual2.getStartDateTime(), "Start time does not match");
        assertEquals(subtaskExpected2.getDuration(), subtaskActual2.getDuration(), "Duration does not match");
        assertEquals(subtaskExpected2.getEndDateTime(), subtaskActual2.getEndDateTime(), "End time does not match");

    }

    @Test
    @DisplayName("Should return a subtask when a GET /subtasks/{id} request is received")
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

        assertEquals(subtaskExpected.getId(), subtaskActual.getId(), "IDs do not match");
        assertEquals(subtaskExpected.getName(), subtaskActual.getName(), "Names do not match");
        assertEquals(subtaskExpected.getDescription(), subtaskActual.getDescription(), "Descriptions do not match");
        assertEquals(subtaskExpected.getStatus(), subtaskActual.getStatus(), "Statuses do not match");
        assertEquals(subtaskExpected.getStartDateTime(), subtaskActual.getStartDateTime(), "Start time does not match");
        assertEquals(subtaskExpected.getDuration(), subtaskActual.getDuration(), "Duration does not match");
        assertEquals(subtaskExpected.getEndDateTime(), subtaskActual.getEndDateTime(), "End time does not match");

    }


    @Test
    @DisplayName("Should return an error code for GET /subtasks/{id} when the subtask does not exist")
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
    @DisplayName("Should delete a subtask when a DELETE /subtasks/{id} request is received")
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
        assertEquals(0, subtasksFromManager.size(), "Task was not deleted, the list is not empty");
        assertThrows(NotFoundException.class, () -> manager.getSubTaskById(2),
                "Task was not deleted, no exception was thrown");
    }

    @Test
    @DisplayName("Should return an error code for DELETE /subtasks/{id} when the subtask does not exist")
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
    @DisplayName("Should create an epic when a POST /epics request is received and the epic has no ID")
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


        assertNotNull(epicsFromManager, "Tasks are not returned");
        assertEquals(1, epicsFromManager.size(), "Incorrect number of tasks");

        assertNotEquals(epicExpected.getId(), epicActual.getId(), "ID was not generated");
        assertEquals(epicExpected.getName(), epicActual.getName(), "Names do not match");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(), "Descriptions do not match");
        assertEquals(Status.NEW, epicActual.getStatus(), "Statuses do not match");

        assertEquals(epicExpected.getStartDateTime(), epicActual.getStartDateTime(), "Start time does not match");
        assertEquals(epicExpected.getDuration(), epicActual.getDuration(), "Duration does not match");
        assertEquals(epicExpected.getEndDateTime(), epicActual.getEndDateTime(), "End time does not match");
    }


    @Test
    @DisplayName("Should update an epic when a POST /epics request is received and the epic has an ID")
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


        assertNotNull(epicsFromManager, "Tasks are not returned");
        assertEquals(1, epicsFromManager.size(), "Incorrect number of tasks");

        assertEquals(epicExpected.getId(), epicActual.getId(), "IDs do not match");
        assertEquals(epicExpected.getName(), epicActual.getName(), "Names do not match");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(), "Descriptions do not match");
        assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "Statuses do not match");

        assertEquals(epicExpected.getStartDateTime(), epicActual.getStartDateTime(), "Start time does not match");
        assertEquals(epicExpected.getDuration(), epicActual.getDuration(), "Duration does not match");
        assertEquals(epicExpected.getEndDateTime(), epicActual.getEndDateTime(), "End time does not match");
    }


    @Test
    @DisplayName("Should return a list of epics when a GET /epics request is received")
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

        assertNotNull(epicFromManager, "Tasks are not returned");
        assertEquals(2, epicFromManager.size(), "Incorrect number of tasks");

        assertEquals(epicExpected1.getId(), epicActual1.getId(), "IDs do not match");
        assertEquals(epicExpected1.getName(), epicActual1.getName(), "Names do not match");
        assertEquals(epicExpected1.getDescription(), epicActual1.getDescription(), "Descriptions do not match");
        assertEquals(epicExpected1.getStatus(), epicActual1.getStatus(), "Statuses do not match");
        assertEquals(epicExpected1.getStartDateTime(), epicActual1.getStartDateTime(), "Start time does not match");
        assertEquals(epicExpected1.getDuration(), epicActual1.getDuration(), "Duration does not match");
        assertEquals(epicExpected1.getEndDateTime(), epicActual1.getEndDateTime(), "End time does not match");

        assertEquals(epicExpected2.getId(), epicActual2.getId(), "IDs do not match");
        assertEquals(epicExpected2.getName(), epicActual2.getName(), "Names do not match");
        assertEquals(epicExpected2.getDescription(), epicActual2.getDescription(), "Descriptions do not match");
        assertEquals(epicExpected2.getStatus(), epicActual2.getStatus(), "Statuses do not match");
        assertEquals(epicExpected2.getStartDateTime(), epicActual2.getStartDateTime(), "Start time does not match");
        assertEquals(epicExpected2.getDuration(), epicActual2.getDuration(), "Duration does not match");
        assertEquals(epicExpected2.getEndDateTime(), epicActual2.getEndDateTime(), "End time does not match");

    }

    @Test
    @DisplayName("Should return an epic when a GET /epics/{id} request is received")
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

        assertEquals(epicExpected.getId(), epicActual.getId(), "IDs do not match");
        assertEquals(epicExpected.getName(), epicActual.getName(), "Names do not match");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(), "Descriptions do not match");
        assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "Statuses do not match");
        assertEquals(epicExpected.getStartDateTime(), epicActual.getStartDateTime(), "Start time does not match");
        assertEquals(epicExpected.getDuration(), epicActual.getDuration(), "Duration does not match");
        assertEquals(epicExpected.getEndDateTime(), epicActual.getEndDateTime(), "End time does not match");

    }


    @Test
    @DisplayName("Should return an error code for GET /epics/{id} when the epic does not exist")
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
    @DisplayName("Should return the list of an epic’s subtasks by its ID when a GET /epics/{id}/subtasks request is received")
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

        assertNotNull(subtasksFromManager, "Tasks are not returned");
        assertEquals(2, subtasksFromManager.size(), "Incorrect number of tasks");

        assertEquals(subtaskExpected1.getId(), subtaskActual1.getId(), "IDs do not match");
        assertEquals(subtaskExpected1.getName(), subtaskActual1.getName(), "Names do not match");
        assertEquals(subtaskExpected1.getDescription(), subtaskActual1.getDescription(), "Descriptions do not match");
        assertEquals(subtaskExpected1.getStatus(), subtaskActual1.getStatus(), "Statuses do not match");
        assertEquals(subtaskExpected1.getStartDateTime(), subtaskActual1.getStartDateTime(), "Start time does not match");
        assertEquals(subtaskExpected1.getDuration(), subtaskActual1.getDuration(), "Duration does not match");
        assertEquals(subtaskExpected1.getEndDateTime(), subtaskActual1.getEndDateTime(), "End time does not match");

        assertEquals(subtaskExpected2.getId(), subtaskActual2.getId(), "IDs do not match");
        assertEquals(subtaskExpected2.getName(), subtaskActual2.getName(), "Names do not match");
        assertEquals(subtaskExpected2.getDescription(), subtaskActual2.getDescription(), "Descriptions do not match");
        assertEquals(subtaskExpected2.getStatus(), subtaskActual2.getStatus(), "Statuses do not match");
        assertEquals(subtaskExpected2.getStartDateTime(), subtaskActual2.getStartDateTime(), "Start time does not match");
        assertEquals(subtaskExpected2.getDuration(), subtaskActual2.getDuration(), "Duration does not match");
        assertEquals(subtaskExpected2.getEndDateTime(), subtaskActual2.getEndDateTime(), "End time does not match");

    }

    @Test
    @DisplayName("Should return an error code for GET /epics/{id}/subtasks when the epic does not exist")
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
    @DisplayName("Should delete an epic when a DELETE /epics/{id} request is received")
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
        assertEquals(0, epicsFromManager.size(), "Task was not deleted, the list is not empty");
        assertThrows(NotFoundException.class, () -> manager.getEpicById(1),
                "Task was not deleted, no exception was thrown");
    }

    @Test
    @DisplayName("Should return an error code for DELETE /epics/{id} when the epic does not exist")
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
    @DisplayName("Should return the history list when a GET /history request is received")
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


        assertEquals(historyExpected.size(), historyActual.size(), "Lists have different sizes");

        for (Task taskExpected : historyExpected) {
            Task taskActual = historyActual.get(historyExpected.indexOf(taskExpected));

            assertEquals(taskExpected.getId(), taskActual.getId(), "IDs do not match");
            assertEquals(taskExpected.getName(), taskActual.getName(), "Names do not match");
            assertEquals(taskExpected.getDescription(), taskActual.getDescription(),
                    "Descriptions do not match");
            assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "Statuses do not match");
            assertEquals(taskExpected.getStartDateTime(), taskActual.getStartDateTime(), "Start time does not match");
            assertEquals(taskExpected.getDuration(), taskActual.getDuration(), "Duration does not match");
            assertEquals(taskExpected.getEndDateTime(), taskActual.getEndDateTime(), "End time does not match");
        }
    }

    @Test
    @DisplayName("Should return the prioritized task list when a GET /prioritized request is received")
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


        assertEquals(prioritizedExpected.size(), prioritizedActual.size(), "Lists have different sizes");

        for (Task taskExpected : prioritizedExpected) {
            Task taskActual = prioritizedActual.get(prioritizedExpected.indexOf(taskExpected));

            assertEquals(taskExpected.getId(), taskActual.getId(), "IDs do not match");
            assertEquals(taskExpected.getName(), taskActual.getName(), "Names do not match");
            assertEquals(taskExpected.getDescription(), taskActual.getDescription(),
                    "Descriptions do not match");
            assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "Statuses do not match");
            assertEquals(taskExpected.getStartDateTime(), taskActual.getStartDateTime(), "Start time does not match");
            assertEquals(taskExpected.getDuration(), taskActual.getDuration(), "Duration does not match");
            assertEquals(taskExpected.getEndDateTime(), taskActual.getEndDateTime(), "End time does not match");
        }
    }

}

