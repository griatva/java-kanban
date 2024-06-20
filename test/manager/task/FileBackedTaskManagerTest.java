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

@DisplayName("Менеджер из файла")
class FileBackedTaskManagerTest {

    private TaskManager manager;
    File fileForRecovery = new File("resources/task.csv");

    @BeforeEach
    void init() {
        manager = Managers.getDefaults();
        manager.deleteAllEpics();
        manager.deleteAllTasks();
        manager.deleteAllSubTasks();
    }

    @Test
    @DisplayName("Возвращает новый восстановленный менеджер на основе данных из файла")
    void loadFromFile_returnsNewRestoredManagerBasedOnFile() {

        //given
        Task task = manager.createTask(new Task("name1", "description1", Status.DONE,
                LocalDateTime.of(2024, 06, 18, 13, 17), Duration.ofMinutes(40)));

        Epic epic = manager.createEpic(new Epic("name2", "description2"));

        SubTask subtask = manager.createSubTask(new SubTask("name3", Status.IN_PROGRESS, "description3",
                epic.getId(), LocalDateTime.of(2024, 06, 19, 14, 30), Duration.ofMinutes(60)));

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
                LocalDateTime.of(2024, 06, 20, 13, 00), Duration.ofMinutes(30)));

        //then
        assertEquals(taskListExpected.size(), taskListActual.size(), "списки разного размера");

        for (Task taskExpected : taskListExpected) {
            Task taskActual = taskListActual.get(taskListExpected.indexOf(taskExpected));

            assertEquals(taskExpected.getId(), taskActual.getId(), "не совпадают id");
            assertEquals(taskExpected.getName(), taskActual.getName(), "не совпадает name");
            assertEquals(taskExpected.getDescription(), taskActual.getDescription(),
                    "не совпадают description");
            assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "не совпадают status");
            assertEquals(taskExpected.getType(), taskActual.getType(), "не совпадают type");
            assertEquals(taskExpected.getStartDateTime(), taskActual.getStartDateTime(), "не совпадает startTime");
            assertEquals(taskExpected.getDuration(), taskActual.getDuration(), "не совпадает duration");
            assertEquals(taskExpected.getEndDateTime(), taskActual.getEndDateTime(), "не совпадает endTime");
        }

        assertEquals(epicListExpected.size(), epicListActual.size(), "списки разного размера");

        for (Epic epicExpected : epicListExpected) {
            Epic epicActual = epicListActual.get(epicListExpected.indexOf(epicExpected));

            assertEquals(epicExpected.getId(), epicActual.getId(), "не совпадают id");
            assertEquals(epicExpected.getName(), epicActual.getName(), "не совпадает name");
            assertEquals(epicExpected.getDescription(), epicActual.getDescription(),
                    "не совпадают description");
            assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "не совпадают status");
            assertEquals(epicExpected.getType(), epicActual.getType(), "не совпадают type");
            assertEquals(epicExpected.getStartDateTime(), epicActual.getStartDateTime(), "не совпадает startTime");
            assertEquals(epicExpected.getDuration(), epicActual.getDuration(), "не совпадает duration");
            assertEquals(epicExpected.getEndDateTime(), epicActual.getEndDateTime(), "не совпадает endTime");

            List<Integer> subTasksIdExpected = epicExpected.getSubTasksId();
            List<Integer> subTasksIdActual = epicActual.getSubTasksId();
            assertEquals(subTasksIdExpected.size(), subTasksIdActual.size(),
                    "не совпадает количество подзадач в списке эпика");
            assertEquals(subTasksIdExpected.getFirst(), subTasksIdActual.getFirst(),
                    "не совпадают id подзадач");
        }

        assertEquals(subTaskListExpected.size(), subTaskListActual.size(), "списки разного размера");

        for (SubTask subTaskExpected : subTaskListExpected) {
            SubTask subTaskActual = subTaskListActual.get(subTaskListExpected.indexOf(subTaskExpected));

            assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "не совпадают id");
            assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "не совпадает name");
            assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                    "не совпадают description");
            assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "не совпадают status");
            assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "не совпадают epicId");
            assertEquals(subTaskExpected.getType(), subTaskActual.getType(), "не совпадают type");
            assertEquals(subTaskExpected.getStartDateTime(), subTaskActual.getStartDateTime(), "не совпадает startTime");
            assertEquals(subTaskExpected.getDuration(), subTaskActual.getDuration(), "не совпадает duration");
            assertEquals(subTaskExpected.getEndDateTime(), subTaskActual.getEndDateTime(), "не совпадает endTime");
        }

        assertEquals(prioritizedTasksExpected.size(), prioritizedTasksActual.size(), "списки разного размера");

        for (Task ptExpected : prioritizedTasksExpected) {
            Task ptActual = prioritizedTasksActual.get(prioritizedTasksExpected.indexOf(ptExpected));

            assertEquals(ptExpected.getId(), ptActual.getId(), "не совпадают id");
            assertEquals(ptExpected.getName(), ptActual.getName(), "не совпадает name");
            assertEquals(ptExpected.getDescription(), ptActual.getDescription(),
                    "не совпадают description");
            assertEquals(ptExpected.getStatus(), ptActual.getStatus(), "не совпадают status");
            assertEquals(ptExpected.getType(), ptActual.getType(), "не совпадают type");
            assertEquals(ptExpected.getStartDateTime(), ptActual.getStartDateTime(), "не совпадает startTime");
            assertEquals(ptExpected.getDuration(), ptActual.getDuration(), "не совпадает duration");
            assertEquals(ptExpected.getEndDateTime(), ptActual.getEndDateTime(), "не совпадает endTime");
        }

        assertEquals(4, task4.getId(), "Счетчик не восстановился");
    }


    @Test
    @DisplayName("Должен добавить задачу в файл")
    void createTask_addsTaskToFile() {

        //when
        Task taskExpected = manager.createTask(new Task("название задачи", "описание", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));


        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);
        List<Task> list = managerActual.getTasksList();
        Task taskActual = list.getFirst();

        //then
        assertEquals(taskExpected.getId(), taskActual.getId(), "не совпадают id");
        assertEquals(taskExpected.getName(), taskActual.getName(), "не совпадает name");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "не совпадают description");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "не совпадают status");
        assertEquals(taskExpected.getType(), taskActual.getType(), "не совпадают type");

    }

    @Test
    @DisplayName("Должен добавить подзадачу в файл")
    void createSubTask_addsSubTaskToFile() {
        //when
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subTaskExpected = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));


        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);
        List<SubTask> list = managerActual.getSubTasksList();
        SubTask subTaskActual = list.getFirst();

        //then
        assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "не совпадают id");
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "не совпадает name");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                "не совпадают description");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "не совпадают status");
        assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "не совпадают epicId");
        assertEquals(subTaskExpected.getType(), subTaskActual.getType(), "не совпадают type");

    }

    @Test
    @DisplayName("Должен добавить подзадачу в файл")
    void createEpic_addsEpicToFile() {
        //when
        Epic epicExpected = manager.createEpic(new Epic("name", "description"));


        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);
        List<Epic> list = managerActual.getEpicList();
        Epic epicActual = list.getFirst();

        //then
        assertEquals(epicExpected.getId(), epicActual.getId(), "не совпадают id");
        assertEquals(epicExpected.getName(), epicActual.getName(), "не совпадает name");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(),
                "не совпадают description");
        assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "не совпадают status");
        assertEquals(epicExpected.getType(), epicActual.getType(), "не совпадают type");

        List<Integer> subTasksIdExpected = epicExpected.getSubTasksId();
        List<Integer> subTasksIdActual = epicActual.getSubTasksId();
        assertEquals(subTasksIdExpected.size(), subTasksIdActual.size(),
                "не совпадает количество подзадач в списке эпика");

    }

    @Test
    @DisplayName("Должен заменить задачу в файле")
    void updateTask_replacesTaskInFile() {
        //given
        Task task = manager.createTask(new Task("название задачи", "описание", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));
        Task taskExpected = new Task("название задачи-2", "описание-2", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 19, 17), Duration.ofMinutes(60));
        taskExpected.setId(1);

        //when
        manager.updateTask(taskExpected);

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);
        Task taskActual = managerActual.getTaskById(1);

        //then
        assertEquals(taskExpected.getId(), taskActual.getId(), "не совпадают id");
        assertEquals(taskExpected.getName(), taskActual.getName(), "не совпадает name");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "не совпадают description");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "не совпадают status");
        assertEquals(taskExpected.getType(), taskActual.getType(), "не совпадают type");

    }

    @Test
    @DisplayName("Должен заменить подзадачу в файле")
    void updateSubTask_replacesSubTaskInFile() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subTaskExpected = new SubTask("Название подзадачи2", Status.IN_PROGRESS, "описание2",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60));
        subTaskExpected.setId(2);

        //when
        manager.updateSubTask(subTaskExpected);

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);
        SubTask subTaskActual = managerActual.getSubTaskById(2);

        //then
        assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "не совпадают id");
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "не совпадает name");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                "не совпадают description");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "не совпадают status");
        assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "не совпадают epicId");
        assertEquals(subTaskExpected.getType(), subTaskActual.getType(), "не совпадают type");
    }

    @Test
    @DisplayName("Должен заменить эпик в файле")
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
        assertEquals(epicExpected.getId(), epicActual.getId(), "не совпадают id");
        assertEquals(epicExpected.getName(), epicActual.getName(), "не совпадает name");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(),
                "не совпадают description");
        assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "не совпадают status");
        assertEquals(epicExpected.getType(), epicActual.getType(), "не совпадают type");

        List<Integer> subTasksIdExpected = epicExpected.getSubTasksId();
        List<Integer> subTasksIdActual = epicActual.getSubTasksId();
        assertEquals(subTasksIdExpected.size(), subTasksIdActual.size(),
                "не совпадает количество подзадач в списке эпика");

    }

    @Test
    @DisplayName("Должен удалить задачу из файла")
    void deleteTaskById_deleteTaskFromFile() {
        //given
        Task task = manager.createTask(new Task("название задачи", "описание", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));

        //when
        manager.deleteTaskById(task.getId());

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);

        //then
        assertThrows(NotFoundException.class, () -> managerActual.getTaskById(task.getId()), "задача не удалилась");
    }

    @Test
    @DisplayName("Должен удалить подзадачу из файла")
    void deleteSubTaskById_deleteSubTaskFromFile() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));


        //when
        manager.deleteSubTaskById(subtask.getId());

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);

        //then
        assertThrows(NotFoundException.class, () -> managerActual.getSubTaskById(subtask.getId()), "задача не удалилась");
    }

    @Test
    @DisplayName("Должен удалить эпик из файла")
    void deleteEpicById_deleteEpicFromFile() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        //when
        manager.deleteEpicById(epic.getId());

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);

        //then
        assertThrows(NotFoundException.class, () -> managerActual.getEpicById(epic.getId()), "эпик не удалился");
    }

    @Test
    @DisplayName("Должен удалить все задачи из файла")
    void deleteAllTasks_deleteAllTasksFromFile() {
        //given
        Task task1 = manager.createTask(new Task("название задачи", "описание", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 13, 17), Duration.ofMinutes(60)));

        Task task2 = manager.createTask(new Task("название задачи", "описание", Status.DONE,
                LocalDateTime.of(2024, 6, 18, 16, 17), Duration.ofMinutes(60)));


        //when
        manager.deleteAllTasks();

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);
        List<Task> taskList = managerActual.getTasksList();

        //then
        assertEquals(0, taskList.size(), "Задачи не удалились");
    }

    @Test
    @DisplayName("Должен удалить все подзадачи из файла")
    void deleteAllSubTasks_deleteAllSubTasksFromFile() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        SubTask subtask2 = manager.createSubTask(new SubTask("Название подзадачи", Status.IN_PROGRESS, "описание",
                epic.getId(), LocalDateTime.of(2024, 6, 19, 16, 17), Duration.ofMinutes(60)));


        //when
        manager.deleteAllSubTasks();

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);
        List<SubTask> subTaskList = managerActual.getSubTasksList();

        //then
        assertEquals(0, subTaskList.size(), "подзадачи не удалились");
    }

    @Test
    @DisplayName("Должен удалить все эпики из файла")
    void deleteAllEpics_deleteAllEpicsFromFile() {
        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description1"));
        Epic epic2 = manager.createEpic(new Epic("name2", "description2"));

        //when
        manager.deleteAllEpics();

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(fileForRecovery);
        List<Epic> epicList = managerActual.getEpicList();

        //then
        assertEquals(0, epicList.size(), "эпики не удалились");
    }
}