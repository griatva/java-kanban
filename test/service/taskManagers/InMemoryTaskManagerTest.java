package service.taskManagers;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Managers;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Менеджер")
class InMemoryTaskManagerTest {

    private static TaskManager manager;

    @BeforeEach
    void init() {
        manager = Managers.getDefaults();
    }

    @Test
    @DisplayName("Должен возвращать задачу по ID из менеджера")
    void getTaskById_returnTaskById() {
        //given
        Task task = manager.createTask(new Task("name", "description", Status.NEW));
        Task taskExpected = new Task("name", "description", Status.NEW);
        taskExpected.setId(1);

        //that
        Task taskActual= manager.getTaskById(task.getId());

        //than
        assertEquals(taskExpected.getId(), taskActual.getId(), "не совпадают id");
        assertEquals(taskExpected.getName(), taskActual.getName(),  "не совпадает name");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "не совпадают description");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "не совпадают status");
    }

    @Test
    @DisplayName("Должен добавлять задачу в историю")
    void getTaskById_addTaskToHistory() {
        //given
        Task taskExpected = manager.createTask(new Task("name", "description", Status.NEW));

        //that
        manager.getTaskById(taskExpected.getId());
        List<Task> historyList = manager.getHistory();
        Task taskActual = historyList.getFirst();

        //than
        assertEquals(taskExpected.getId(), taskActual.getId(), "не совпадают id");
        assertEquals(taskExpected.getName(), taskActual.getName(),  "не совпадает name");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "не совпадают description");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "не совпадают status");
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

        //that
        SubTask subTaskActual= manager.getSubTaskById(subTask.getId());

        //than
        assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "не совпадают id");
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(),  "не совпадает name");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                "не совпадают description");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "не совпадают status");
        assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "не совпадают epicId");
    }
    @Test
    @DisplayName("Должен добавлять подзадачу в историю")
    void getSubTaskById_addSubTaskToHistory() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subTaskExpected = manager.createSubTask(new SubTask("name", "description", Status.NEW,
                epic.getId()));

        //that
        manager.getSubTaskById(subTaskExpected.getId());

        List<Task> historyList = manager.getHistory();
        SubTask subTaskActual = (SubTask) historyList.get(0);

        //than
        assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "не совпадают id");
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(),  "не совпадает name");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                "не совпадают description");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "не совпадают status");
        assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "не совпадают epicId");
    }


    @Test
    @DisplayName("Должен возвращать эпик по ID из менеджера")
    void getEpicById_returnEpicById() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        Epic epicExpected = new Epic("name", "description");
        epicExpected.setId(1);
        epicExpected.setStatus(Status.NEW);

        //that
        Epic epicActual= manager.getEpicById(epic.getId());

        //than
        assertEquals(epicExpected.getId(), epicActual.getId(), "не совпадают id");
        assertEquals(epicExpected.getName(), epicActual.getName(),  "не совпадает name");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(), "не совпадают description");
        assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "не совпадают status");
    }

    @Test
    @DisplayName("Должен добавлять эпик в историю")
    void getEpicById_addEpicToHistory() {
        //given
        Epic epicExpected = manager.createEpic(new Epic("name", "description"));

        //that
        manager.getEpicById(epicExpected.getId());

        List<Task> historyList = manager.getHistory();
        Epic epicActual = (Epic) historyList.get(0);

        //than
        assertEquals(epicExpected.getId(), epicActual.getId(), "не совпадают id");
        assertEquals(epicExpected.getName(), epicActual.getName(),  "не совпадает name");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(), "не совпадают description");
        assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "не совпадают status");
    }

    @Test
    @DisplayName("Должен возвращать задачу со сгенерированным id")
    void createTask_returnTaskWithGeneratedId() {
        //given
        Task task = new Task("name", "description", Status.NEW);
        task.setId(5);

        //that
        manager.createTask(task);

        //than
        assertEquals(1, task.getId(), "индивидуальный id не сгенерирован");
    }

