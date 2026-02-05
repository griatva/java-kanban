package manager.task;

import exception.NotFoundException;
import manager.Managers;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("File Backed Task Manager")
class FileBackedTaskManagerTest extends InMemoryTaskManagerTest {

    File fileForRecovery = new File("resources/task.csv");

    @BeforeEach
    void init() {
        this.manager = Managers.getDefaults();
        manager.deleteAllEpics();
        manager.deleteAllTasks();
        manager.deleteAllSubTasks();
    }

    @Test
    @DisplayName("Returns a new restored manager based on data from the file")
    void loadFromFile_returnsNewRestoredManagerBasedOnFile() {

        //given
        Task task = manager.createTask(new Task("name1", "description1", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(40)));

        Epic epic = manager.createEpic(new Epic("name2", "description2"));

        SubTask subtask = manager.createSubTask(new SubTask("name3", Status.IN_PROGRESS, "description3",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 30), Duration.ofMinutes(60)));

        FileBackedTaskManager managerExpected = (FileBackedTaskManager) manager;


        //when
        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);

        List<Task> taskListExpected = managerExpected.getTasksList();
        List<Epic> epicListExpected = managerExpected.getEpicList();
        List<SubTask> subTaskListExpected = managerExpected.getSubTasksList();
        List<Task> prioritizedTasksExpected = managerExpected.getPrioritizedTasks();

        List<Task> taskListActual = managerActual.getTasksList();
        List<Epic> epicListActual = managerActual.getEpicList();
        List<SubTask> subTaskListActual = managerActual.getSubTasksList();
        List<Task> prioritizedTasksActual = managerActual.getPrioritizedTasks();


        Task task4 = managerActual.createTask(new Task("name4", "description4", Status.NEW,
                LocalDateTime.of(2024, 6, 20, 13, 0), Duration.ofMinutes(30)));

        //then
        assertEquals(taskListExpected.size(), taskListActual.size(), "Lists have different sizes");

        for (Task taskExpected : taskListExpected) {
            Task taskActual = taskListActual.get(taskListExpected.indexOf(taskExpected));

            assertEquals(taskExpected.getId(), taskActual.getId(), "IDs do not match");
            assertEquals(taskExpected.getName(), taskActual.getName(), "Names do not match");
            assertEquals(taskExpected.getDescription(), taskActual.getDescription(),
                    "Descriptions do not match");
            assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "Statuses do not match");
            assertEquals(taskExpected.getType(), taskActual.getType(), "Types do not match");
            assertEquals(taskExpected.getStartDateTime(), taskActual.getStartDateTime(), "Start time does not match");
            assertEquals(taskExpected.getDuration(), taskActual.getDuration(), "Duration does not match");
            assertEquals(taskExpected.getEndDateTime(), taskActual.getEndDateTime(), "End time does not match");
        }

        assertEquals(epicListExpected.size(), epicListActual.size(), "Lists have different sizes");

        for (Epic epicExpected : epicListExpected) {
            Epic epicActual = epicListActual.get(epicListExpected.indexOf(epicExpected));

            assertEquals(epicExpected.getId(), epicActual.getId(), "IDs do not match");
            assertEquals(epicExpected.getName(), epicActual.getName(), "Names do not match");
            assertEquals(epicExpected.getDescription(), epicActual.getDescription(),
                    "Descriptions do not match");
            assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "Statuses do not match");
            assertEquals(epicExpected.getType(), epicActual.getType(), "Types do not match");
            assertEquals(epicExpected.getStartDateTime(), epicActual.getStartDateTime(), "Start time does not match");
            assertEquals(epicExpected.getDuration(), epicActual.getDuration(), "Duration does not match");
            assertEquals(epicExpected.getEndDateTime(), epicActual.getEndDateTime(), "End time does not match");

            List<Integer> subTasksIdExpected = epicExpected.getSubTasksId();
            List<Integer> subTasksIdActual = epicActual.getSubTasksId();
            assertEquals(subTasksIdExpected.size(), subTasksIdActual.size(),
                    "The number of subtasks in the epic list does not match");
            assertEquals(subTasksIdExpected.getFirst(), subTasksIdActual.getFirst(),
                    "Subtasks' IDs do not match");
        }

        assertEquals(subTaskListExpected.size(), subTaskListActual.size(), "Lists have different sizes");

        for (SubTask subTaskExpected : subTaskListExpected) {
            SubTask subTaskActual = subTaskListActual.get(subTaskListExpected.indexOf(subTaskExpected));

            assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "IDs do not match");
            assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "Names do not match");
            assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                    "Descriptions do not match");
            assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "Statuses do not match");
            assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "Epic' IDs do not match");
            assertEquals(subTaskExpected.getType(), subTaskActual.getType(), "Types do not match");
            assertEquals(subTaskExpected.getStartDateTime(), subTaskActual.getStartDateTime(), "Start time does not match");
            assertEquals(subTaskExpected.getDuration(), subTaskActual.getDuration(), "Duration does not match");
            assertEquals(subTaskExpected.getEndDateTime(), subTaskActual.getEndDateTime(), "End time does not match");
        }

        assertEquals(prioritizedTasksExpected.size(), prioritizedTasksActual.size(), "Lists have different sizes");

        for (Task ptExpected : prioritizedTasksExpected) {
            Task ptActual = prioritizedTasksActual.get(prioritizedTasksExpected.indexOf(ptExpected));

            assertEquals(ptExpected.getId(), ptActual.getId(), "IDs do not match");
            assertEquals(ptExpected.getName(), ptActual.getName(), "Names do not match");
            assertEquals(ptExpected.getDescription(), ptActual.getDescription(),
                    "Descriptions do not match");
            assertEquals(ptExpected.getStatus(), ptActual.getStatus(), "Statuses do not match");
            assertEquals(ptExpected.getType(), ptActual.getType(), "Types do not match");
            assertEquals(ptExpected.getStartDateTime(), ptActual.getStartDateTime(), "Start time does not match");
            assertEquals(ptExpected.getDuration(), ptActual.getDuration(), "Duration does not match");
            assertEquals(ptExpected.getEndDateTime(), ptActual.getEndDateTime(), "End time does not match");
        }

        assertEquals(4, task4.getId(), "The counter was not restored");
    }


    @Test
    @DisplayName("Should add a task to the file")
    void createTask_addsTaskToFile() {

        //when
        Task taskExpected = manager.createTask(new Task("Task's name", "description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));


        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);
        List<Task> list = managerActual.getTasksList();
        Task taskActual = list.getFirst();

        //then
        assertEquals(taskExpected.getId(), taskActual.getId(), "IDs do not match");
        assertEquals(taskExpected.getName(), taskActual.getName(), "Names do not match");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "Descriptions do not match");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "Statuses do not match");
        assertEquals(taskExpected.getType(), taskActual.getType(), "Types do not match");

    }

    @Test
    @DisplayName("Should add a subtask to the file")
    void createSubTask_addsSubTaskToFile() {
        //when
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subTaskExpected = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));


        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);
        List<SubTask> list = managerActual.getSubTasksList();
        SubTask subTaskActual = list.getFirst();

        //then
        assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "IDs do not match");
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "Names do not match");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                "Descriptions do not match");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "Statuses do not match");
        assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "Epic' IDs do not match");
        assertEquals(subTaskExpected.getType(), subTaskActual.getType(), "Types do not match");

    }

    @Test
    @DisplayName("Should add a Epic to the file")
    void createEpic_addsEpicToFile() {
        //when
        Epic epicExpected = manager.createEpic(new Epic("name", "description"));


        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);
        List<Epic> list = managerActual.getEpicList();
        Epic epicActual = list.getFirst();

        //then
        assertEquals(epicExpected.getId(), epicActual.getId(), "IDs do not match");
        assertEquals(epicExpected.getName(), epicActual.getName(), "Names do not match");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(),
                "Descriptions do not match");
        assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "Statuses do not match");
        assertEquals(epicExpected.getType(), epicActual.getType(), "Types do not match");

        List<Integer> subTasksIdExpected = epicExpected.getSubTasksId();
        List<Integer> subTasksIdActual = epicActual.getSubTasksId();
        assertEquals(subTasksIdExpected.size(), subTasksIdActual.size(),
                "The number of subtasks in the epic list does not match");

    }

    @Test
    @DisplayName("Should replace the task in the file")
    void updateTask_replacesTaskInFile() {
        //given
        Task task = manager.createTask(new Task("Task's name", "description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));
        Task taskExpected = new Task("Task's name-2", "description-2", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 19, 17), Duration.ofMinutes(60));
        taskExpected.setId(1);

        //when
        manager.updateTask(taskExpected);

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);
        Task taskActual = managerActual.getTaskById(1);

        //then
        assertEquals(taskExpected.getId(), taskActual.getId(), "IDs do not match");
        assertEquals(taskExpected.getName(), taskActual.getName(), "Names do not match");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "Descriptions do not match");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "Statuses do not match");
        assertEquals(taskExpected.getType(), taskActual.getType(), "Types do not match");

    }

    @Test
    @DisplayName("Should replace the subtask in the file")
    void updateSubTask_replacesSubTaskInFile() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subTaskExpected = new SubTask("Subtask's name2", Status.IN_PROGRESS, "description2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60));
        subTaskExpected.setId(2);

        //when
        manager.updateSubTask(subTaskExpected);

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);
        SubTask subTaskActual = managerActual.getSubTaskById(2);

        //then
        assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "IDs do not match");
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "Names do not match");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                "Descriptions do not match");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "Statuses do not match");
        assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "Epic' IDs do not match");
        assertEquals(subTaskExpected.getType(), subTaskActual.getType(), "Types do not match");
    }

    @Test
    @DisplayName("Should replace the epic in the file")
    void updateEpic_replacesEpicInFile() {
        //given
        Epic epic = manager.createEpic(new Epic("name1", "description1"));
        Epic epicExpected = new Epic("name2", "description2");
        epicExpected.setId(1);
        epicExpected.setStatus(Status.NEW);

        //when
        manager.updateEpic(epicExpected);

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);
        Epic epicActual = managerActual.getEpicById(1);

        //then
        assertEquals(epicExpected.getId(), epicActual.getId(), "IDs do not match");
        assertEquals(epicExpected.getName(), epicActual.getName(), "Names do not match");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(),
                "Descriptions do not match");
        assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "Statuses do not match");
        assertEquals(epicExpected.getType(), epicActual.getType(), "Types do not match");

        List<Integer> subTasksIdExpected = epicExpected.getSubTasksId();
        List<Integer> subTasksIdActual = epicActual.getSubTasksId();
        assertEquals(subTasksIdExpected.size(), subTasksIdActual.size(),
                "The number of subtasks in the epic list does not match");

    }

    @Test
    @DisplayName("Should delete the task from the file")
    void deleteTaskById_deleteTaskFromFile() {
        //given
        Task task = manager.createTask(new Task("Task's name", "description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));

        //when
        manager.deleteTaskById(task.getId());

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);

        //then
        assertThrows(NotFoundException.class, () -> managerActual.getTaskById(task.getId()), "Task was not deleted");
    }

    @Test
    @DisplayName("Should delete the subtask from the file")
    void deleteSubTaskById_deleteSubTaskFromFile() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));


        //when
        manager.deleteSubTaskById(subtask.getId());

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);

        //then
        assertThrows(NotFoundException.class, () -> managerActual.getSubTaskById(subtask.getId()), "Task was not deleted");
    }

    @Test
    @DisplayName("Should delete the epic from the file")
    void deleteEpicById_deleteEpicFromFile() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        //when
        manager.deleteEpicById(epic.getId());

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);

        //then
        assertThrows(NotFoundException.class, () -> managerActual.getEpicById(epic.getId()), "Epic was not deleted");
    }

    @Test
    @DisplayName("Should delete all tasks from the file")
    void deleteAllTasks_deleteAllTasksFromFile() {
        //given
        Task task1 = manager.createTask(new Task("Task's name", "description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));

        Task task2 = manager.createTask(new Task("Task's name", "description", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 16, 17), Duration.ofMinutes(60)));


        //when
        manager.deleteAllTasks();

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);
        List<Task> taskList = managerActual.getTasksList();

        //then
        assertEquals(0, taskList.size(), "Tasks were not deleted");
    }

    @Test
    @DisplayName("Should delete all subtasks from the file")
    void deleteAllSubTasks_deleteAllSubTasksFromFile() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subtask2 = manager.createSubTask(new SubTask("Subtask's name", Status.IN_PROGRESS, "description",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));


        //when
        manager.deleteAllSubTasks();

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);
        List<SubTask> subTaskList = managerActual.getSubTasksList();

        //then
        assertEquals(0, subTaskList.size(), "Subtasks were not deleted");
    }

    @Test
    @DisplayName("Should delete all epics from the file")
    void deleteAllEpics_deleteAllEpicsFromFile() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description1"));
        Epic epic2 = manager.createEpic(new Epic("name2", "description2"));

        //when
        manager.deleteAllEpics();

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);
        List<Epic> epicList = managerActual.getEpicList();

        //then
        assertEquals(0, epicList.size(), "Epics were not deleted");
    }
}