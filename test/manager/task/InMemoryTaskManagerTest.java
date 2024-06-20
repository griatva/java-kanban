package manager.task;

import exception.NotFoundException;
import exception.ValidationException;
import manager.Managers;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Менеджер")
class InMemoryTaskManagerTest {

    private TaskManager manager;

    @BeforeEach
    void init() {
        manager = Managers.getDefaults();
        manager.deleteAllEpics();
        manager.deleteAllTasks();
        manager.deleteAllSubTasks();
    }

    @Test
    @DisplayName("Должен возвращать задачу по ID из менеджера")
    void getTaskById_returnTaskById() {
        //given
        Task task = manager.createTask(new Task("название задачи", "описание", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));
        Task taskExpected = new Task("название задачи", "описание", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60));
        taskExpected.setId(1);

        //when
        Task taskActual = manager.getTaskById(task.getId());

        //then
        assertEquals(taskExpected.getId(), taskActual.getId(), "не совпадают id");
        assertEquals(taskExpected.getName(), taskActual.getName(), "не совпадает name");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "не совпадают description");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "не совпадают status");
        assertEquals(taskExpected.getStartDateTime(), taskActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(taskExpected.getDuration(), taskActual.getDuration(), "не совпадает duration");
        assertEquals(taskExpected.getEndDateTime(), taskActual.getEndDateTime(), "не совпадает endTime");
    }

    @Test
    @DisplayName("Должен добавлять задачу в историю")
    void getTaskById_addTaskToHistory() {
        //given
        Task taskExpected = manager.createTask(new Task("название задачи", "описание", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));

        //when
        manager.getTaskById(taskExpected.getId());
        List<Task> historyList = manager.getHistory();
        Task taskActual = historyList.getFirst();

        //then
        assertEquals(taskExpected.getId(), taskActual.getId(), "не совпадают id");
        assertEquals(taskExpected.getName(), taskActual.getName(), "не совпадает name");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "не совпадают description");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "не совпадают status");
        assertEquals(taskExpected.getStartDateTime(), taskActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(taskExpected.getDuration(), taskActual.getDuration(), "не совпадает duration");
        assertEquals(taskExpected.getEndDateTime(), taskActual.getEndDateTime(), "не совпадает endTime");
    }

    @Test
    @DisplayName("Должен выбрасывать исключение, если не может найти задачу")
    void getTaskById_throwException_notFoundTask() {
        //given
        Task task = manager.createTask(new Task("название задачи", "описание", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));

        //then
        assertThrows(NotFoundException.class, () -> manager.getTaskById(2), "исключение не выброшено");
    }

    @Test
    @DisplayName("Должен возвращать подзадачу по ID из менеджера")
    void getSubTaskById_returnSubTaskById() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subTask = manager.createSubTask(new SubTask("name", "description", Status.NEW,
                epic.getId()));
        SubTask subTaskExpected = new SubTask("name", "description", Status.NEW, 1);
        subTaskExpected.setId(2);

        //when
        SubTask subTaskActual = manager.getSubTaskById(subTask.getId());

        //then
        assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "не совпадают id");
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "не совпадает name");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                "не совпадают description");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "не совпадают status");
        assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "не совпадают epicId");
        assertEquals(subTaskExpected.getStartDateTime(), subTaskActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(subTaskExpected.getDuration(), subTaskActual.getDuration(), "не совпадает duration");
        assertEquals(subTaskExpected.getEndDateTime(), subTaskActual.getEndDateTime(), "не совпадает endTime");
    }

    @Test
    @DisplayName("Должен добавлять подзадачу в историю")
    void getSubTaskById_addSubTaskToHistory() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subTaskExpected = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        //when
        manager.getSubTaskById(subTaskExpected.getId());

        List<Task> historyList = manager.getHistory();
        SubTask subTaskActual = (SubTask) historyList.get(0);

        //then
        assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "не совпадают id");
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "не совпадает name");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                "не совпадают description");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "не совпадают status");
        assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "не совпадают epicId");
        assertEquals(subTaskExpected.getStartDateTime(), subTaskActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(subTaskExpected.getDuration(), subTaskActual.getDuration(), "не совпадает duration");
        assertEquals(subTaskExpected.getEndDateTime(), subTaskActual.getEndDateTime(), "не совпадает endTime");
    }

    @Test
    @DisplayName("Должен выбрасывать исключение, если не может найти подзадачу")
    void getSubTaskById_throwException_notFoundSubTask() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        manager.deleteEpicById(1);


        //then
        assertThrows(NotFoundException.class, () -> manager.getSubTaskById(2), "исключение не выброшено");
    }

    @Test
    @DisplayName("Должен возвращать эпик по ID из менеджера")
    void getEpicById_returnEpicById() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        Epic epicExpected = new Epic("name", "description");
        epicExpected.setId(1);
        epicExpected.setStatus(Status.NEW);

        //when
        Epic epicActual = manager.getEpicById(epic.getId());

        //then
        assertEquals(epicExpected.getId(), epicActual.getId(), "не совпадают id");
        assertEquals(epicExpected.getName(), epicActual.getName(), "не совпадает name");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(), "не совпадают description");
        assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "не совпадают status");
        assertEquals(epicExpected.getStartDateTime(), epicActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(epicExpected.getDuration(), epicActual.getDuration(), "не совпадает duration");
        assertEquals(epicExpected.getEndDateTime(), epicActual.getEndDateTime(), "не совпадает endTime");
    }

    @Test
    @DisplayName("Должен добавлять эпик в историю")
    void getEpicById_addEpicToHistory() {
        //given
        Epic epicExpected = manager.createEpic(new Epic("name", "description"));

        //when
        manager.getEpicById(epicExpected.getId());

        List<Task> historyList = manager.getHistory();
        Epic epicActual = (Epic) historyList.get(0);

        //then
        assertEquals(epicExpected.getId(), epicActual.getId(), "не совпадают id");
        assertEquals(epicExpected.getName(), epicActual.getName(), "не совпадает name");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(), "не совпадают description");
        assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "не совпадают status");
        assertEquals(epicExpected.getStartDateTime(), epicActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(epicExpected.getDuration(), epicActual.getDuration(), "не совпадает duration");
        assertEquals(epicExpected.getEndDateTime(), epicActual.getEndDateTime(), "не совпадает endTime");
    }

    @Test
    @DisplayName("Должен выбрасывать исключение, если не может найти эпик")
    void getEpicById_throwException_notFoundEpic() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        manager.deleteEpicById(1);


        //then
        assertThrows(NotFoundException.class, () -> manager.getEpicById(1), "исключение не выброшено");
    }

    @Test
    @DisplayName("Должен возвращать задачу со сгенерированным id")
    void createTask_returnTaskWithGeneratedId() {
        //given
        Task task = new Task("название задачи", "описание", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60));
        task.setId(5);

        //when
        manager.createTask(task);

        //then
        assertEquals(1, task.getId(), "индивидуальный id не сгенерирован");
    }

    @Test
    @DisplayName("Должен добавить задачу в менеджер")
    void createTask_putTaskInManager() {
        //given
        Task taskExpected = new Task("название задачи", "описание", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60));
        taskExpected.setId(1);

        //when
        manager.createTask(taskExpected);
        List<Task> taskList = manager.getTasksList();
        Task taskActual = taskList.getFirst();

        //then
        assertEquals(taskExpected.getId(), taskActual.getId(), "не совпадают id");
        assertEquals(taskExpected.getName(), taskActual.getName(), "не совпадает name");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "не совпадают description");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "не совпадают status");
        assertEquals(taskExpected.getStartDateTime(), taskActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(taskExpected.getDuration(), taskActual.getDuration(), "не совпадает duration");
        assertEquals(taskExpected.getEndDateTime(), taskActual.getEndDateTime(), "не совпадает endTime");
    }

    @Test
    @DisplayName("Должен добавить задачу в список приоритетных задач, если задано startDateTime")
    void createTask_putTaskInPrioritizedSet_startDateTimeIsSet() {
        //given
        Task taskExpected = new Task("name", "description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        taskExpected.setId(1);

        //when
        manager.createTask(taskExpected);

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        Task taskActual = prioritizedTasks.getFirst();

        //then
        assertEquals(taskExpected.getId(), taskActual.getId(), "не совпадают id");
        assertEquals(taskExpected.getName(), taskActual.getName(), "не совпадает name");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "не совпадают description");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "не совпадают status");
        assertEquals(taskExpected.getStartDateTime(), taskActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(taskExpected.getDuration(), taskActual.getDuration(), "не совпадает duration");
        assertEquals(taskExpected.getEndDateTime(), taskActual.getEndDateTime(), "не совпадает endTime");
    }

    @Test
    @DisplayName("Должен выбросить исключение, если задачи пересекаются по времени")
    void createTask_throwException_ifTheTasksOverlapInTime() {
        //given
        Task task1 = new Task("название задачи-1", "описание-1", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60));
        Task task2 = new Task("название задачи-2", "описание-2", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 13, 0), Duration.ofMinutes(60));

        //when
        manager.createTask(task1);

        //then
        assertThrows(ValidationException.class, () -> manager.createTask(task2), "исключение не выброшено");
    }

    @Test
    @DisplayName("Не должен добавлять задачу в сет приоритетных задач, " +
            "если эта задача пересекается во времени с уже добавленной в сет")
    void createTask_notAddTaskToPrioritizedTasksSet_ifTheTasksOverlapInTime() {
        //given
        Task task1 = new Task("название задачи-1", "описание-1", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60));
        Task task2 = new Task("название задачи-2", "описание-2", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 13, 0), Duration.ofMinutes(60));

        //when
        manager.createTask(task1);

        //then
        assertThrows(ValidationException.class, () -> manager.createTask(task2), "исключение не выброшено");
        List<Task> list = manager.getPrioritizedTasks();
        assertEquals(1, list.size(), "вторая задача не должна добавляться в сет");
    }

    @Test
    @DisplayName("Не должен выбросывать исключение при граничных значениях")
    void createTask_NotThrowExceptionAtBoundaryValues() {
        //given
        Task task1 = new Task("название задачи-1", "описание-1", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60));
        Task task2 = new Task("название задачи-2", "описание-2", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 13, 15), Duration.ofMinutes(60));

        //when
        manager.createTask(task1);

        //then
        assertDoesNotThrow(() -> manager.createTask(task2), "исключения не должно быть");
    }

    @Test
    @DisplayName("Не должен добавлять задачу в сет приоритетных задач, " +
            "если у нее не указано стартовое время")
    void createTask_notAddTaskToPrioritizedTasksSet_ifTaskDoesNotHaveSpecifiedStartingTime() {
        //given
        Task task1 = new Task("название задачи-1", "описание-1", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60));
        Task task2 = new Task("название задачи-2", "описание-2", Status.NEW,
                null, null);

        //when
        manager.createTask(task1);
        manager.createTask(task2);
        List<Task> list = manager.getPrioritizedTasks();

        //then
        assertEquals(1, list.size(), "вторая задача не должна добавляться в сет");
    }

    @Test
    @DisplayName("Должен возвращать подзадачу со сгенерированным id")
    void createSubTask_returnSubTaskWithGeneratedId() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60));
        subtask.setId(5);

        //when
        manager.createSubTask(subtask);

        //then
        assertEquals(2, subtask.getId(), "индивидуальный id не сгенерирован");
    }

    @Test
    @DisplayName("Должен добавить подзадачу в менеджер")
    void createSubTask_putSubTaskInManager() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subTaskExpected = new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60));
        subTaskExpected.setId(2);

        //when
        manager.createSubTask(subTaskExpected);
        List<SubTask> subTaskList = manager.getSubTasksList();
        SubTask subTaskActual = subTaskList.getFirst();

        //then
        assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "не совпадают id");
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "не совпадает name");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                "не совпадают description");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "не совпадают status");
        assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "не совпадают epicId");
        assertEquals(subTaskExpected.getStartDateTime(), subTaskActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(subTaskExpected.getDuration(), subTaskActual.getDuration(), "не совпадает duration");
        assertEquals(subTaskExpected.getEndDateTime(), subTaskActual.getEndDateTime(), "не совпадает endTime");
    }

    @Test
    @DisplayName("Должен сохранить id подзадачи в ее эпике")
    void createSubTask_putIdToListInItsEpic() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subTask = new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60));
        //when
        manager.createSubTask(subTask);
        List<Integer> subTasksIdList = epic.getSubTasksId();

        //then
        assertEquals(2, subTasksIdList.getFirst(), "id не сохранился");
    }

    @Test
    @DisplayName("Должен добавить подзадачу в список приоритетных задач, если задано startDateTime")
    void createSubTask_putSubTaskInPrioritizedSet_startDateTimeIsSet() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name", "description"));

        SubTask subTaskExpected = new SubTask("name", Status.NEW, "description", epic1.getId(), LocalDateTime.now(), Duration.ofMinutes(30));
        subTaskExpected.setId(2);

        //when
        manager.createSubTask(subTaskExpected);

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        SubTask subTaskActual = (SubTask) prioritizedTasks.getFirst();

        //then
        assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "не совпадают id");
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "не совпадает name");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(), "не совпадают description");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "не совпадают status");
        assertEquals(subTaskExpected.getStartDateTime(), subTaskActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(subTaskExpected.getDuration(), subTaskActual.getDuration(), "не совпадает duration");
        assertEquals(subTaskExpected.getEndDateTime(), subTaskActual.getEndDateTime(), "не совпадает endTime");
        assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "не совпадают epicId");
    }

    @Test
    @DisplayName("Должен выбросить исключение, если задачи пересекаются по времени")
    void createSubTask_throwException_ifTheTasksOverlapInTime() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name", "description"));
        SubTask subTask1 = new SubTask("название задачи-1", Status.NEW, "описание-1", epic1.getId(),
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60));
        SubTask subTask2 = new SubTask("название задачи-2", Status.NEW, "описание-2", epic1.getId(),
                LocalDateTime.of(2024, 6, 19, 12, 20), Duration.ofMinutes(30));

        //when
        manager.createSubTask(subTask1);

        //then
        assertThrows(ValidationException.class, () -> manager.createSubTask(subTask2), "исключение не выброшено");
    }

    @Test
    @DisplayName("Не должен добавлять подзадачу в сет приоритетных задач, " +
            "если эта задача пересекается во времени с уже добавленной в сет")
    void createSubTask_notAddSubTaskToPrioritizedTasksSet_ifTheTasksOverlapInTime() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name", "description"));
        SubTask subTask1 = new SubTask("название задачи-1", Status.NEW, "описание-1", epic1.getId(),
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60));
        SubTask subTask2 = new SubTask("название задачи-2", Status.NEW, "описание-2", epic1.getId(),
                LocalDateTime.of(2024, 6, 19, 12, 20), Duration.ofMinutes(30));

        //when
        manager.createSubTask(subTask1);

        //then
        assertThrows(ValidationException.class, () -> manager.createSubTask(subTask2), "исключение не выброшено");
        List<Task> list = manager.getPrioritizedTasks();
        assertEquals(1, list.size(), "вторая подзадача не должна добавляться в сет");
    }

    @Test
    @DisplayName("Не должен выбросывать исключение при граничных значениях")
    void createSubTask_NotThrowExceptionAtBoundaryValues() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name", "description"));
        SubTask subTask1 = new SubTask("название задачи-1", Status.NEW, "описание-1", epic1.getId(),
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60));
        SubTask subTask2 = new SubTask("название задачи-2", Status.NEW, "описание-2", epic1.getId(),
                LocalDateTime.of(2024, 6, 19, 13, 15), Duration.ofMinutes(30));

        //when
        manager.createSubTask(subTask1);

        //then
        assertDoesNotThrow(() -> manager.createSubTask(subTask2), "исключения не должно быть");
    }

    @Test
    @DisplayName("Не должен добавлять задачу в сет приоритетных задач, " +
            "если у нее не указано стартовое время")
    void createSubTask_notAddSubTaskToPrioritizedTasksSet_ifSubTaskDoesNotHaveSpecifiedStartingTime() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name", "description"));
        SubTask subTask1 = new SubTask("название задачи-1", Status.NEW, "описание-1", epic1.getId(),
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60));
        SubTask subTask2 = new SubTask("название задачи-2", Status.NEW, "описание-2", epic1.getId(),
                null, null);

        //when
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        List<Task> list = manager.getPrioritizedTasks();

        //then
        assertEquals(1, list.size(), "вторая подзадача не должна добавляться в сет");
    }

    @Test
    @DisplayName("Должен обновлять поле duration у эпика, к которому относится подзадача, если у нее задано время начала")
    void createSubTask_updateFieldDurationOfEpic_ifSubTaskHaveSpecifiedStartingTime() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        Duration duration = epic.getDuration();

        SubTask subTask1 = new SubTask("название задачи-1", Status.NEW, "описание-1", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60));
        SubTask subTask2 = new SubTask("название задачи-2", Status.NEW, "описание-2", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 13, 15), Duration.ofMinutes(30));

        //when
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        //then
        assertNotEquals(duration, epic.getDuration(), "поле duration не поменялось");
        assertEquals((60 + 30) * 60, epic.getDuration().getSeconds(), "поле duration поменялось некорректно");
    }

    @Test
    @DisplayName("Должен обновлять поле startDateTime у эпика, к которому относится подзадача, " +
            "если у нее задано время начала и если оно раньше всех остальных подзадач этого эпика")
    void createSubTask_updateFieldStartDateTimeOfEpic_ifSubTaskHaveTheEarliestSpecifiedStartingTime() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        LocalDateTime startDateTime = epic.getStartDateTime();

        SubTask subTask1 = new SubTask("название задачи-1", Status.NEW, "описание-1", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 15, 15), Duration.ofMinutes(60));
        SubTask earliestSubTask = new SubTask("название задачи-2", Status.NEW, "описание-2", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60));

        //when
        manager.createSubTask(subTask1);
        manager.createSubTask(earliestSubTask);

        //then
        assertNotEquals(startDateTime, epic.getStartDateTime(), "поле startDateTime не поменялось");
        assertEquals(earliestSubTask.getStartDateTime(), epic.getStartDateTime(), "поле startDateTime поменялось некорректно");
    }

    @Test
    @DisplayName("Должен обновлять поле endDateTime у эпика, к которому относится подзадача, " +
            "если у нее задано время начала и если endDateTime сабтаска позже всех остальных подзадач этого эпика")
    void createSubTask_updateFieldEndDateTimeOfEpic_ifSubTaskHaveSpecifiedStartingTimeAndTheLatestEndDateTime() {
        //given

        Epic epic = manager.createEpic(new Epic("name", "description"));
        LocalDateTime endDateTime = epic.getEndDateTime();

        SubTask subTask1 = new SubTask("название задачи-1", Status.NEW, "описание-1", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 15, 15), Duration.ofMinutes(60));
        SubTask latestSubTask = new SubTask("название задачи-2", Status.NEW, "описание-2", epic.getId(),
                LocalDateTime.of(2024, 6, 20, 12, 15), Duration.ofMinutes(60));

        //when
        manager.createSubTask(subTask1);
        manager.createSubTask(latestSubTask);

        //then
        assertNotEquals(endDateTime, epic.getEndDateTime(), "поле endDateTime не поменялось");
        assertEquals(latestSubTask.getEndDateTime(), epic.getEndDateTime(), "поле endDateTime поменялось некорректно");
    }

    @Test
    @DisplayName("Должен вернуть эпик со сгенерированным id")
    void createEpic_returnEpicWithGeneratedId() {

        //given
        Epic epic = new Epic("name", "description");
        epic.setId(5);

        //when
        manager.createEpic(epic);

        //then
        assertEquals(1, epic.getId(), "индивидуальный id не сгенерирован");
    }

    @Test
    @DisplayName("Должен добавить эпик в менеджер")
    void createEpic_putEpicInManager() {
        //given
        Epic epicExpected = new Epic("name", "description");
        epicExpected.setId(1);
        epicExpected.setStatus(Status.NEW);

        //when
        manager.createEpic(epicExpected);
        List<Epic> epicList = manager.getEpicList();
        Epic epicActual = epicList.getFirst();

        //then
        assertEquals(epicExpected.getId(), epicActual.getId(), "не совпадают id");
        assertEquals(epicExpected.getName(), epicActual.getName(), "не совпадает name");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(), "не совпадают description");
        assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "не совпадают status");
        assertEquals(epicExpected.getStartDateTime(), epicActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(epicExpected.getDuration(), epicActual.getDuration(), "не совпадает duration");
        assertEquals(epicExpected.getEndDateTime(), epicActual.getEndDateTime(), "не совпадает endTime");
    }

    @Test
    @DisplayName("Должен установить статус эпика NEW")
    void createEpic_makeStatusNew() {
        //given
        Epic epic = new Epic("name", "description");

        //when
        manager.createEpic(epic);

        //then
        assertEquals(Status.NEW, epic.getStatus(), "статус NEW не установлен");
    }

    @Test
    @DisplayName("Должен поменять задачу на новую по id")
    void updateTask_changeTaskToNewOne_tasksHaveSameId() {

        //given
        Task task = manager.createTask(new Task("name", "description", Status.DONE));
        Task taskExpected = new Task("NewName", "NewDescription", Status.IN_PROGRESS);
        taskExpected.setId(1);

        //when
        manager.updateTask(taskExpected);
        Task taskActual = manager.getTaskById(task.getId());

        //then
        assertEquals(taskExpected.getName(), taskActual.getName(), "не совпадает name");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "не совпадают description");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "не совпадают status");
        assertEquals(taskExpected.getStartDateTime(), taskActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(taskExpected.getDuration(), taskActual.getDuration(), "не совпадает duration");
        assertEquals(taskExpected.getEndDateTime(), taskActual.getEndDateTime(), "не совпадает endTime");
    }

    @Test
    @DisplayName("Должен выбросить исключение, если задачи пересекаются по времени")
    void updateTask_throwException_ifTheTasksOverlapInTime() {
        //given
        Task immutableTask = manager.createTask(new Task("название задачи", "описание", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60)));

        Task taskForUpdateExpected = manager.createTask(new Task("название задачи-1", "описание-1", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 18, 15), Duration.ofMinutes(60)));

        Task newTask = new Task("название задачи-2", "описание-2", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 13, 5), Duration.ofMinutes(60));
        newTask.setId(2);

        //then
        assertThrows(ValidationException.class, () -> manager.updateTask(newTask), "исключение не выброшено");
    }

    @Test
    @DisplayName("Не должен выбросить исключение при пересечении задачи по времени с самой собой")
    void updateTask_notThrowException_whenTaskIntersectsWithItselfInTime() {
        //given
        Task oldTask = manager.createTask(new Task("название задачи-1", "описание-1", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60)));

        Task newTask = new Task("название задачи-2", "описание-2", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 13, 5), Duration.ofMinutes(60));
        newTask.setId(1);

        //then
        assertDoesNotThrow(() -> manager.updateTask(newTask), "исключение не должно быть выброшено");
    }

    @Test
    @DisplayName("Должен удалить старую задачу из сета и добавить вместо нее новую с тем же id")
    void updateTask_removeOldTaskFromPrioritizedTasksAndAddNewOneWithTheSameIdInstead() {
        //given
        Task immutableTask = manager.createTask(new Task("название задачи", "описание", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60)));

        Task taskForUpdate = manager.createTask(new Task("название задачи-1", "описание-1", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 18, 15), Duration.ofMinutes(60)));

        Task newTaskExpected = new Task("название задачи-2", "описание-2", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 18, 0), Duration.ofMinutes(30));
        newTaskExpected.setId(2);

        //when
        manager.updateTask(newTaskExpected);
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        Task newTaskActual = prioritizedTasks.get(1);

        //then
        assertEquals(2, prioritizedTasks.size(), "старая задача не удалилась");

        assertEquals(newTaskExpected.getId(), newTaskActual.getId(), "не совпадают id");
        assertEquals(newTaskExpected.getName(), newTaskActual.getName(), "не совпадает name");
        assertEquals(newTaskExpected.getDescription(), newTaskActual.getDescription(), "не совпадают description");
        assertEquals(newTaskExpected.getStatus(), newTaskActual.getStatus(), "не совпадают status");
        assertEquals(newTaskExpected.getStartDateTime(), newTaskActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(newTaskExpected.getDuration(), newTaskActual.getDuration(), "не совпадает duration");
        assertEquals(newTaskExpected.getEndDateTime(), newTaskActual.getEndDateTime(), "не совпадает endTime");
    }

    @Test
    @DisplayName("Должен удалить старую задачу из сета, если у новой не задано время старта")
    void updateTask_removeOldTaskFromPrioritizedTasks_ifTheNewOneDoesNotHaveStartTimeSet() {
        //given
        Task immutableTask = manager.createTask(new Task("название задачи", "описание", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60)));

        Task taskForUpdate = manager.createTask(new Task("название задачи-1", "описание-1", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 18, 15), Duration.ofMinutes(60)));

        Task newTaskExpected = new Task("название задачи-2", "описание-2", Status.NEW,
                null, null);
        newTaskExpected.setId(2);

        //when
        manager.updateTask(newTaskExpected);
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();


        //then
        assertEquals(1, prioritizedTasks.size(), "старая задача не удалилась");
    }

    @Test
    @DisplayName("Должен выбрасывать исключение, если не может найти задачу")
    void updateTask_throwException_notFoundTask() {
        //given
        Task task = manager.createTask(new Task("название задачи", "описание", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));
        manager.deleteTaskById(1);

        //then
        assertThrows(NotFoundException.class, () -> manager.updateTask(task), "исключение не выброшено");
    }

    @Test
    @DisplayName("Должен поменять все поля кроме epicId у подзадачи на новые по id")
    void updateSubTask_changeAllFieldsExceptEpicId_subtasksHaveSameId() {

        //given
        Epic epic1 = manager.createEpic(new Epic("name", "description"));
        Epic epic2 = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic1.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask subTaskExpected = new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic2.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60));
        subTaskExpected.setId(3);

        //when
        manager.updateSubTask(subTaskExpected);
        SubTask subTaskActual = manager.getSubTaskById(3);

        //then
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "не совпадает name");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                "не совпадают description");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "не совпадают status");
        assertEquals(subTaskExpected.getStartDateTime(), subTaskActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(subTaskExpected.getDuration(), subTaskActual.getDuration(), "не совпадает duration");
        assertEquals(subTaskExpected.getEndDateTime(), subTaskActual.getEndDateTime(), "не совпадает endTime");

        assertNotEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "epicId не должен поменяться");
        assertEquals(subtask.getEpicId(), subTaskActual.getEpicId(), "epicId не должен поменяться");
    }

    @Test
    @DisplayName("Должен менять статус эпика на NEW, если статус всех его подзадач NEW")
    void updateSubTask_changeEpicsStatusToNEW_allItsSubtasksHaveStatusNEW() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Название подзадачи", Status.NEW, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subtask2 = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));
        Status statusBeforeChange = epic.getStatus();
        subtask2.setStatus(Status.NEW);

        //when
        manager.updateSubTask(subtask2);
        Status statusAfterChange = epic.getStatus();

        //then
        assertNotEquals(statusBeforeChange, statusAfterChange, "статус эпика не обновился");
        assertEquals(Status.NEW, epic.getStatus(), "статус эпика не поменялся");
    }

    @Test
    @DisplayName("Должен менять статус эпика на DONE, если статус всех его подзадач DONE")
    void updateSubTask_changeEpicsStatusToDONE_allItsSubtasksHaveStatusDONE() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Название подзадачи", Status.NEW, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subtask2 = manager.createSubTask(new SubTask("Название подзадачи", Status.DONE, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));
        Status statusBeforeChange = epic.getStatus();
        subtask1.setStatus(Status.DONE);

        //when
        manager.updateSubTask(subtask1);
        Status statusAfterChange = epic.getStatus();

        //then
        assertNotEquals(statusBeforeChange, statusAfterChange, "статус эпика не обновился");
        assertEquals(Status.DONE, epic.getStatus(), "статус эпика не поменялся");
    }

    @Test
    @DisplayName("Должен менять статус эпика на IN_PROGRESS, если статус его подзадач не одинаковый")
    void updateSubTask_changeEpicsStatusToINPROGRESS_statusOfItsSubtasksIsDiverse() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Название подзадачи", Status.NEW, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subtask2 = manager.createSubTask(new SubTask("Название подзадачи", Status.NEW, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));
        Status statusBeforeChange = epic.getStatus();
        subtask1.setStatus(Status.DONE);

        //when
        manager.updateSubTask(subtask1);
        Status statusAfterChange = epic.getStatus();

        //then
        assertNotEquals(statusBeforeChange, statusAfterChange, "статус эпика не обновился");
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "статус эпика не поменялся");
    }

    @Test
    @DisplayName("Должен выбросить исключение, если подзадачи пересекаются по времени")
    void updateSubTask_throwException_ifTheSubTasksOverlapInTime() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        SubTask immutableTask = manager.createSubTask(new SubTask("Название подзадачи-1", Status.IN_PROGRESS, "описание-1",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask taskForUpdateExpected = manager.createSubTask(new SubTask("Название подзадачи-2", Status.IN_PROGRESS, "описание-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 18, 17), Duration.ofMinutes(60)));

        SubTask newTask = new SubTask("Название подзадачи-2", Status.IN_PROGRESS, "описание-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 20), Duration.ofMinutes(60));
        newTask.setId(3);


        //then
        assertThrows(ValidationException.class, () -> manager.updateSubTask(newTask), "исключение не выброшено");
    }

    @Test
    @DisplayName("Не должен выбросить исключение при пересечении подзадачи по времени с самой собой")
    void updateSubTask_notThrowException_whenSubTaskIntersectsWithItselfInTime() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask oldTask = manager.createSubTask(new SubTask("Название подзадачи-2", Status.IN_PROGRESS, "описание-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask newTask = new SubTask("Название подзадачи-2", Status.IN_PROGRESS, "описание-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 20), Duration.ofMinutes(60));
        newTask.setId(2);


        //then
        assertDoesNotThrow(() -> manager.updateSubTask(newTask), "исключение не должно быть выброшено");
    }

    @Test
    @DisplayName("Должен удалить старую подзадачу из сета и добавить вместо нее новую с тем же id")
    void updateSubTask_removeOldSubTaskFromPrioritizedTasksAndAddNewOneWithTheSameIdInstead() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        SubTask immutableTask = manager.createSubTask(new SubTask("Название подзадачи-1", Status.IN_PROGRESS, "описание-1",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask taskForUpdate = manager.createSubTask(new SubTask("Название подзадачи-2", Status.IN_PROGRESS, "описание-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 18, 17), Duration.ofMinutes(60)));

        SubTask newTaskExpected = new SubTask("Название подзадачи-2", Status.IN_PROGRESS, "описание-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 17, 0), Duration.ofMinutes(60));
        newTaskExpected.setId(3);

        //when
        manager.updateSubTask(newTaskExpected);
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        SubTask newTaskActual = (SubTask) prioritizedTasks.get(1);

        //then
        assertEquals(2, prioritizedTasks.size(), "старая задача не удалилась");

        assertEquals(newTaskExpected.getId(), newTaskActual.getId(), "не совпадают id");
        assertEquals(newTaskExpected.getName(), newTaskActual.getName(), "не совпадает name");
        assertEquals(newTaskExpected.getDescription(), newTaskActual.getDescription(), "не совпадают description");
        assertEquals(newTaskExpected.getStatus(), newTaskActual.getStatus(), "не совпадают status");
        assertEquals(newTaskExpected.getStartDateTime(), newTaskActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(newTaskExpected.getDuration(), newTaskActual.getDuration(), "не совпадает duration");
        assertEquals(newTaskExpected.getEndDateTime(), newTaskActual.getEndDateTime(), "не совпадает endTime");

    }

    @Test
    @DisplayName("Должен удалить старую подзадачу из сета, если у новой не задано время старта")
    void updateSubTask_removeOldSubTaskFromPrioritizedTasks_ifTheNewOneDoesNotHaveStartTimeSet() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        SubTask immutableTask = manager.createSubTask(new SubTask("Название подзадачи-1", Status.IN_PROGRESS, "описание-1",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask taskForUpdate = manager.createSubTask(new SubTask("Название подзадачи-2", Status.IN_PROGRESS, "описание-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 18, 17), Duration.ofMinutes(60)));

        SubTask newTask = new SubTask("Название подзадачи-2", Status.IN_PROGRESS, "описание-2",
                epic.getId(), null, null);
        newTask.setId(3);

        //when
        manager.updateSubTask(newTask);
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        //then
        assertEquals(1, prioritizedTasks.size(), "старая задача не удалилась");

    }

    @Test
    @DisplayName("Должен менять поле duration у эпика, если оно меняется у подзадачи")
    void updateSubTask_changeFieldDurationForTheEpic_ifItChangesForTheSubtask() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        SubTask immutableTask = manager.createSubTask(new SubTask("Название подзадачи-1", Status.IN_PROGRESS, "описание-1",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask taskForUpdate = manager.createSubTask(new SubTask("Название подзадачи-2", Status.IN_PROGRESS, "описание-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 18, 17), Duration.ofMinutes(60)));

        SubTask newTaskExpected = new SubTask("Название подзадачи-2", Status.IN_PROGRESS, "описание-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 17, 0), Duration.ofMinutes(30));
        newTaskExpected.setId(3);

        Duration epicDurationBeforeChange = epic.getDuration();
        //when
        manager.updateSubTask(newTaskExpected);
        Duration epicDurationAfterChange = epic.getDuration();

        //then
        assertNotEquals(epicDurationBeforeChange, epicDurationAfterChange, "duration не поменялось");
        assertEquals((60 + 30) * 60, epicDurationAfterChange.getSeconds(), "duration поменялось некорректно");
    }

    @Test
    @DisplayName("Должен менять поле startDateTime у эпика, если оно меняется у подзадачи, " +
            "при условии, что startDateTime у подзадачи было или стало самым ранним из всех подзадач эпика")
    void updateSubTask_changeFieldStartDateTimeForTheEpic_ifItChangesForTheSubtaskAndItWasOrBecameTheEarliestTime() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        SubTask immutableTask = manager.createSubTask(new SubTask("Название подзадачи-1", Status.IN_PROGRESS, "описание-1",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask taskForUpdate = manager.createSubTask(new SubTask("Название подзадачи-2", Status.IN_PROGRESS, "описание-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 18, 17), Duration.ofMinutes(60)));

        SubTask newTaskExpected = new SubTask("Название подзадачи-2", Status.IN_PROGRESS, "описание-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 12, 0), Duration.ofMinutes(30));
        newTaskExpected.setId(3);

        LocalDateTime oldEpicStartDateTime = epic.getStartDateTime();

        //when
        manager.updateSubTask(newTaskExpected);
        LocalDateTime newEpicStartDateTime = epic.getStartDateTime();

        //then
        assertNotEquals(oldEpicStartDateTime, newEpicStartDateTime, "startDateTime не поменялось");
        assertEquals(newTaskExpected.getStartDateTime(), newEpicStartDateTime, "startDateTime поменялось некорректно");
        assertTrue(oldEpicStartDateTime.isAfter(newEpicStartDateTime), "startDateTime поменялось некорректно");
    }

    @Test
    @DisplayName("Должен менять поле endDateTime у эпика, если оно меняется у подзадачи, " +
            "при условии, что endDateTime у подзадачи было или стало самым поздним из всех подзадач эпика")
    void updateSubTask_changeFieldEndDateTimeForTheEpic_ifItChangesForTheSubtaskAndItWasOrBecameTheLatestTime() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        Epic epic1 = manager.createEpic(new Epic("name", "description"));

        SubTask immutableTask1 = manager.createSubTask(new SubTask("Название подзадачи-1", Status.IN_PROGRESS, "описание-1",
                epic1.getId(), LocalDateTime.of(2024, 6, 19, 20, 17), Duration.ofMinutes(60)));

        SubTask immutableTask = manager.createSubTask(new SubTask("Название подзадачи-1", Status.IN_PROGRESS, "описание-1",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask taskForUpdate = manager.createSubTask(new SubTask("Название подзадачи-2", Status.IN_PROGRESS, "описание-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 18, 17), Duration.ofMinutes(60)));

        SubTask newTaskExpected = new SubTask("Название подзадачи-2", Status.IN_PROGRESS, "описание-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 12, 0), Duration.ofMinutes(30));
        newTaskExpected.setId(5);

        LocalDateTime oldEpicEndDateTime = epic.getEndDateTime();

        //when
        manager.updateSubTask(newTaskExpected);
        LocalDateTime newEpicEndDateTime = epic.getEndDateTime();

        //then
        assertNotEquals(oldEpicEndDateTime, newEpicEndDateTime, "endDateTime не поменялось");
        assertEquals(immutableTask.getEndDateTime(), newEpicEndDateTime, "endDateTime поменялось некорректно");
        assertTrue(oldEpicEndDateTime.isAfter(newEpicEndDateTime), "endDateTime поменялось некорректно");
    }

    @Test
    @DisplayName("Должен выбрасывать исключение, если не может найти подзадачу")
    void updateSubTask_throwException_notFoundSubtask() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description1"));
        SubTask subtask = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic1.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        manager.deleteSubTaskById(2);

        //then
        assertThrows(NotFoundException.class, () -> manager.updateSubTask(subtask), "исключение не выброшено");
    }


    @Test
    @DisplayName("Должен поменять все поля кроме статуса у эпика на новые по id")
    void updateEpic_changeAllFieldsExceptStatus_epicHaveSameId() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        Epic epicExpected = new Epic("NewName", "NewDescription");
        epicExpected.setId(1);
        epicExpected.setStatus(Status.DONE);

        //when
        manager.updateEpic(epicExpected);
        Epic epicActual = manager.getEpicById(epic.getId());

        //then
        assertEquals(epicExpected.getName(), epicActual.getName(), "не совпадает name");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(), "не совпадают description");
        assertEquals(epicExpected.getStartDateTime(), epicActual.getStartDateTime(), "не совпадает startTime");
        assertEquals(epicExpected.getDuration(), epicActual.getDuration(), "не совпадает duration");
        assertEquals(epicExpected.getEndDateTime(), epicActual.getEndDateTime(), "не совпадает endTime");

        assertNotEquals(epicExpected.getStatus(), epicActual.getStatus(), "status не должен поменяться");
        assertEquals(epic.getStatus(), epicActual.getStatus(), "status не должен поменяться");
    }

    @Test
    @DisplayName("Должен выбрасывать исключение, если не может найти эпик")
    void updateEpic_throwException_notFoundEpic() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description1"));
        Epic epic2 = manager.createEpic(new Epic("name2", "description2"));
        manager.deleteEpicById(2);

        //then
        assertThrows(NotFoundException.class, () -> manager.updateEpic(epic2), "исключение не выброшено");
    }

    @Test
    @DisplayName("Должен удалять задачу из менеджера по id")
    void deleteTaskById_deleteTaskById() {
        //given
        Task task = manager.createTask(new Task("название задачи", "описание", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));


        //when
        manager.deleteTaskById(task.getId());

        //then
        assertThrows(NotFoundException.class, () -> manager.getTaskById(task.getId()), "задача не удалилась");
        assertEquals(0, manager.getTasksList().size(), "задача не удалилась");
    }

    @Test
    @DisplayName("Должен удалять задачу по id из истории")
    void deleteTaskById_deleteTaskByIdFromHistoryList() {
        //given
        Task task = manager.createTask(new Task("название задачи", "описание", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));

        manager.getTaskById(task.getId());

        //when
        manager.deleteTaskById(task.getId());
        List<Task> historyList = manager.getHistory();

        //then
        assertTrue(historyList.isEmpty(), "задача не удалилась");
    }

    @Test
    @DisplayName("Должен удалять задачу из prioritizedTasks")
    void deleteTaskById_deleteTaskFromPrioritizedTasks() {
        //given
        Task task = manager.createTask(new Task("название задачи", "описание", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60)));
        //when
        manager.deleteTaskById(task.getId());
        //then
        assertEquals(0, manager.getPrioritizedTasks().size(), "задача не удалилась");
    }

    @Test
    @DisplayName("Должен удалять подзадачу из менеджера по id")
    void deleteSubTaskById_deleteSubTaskById() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));


        //when
        manager.deleteSubTaskById(subtask.getId());

        //then
        assertThrows(NotFoundException.class, () -> manager.getSubTaskById(subtask.getId()), "подзадача не удалилась");
    }

    @Test
    @DisplayName("Должен удалять id подзадачи из списка в эпике")
    void deleteSubTaskById_deleteSubTasksIdFromEpic() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));


        //when
        manager.deleteSubTaskById(subtask.getId());
        List<Integer> subTasksId = epic.getSubTasksId();

        //then
        assertTrue(subTasksId.isEmpty(), "подзадача не удалилась");
    }

    @Test
    @DisplayName("Должен удалять подзадачу по id из истории")
    void deleteSubTaskById_deleteSubTaskByIdFromHistoryList() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        manager.getSubTaskById(subtask.getId());

        //when
        manager.deleteSubTaskById(subtask.getId());
        List<Task> historyList = manager.getHistory();

        //then
        assertTrue(historyList.isEmpty(), "подзадача не удалилась");
    }

    @Test
    @DisplayName("Должен удалять подзадачу из prioritizedTasks")
    void deleteSubTaskById_deleteSubTaskFromPrioritizedTasks() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subTask = manager.createSubTask(new SubTask("Название подзадачи-1", Status.IN_PROGRESS, "описание-1",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        //when
        manager.deleteSubTaskById(subTask.getId());

        //then
        assertEquals(0, manager.getPrioritizedTasks().size(), "подзадача не удалилась");
    }

    @Test
    @DisplayName("Должен удалять эпик из менеджера по id")
    void deleteEpicById_deleteEpicFromManagerById() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        //when
        manager.deleteEpicById(epic.getId());

        //then
        assertThrows(NotFoundException.class, () -> manager.getEpicById(epic.getId()), "эпик не удалился");
    }

    @Test
    @DisplayName("Должен удалять все подзадачи удаляемого эпика из менеджера по id эпика")
    void deleteEpicById_deleteAllEpicsSubTasksFromManager() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subtask2 = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));

        //when
        manager.deleteEpicById(epic.getId());

        //then

        assertThrows(NotFoundException.class, () -> manager.getSubTaskById(subtask1.getId()), "подзадача не удалилась");
        assertThrows(NotFoundException.class, () -> manager.getSubTaskById(subtask2.getId()), "подзадача не удалилась");
    }

    @Test
    @DisplayName("Должен удалять эпик и его подзадачи по id из истории")
    void deleteEpicById_deleteEpicByIdAndItsSubTasksFromHistoryList() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subtask1 = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));
        manager.getEpicById(epic.getId());
        manager.getSubTaskById(subtask.getId());
        manager.getSubTaskById(subtask1.getId());


        //when
        manager.deleteEpicById(epic.getId());
        List<Task> historyList = manager.getHistory();

        //then
        assertTrue(historyList.isEmpty(), "'эпик не удалился");
    }

    @Test
    @DisplayName("Должен возвращать список подзадач эпика")
    void getSubTasksByEpic_shouldGetListSubTasksByEpic() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask subtask2 = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));


        List<SubTask> listExpected = new ArrayList<>();
        listExpected.add(subtask1);
        listExpected.add(subtask2);

        //when
        List<SubTask> listActual = manager.getSubTasksByEpic(epic);

        //then
        assertEquals(listExpected.size(), listActual.size(), "списки разного размера");

        for (SubTask subTaskExpected : listExpected) {
            SubTask subTaskActual = listActual.get(listExpected.indexOf(subTaskExpected));

            assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "не совпадают id");
            assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "не совпадает name");
            assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                    "не совпадают description");
            assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "не совпадают status");
            assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "не совпадают epicId");
            assertEquals(subTaskExpected.getStartDateTime(), subTaskActual.getStartDateTime(), "не совпадает startTime");
            assertEquals(subTaskExpected.getDuration(), subTaskActual.getDuration(), "не совпадает duration");
            assertEquals(subTaskExpected.getEndDateTime(), subTaskActual.getEndDateTime(), "не совпадает endTime");
        }
    }

    @Test
    @DisplayName("Должен возвращать список задач")
    void getTasksList_returnTasksList() {

        //given
        Task task1 = manager.createTask(new Task("название задачи", "описание", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));
        Task task2 = manager.createTask(new Task("название задачи", "описание", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 16, 17), Duration.ofMinutes(60)));


        List<Task> listExpected = new ArrayList<>();
        listExpected.add(task1);
        listExpected.add(task2);

        //when
        List<Task> listActual = manager.getTasksList();

        //then
        assertEquals(listExpected.size(), listActual.size(), "списки разного размера");

        for (Task taskExpected : listExpected) {
            Task taskActual = listActual.get(listExpected.indexOf(taskExpected));

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
    @DisplayName("Должен возвращать список подзадач")
    void getSubTasksList_returnSubTasksList() {

        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description1"));
        Epic epic2 = manager.createEpic(new Epic("name2", "description2"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic1.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subtask2 = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic2.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));


        List<SubTask> listExpected = new ArrayList<>();
        listExpected.add(subtask1);
        listExpected.add(subtask2);

        //when
        List<SubTask> listActual = manager.getSubTasksList();

        //then
        assertEquals(listExpected.size(), listActual.size(), "списки разного размера");
        for (SubTask subTaskExpected : listExpected) {
            SubTask subTaskActual = listActual.get(listExpected.indexOf(subTaskExpected));

            assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "не совпадают id");
            assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "не совпадает name");
            assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                    "не совпадают description");
            assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "не совпадают status");
            assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "не совпадают epicId");
            assertEquals(subTaskExpected.getStartDateTime(), subTaskActual.getStartDateTime(), "не совпадает startTime");
            assertEquals(subTaskExpected.getDuration(), subTaskActual.getDuration(), "не совпадает duration");
            assertEquals(subTaskExpected.getEndDateTime(), subTaskActual.getEndDateTime(), "не совпадает endTime");
        }
    }

    @Test
    @DisplayName("Должен возвращать список эпиков")
    void getEpicList_returnEpicList() {

        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description1"));
        Epic epic2 = manager.createEpic(new Epic("name2", "description2"));

        List<Epic> listExpected = new ArrayList<>();
        listExpected.add(epic1);
        listExpected.add(epic2);

        //when
        List<Epic> listActual = manager.getEpicList();

        //then
        assertEquals(listExpected.size(), listActual.size(), "списки разного размера");

        for (Epic epicExpected : listExpected) {
            Epic epicActual = listActual.get(listExpected.indexOf(epicExpected));

            assertEquals(epicExpected.getId(), epicActual.getId(), "не совпадают id");
            assertEquals(epicExpected.getName(), epicActual.getName(), "не совпадает name");
            assertEquals(epicExpected.getDescription(), epicActual.getDescription(),
                    "не совпадают description");
            assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "не совпадают status");
            assertEquals(epicExpected.getStartDateTime(), epicActual.getStartDateTime(), "не совпадает startTime");
            assertEquals(epicExpected.getDuration(), epicActual.getDuration(), "не совпадает duration");
            assertEquals(epicExpected.getEndDateTime(), epicActual.getEndDateTime(), "не совпадает endTime");
        }
    }

    @Test
    @DisplayName("Должен удалять все задачи")
    void deleteAllTasks_deleteAllTasks() {
        //given
        Task task1 = manager.createTask(new Task("название задачи", "описание", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));

        Task task2 = manager.createTask(new Task("название задачи", "описание", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 16, 17), Duration.ofMinutes(60)));


        //when
        manager.deleteAllTasks();
        List<Task> tasksList = manager.getTasksList();

        //then
        assertTrue(tasksList.isEmpty(), "задачи не удалились");
    }

    @Test
    @DisplayName("Должен удалять все задачи из истории")
    void deleteAllTasks_deleteAllTasksFromHistoryList() {
        //given
        Task task1 = manager.createTask(new Task("название задачи", "описание", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));
        Task task2 = manager.createTask(new Task("название задачи", "описание", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 15, 17), Duration.ofMinutes(60)));
        Task task3 = manager.createTask(new Task("название задачи", "описание", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 17, 17), Duration.ofMinutes(60)));
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getTaskById(task3.getId());

        //when
        manager.deleteAllTasks();
        List<Task> historyList = manager.getHistory();

        //then
        assertTrue(historyList.isEmpty(), "задачи не удалились");
    }

    @Test
    @DisplayName("Должен удалять все Task из prioritizedTasks")
    void deleteAllTasks_deleteAllTasksFromPrioritizedTasks() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        SubTask subTask = manager.createSubTask(new SubTask("Название подзадачи-1", Status.IN_PROGRESS,
                "описание-1", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        Task task1 = manager.createTask(new Task("название задачи", "описание", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60)));
        Task task2 = manager.createTask(new Task("название задачи-1", "описание-1", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 18, 15), Duration.ofMinutes(60)));

        //when
        manager.deleteAllTasks();

        //then

        assertEquals(1, manager.getPrioritizedTasks().size(), "задачи не удалилась");
        assertEquals(subTask.getType(), manager.getPrioritizedTasks().getFirst().getType(), "задачи удалились некорректно");
    }

    @Test
    @DisplayName("Должен удалять все подзадачи из менеджера")
    void deleteAllSubTasks_deleteAllSubTasksFromManager() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description1"));
        Epic epic2 = manager.createEpic(new Epic("name2", "description2"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic1.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask subtask2 = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic2.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));


        //when
        manager.deleteAllSubTasks();
        List<SubTask> subTasksList = manager.getSubTasksList();

        //then
        assertTrue(subTasksList.isEmpty(), "задачи не удалились");
    }

    @Test
    @DisplayName("Должен очищать листы с id подзадач в эпиках")
    void deleteAllSubTasks_clearSubTasksIdListsInEpics() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description1"));
        Epic epic2 = manager.createEpic(new Epic("name2", "description2"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic1.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subtask2 = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic2.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));


        //when
        manager.deleteAllSubTasks();
        List<Integer> subTasksIdInEpic1 = epic1.getSubTasksId();
        List<Integer> subTasksIdInEpic2 = epic2.getSubTasksId();

        //then
        assertTrue(subTasksIdInEpic1.isEmpty(), "задачи не удалились");
        assertTrue(subTasksIdInEpic2.isEmpty(), "задачи не удалились");
    }

    @Test
    @DisplayName("Должен удалять все подзадачи из истории")
    void deleteAllSubTasks_deleteAllSubTasksFromHistoryList() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask subtask2 = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));

        SubTask subtask3 = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 18, 17), Duration.ofMinutes(60)));

        manager.getSubTaskById(subtask1.getId());
        manager.getSubTaskById(subtask2.getId());
        manager.getSubTaskById(subtask3.getId());

        //when
        manager.deleteAllSubTasks();
        List<Task> historyList = manager.getHistory();

        //then
        assertTrue(historyList.isEmpty(), "задачи не удалились");
    }

    @Test
    @DisplayName("Должен удалять все SubTask из prioritizedTasks")
    void deleteAllSubTasks_deleteAllSubTasksFromPrioritizedTasks() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        SubTask subTask1 = manager.createSubTask(new SubTask("Название подзадачи-1", Status.IN_PROGRESS,
                "описание-1", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subTask2 = manager.createSubTask(new SubTask("Название подзадачи-2", Status.IN_PROGRESS,
                "описание-2", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 15, 17), Duration.ofMinutes(40)));

        Task task1 = manager.createTask(new Task("название задачи", "описание", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60)));

        //when
        manager.deleteAllSubTasks();

        //then
        assertEquals(1, manager.getPrioritizedTasks().size(), "подзадачи не удалилась");
        assertEquals(task1.getType(), manager.getPrioritizedTasks().getFirst().getType(), "подзадачи удалились некорректно");
    }

    @Test
    @DisplayName("Должен удалять все эпики")
    void deleteAllEpics_deleteAllEpics() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description1"));
        Epic epic2 = manager.createEpic(new Epic("name2", "description2"));

        //when
        manager.deleteAllEpics();
        List<Epic> epicList = manager.getEpicList();

        //then
        assertTrue(epicList.isEmpty(), "задачи не удалились");
    }

    @Test
    @DisplayName("Должен удалять все подзадачи")
    void deleteAllEpics_deleteAllSubTasks() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description1"));
        Epic epic2 = manager.createEpic(new Epic("name2", "description2"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic1.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subtask2 = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic2.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));

        //when
        manager.deleteAllEpics();
        List<SubTask> subTasksList = manager.getSubTasksList();

        //then
        assertTrue(subTasksList.isEmpty(), "задачи не удалились");
    }

    @Test
    @DisplayName("Должен удалять все эпики и их подзадачи из истории")
    void deleteAllEpics_deleteAllEpicsAndItsSubTasksFromHistoryList() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description"));
        Epic epic2 = manager.createEpic(new Epic("name2", "description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic1.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask subtask2 = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic1.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));

        SubTask subtask3 = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic2.getId(), LocalDateTime.of(2024, 6, 19, 18, 17), Duration.ofMinutes(60)));

        manager.getEpicById(epic1.getId());
        manager.getEpicById(epic2.getId());
        manager.getSubTaskById(subtask1.getId());
        manager.getSubTaskById(subtask2.getId());
        manager.getSubTaskById(subtask3.getId());

        //when
        manager.deleteAllEpics();
        List<Task> historyList = manager.getHistory();

        //then
        assertTrue(historyList.isEmpty(), "эпики не удалились");
    }

    @Test
    @DisplayName("Должен удалять все SubTask из prioritizedTasks")
    void deleteAllEpics_deleteAllSubTasksFromPrioritizedTasks() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        SubTask subTask1 = manager.createSubTask(new SubTask("Название подзадачи-1", Status.IN_PROGRESS,
                "описание-1", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subTask2 = manager.createSubTask(new SubTask("Название подзадачи-2", Status.IN_PROGRESS,
                "описание-2", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 15, 17), Duration.ofMinutes(40)));

        Task task1 = manager.createTask(new Task("название задачи", "описание", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60)));

        //when
        manager.deleteAllEpics();

        //then
        assertEquals(1, manager.getPrioritizedTasks().size(), "подзадачи не удалилась");
        assertEquals(task1.getType(), manager.getPrioritizedTasks().getFirst().getType(), "подзадачи удалились некорректно");
    }

    @Test
    @DisplayName("Должен возвращать лист с отсортированными по времени задачами")
    void getPrioritizedTasks_returnSortedByTimeTasksList() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        SubTask subTask1 = manager.createSubTask(new SubTask("Название подзадачи-1", Status.IN_PROGRESS, //id=2, место - 2
                "описание-1", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subTask2 = manager.createSubTask(new SubTask("Название подзадачи-2", Status.IN_PROGRESS, //id=3, место - 3
                "описание-2", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 15, 17), Duration.ofMinutes(40)));

        Task task1 = manager.createTask(new Task("название задачи", "описание", Status.NEW,    //id=4, место - 1
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60)));

        //when
        List<Task> list = manager.getPrioritizedTasks();

        //then
        assertEquals(4, list.getFirst().getId(), "задача не на своем месте");
        assertEquals(2, list.get(1).getId(), "задача не на своем месте");
        assertEquals(3, list.getLast().getId(), "задача не на своем месте");
    }
}