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


@DisplayName("In Memory Task Manager")
class InMemoryTaskManagerTest {

    protected TaskManager manager;

    @BeforeEach
    void init() {
        manager = Managers.getDefaults();
        manager.deleteAllEpics();
        manager.deleteAllTasks();
        manager.deleteAllSubTasks();
    }

    @Test
    @DisplayName("Should return a task by ID from the manager")
    void getTaskById_returnTaskById() {
        //given
        Task task = manager.createTask(new Task("Task's name", "description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));
        Task taskExpected = new Task("Task's name", "description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60));
        taskExpected.setId(1);

        //when
        Task taskActual = manager.getTaskById(task.getId());

        //then
        assertEquals(taskExpected.getId(), taskActual.getId(), "IDs do not match");
        assertEquals(taskExpected.getName(), taskActual.getName(), "Names do not match");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "Descriptions do not match");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "Statuses do not match");
        assertEquals(taskExpected.getStartDateTime(), taskActual.getStartDateTime(), "Start time does not match");
        assertEquals(taskExpected.getDuration(), taskActual.getDuration(), "Duration does not match");
        assertEquals(taskExpected.getEndDateTime(), taskActual.getEndDateTime(), "End time does not match");
    }

    @Test
    @DisplayName("Should add a task to the history")
    void getTaskById_addTaskToHistory() {
        //given
        Task taskExpected = manager.createTask(new Task("Task's name", "description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));

        //when
        manager.getTaskById(taskExpected.getId());
        List<Task> historyList = manager.getHistory();
        Task taskActual = historyList.getFirst();

        //then
        assertEquals(taskExpected.getId(), taskActual.getId(), "IDs do not match");
        assertEquals(taskExpected.getName(), taskActual.getName(), "Names do not match");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "Descriptions do not match");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "Statuses do not match");
        assertEquals(taskExpected.getStartDateTime(), taskActual.getStartDateTime(), "Start time does not match");
        assertEquals(taskExpected.getDuration(), taskActual.getDuration(), "Duration does not match");
        assertEquals(taskExpected.getEndDateTime(), taskActual.getEndDateTime(), "End time does not match");
    }

    @Test
    @DisplayName("Should throw an exception if the task cannot be found")
    void getTaskById_throwException_notFoundTask() {
        //given
        Task task = manager.createTask(new Task("Task's name", "description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));

        //then
        assertThrows(NotFoundException.class, () -> manager.getTaskById(2), "Exception was not thrown");
    }

    @Test
    @DisplayName("Should return a subtask by ID from the manager")
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
        assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "IDs do not match");
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "Names do not match");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                "Descriptions do not match");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "Statuses do not match");
        assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "Epic IDs do not match");
        assertEquals(subTaskExpected.getStartDateTime(), subTaskActual.getStartDateTime(), "Start time does not match");
        assertEquals(subTaskExpected.getDuration(), subTaskActual.getDuration(), "Duration does not match");
        assertEquals(subTaskExpected.getEndDateTime(), subTaskActual.getEndDateTime(), "End time does not match");
    }

    @Test
    @DisplayName("Should add a subtask to the history")
    void getSubTaskById_addSubTaskToHistory() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subTaskExpected = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        //when
        manager.getSubTaskById(subTaskExpected.getId());

        List<Task> historyList = manager.getHistory();
        SubTask subTaskActual = (SubTask) historyList.get(0);

        //then
        assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "IDs do not match");
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "Names do not match");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                "Descriptions do not match");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "Statuses do not match");
        assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "Epic IDs do not match");
        assertEquals(subTaskExpected.getStartDateTime(), subTaskActual.getStartDateTime(), "Start time does not match");
        assertEquals(subTaskExpected.getDuration(), subTaskActual.getDuration(), "Duration does not match");
        assertEquals(subTaskExpected.getEndDateTime(), subTaskActual.getEndDateTime(), "End time does not match");
    }

    @Test
    @DisplayName("Should throw an exception if the subtask cannot be found")
    void getSubTaskById_throwException_notFoundSubTask() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        manager.deleteEpicById(1);


        //then
        assertThrows(NotFoundException.class, () -> manager.getSubTaskById(2), "Exception was not thrown");
    }

    @Test
    @DisplayName("Should return an epic by ID from the manager")
    void getEpicById_returnEpicById() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        Epic epicExpected = new Epic("name", "description");
        epicExpected.setId(1);
        epicExpected.setStatus(Status.NEW);

        //when
        Epic epicActual = manager.getEpicById(epic.getId());

        //then
        assertEquals(epicExpected.getId(), epicActual.getId(), "IDs do not match");
        assertEquals(epicExpected.getName(), epicActual.getName(), "Names do not match");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(), "Descriptions do not match");
        assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "Statuses do not match");
        assertEquals(epicExpected.getStartDateTime(), epicActual.getStartDateTime(), "Start time does not match");
        assertEquals(epicExpected.getDuration(), epicActual.getDuration(), "Duration does not match");
        assertEquals(epicExpected.getEndDateTime(), epicActual.getEndDateTime(), "End time does not match");
    }

    @Test
    @DisplayName("Should add an epic to the history")
    void getEpicById_addEpicToHistory() {
        //given
        Epic epicExpected = manager.createEpic(new Epic("name", "description"));

        //when
        manager.getEpicById(epicExpected.getId());

        List<Task> historyList = manager.getHistory();
        Epic epicActual = (Epic) historyList.get(0);

        //then
        assertEquals(epicExpected.getId(), epicActual.getId(), "IDs do not match");
        assertEquals(epicExpected.getName(), epicActual.getName(), "Names do not match");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(), "Descriptions do not match");
        assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "Statuses do not match");
        assertEquals(epicExpected.getStartDateTime(), epicActual.getStartDateTime(), "Start time does not match");
        assertEquals(epicExpected.getDuration(), epicActual.getDuration(), "Duration does not match");
        assertEquals(epicExpected.getEndDateTime(), epicActual.getEndDateTime(), "End time does not match");
    }

    @Test
    @DisplayName("Should throw an exception if the epic cannot be found")
    void getEpicById_throwException_notFoundEpic() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        manager.deleteEpicById(1);


        //then
        assertThrows(NotFoundException.class, () -> manager.getEpicById(1), "Exception was not thrown");
    }

    @Test
    @DisplayName("Should return a task with a generated ID")
    void createTask_returnTaskWithGeneratedId() {
        //given
        Task task = new Task("Task's name", "description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60));
        task.setId(5);

        //when
        manager.createTask(task);

        //then
        assertEquals(1, task.getId(), "Unique ID was not generated");
    }

    @Test
    @DisplayName("Should add a task to the manager")
    void createTask_putTaskInManager() {
        //given
        Task taskExpected = new Task("Task's name", "description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60));
        taskExpected.setId(1);

        //when
        manager.createTask(taskExpected);
        List<Task> taskList = manager.getTasksList();
        Task taskActual = taskList.getFirst();

        //then
        assertEquals(taskExpected.getId(), taskActual.getId(), "IDs do not match");
        assertEquals(taskExpected.getName(), taskActual.getName(), "Names do not match");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "Descriptions do not match");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "Statuses do not match");
        assertEquals(taskExpected.getStartDateTime(), taskActual.getStartDateTime(), "Start time does not match");
        assertEquals(taskExpected.getDuration(), taskActual.getDuration(), "Duration does not match");
        assertEquals(taskExpected.getEndDateTime(), taskActual.getEndDateTime(), "End time does not match");
    }

    @Test
    @DisplayName("Should add a task to the prioritized task list if startDateTime is set")
    void createTask_putTaskInPrioritizedSet_startDateTimeIsSet() {
        //given
        Task taskExpected = new Task("name", "description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        taskExpected.setId(1);

        //when
        manager.createTask(taskExpected);

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        Task taskActual = prioritizedTasks.getFirst();

        //then
        assertEquals(taskExpected.getId(), taskActual.getId(), "IDs do not match");
        assertEquals(taskExpected.getName(), taskActual.getName(), "Names do not match");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "Descriptions do not match");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "Statuses do not match");
        assertEquals(taskExpected.getStartDateTime(), taskActual.getStartDateTime(), "Start time does not match");
        assertEquals(taskExpected.getDuration(), taskActual.getDuration(), "Duration does not match");
        assertEquals(taskExpected.getEndDateTime(), taskActual.getEndDateTime(), "End time does not match");
    }

    @Test
    @DisplayName("Should throw an exception if tasks overlap in time")
    void createTask_throwException_ifTheTasksOverlapInTime() {
        //given
        Task task1 = new Task("Task's name-1", "description-1", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60));
        Task task2 = new Task("Task's name-2", "description-2", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 13, 0), Duration.ofMinutes(60));

        //when
        manager.createTask(task1);

        //then
        assertThrows(ValidationException.class, () -> manager.createTask(task2), "Exception was not thrown");
    }

    @Test
    @DisplayName("Should not add a task to the prioritized task set if it overlaps in time with a task already in the set")
    void createTask_notAddTaskToPrioritizedTasksSet_ifTheTasksOverlapInTime() {
        //given
        Task task1 = new Task("Task's name-1", "description-1", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60));
        Task task2 = new Task("Task's name-2", "description-2", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 13, 0), Duration.ofMinutes(60));

        //when
        manager.createTask(task1);

        //then
        assertThrows(ValidationException.class, () -> manager.createTask(task2), "Exception was not thrown");
        List<Task> list = manager.getPrioritizedTasks();
        assertEquals(1, list.size(), "The second task should not be added to the set");
    }

    @Test
    @DisplayName("Should not throw an exception for boundary values")
    void createTask_NotThrowExceptionAtBoundaryValues() {
        //given
        Task task1 = new Task("Task's name-1", "description-1", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60));
        Task task2 = new Task("Task's name-2", "description-2", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 13, 15), Duration.ofMinutes(60));

        //when
        manager.createTask(task1);

        //then
        assertDoesNotThrow(() -> manager.createTask(task2), "No exception should be thrown");
    }

    @Test
    @DisplayName("Should not add a task to the prioritized task set if its start time is not specified")
    void createTask_notAddTaskToPrioritizedTasksSet_ifTaskDoesNotHaveSpecifiedStartingTime() {
        //given
        Task task1 = new Task("Task's name-1", "description-1", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60));
        Task task2 = new Task("Task's name-2", "description-2", Status.NEW,
                null, null);

        //when
        manager.createTask(task1);
        manager.createTask(task2);
        List<Task> list = manager.getPrioritizedTasks();

        //then
        assertEquals(1, list.size(), "The second task should not be added to the set");
    }

    @Test
    @DisplayName("Should return a subtask with a generated ID")
    void createSubTask_returnSubTaskWithGeneratedId() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60));
        subtask.setId(5);

        //when
        manager.createSubTask(subtask);

        //then
        assertEquals(2, subtask.getId(), "Unique ID was not generated");
    }

    @Test
    @DisplayName("Should add a subtask to the manager")
    void createSubTask_putSubTaskInManager() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subTaskExpected = new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60));
        subTaskExpected.setId(2);

        //when
        manager.createSubTask(subTaskExpected);
        List<SubTask> subTaskList = manager.getSubTasksList();
        SubTask subTaskActual = subTaskList.getFirst();

        //then
        assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "IDs do not match");
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "Names do not match");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                "Descriptions do not match");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "Statuses do not match");
        assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "Epic IDs do not match");
        assertEquals(subTaskExpected.getStartDateTime(), subTaskActual.getStartDateTime(), "Start time does not match");
        assertEquals(subTaskExpected.getDuration(), subTaskActual.getDuration(), "Duration does not match");
        assertEquals(subTaskExpected.getEndDateTime(), subTaskActual.getEndDateTime(), "End time does not match");
    }

    @Test
    @DisplayName("Should store the subtask ID in its epic")
    void createSubTask_putIdToListInItsEpic() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subTask = new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60));
        //when
        manager.createSubTask(subTask);
        List<Integer> subTasksIdList = epic.getSubTasksId();

        //then
        assertEquals(2, subTasksIdList.getFirst(), "ID was not saved");
    }

    @Test
    @DisplayName("Should add a subtask to the prioritized task list if startDateTime is set")
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
        assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "IDs do not match");
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "Names do not match");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(), "Descriptions do not match");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "Statuses do not match");
        assertEquals(subTaskExpected.getStartDateTime(), subTaskActual.getStartDateTime(), "Start time does not match");
        assertEquals(subTaskExpected.getDuration(), subTaskActual.getDuration(), "Duration does not match");
        assertEquals(subTaskExpected.getEndDateTime(), subTaskActual.getEndDateTime(), "End time does not match");
        assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "Epic IDs do not match");
    }

    @Test
    @DisplayName("Should throw an exception if tasks overlap in time")
    void createSubTask_throwException_ifTheTasksOverlapInTime() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name", "description"));
        SubTask subTask1 = new SubTask("Task's name-1", Status.NEW, "description-1", epic1.getId(),
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60));
        SubTask subTask2 = new SubTask("Task's name-2", Status.NEW, "description-2", epic1.getId(),
                LocalDateTime.of(2024, 6, 19, 12, 20), Duration.ofMinutes(30));

        //when
        manager.createSubTask(subTask1);

        //then
        assertThrows(ValidationException.class, () -> manager.createSubTask(subTask2), "Exception was not thrown");
    }

    @Test
    @DisplayName("Should not add a subtask to the prioritized task set if it overlaps in time with a task already in the set")
    void createSubTask_notAddSubTaskToPrioritizedTasksSet_ifTheTasksOverlapInTime() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name", "description"));
        SubTask subTask1 = new SubTask("Task's name-1", Status.NEW, "description-1", epic1.getId(),
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60));
        SubTask subTask2 = new SubTask("Task's name-2", Status.NEW, "description-2", epic1.getId(),
                LocalDateTime.of(2024, 6, 19, 12, 20), Duration.ofMinutes(30));

        //when
        manager.createSubTask(subTask1);

        //then
        assertThrows(ValidationException.class, () -> manager.createSubTask(subTask2), "Exception was not thrown");
        List<Task> list = manager.getPrioritizedTasks();
        assertEquals(1, list.size(), "The second subtask should not be added to the set");
    }

    @Test
    @DisplayName("No exception should be thrown for edge cases")
    void createSubTask_NotThrowExceptionAtBoundaryValues() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name", "description"));
        SubTask subTask1 = new SubTask("Task's name-1", Status.NEW, "description-1", epic1.getId(),
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60));
        SubTask subTask2 = new SubTask("Task's name-2", Status.NEW, "description-2", epic1.getId(),
                LocalDateTime.of(2024, 6, 19, 13, 15), Duration.ofMinutes(30));

        //when
        manager.createSubTask(subTask1);

        //then
        assertDoesNotThrow(() -> manager.createSubTask(subTask2), "No exception should be thrown");
    }

    @Test
    @DisplayName("Should not add a task to the prioritized task set if its start time is not specified")
    void createSubTask_notAddSubTaskToPrioritizedTasksSet_ifSubTaskDoesNotHaveSpecifiedStartingTime() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name", "description"));
        SubTask subTask1 = new SubTask("Task's name-1", Status.NEW, "description-1", epic1.getId(),
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60));
        SubTask subTask2 = new SubTask("Task's name-2", Status.NEW, "description-2", epic1.getId(),
                null, null);

        //when
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        List<Task> list = manager.getPrioritizedTasks();

        //then
        assertEquals(1, list.size(), "The second subtask should not be added to the set");
    }

    @Test
    @DisplayName("Should update the duration field of the epic to which the subtask belongs if its start time is set")
    void createSubTask_updateFieldDurationOfEpic_ifSubTaskHaveSpecifiedStartingTime() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        Duration duration = epic.getDuration();

        SubTask subTask1 = new SubTask("Task's name-1", Status.NEW, "description-1", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60));
        SubTask subTask2 = new SubTask("Task's name-2", Status.NEW, "description-2", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 13, 15), Duration.ofMinutes(30));

        //when
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        //then
        assertNotEquals(duration, epic.getDuration(), "Duration did not change");
        assertEquals((60 + 30) * 60, epic.getDuration().getSeconds(), "Duration changed incorrectly");
    }

    @Test
    @DisplayName("Should update the startDateTime field of the epic to which the subtask belongs if its start time is set " +
            "and it is earlier than all other subtasks of that epic")
    void createSubTask_updateFieldStartDateTimeOfEpic_ifSubTaskHaveTheEarliestSpecifiedStartingTime() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        LocalDateTime startDateTime = epic.getStartDateTime();

        SubTask subTask1 = new SubTask("Task's name-1", Status.NEW, "description-1", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 15, 15), Duration.ofMinutes(60));
        SubTask earliestSubTask = new SubTask("Task's name-2", Status.NEW, "description-2", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60));

        //when
        manager.createSubTask(subTask1);
        manager.createSubTask(earliestSubTask);

        //then
        assertNotEquals(startDateTime, epic.getStartDateTime(), "The startDateTime field did not change");
        assertEquals(earliestSubTask.getStartDateTime(), epic.getStartDateTime(), "The startDateTime field changed incorrectly");
    }

    @Test
    @DisplayName("Should update the endDateTime field of the epic to which the subtask belongs if its start time is set " +
            "and the subtask’s endDateTime is later than all other subtasks of that epic")
    void createSubTask_updateFieldEndDateTimeOfEpic_ifSubTaskHaveSpecifiedStartingTimeAndTheLatestEndDateTime() {
        //given

        Epic epic = manager.createEpic(new Epic("name", "description"));
        LocalDateTime endDateTime = epic.getEndDateTime();

        SubTask subTask1 = new SubTask("Task's name-1", Status.NEW, "description-1", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 15, 15), Duration.ofMinutes(60));
        SubTask latestSubTask = new SubTask("Task's name-2", Status.NEW, "description-2", epic.getId(),
                LocalDateTime.of(2024, 6, 20, 12, 15), Duration.ofMinutes(60));

        //when
        manager.createSubTask(subTask1);
        manager.createSubTask(latestSubTask);

        //then
        assertNotEquals(endDateTime, epic.getEndDateTime(), "The endDateTime field did not change");
        assertEquals(latestSubTask.getEndDateTime(), epic.getEndDateTime(), "The endDateTime field changed incorrectly");
    }

    @Test
    @DisplayName("Should return an epic with a generated ID")
    void createEpic_returnEpicWithGeneratedId() {

        //given
        Epic epic = new Epic("name", "description");
        epic.setId(5);

        //when
        manager.createEpic(epic);

        //then
        assertEquals(1, epic.getId(), "Unique ID was not generated");
    }

    @Test
    @DisplayName("Should add an epic to the manager")
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
        assertEquals(epicExpected.getId(), epicActual.getId(), "IDs do not match");
        assertEquals(epicExpected.getName(), epicActual.getName(), "Names do not match");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(), "Descriptions do not match");
        assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "Statuses do not match");
        assertEquals(epicExpected.getStartDateTime(), epicActual.getStartDateTime(), "Start time does not match");
        assertEquals(epicExpected.getDuration(), epicActual.getDuration(), "Duration does not match");
        assertEquals(epicExpected.getEndDateTime(), epicActual.getEndDateTime(), "End time does not match");
    }

    @Test
    @DisplayName("Should set the epic status to NEW")
    void createEpic_makeStatusNew() {
        //given
        Epic epic = new Epic("name", "description");

        //when
        manager.createEpic(epic);

        //then
        assertEquals(Status.NEW, epic.getStatus(), "NEW status was not set");
    }

    @Test
    @DisplayName("Should replace a task with a new one by ID")
    void updateTask_changeTaskToNewOne_tasksHaveSameId() {

        //given
        Task task = manager.createTask(new Task("name", "description", Status.DONE));
        Task taskExpected = new Task("NewName", "NewDescription", Status.IN_PROGRESS);
        taskExpected.setId(1);

        //when
        manager.updateTask(taskExpected);
        Task taskActual = manager.getTaskById(task.getId());

        //then
        assertEquals(taskExpected.getName(), taskActual.getName(), "Names do not match");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "Descriptions do not match");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "Statuses do not match");
        assertEquals(taskExpected.getStartDateTime(), taskActual.getStartDateTime(), "Start time does not match");
        assertEquals(taskExpected.getDuration(), taskActual.getDuration(), "Duration does not match");
        assertEquals(taskExpected.getEndDateTime(), taskActual.getEndDateTime(), "End time does not match");
    }

    @Test
    @DisplayName("Should throw an exception if tasks overlap in time")
    void updateTask_throwException_ifTheTasksOverlapInTime() {
        //given
        Task immutableTask = manager.createTask(new Task("Task's name", "description", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60)));

        Task taskForUpdateExpected = manager.createTask(new Task("Task's name-1", "description-1", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 18, 15), Duration.ofMinutes(60)));

        Task newTask = new Task("Task's name-2", "description-2", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 13, 5), Duration.ofMinutes(60));
        newTask.setId(2);

        //then
        assertThrows(ValidationException.class, () -> manager.updateTask(newTask), "Exception was not thrown");
    }

    @Test
    @DisplayName("No exception should be thrown when a task overlaps with itself")
    void updateTask_notThrowException_whenTaskIntersectsWithItselfInTime() {
        //given
        Task oldTask = manager.createTask(new Task("Task's name-1", "description-1", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60)));

        Task newTask = new Task("Task's name-2", "description-2", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 13, 5), Duration.ofMinutes(60));
        newTask.setId(1);

        //then
        assertDoesNotThrow(() -> manager.updateTask(newTask), "Exception should not be thrown");
    }

    @Test
    @DisplayName("Should remove the old task from the set and add a new one with the same ID")
    void updateTask_removeOldTaskFromPrioritizedTasksAndAddNewOneWithTheSameIdInstead() {
        //given
        Task immutableTask = manager.createTask(new Task("Task's name", "description", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60)));

        Task taskForUpdate = manager.createTask(new Task("Task's name-1", "description-1", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 18, 15), Duration.ofMinutes(60)));

        Task newTaskExpected = new Task("Task's name-2", "description-2", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 18, 0), Duration.ofMinutes(30));
        newTaskExpected.setId(2);

        //when
        manager.updateTask(newTaskExpected);
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        Task newTaskActual = prioritizedTasks.get(1);

        //then
        assertEquals(2, prioritizedTasks.size(), "The old task was not deleted");

        assertEquals(newTaskExpected.getId(), newTaskActual.getId(), "IDs do not match");
        assertEquals(newTaskExpected.getName(), newTaskActual.getName(), "Names do not match");
        assertEquals(newTaskExpected.getDescription(), newTaskActual.getDescription(), "Descriptions do not match");
        assertEquals(newTaskExpected.getStatus(), newTaskActual.getStatus(), "Statuses do not match");
        assertEquals(newTaskExpected.getStartDateTime(), newTaskActual.getStartDateTime(), "Start time does not match");
        assertEquals(newTaskExpected.getDuration(), newTaskActual.getDuration(), "Duration does not match");
        assertEquals(newTaskExpected.getEndDateTime(), newTaskActual.getEndDateTime(), "End time does not match");
    }

    @Test
    @DisplayName("Should remove the old task from the set if the new one has no start time set")
    void updateTask_removeOldTaskFromPrioritizedTasks_ifTheNewOneDoesNotHaveStartTimeSet() {
        //given
        Task immutableTask = manager.createTask(new Task("Task's name", "description", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60)));

        Task taskForUpdate = manager.createTask(new Task("Task's name-1", "description-1", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 18, 15), Duration.ofMinutes(60)));

        Task newTaskExpected = new Task("Task's name-2", "description-2", Status.NEW,
                null, null);
        newTaskExpected.setId(2);

        //when
        manager.updateTask(newTaskExpected);
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();


        //then
        assertEquals(1, prioritizedTasks.size(), "The old task was not deleted");
    }

    @Test
    @DisplayName("Should throw an exception if the task cannot be found")
    void updateTask_throwException_notFoundTask() {
        //given
        Task task = manager.createTask(new Task("Task's name", "description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));
        manager.deleteTaskById(1);

        //then
        assertThrows(NotFoundException.class, () -> manager.updateTask(task), "Exception was not thrown");
    }

    @Test
    @DisplayName("Should update all fields of a subtask except epicId by ID")
    void updateSubTask_changeAllFieldsExceptEpicId_subtasksHaveSameId() {

        //given
        Epic epic1 = manager.createEpic(new Epic("name", "description"));
        Epic epic2 = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic1.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask subTaskExpected = new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic2.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60));
        subTaskExpected.setId(3);

        //when
        manager.updateSubTask(subTaskExpected);
        SubTask subTaskActual = manager.getSubTaskById(3);

        //then
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "Names do not match");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                "Descriptions do not match");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "Statuses do not match");
        assertEquals(subTaskExpected.getStartDateTime(), subTaskActual.getStartDateTime(), "Start time does not match");
        assertEquals(subTaskExpected.getDuration(), subTaskActual.getDuration(), "Duration does not match");
        assertEquals(subTaskExpected.getEndDateTime(), subTaskActual.getEndDateTime(), "End time does not match");

        assertNotEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "Epic ID should not change");
        assertEquals(subtask.getEpicId(), subTaskActual.getEpicId(), "Epic ID should not change");
    }

    @Test
    @DisplayName("Should set the epic status to NEW if all its subtasks have status NEW")
    void updateSubTask_changeEpicsStatusToNEW_allItsSubtasksHaveStatusNEW() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Subtask's name", Status.NEW, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subtask2 = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));
        Status statusBeforeChange = epic.getStatus();
        subtask2.setStatus(Status.NEW);

        //when
        manager.updateSubTask(subtask2);
        Status statusAfterChange = epic.getStatus();

        //then
        assertNotEquals(statusBeforeChange, statusAfterChange, "Epic status was not updated");
        assertEquals(Status.NEW, epic.getStatus(), "Epic status did not change");
    }

    @Test
    @DisplayName("Should set the epic status to DONE if all its subtasks have status DONE")
    void updateSubTask_changeEpicsStatusToDONE_allItsSubtasksHaveStatusDONE() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Subtask's name", Status.NEW, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subtask2 = manager.createSubTask(new SubTask("Subtask's name", Status.DONE, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));
        Status statusBeforeChange = epic.getStatus();
        subtask1.setStatus(Status.DONE);

        //when
        manager.updateSubTask(subtask1);
        Status statusAfterChange = epic.getStatus();

        //then
        assertNotEquals(statusBeforeChange, statusAfterChange, "Epic status was not updated");
        assertEquals(Status.DONE, epic.getStatus(), "Epic status did not change");
    }

    @Test
    @DisplayName("Should set the epic status to IN_PROGRESS if its subtasks have different statuses")
    void updateSubTask_changeEpicsStatusToINPROGRESS_statusOfItsSubtasksIsDiverse() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Subtask's name", Status.NEW, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subtask2 = manager.createSubTask(new SubTask("Subtask's name", Status.NEW, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));
        Status statusBeforeChange = epic.getStatus();
        subtask1.setStatus(Status.DONE);

        //when
        manager.updateSubTask(subtask1);
        Status statusAfterChange = epic.getStatus();

        //then
        assertNotEquals(statusBeforeChange, statusAfterChange, "Epic status was not updated");
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Epic status did not change");
    }

    @Test
    @DisplayName("Should throw an exception if subtasks overlap in time")
    void updateSubTask_throwException_ifTheSubTasksOverlapInTime() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        SubTask immutableTask = manager.createSubTask(new SubTask("Subtask's name-1", Status.IN_PROGRESS, "description-1",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask taskForUpdateExpected = manager.createSubTask(new SubTask("Subtask's name-2", Status.IN_PROGRESS, "description-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 18, 17), Duration.ofMinutes(60)));

        SubTask newTask = new SubTask("Subtask's name-2", Status.IN_PROGRESS, "description-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 20), Duration.ofMinutes(60));
        newTask.setId(3);


        //then
        assertThrows(ValidationException.class, () -> manager.updateSubTask(newTask), "Exception was not thrown");
    }

    @Test
    @DisplayName("Should not throw an exception when a subtask overlaps in time with itself")
    void updateSubTask_notThrowException_whenSubTaskIntersectsWithItselfInTime() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask oldTask = manager.createSubTask(new SubTask("Subtask's name-2", Status.IN_PROGRESS, "description-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask newTask = new SubTask("Subtask's name-2", Status.IN_PROGRESS, "description-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 20), Duration.ofMinutes(60));
        newTask.setId(2);


        //then
        assertDoesNotThrow(() -> manager.updateSubTask(newTask), "Exception should not be thrown");
    }

    @Test
    @DisplayName("Should remove the old subtask from the set and add a new one with the same ID")
    void updateSubTask_removeOldSubTaskFromPrioritizedTasksAndAddNewOneWithTheSameIdInstead() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        SubTask immutableTask = manager.createSubTask(new SubTask("Subtask's name-1", Status.IN_PROGRESS, "description-1",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask taskForUpdate = manager.createSubTask(new SubTask("Subtask's name-2", Status.IN_PROGRESS, "description-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 18, 17), Duration.ofMinutes(60)));

        SubTask newTaskExpected = new SubTask("Subtask's name-2", Status.IN_PROGRESS, "description-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 17, 0), Duration.ofMinutes(60));
        newTaskExpected.setId(3);

        //when
        manager.updateSubTask(newTaskExpected);
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        SubTask newTaskActual = (SubTask) prioritizedTasks.get(1);

        //then
        assertEquals(2, prioritizedTasks.size(), "The old task was not deleted");

        assertEquals(newTaskExpected.getId(), newTaskActual.getId(), "IDs do not match");
        assertEquals(newTaskExpected.getName(), newTaskActual.getName(), "Names do not match");
        assertEquals(newTaskExpected.getDescription(), newTaskActual.getDescription(), "Descriptions do not match");
        assertEquals(newTaskExpected.getStatus(), newTaskActual.getStatus(), "Statuses do not match");
        assertEquals(newTaskExpected.getStartDateTime(), newTaskActual.getStartDateTime(), "Start time does not match");
        assertEquals(newTaskExpected.getDuration(), newTaskActual.getDuration(), "Duration does not match");
        assertEquals(newTaskExpected.getEndDateTime(), newTaskActual.getEndDateTime(), "End time does not match");

    }

    @Test
    @DisplayName("Should remove the old subtask from the set if the new one has no start time set")
    void updateSubTask_removeOldSubTaskFromPrioritizedTasks_ifTheNewOneDoesNotHaveStartTimeSet() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        SubTask immutableTask = manager.createSubTask(new SubTask("Subtask's name-1", Status.IN_PROGRESS, "description-1",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask taskForUpdate = manager.createSubTask(new SubTask("Subtask's name-2", Status.IN_PROGRESS, "description-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 18, 17), Duration.ofMinutes(60)));

        SubTask newTask = new SubTask("Subtask's name-2", Status.IN_PROGRESS, "description-2",
                epic.getId(), null, null);
        newTask.setId(3);

        //when
        manager.updateSubTask(newTask);
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        //then
        assertEquals(1, prioritizedTasks.size(), "The old task was not deleted");

    }

    @Test
    @DisplayName("Should update the epic duration field when the subtask duration changes")
    void updateSubTask_changeFieldDurationForTheEpic_ifItChangesForTheSubtask() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        SubTask immutableTask = manager.createSubTask(new SubTask("Subtask's name-1", Status.IN_PROGRESS, "description-1",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask taskForUpdate = manager.createSubTask(new SubTask("Subtask's name-2", Status.IN_PROGRESS, "description-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 18, 17), Duration.ofMinutes(60)));

        SubTask newTaskExpected = new SubTask("Subtask's name-2", Status.IN_PROGRESS, "description-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 17, 0), Duration.ofMinutes(30));
        newTaskExpected.setId(3);

        Duration epicDurationBeforeChange = epic.getDuration();
        //when
        manager.updateSubTask(newTaskExpected);
        Duration epicDurationAfterChange = epic.getDuration();

        //then
        assertNotEquals(epicDurationBeforeChange, epicDurationAfterChange, "Duration did not change");
        assertEquals((60 + 30) * 60, epicDurationAfterChange.getSeconds(), "Duration changed incorrectly");
    }

    @Test
    @DisplayName("Should update the epic startDateTime field when the subtask startDateTime changes," +
            " provided that the subtask startDateTime was or becomes the earliest among all epic subtasks")
    void updateSubTask_changeFieldStartDateTimeForTheEpic_ifItChangesForTheSubtaskAndItWasOrBecameTheEarliestTime() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        SubTask immutableTask = manager.createSubTask(new SubTask("Subtask's name-1", Status.IN_PROGRESS, "description-1",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask taskForUpdate = manager.createSubTask(new SubTask("Subtask's name-2", Status.IN_PROGRESS, "description-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 18, 17), Duration.ofMinutes(60)));

        SubTask newTaskExpected = new SubTask("Subtask's name-2", Status.IN_PROGRESS, "description-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 12, 0), Duration.ofMinutes(30));
        newTaskExpected.setId(3);

        LocalDateTime oldEpicStartDateTime = epic.getStartDateTime();

        //when
        manager.updateSubTask(newTaskExpected);
        LocalDateTime newEpicStartDateTime = epic.getStartDateTime();

        //then
        assertNotEquals(oldEpicStartDateTime, newEpicStartDateTime, "startDateTime did not change");
        assertEquals(newTaskExpected.getStartDateTime(), newEpicStartDateTime, "startDateTime changed incorrectly");
        assertTrue(oldEpicStartDateTime.isAfter(newEpicStartDateTime), "startDateTime changed incorrectly");
    }

    @Test
    @DisplayName("Should update the epic endDateTime field when the subtask endDateTime changes, " +
            "provided that the subtask endDateTime was or becomes the latest among all epic subtasks")
    void updateSubTask_changeFieldEndDateTimeForTheEpic_ifItChangesForTheSubtaskAndItWasOrBecameTheLatestTime() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        Epic epic1 = manager.createEpic(new Epic("name", "description"));

        SubTask immutableTask1 = manager.createSubTask(new SubTask("Subtask's name-1", Status.IN_PROGRESS, "description-1",
                epic1.getId(), LocalDateTime.of(2024, 6, 19, 20, 17), Duration.ofMinutes(60)));

        SubTask immutableTask = manager.createSubTask(new SubTask("Subtask's name-1", Status.IN_PROGRESS, "description-1",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask taskForUpdate = manager.createSubTask(new SubTask("Subtask's name-2", Status.IN_PROGRESS, "description-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 18, 17), Duration.ofMinutes(60)));

        SubTask newTaskExpected = new SubTask("Subtask's name-2", Status.IN_PROGRESS, "description-2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 12, 0), Duration.ofMinutes(30));
        newTaskExpected.setId(5);

        LocalDateTime oldEpicEndDateTime = epic.getEndDateTime();

        //when
        manager.updateSubTask(newTaskExpected);
        LocalDateTime newEpicEndDateTime = epic.getEndDateTime();

        //then
        assertNotEquals(oldEpicEndDateTime, newEpicEndDateTime, "endDateTime did not change");
        assertEquals(immutableTask.getEndDateTime(), newEpicEndDateTime, "endDateTime changed incorrectly");
        assertTrue(oldEpicEndDateTime.isAfter(newEpicEndDateTime), "endDateTime changed incorrectly");
    }

    @Test
    @DisplayName("Should throw an exception if the subtask cannot be found")
    void updateSubTask_throwException_notFoundSubtask() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description1"));
        SubTask subtask = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic1.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        manager.deleteSubTaskById(2);

        //then
        assertThrows(NotFoundException.class, () -> manager.updateSubTask(subtask), "Exception was not thrown");
    }


    @Test
    @DisplayName("Should update all fields of an epic except the status by ID")
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
        assertEquals(epicExpected.getName(), epicActual.getName(), "Names do not match");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(), "Descriptions do not match");
        assertEquals(epicExpected.getStartDateTime(), epicActual.getStartDateTime(), "Start time does not match");
        assertEquals(epicExpected.getDuration(), epicActual.getDuration(), "Duration does not match");
        assertEquals(epicExpected.getEndDateTime(), epicActual.getEndDateTime(), "End time does not match");

        assertNotEquals(epicExpected.getStatus(), epicActual.getStatus(), "Status should not change");
        assertEquals(epic.getStatus(), epicActual.getStatus(), "Status should not change");
    }

    @Test
    @DisplayName("Should throw an exception if the epic cannot be found")
    void updateEpic_throwException_notFoundEpic() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description1"));
        Epic epic2 = manager.createEpic(new Epic("name2", "description2"));
        manager.deleteEpicById(2);

        //then
        assertThrows(NotFoundException.class, () -> manager.updateEpic(epic2), "Exception was not thrown");
    }

    @Test
    @DisplayName("Should delete a task from the manager by ID")
    void deleteTaskById_deleteTaskById() {
        //given
        Task task = manager.createTask(new Task("Task's name", "description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));


        //when
        manager.deleteTaskById(task.getId());

        //then
        assertThrows(NotFoundException.class, () -> manager.getTaskById(task.getId()), "Task was not deleted");
        assertEquals(0, manager.getTasksList().size(), "Task was not deleted");
    }

    @Test
    @DisplayName("Should remove a task from the history by ID")
    void deleteTaskById_deleteTaskByIdFromHistoryList() {
        //given
        Task task = manager.createTask(new Task("Task's name", "description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));

        manager.getTaskById(task.getId());

        //when
        manager.deleteTaskById(task.getId());
        List<Task> historyList = manager.getHistory();

        //then
        assertTrue(historyList.isEmpty(), "Task was not deleted");
    }

    @Test
    @DisplayName("Should remove a task from prioritizedTasks")
    void deleteTaskById_deleteTaskFromPrioritizedTasks() {
        //given
        Task task = manager.createTask(new Task("Task's name", "description", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60)));
        //when
        manager.deleteTaskById(task.getId());
        //then
        assertEquals(0, manager.getPrioritizedTasks().size(), "Task was not deleted");
    }

    @Test
    @DisplayName("Should delete a subtask from the manager by ID")
    void deleteSubTaskById_deleteSubTaskById() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));


        //when
        manager.deleteSubTaskById(subtask.getId());

        //then
        assertThrows(NotFoundException.class, () -> manager.getSubTaskById(subtask.getId()), "Subtask was not deleted");
    }

    @Test
    @DisplayName("Should remove the subtask ID from the epic’s list")
    void deleteSubTaskById_deleteSubTasksIdFromEpic() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));


        //when
        manager.deleteSubTaskById(subtask.getId());
        List<Integer> subTasksId = epic.getSubTasksId();

        //then
        assertTrue(subTasksId.isEmpty(), "Subtask was not deleted");
    }

    @Test
    @DisplayName("Should remove a subtask from the history by ID")
    void deleteSubTaskById_deleteSubTaskByIdFromHistoryList() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        manager.getSubTaskById(subtask.getId());

        //when
        manager.deleteSubTaskById(subtask.getId());
        List<Task> historyList = manager.getHistory();

        //then
        assertTrue(historyList.isEmpty(), "Subtask was not deleted");
    }

    @Test
    @DisplayName("Should remove a subtask from prioritizedTasks")
    void deleteSubTaskById_deleteSubTaskFromPrioritizedTasks() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subTask = manager.createSubTask(new SubTask("Subtask's name-1", Status.IN_PROGRESS, "description-1",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        //when
        manager.deleteSubTaskById(subTask.getId());

        //then
        assertEquals(0, manager.getPrioritizedTasks().size(), "Subtask was not deleted");
    }

    @Test
    @DisplayName("Should remove a subtask from prioritizedTasks")
    void deleteEpicById_deleteEpicFromManagerById() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        //when
        manager.deleteEpicById(epic.getId());

        //then
        assertThrows(NotFoundException.class, () -> manager.getEpicById(epic.getId()), "Epic was not deleted");
    }

    @Test
    @DisplayName("Should delete all subtasks of the deleted epic from the manager by epic ID")
    void deleteEpicById_deleteAllEpicsSubTasksFromManager() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subtask2 = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));

        //when
        manager.deleteEpicById(epic.getId());

        //then

        assertThrows(NotFoundException.class, () -> manager.getSubTaskById(subtask1.getId()), "Subtask was not deleted");
        assertThrows(NotFoundException.class, () -> manager.getSubTaskById(subtask2.getId()), "Subtask was not deleted");
    }

    @Test
    @DisplayName("Should remove an epic and its subtasks from the history by ID")
    void deleteEpicById_deleteEpicByIdAndItsSubTasksFromHistoryList() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subtask1 = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));
        manager.getEpicById(epic.getId());
        manager.getSubTaskById(subtask.getId());
        manager.getSubTaskById(subtask1.getId());


        //when
        manager.deleteEpicById(epic.getId());
        List<Task> historyList = manager.getHistory();

        //then
        assertTrue(historyList.isEmpty(), "Epic was not deleted");
    }

    @Test
    @DisplayName("Should return the list of an epic’s subtasks")
    void getSubTasksByEpic_shouldGetListSubTasksByEpic() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask subtask2 = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));


        List<SubTask> listExpected = new ArrayList<>();
        listExpected.add(subtask1);
        listExpected.add(subtask2);

        //when
        List<SubTask> listActual = manager.getSubTasksByEpic(epic);

        //then
        assertEquals(listExpected.size(), listActual.size(), "Lists have different sizes");

        for (SubTask subTaskExpected : listExpected) {
            SubTask subTaskActual = listActual.get(listExpected.indexOf(subTaskExpected));

            assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "IDs do not match");
            assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "Names do not match");
            assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                    "Descriptions do not match");
            assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "Statuses do not match");
            assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "Epic IDs do not match");
            assertEquals(subTaskExpected.getStartDateTime(), subTaskActual.getStartDateTime(), "Start time does not match");
            assertEquals(subTaskExpected.getDuration(), subTaskActual.getDuration(), "Duration does not match");
            assertEquals(subTaskExpected.getEndDateTime(), subTaskActual.getEndDateTime(), "End time does not match");
        }
    }

    @Test
    @DisplayName("Should return the list of tasks")
    void getTasksList_returnTasksList() {

        //given
        Task task1 = manager.createTask(new Task("Task's name", "description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));
        Task task2 = manager.createTask(new Task("Task's name", "description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 16, 17), Duration.ofMinutes(60)));


        List<Task> listExpected = new ArrayList<>();
        listExpected.add(task1);
        listExpected.add(task2);

        //when
        List<Task> listActual = manager.getTasksList();

        //then
        assertEquals(listExpected.size(), listActual.size(), "Lists have different sizes");

        for (Task taskExpected : listExpected) {
            Task taskActual = listActual.get(listExpected.indexOf(taskExpected));

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
    @DisplayName("Should return the list of subtasks")
    void getSubTasksList_returnSubTasksList() {

        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description1"));
        Epic epic2 = manager.createEpic(new Epic("name2", "description2"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic1.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subtask2 = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic2.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));


        List<SubTask> listExpected = new ArrayList<>();
        listExpected.add(subtask1);
        listExpected.add(subtask2);

        //when
        List<SubTask> listActual = manager.getSubTasksList();

        //then
        assertEquals(listExpected.size(), listActual.size(), "Lists have different sizes");
        for (SubTask subTaskExpected : listExpected) {
            SubTask subTaskActual = listActual.get(listExpected.indexOf(subTaskExpected));

            assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "IDs do not match");
            assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "Names do not match");
            assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                    "Descriptions do not match");
            assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "Statuses do not match");
            assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "Epic IDs do not match");
            assertEquals(subTaskExpected.getStartDateTime(), subTaskActual.getStartDateTime(), "Start time does not match");
            assertEquals(subTaskExpected.getDuration(), subTaskActual.getDuration(), "Duration does not match");
            assertEquals(subTaskExpected.getEndDateTime(), subTaskActual.getEndDateTime(), "End time does not match");
        }
    }

    @Test
    @DisplayName("Should return the list of epics")
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
        assertEquals(listExpected.size(), listActual.size(), "Lists have different sizes");

        for (Epic epicExpected : listExpected) {
            Epic epicActual = listActual.get(listExpected.indexOf(epicExpected));

            assertEquals(epicExpected.getId(), epicActual.getId(), "IDs do not match");
            assertEquals(epicExpected.getName(), epicActual.getName(), "Names do not match");
            assertEquals(epicExpected.getDescription(), epicActual.getDescription(),
                    "Descriptions do not match");
            assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "Statuses do not match");
            assertEquals(epicExpected.getStartDateTime(), epicActual.getStartDateTime(), "Start time does not match");
            assertEquals(epicExpected.getDuration(), epicActual.getDuration(), "Duration does not match");
            assertEquals(epicExpected.getEndDateTime(), epicActual.getEndDateTime(), "End time does not match");
        }
    }

    @Test
    @DisplayName("Should delete all tasks")
    void deleteAllTasks_deleteAllTasks() {
        //given
        Task task1 = manager.createTask(new Task("Task's name", "description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));

        Task task2 = manager.createTask(new Task("Task's name", "description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 16, 17), Duration.ofMinutes(60)));


        //when
        manager.deleteAllTasks();
        List<Task> tasksList = manager.getTasksList();

        //then
        assertTrue(tasksList.isEmpty(), "Tasks were not deleted");
    }

    @Test
    @DisplayName("Should remove all tasks from the history")
    void deleteAllTasks_deleteAllTasksFromHistoryList() {
        //given
        Task task1 = manager.createTask(new Task("Task's name", "description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));
        Task task2 = manager.createTask(new Task("Task's name", "description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 15, 17), Duration.ofMinutes(60)));
        Task task3 = manager.createTask(new Task("Task's name", "description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 17, 17), Duration.ofMinutes(60)));
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getTaskById(task3.getId());

        //when
        manager.deleteAllTasks();
        List<Task> historyList = manager.getHistory();

        //then
        assertTrue(historyList.isEmpty(), "Tasks were not deleted");
    }

    @Test
    @DisplayName("Should remove all tasks from prioritizedTasks")
    void deleteAllTasks_deleteAllTasksFromPrioritizedTasks() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        SubTask subTask = manager.createSubTask(new SubTask("Subtask's name-1", Status.IN_PROGRESS,
                "description-1", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        Task task1 = manager.createTask(new Task("Task's name", "description", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60)));
        Task task2 = manager.createTask(new Task("Task's name-1", "description-1", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 18, 15), Duration.ofMinutes(60)));

        //when
        manager.deleteAllTasks();

        //then

        assertEquals(1, manager.getPrioritizedTasks().size(), "Tasks were not deleted");
        assertEquals(subTask.getType(), manager.getPrioritizedTasks().getFirst().getType(), "Tasks were deleted incorrectly");
    }

    @Test
    @DisplayName("Should delete all subtasks from the manager")
    void deleteAllSubTasks_deleteAllSubTasksFromManager() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description1"));
        Epic epic2 = manager.createEpic(new Epic("name2", "description2"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic1.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask subtask2 = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic2.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));


        //when
        manager.deleteAllSubTasks();
        List<SubTask> subTasksList = manager.getSubTasksList();

        //then
        assertTrue(subTasksList.isEmpty(), "Tasks were not deleted");
    }

    @Test
    @DisplayName("Should clear the lists of subtask IDs in epics")
    void deleteAllSubTasks_clearSubTasksIdListsInEpics() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description1"));
        Epic epic2 = manager.createEpic(new Epic("name2", "description2"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic1.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subtask2 = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic2.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));


        //when
        manager.deleteAllSubTasks();
        List<Integer> subTasksIdInEpic1 = epic1.getSubTasksId();
        List<Integer> subTasksIdInEpic2 = epic2.getSubTasksId();

        //then
        assertTrue(subTasksIdInEpic1.isEmpty(), "Tasks were not deleted");
        assertTrue(subTasksIdInEpic2.isEmpty(), "Tasks were not deleted");
    }

    @Test
    @DisplayName("Should remove all subtasks from the history")
    void deleteAllSubTasks_deleteAllSubTasksFromHistoryList() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask subtask2 = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));

        SubTask subtask3 = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 18, 17), Duration.ofMinutes(60)));

        manager.getSubTaskById(subtask1.getId());
        manager.getSubTaskById(subtask2.getId());
        manager.getSubTaskById(subtask3.getId());

        //when
        manager.deleteAllSubTasks();
        List<Task> historyList = manager.getHistory();

        //then
        assertTrue(historyList.isEmpty(), "Tasks were not deleted");
    }

    @Test
    @DisplayName("Should remove all subtasks from prioritizedTasks")
    void deleteAllSubTasks_deleteAllSubTasksFromPrioritizedTasks() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        SubTask subTask1 = manager.createSubTask(new SubTask("Subtask's name-1", Status.IN_PROGRESS,
                "description-1", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subTask2 = manager.createSubTask(new SubTask("Subtask's name-2", Status.IN_PROGRESS,
                "description-2", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 15, 17), Duration.ofMinutes(40)));

        Task task1 = manager.createTask(new Task("Task's name", "description", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60)));

        //when
        manager.deleteAllSubTasks();

        //then
        assertEquals(1, manager.getPrioritizedTasks().size(), "Subtasks were not deleted");
        assertEquals(task1.getType(), manager.getPrioritizedTasks().getFirst().getType(), "Subtasks were deleted incorrectly");
    }

    @Test
    @DisplayName("Should delete all epics")
    void deleteAllEpics_deleteAllEpics() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description1"));
        Epic epic2 = manager.createEpic(new Epic("name2", "description2"));

        //when
        manager.deleteAllEpics();
        List<Epic> epicList = manager.getEpicList();

        //then
        assertTrue(epicList.isEmpty(), "Tasks were not deleted");
    }

    @Test
    @DisplayName("Should delete all subtasks")
    void deleteAllEpics_deleteAllSubTasks() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description1"));
        Epic epic2 = manager.createEpic(new Epic("name2", "description2"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic1.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subtask2 = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic2.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));

        //when
        manager.deleteAllEpics();
        List<SubTask> subTasksList = manager.getSubTasksList();

        //then
        assertTrue(subTasksList.isEmpty(), "Tasks were not deleted");
    }

    @Test
    @DisplayName("Should remove all epics and their subtasks from the history")
    void deleteAllEpics_deleteAllEpicsAndItsSubTasksFromHistoryList() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description"));
        Epic epic2 = manager.createEpic(new Epic("name2", "description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic1.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));

        SubTask subtask2 = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic1.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));

        SubTask subtask3 = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
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
        assertTrue(historyList.isEmpty(), "Epics were not deleted");
    }

    @Test
    @DisplayName("Should remove all subtasks from prioritizedTasks")
    void deleteAllEpics_deleteAllSubTasksFromPrioritizedTasks() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        SubTask subTask1 = manager.createSubTask(new SubTask("Subtask's name-1", Status.IN_PROGRESS,
                "description-1", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subTask2 = manager.createSubTask(new SubTask("Subtask's name-2", Status.IN_PROGRESS,
                "description-2", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 15, 17), Duration.ofMinutes(40)));

        Task task1 = manager.createTask(new Task("Task's name", "description", Status.NEW,
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60)));

        //when
        manager.deleteAllEpics();

        //then
        assertEquals(1, manager.getPrioritizedTasks().size(), "Subtasks were not deleted");
        assertEquals(task1.getType(), manager.getPrioritizedTasks().getFirst().getType(), "Subtasks were deleted incorrectly");
    }

    @Test
    @DisplayName("Should return a list of tasks sorted by time")
    void getPrioritizedTasks_returnSortedByTimeTasksList() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        SubTask subTask1 = manager.createSubTask(new SubTask("Subtask's name-1", Status.IN_PROGRESS, //id=2, position - 2
                "description-1", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subTask2 = manager.createSubTask(new SubTask("Subtask's name-2", Status.IN_PROGRESS, //id=3, position - 3
                "description-2", epic.getId(),
                LocalDateTime.of(2024, 6, 19, 15, 17), Duration.ofMinutes(40)));

        Task task1 = manager.createTask(new Task("Task's name", "description", Status.NEW,    //id=4, position - 1
                LocalDateTime.of(2024, 6, 19, 12, 15), Duration.ofMinutes(60)));

        //when
        List<Task> list = manager.getPrioritizedTasks();

        //then
        assertEquals(4, list.getFirst().getId(), "Task is not in the correct position");
        assertEquals(2, list.get(1).getId(), "Task is not in the correct position");
        assertEquals(3, list.getLast().getId(), "Task is not in the correct position");
    }
}