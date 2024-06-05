package manager.task;


import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Менеджер из файла")
class FileBackedTaskManagerTest {

    private TaskManager manager;
    File tempFile;

    @BeforeEach
    void init() {
        File directoryForTempFile = new File("test\\testFiles");
        try {
            tempFile = File.createTempFile("test-", ".csv", directoryForTempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        manager = FileBackedTaskManager.loadFromFile(tempFile);
    }

    @Test
    @DisplayName("Возвращает новый восстановленный менеджер на основе данных из файла")
    void loadFromFile_returnsNewRestoredManagerBasedOnFile() {

        //given
        Task task = manager.createTask(new Task("name1", "description1", Status.DONE));
        Epic epic = manager.createEpic(new Epic("name2", "description2"));
        SubTask subtask = manager.createSubTask(new SubTask("name3", "description3",
                Status.NEW, epic.getId()));

        FileBackedTaskManager managerExpected = (FileBackedTaskManager) manager;

        //when
        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> taskListExpected = managerExpected.getTasksList();
        List<Epic> epicListExpected = managerExpected.getEpicList();
        List<SubTask> subTaskListExpected = managerExpected.getSubTasksList();

        List<Task> taskListActual = managerActual.getTasksList();
        List<Epic> epicListActual = managerActual.getEpicList();
        List<SubTask> subTaskListActual = managerActual.getSubTasksList();

        Task task4 = managerActual.createTask(new Task("name2", "description4", Status.DONE));

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
        }

        assertEquals(4, task4.getId(), "Счетчик не восстановился");

    }

    @Test
    @DisplayName("Должен добавить задачу в файл")
    void createTask_addsTaskToFile() {

        //when
        Task taskExpected = manager.createTask(new Task("name1", "description1", Status.DONE));

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(tempFile);
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
        SubTask subTaskExpected = manager.createSubTask(new SubTask("name", "description", Status.NEW,
                epic.getId()));

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(tempFile);
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


        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(tempFile);
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
        Task task = manager.createTask(new Task("name1", "description1", Status.DONE));
        Task taskExpected = new Task("name2", "description2", Status.NEW);
        taskExpected.setId(1);

        //when
        manager.updateTask(taskExpected);

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(tempFile);
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
        SubTask subtask = manager.createSubTask(new SubTask("name1", "description1", Status.NEW,
                epic.getId()));
        SubTask subTaskExpected = new SubTask("name2", "description2", Status.NEW, epic.getId());
        subTaskExpected.setId(2);

        //when
        manager.updateSubTask(subTaskExpected);

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(tempFile);
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

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(tempFile);
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
        Task task = manager.createTask(new Task("name", "description", Status.DONE));

        //when
        manager.deleteTaskById(task.getId());

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(tempFile);

        //then
        assertNull(managerActual.getTaskById(task.getId()), "задача не удалилась");
    }

    @Test
    @DisplayName("Должен удалить подзадачу из файла")
    void deleteSubTaskById_deleteSubTaskFromFile() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = manager.createSubTask(new SubTask("name", "description", Status.NEW,
                epic.getId()));

        //when
        manager.deleteSubTaskById(subtask.getId());

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(tempFile);

        //then
        assertNull(managerActual.getSubTaskById(subtask.getId()), "подзадача не удалилась");
    }

    @Test
    @DisplayName("Должен удалить эпик из файла")
    void deleteEpicById_deleteEpicFromFile() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));


        //when
        manager.deleteEpicById(epic.getId());

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(tempFile);

        //then
        assertNull(managerActual.getEpicById(epic.getId()), "эпик не удалился");
    }

    @Test
    @DisplayName("Должен удалить все задачи из файла")
    void deleteAllTasks_deleteAllTasksFromFile() {
        //given
        Task task1 = manager.createTask(new Task("name1", "description1", Status.DONE));
        Task task2 = manager.createTask(new Task("name2", "description2", Status.DONE));


        //when
        manager.deleteAllTasks();

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> taskList = managerActual.getTasksList();

        //then
        assertNull(managerActual.getTaskById(task1.getId()), "задача не удалилась");
        assertNull(managerActual.getTaskById(task2.getId()), "задача не удалилась");
        assertEquals(0, taskList.size(), "Задачи не удалились");
    }

    @Test
    @DisplayName("Должен удалить все подзадачи из файла")
    void deleteAllSubTasks_deleteAllSubTasksFromFile() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("name1", "description1", Status.NEW, epic.getId()));
        SubTask subtask2 = manager.createSubTask(new SubTask("name2", "description2", Status.NEW, epic.getId()));


        //when
        manager.deleteAllSubTasks();

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(tempFile);
        List<SubTask> subTaskList = managerActual.getSubTasksList();

        //then
        assertNull(managerActual.getSubTaskById(subtask1.getId()), "подзадача не удалилась");
        assertNull(managerActual.getSubTaskById(subtask2.getId()), "подзадача не удалилась");
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

        FileBackedTaskManager managerActual = FileBackedTaskManager.loadFromFile(tempFile);
        List<Epic> epicList = managerActual.getEpicList();

        //then
        assertNull(managerActual.getEpicById(epic1.getId()), "эпик не удалился");
        assertNull(managerActual.getEpicById(epic2.getId()), "эпик не удалился");
        assertEquals(0, epicList.size(), "эпики не удалились");
    }
}