@Test
@DisplayName("Должен добавить задачу в менеджер")
void createTask_putTaskInManager() {
    //given
    Task taskExpected = new Task("name", "description", Status.NEW);
    taskExpected.setId(1);

    //that
    manager.createTask(taskExpected);
    List<Task> taskList = manager.getTasksList();
    Task taskActual = taskList.getFirst();

    //than
    assertEquals(taskExpected.getId(), taskActual.getId(), "не совпадают id");
    assertEquals(taskExpected.getName(), taskActual.getName(),  "не совпадает name");
    assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "не совпадают description");
    assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "не совпадают status");
}

    @Test
    @DisplayName("Должен возвращать подзадачу со сгенерированным id")
    void createSubTask_returnSubTaskWithGeneratedId() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = new SubTask("name", "description", Status.NEW, epic.getId());
        subtask.setId(5);

        //that
        manager.createSubTask(subtask);

        //than
        assertEquals(2, subtask.getId(), "индивидуальный id не сгенерирован");
    }

    @Test
    @DisplayName("Должен добавить подзадачу в менеджер")
    void createSubTask_putSubTaskInManager() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subTaskExpected = new SubTask("name", "description", Status.NEW, epic.getId());
        subTaskExpected.setId(2);

        //that
        manager.createSubTask(subTaskExpected);
        List<SubTask> subTaskList = manager.getSubTasksList();
        SubTask subTaskActual = subTaskList.getFirst();

        //than
        assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "не совпадают id");
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(),  "не совпадает name");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                "не совпадают description");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "не совпадают status");
        assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "не совпадают epicId");
    }

    @Test
    @DisplayName("Должен сохранить id подзадачи в ее эпике")
    void createSubTask_putIdToListInItsEpic() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subTask = new SubTask("name", "description", Status.NEW, epic.getId());

        //that
        manager.createSubTask(subTask);
        List<Integer> subTasksIdList = epic.getSubTasksId();

        //than
        assertEquals(2, subTasksIdList.getFirst(), "id не сохранился");
    }

    @Test
    @DisplayName("Должен вернуть эпик со сгенерированным id")
    void createEpic_returnEpicWithGeneratedId() {

        //given
        Epic epic = new Epic("name", "description");
        epic.setId(5);

        //that
        manager.createEpic(epic);

        //than
        assertEquals(1, epic.getId(), "индивидуальный id не сгенерирован");
    }

    @Test
    @DisplayName("Должен добавить эпик в менеджер")
    void createEpic_putEpicInManager() {
        //given
        Epic epicExpected = new Epic("name", "description");
        epicExpected.setId(1);
        epicExpected.setStatus(Status.NEW);

        //that
        manager.createEpic(epicExpected);
        List<Epic> epicList = manager.getEpicList();
        Epic epicActual = epicList.getFirst();

        //than
        assertEquals(epicExpected.getId(), epicActual.getId(), "не совпадают id");
        assertEquals(epicExpected.getName(), epicActual.getName(),  "не совпадает name");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(), "не совпадают description");
        assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "не совпадают status");
    }

    @Test
    @DisplayName("Должен установить статус эпика NEW")
    void createEpic_makeStatusNew() {
        //given
        Epic epic = new Epic("name", "description");

        //that
        manager.createEpic(epic);

        //than
        assertEquals(Status.NEW, epic.getStatus(), "статус NEW не установлен");
    }

    @Test
    @DisplayName("Должен поменять задачу на новую по id")
    void updateTask_changeTaskToNewOne_tasksHaveSameId() {

        //given
        Task task = manager.createTask(new Task("name", "description", Status.DONE));
        Task taskExpected = new Task("NewName", "NewDescription", Status.IN_PROGRESS);
        taskExpected.setId(1);

        //that
        manager.updateTask(taskExpected);
        Task taskActual = manager.getTaskById(task.getId());

        //than
        assertEquals(taskExpected.getName(), taskActual.getName(),  "не совпадает name");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "не совпадают description");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "не совпадают status");
    }

    @Test
    @DisplayName("Должен поменять все поля кроме epicId у подзадачи на новые по id")
    void updateSubTask_changeAllFieldsExceptEpicId_subtasksHaveSameId() {

        //given
        Epic epic1 = manager.createEpic(new Epic("name", "description"));
        Epic epic2 = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = manager.createSubTask(new SubTask("name", "description", Status.NEW,
                epic1.getId()));

        SubTask subTaskExpected = new SubTask("NewName", "NewDescription", Status.IN_PROGRESS,
                epic2.getId());
        subTaskExpected.setId(3);

        //that
        manager.updateSubTask(subTaskExpected);
        SubTask subTaskActual = manager.getSubTaskById(3);

        //than
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(),  "не совпадает name");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                "не совпадают description");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "не совпадают status");

        assertNotEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "epicId не должен поменяться");
        assertEquals(subtask.getEpicId(), subTaskActual.getEpicId(), "epicId не должен поменяться");
    }

    @Test
    @DisplayName("Должен менять статус эпика на NEW, если статус всех его подзадач NEW")
    void updateSubTask_changeEpicsStatusToNEW_allItsSubtasksHaveStatusNEW() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("name1", "description1", Status.NEW,
                epic.getId()));
        SubTask subtask2 = manager.createSubTask(new SubTask("name2", "description2", Status.DONE,
                epic.getId()));
        Status statusBeforeChange = epic.getStatus();
        subtask2.setStatus(Status.NEW);

        //that
        manager.updateSubTask(subtask2);
        Status statusAfterChange = epic.getStatus();

        //than
        assertNotEquals(statusBeforeChange, statusAfterChange, "статус эпика не обновился");
        assertEquals(Status.NEW, epic.getStatus(), "статус эпика не поменялся");
    }

    @Test
    @DisplayName("Должен менять статус эпика на DONE, если статус всех его подзадач DONE")
    void updateSubTask_changeEpicsStatusToDONE_allItsSubtasksHaveStatusDONE() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("name1", "description1", Status.NEW,
                epic.getId()));
        SubTask subtask2 = manager.createSubTask(new SubTask("name2", "description2", Status.DONE,
                epic.getId()));
        Status statusBeforeChange = epic.getStatus();
        subtask1.setStatus(Status.DONE);


        //that
        manager.updateSubTask(subtask1);
        Status statusAfterChange = epic.getStatus();

        //than
        assertNotEquals(statusBeforeChange, statusAfterChange, "статус эпика не обновился");
        assertEquals(Status.DONE, epic.getStatus(), "статус эпика не поменялся");
    }
    @Test
    @DisplayName("Должен менять статус эпика на IN_PROGRESS, если статус его подзадач не одинаковый")
    void updateSubTask_changeEpicsStatusToINPROGRESS_statusOfItsSubtasksIsDiverse() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("name1", "description1", Status.NEW,
                epic.getId()));
        SubTask subtask2 = manager.createSubTask(new SubTask("name2", "description2", Status.NEW,
                epic.getId()));
        Status statusBeforeChange = epic.getStatus();
        subtask1.setStatus(Status.DONE);

        //that
        manager.updateSubTask(subtask1);
        Status statusAfterChange = epic.getStatus();

        //than
        assertNotEquals(statusBeforeChange, statusAfterChange, "статус эпика не обновился");
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "статус эпика не поменялся");

    }


    @Test
    @DisplayName("Должен поменять все поля кроме статуса у эпика на новые по id")
    void updateEpic_changeAllFieldsExceptStatus_epicHaveSameId() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        Epic epicExpected = new Epic("NewName", "NewDescription");
        epicExpected.setId(1);
        epicExpected.setStatus(Status.DONE);

        //that
        manager.updateEpic(epicExpected);
        Epic epicActual = manager.getEpicById(epic.getId());

        //than
        assertEquals(epicExpected.getName(), epicActual.getName(),  "не совпадает name");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(), "не совпадают description");

        assertNotEquals(epicExpected.getStatus(), epicActual.getStatus(), "status не должен поменяться");
        assertEquals(epic.getStatus(), epicActual.getStatus(), "status не должен поменяться");
    }

    @Test
    @DisplayName("Должен удалять задачу из менеджера по id")
    void deleteTaskById_deleteTaskById() {
        //given
        Task task = manager.createTask(new Task("name", "description", Status.DONE));

        //that
        manager.deleteTaskById(task.getId());

        //than
        assertNull(manager.getTaskById(task.getId()), "задача не удалилась");
    }


    @Test
    @DisplayName("Должен удалять подзадачу из менеджера по id")
    void deleteSubTaskById_deleteSubTaskById() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = manager.createSubTask(new SubTask("name", "description", Status.NEW,
                epic.getId()));

        //that
        manager.deleteSubTaskById(subtask.getId());

        //than
        assertNull(manager.getSubTaskById(subtask.getId()), "подзадача не удалилась");
    }

    @Test
    @DisplayName("Должен удалять id подзадачи из списка в эпике")
    void deleteSubTaskById_deleteSubTasksIdFromEpic() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask = manager.createSubTask(new SubTask("name", "description", Status.NEW,
                epic.getId()));

        //that
        manager.deleteSubTaskById(subtask.getId());
        List<Integer> subTasksId = epic.getSubTasksId();

        //than
        assertTrue(subTasksId.isEmpty(), "подзадача не удалилась");
    }

    @Test
    @DisplayName("Должен удалять эпик из менеджера по id")
    void deleteEpicById_deleteEpicFromManagerById() {
        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));

        //that
        manager.deleteEpicById(epic.getId());

        //than
        assertNull(manager.getTaskById(epic.getId()), "эпик не удалился");

    }

    @Test
    @DisplayName("Должен удалять все подзадачи удаляемого эпика из менеджера по id эпика")
    void deleteEpicById_deleteAllEpicsSubTasksFromManager() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("name1", "description1", Status.NEW,
                epic.getId()));
        SubTask subtask2 = manager.createSubTask(new SubTask("name2", "description2", Status.DONE,
                epic.getId()));

        //that
        manager.deleteEpicById(epic.getId());

        //than
        assertNull(manager.getSubTaskById(subtask1.getId()), "подзадача не удалилась");
        assertNull(manager.getSubTaskById(subtask2.getId()), "подзадача не удалилась");
    }

    @Test
    @DisplayName("Должен возвращать список подзадач эпика")
    void getSubTasksByEpic_shouldGetListSubTasksByEpic() {

        //given
        Epic epic = manager.createEpic(new Epic("name", "description"));
        SubTask subtask1 = manager.createSubTask(new SubTask("name1", "description1", Status.NEW,
                epic.getId()));
        SubTask subtask2 = manager.createSubTask(new SubTask("name2", "description2", Status.DONE,
                epic.getId()));

        List<SubTask> listExpected = new ArrayList<>();
        listExpected.add(subtask1);
        listExpected.add(subtask2);

        //that
        List<SubTask> listActual = manager.getSubTasksByEpic(epic);

        //than
        assertEquals(listExpected.size(), listActual.size(), "списки разного размера");

        for (SubTask subTaskExpected : listExpected) {
            SubTask subTaskActual = listActual.get(listExpected.indexOf(subTaskExpected));

            assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "не совпадают id");
            assertEquals(subTaskExpected.getName(), subTaskActual.getName(),  "не совпадает name");
            assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                    "не совпадают description");
            assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "не совпадают status");
            assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "не совпадают epicId");
        }
    }

    @Test
    @DisplayName("Должен возвращать список задач")
    void getTasksList_returnTasksList() {

        //given
        Task task1 = manager.createTask(new Task("name", "description", Status.DONE));
        Task task2 = manager.createTask(new Task("name", "description", Status.DONE));

        List<Task> listExpected = new ArrayList<>();
        listExpected.add(task1);
        listExpected.add(task2);

        //that
        List<Task> listActual = manager.getTasksList();

        //than
        assertEquals(listExpected.size(), listActual.size(), "списки разного размера");

        for (Task taskExpected : listExpected) {
            Task taskActual = listActual.get(listExpected.indexOf(taskExpected));

            assertEquals(taskExpected.getId(), taskActual.getId(), "не совпадают id");
            assertEquals(taskExpected.getName(), taskActual.getName(),  "не совпадает name");
            assertEquals(taskExpected.getDescription(), taskActual.getDescription(),
                    "не совпадают description");
            assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "не совпадают status");
        }
    }

    @Test
    @DisplayName("Должен возвращать список подзадач")
    void getSubTasksList_returnSubTasksList() {

        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description1"));
        Epic epic2 = manager.createEpic(new Epic("name2", "description2"));
        SubTask subtask1 = manager.createSubTask(new SubTask("name1", "description1", Status.NEW,
                epic1.getId()));
        SubTask subtask2 = manager.createSubTask(new SubTask("name2", "description2", Status.DONE,
                epic2.getId()));

        List<SubTask> listExpected = new ArrayList<>();
        listExpected.add(subtask1);
        listExpected.add(subtask2);

        //that
        List<SubTask> listActual = manager.getSubTasksList();

        assertEquals(listExpected.size(), listActual.size(), "списки разного размера");
        for (SubTask subTaskExpected : listExpected) {
            SubTask subTaskActual = listActual.get(listExpected.indexOf(subTaskExpected));

            assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "не совпадают id");
            assertEquals(subTaskExpected.getName(), subTaskActual.getName(),  "не совпадает name");
            assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(),
                    "не совпадают description");
            assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "не совпадают status");
            assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "не совпадают epicId");
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

        //that
        List<Epic> listActual = manager.getEpicList();

        //than
        assertEquals(listExpected.size(), listActual.size(), "списки разного размера");

        for (Epic epicExpected : listExpected) {
            Epic epicActual = listActual.get(listExpected.indexOf(epicExpected));

            assertEquals(epicExpected.getId(), epicActual.getId(), "не совпадают id");
            assertEquals(epicExpected.getName(), epicActual.getName(),  "не совпадает name");
            assertEquals(epicExpected.getDescription(), epicActual.getDescription(),
                    "не совпадают description");
            assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "не совпадают status");

        }
    }

    @Test
    @DisplayName("Должен удалять все задачи")
    void deleteAllTasks_deleteAllTasks() {

        //given
        Task task1 = manager.createTask(new Task("name", "description", Status.DONE));
        Task task2 = manager.createTask(new Task("name", "description", Status.DONE));

        //that
        manager.deleteAllTasks();
        List<Task> tasksList = manager.getTasksList();

        //than
        assertNull(manager.getTaskById(task1.getId()), "задача не удалилась");
        assertNull(manager.getTaskById(task2.getId()), "задача не удалилась");
        assertTrue(tasksList.isEmpty(), "задачи не удалились");
    }

    @Test
    @DisplayName("Должен удалять все подзадачи из менеджера")
    void deleteAllSubTasks_deleteAllSubTasksFromManager() {

        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description1"));
        Epic epic2 = manager.createEpic(new Epic("name2", "description2"));
        SubTask subtask1 = manager.createSubTask(new SubTask("name1", "description1", Status.NEW,
                epic1.getId()));
        SubTask subtask2 = manager.createSubTask(new SubTask("name2", "description2", Status.DONE,
                epic2.getId()));

        //that
        manager.deleteAllSubTasks();
        List<SubTask> subTasksList = manager.getSubTasksList();

        //than
        assertNull(manager.getSubTaskById(subtask1.getId()), "задача не удалилась");
        assertNull(manager.getSubTaskById(subtask2.getId()), "задача не удалилась");
        assertTrue(subTasksList.isEmpty(), "задачи не удалились");
    }

    @Test
    @DisplayName("Должен очищать листы с id подзадач в эпиках")
    void deleteAllSubTasks_clearSubTasksIdListsInEpics() {

        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description1"));
        Epic epic2 = manager.createEpic(new Epic("name2", "description2"));
        SubTask subtask1 = manager.createSubTask(new SubTask("name1", "description1", Status.NEW,
                epic1.getId()));
        SubTask subtask2 = manager.createSubTask(new SubTask("name2", "description2", Status.DONE,
                epic2.getId()));


        //that
        manager.deleteAllSubTasks();
        List<Integer> subTasksIdInEpic1 = epic1.getSubTasksId();
        List<Integer> subTasksIdInEpic2 = epic2.getSubTasksId();

        //than
        assertTrue(subTasksIdInEpic1.isEmpty(), "задачи не удалились");
        assertTrue(subTasksIdInEpic2.isEmpty(), "задачи не удалились");
    }

    @Test
    @DisplayName("Должен удалять все эпики")
    void deleteAllEpics_deleteAllEpics() {

        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description1"));
        Epic epic2 = manager.createEpic(new Epic("name2", "description2"));

        //that
        manager.deleteAllEpics();
        List<Epic> epicList = manager.getEpicList();

        //than
        assertNull(manager.getEpicById(epic1.getId()), "задача не удалилась");
        assertNull(manager.getEpicById(epic2.getId()), "задача не удалилась");
        assertTrue(epicList.isEmpty(), "задачи не удалились");
    }

    @Test
    @DisplayName("Должен удалять все подзадачи")
    void deleteAllEpics_deleteAllSubTasks() {

        //given
        Epic epic1 = manager.createEpic(new Epic("name1", "description1"));
        Epic epic2 = manager.createEpic(new Epic("name2", "description2"));
        SubTask subtask1 = manager.createSubTask(new SubTask("name1", "description1", Status.NEW,
                epic1.getId()));
        SubTask subtask2 = manager.createSubTask(new SubTask("name2", "description2", Status.DONE,
                epic2.getId()));

        //that
        manager.deleteAllEpics();
        List<SubTask> subTasksList = manager.getSubTasksList();

        //than
        assertNull(manager.getSubTaskById(subtask1.getId()), "задача не удалилась");
        assertNull(manager.getSubTaskById(subtask2.getId()), "задача не удалилась");
        assertTrue(subTasksList.isEmpty(), "задачи не удалились");
    }
}