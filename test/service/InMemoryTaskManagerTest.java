package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Менеджер")
class InMemoryTaskManagerTest {

    private static TaskManager manager;
    private static Task task1;
    private static Task task2;
    private static Epic epic1;
    private static Epic epic2;
    private static SubTask subtask1;
    private static SubTask subtask2;
    private static SubTask subtask3;


    @BeforeEach
    void init() {

        manager = Managers.getDefaults();

        task1 = manager.createTask(new Task("имя задачи-1", "описание-1", Status.NEW));
        task2 = manager.createTask(new Task("имя задачи-2", "описание-2", Status.DONE));
        epic1 = manager.createEpic(new Epic("имя эпика-1", "описание эпика-1"));
        epic2 = manager.createEpic(new Epic("имя эпика-2", "описание эпика-2"));
        subtask1 = manager.createSubTask(new SubTask("имя подзадачи-1", "описание-1",
                Status.NEW, epic1.getId()));
        subtask2 = manager.createSubTask(new SubTask("имя подзадачи-2", "описание-2",
                Status.IN_PROGRESS, epic1.getId()));
        subtask3 = manager.createSubTask(new SubTask("имя подзадачи-3", "описание-3",
                Status.IN_PROGRESS, epic2.getId()));

    }

    @Test
    @DisplayName("Должен выдавать максимум 10 последних задач любого типа")
    void shouldShowLast10TasksOfAnyType() {
        get10Tasks();
        List<Task> historyFromManager = manager.getHistory();
        List<Task> historyExpected = put10TaskInList();
        assertEquals(historyExpected.size(), historyFromManager.size(), "Размер листов разный");
        for (Task task : historyExpected) {
            assertEquals(task.getId(), historyFromManager.get(historyExpected.indexOf(task)).getId(),
                    "Разный порядок задач в списках");
        }

        manager.getSubTaskById(7);
        assertEquals(2, manager.getHistory().getFirst().getId(), "Первый элемент не удалился");
        assertEquals(false, manager.getHistory().contains(task1), "Первый элемент не удалился");
        assertEquals(10, manager.getHistory().size(), "Список неправильного размера");
    }

    private static void get10Tasks() {
        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getEpicById(3);
        manager.getEpicById(4);
        manager.getSubTaskById(5);
        manager.getSubTaskById(6);
        manager.getSubTaskById(7);
        manager.getSubTaskById(7);
        manager.getSubTaskById(7);
        manager.getSubTaskById(7);
    }

    private static List<Task> put10TaskInList() {
        List<Task> historyTest = new ArrayList<>();
        historyTest.add(task1);
        historyTest.add(task2);
        historyTest.add(epic1);
        historyTest.add(epic2);
        historyTest.add(subtask1);
        historyTest.add(subtask2);
        historyTest.add(subtask3);
        historyTest.add(subtask3);
        historyTest.add(subtask3);
        historyTest.add(subtask3);
        return historyTest;
    }

    @Test
    @DisplayName("Должен возвращать задачу по ID из таблицы")
    void shouldReturnTaskById() {
        Task taskForTest1 = new Task("имя задачи-1", "описание-1", Status.NEW);
        taskForTest1.setId(1);
        Task taskFromManager1 = manager.getTaskById(1);
        assertEqualsTask(taskForTest1, taskFromManager1, "Возвращена неверная задача");
    }

    private static void assertEqualsTask(Task expected, Task actual, String massage) {
        assertEquals(expected.getId(), actual.getId(), massage + ", id");
        assertEquals(expected.getName(), actual.getName(), massage + ", name");
        assertEquals(expected.getDescription(), actual.getDescription(), massage + ", description");
        assertEquals(expected.getStatus(), actual.getStatus(), massage + ", status");
    }

    @Test
    @DisplayName("Должен возвращать подзадачу по ID из таблицы")
    void shouldReturnSubTaskById() {
        SubTask subTaskForTest1 = new SubTask("имя подзадачи-1", "описание-1",
                Status.NEW, epic1.getId());
        subTaskForTest1.setId(5);
        Task subTaskFromManager1 = manager.getSubTaskById(5);
        assertEqualsTask(subTaskForTest1, subTaskFromManager1, "Возвращена неверная подзадача");
    }

    @Test
    @DisplayName("Должен возвращать эпик по ID из таблицы")
    void shouldReturnEpicById() {
        Epic epicForTest1 = new Epic("имя эпика-1", "описание эпика-1");
        epicForTest1.setId(3);
        epicForTest1.setStatus(Status.IN_PROGRESS);
        Epic epicFromManager1 = manager.getEpicById(3);
        assertEqualsTask(epicForTest1, epicFromManager1, "возвращен неверный эпик");
    }

    @Test
    @DisplayName("Должен создать Id задаче и положить ее в HashMap")
    void shouldCreateTaskIdAndPutTaskInHashMap() {

        Task taskFromManager1 = manager.getTaskById(1);
        List<Task> tasksList = manager.getTasksList();

        assertNotNull(taskFromManager1, "задача не найдена");
        assertEqualsTask(task1, taskFromManager1, "задачи не совпадают");
        assertNotNull(tasksList, "задачи не возвращаются");
        assertEquals(2, tasksList.size(), "неверное количество задач");
        assertEqualsTask(task1, tasksList.get(0), "задачи не совпадают");
    }

    @Test
    @DisplayName("Должен создать Id подзадаче и положить ее в HashMap и положить свой Id в соответствующий эпик")
    void shouldCreateSubTaskIdAndPutSubTaskInHashMapAndPutSubTasksIdInTheAppropriateEpic() {

        SubTask subTaskFromManager1 = manager.getSubTaskById(5);
        List<SubTask> subTasksList = manager.getSubTasksList();
        List<Integer> subTasksId = manager.getEpicById(subTaskFromManager1.getEpicId()).getSubTasksId();

        assertNotNull(subTaskFromManager1, "подзадача не найдена");
        assertEqualsTask(subtask1, subTaskFromManager1, "подзадачи не совпадают");
        assertNotNull(subTasksList, "подзадачи не возвращаются");
        assertEquals(3, subTasksList.size(), "неверное количество подзадач");
        assertEqualsTask(subtask1, subTasksList.get(0), "подзадачи не совпадают");
        assertEquals(subTaskFromManager1.getId(), subTasksId.get(0),
                "Id не совпадают, подзадача не добавилась в эпик");

    }

    @Test
    @DisplayName("Должен создать Id эпику и положить его в HashMap")
    void shouldCreateEpicIdAndPutEpicInHashMap() {

        Epic EpicFomManager1 = manager.getEpicById(3);
        List<Epic> epicsList = manager.getEpicList();

        assertNotNull(EpicFomManager1, "эпик не найден");
        assertEqualsTask(epic1, EpicFomManager1, "эпики не совпадают");
        assertNotNull(epicsList, "эпик не возвращаются");
        assertEquals(2, epicsList.size(), "неверное количество эпиков");
        assertEqualsTask(epic1, epicsList.get(0), "эпики не совпадают");
    }

    @Test
    @DisplayName("Должен поменять задачу в таблице на новую")
    void shouldChangeTaskInHashMapToANewOne() {

        Task taskForTest1 = new Task("NEW имя задачи-1", "NEW описание-1", Status.DONE);
        taskForTest1.setId(1);
        manager.updateTask(taskForTest1);
        Task newTaskFromManager = manager.getTaskById(1);

        assertEqualsTask(taskForTest1, newTaskFromManager, "задача не заменилась");
    }

    @Test
    @DisplayName("Должен поменять имя, описание и статус у подзадачи на новые")
    void shouldChangeNameDescriptionStatusOfSubtaskToNewOnes() {
        SubTask subTaskForTest1 = new SubTask("NEW имя подзадачи-1", "NEW описание-1",
                Status.DONE, epic1.getId());
        subTaskForTest1.setId(5);
        manager.updateSubTask(subTaskForTest1);
        SubTask newSubTaskFromManager = manager.getSubTaskById(5);

        assertEqualsTask(subTaskForTest1, newSubTaskFromManager, "подзадача не заменилась");
    }

    @Test
    @DisplayName("Должен поменять имя и описание у эпика на новые")
    void shouldChangeNameDescriptionOfEpicToNewOnes() {
        Epic epicForTest1 = new Epic("NEW имя эпика-1", "NEW описание эпика-1");
        epicForTest1.setId(3);
        epicForTest1.setStatus(Status.IN_PROGRESS);
        manager.updateEpic(epicForTest1);
        Epic newEpicFromManager = manager.getEpicById(3);

        assertEqualsTask(epicForTest1, newEpicFromManager, "эпик не заменился");
    }

    @Test
    @DisplayName("Должен удалять задачу из HashMap по id")
    void shouldDeleteTaskById() {
        assertNotNull(manager.getTaskById(1), "Задачи нет");
        manager.deleteTaskById(1);
        assertNull(manager.getTaskById(1), "задача не удалилась");
        List<Task> tasksList = manager.getTasksList();
        assertEquals(1, tasksList.size(), "задача не удалилась");
    }

    @Test
    @DisplayName("Должен удалять подзадачу из HashMap по id и id подзадачи из списка в эпике")
    void shouldDeleteSubTaskByIdAndSubTasksIdFromListInEpic() {
        assertNotNull(manager.getSubTaskById(5), "подзадачи нет");
        assertEquals(5, manager.getEpicById(subtask1.getEpicId()).getSubTasksId().get(0),
                "подзадачи нет в эпике");

        manager.deleteSubTaskById(5);
        assertNull(manager.getSubTaskById(5), "подзадача не удалилась");

        List<SubTask> subTasksList = manager.getSubTasksList();
        List<Integer> subTasksIdInEpic = manager.getEpicById(subtask1.getEpicId()).getSubTasksId();

        assertEquals(false, subTasksIdInEpic.contains(5), "подзадача не удалилась");
        assertEquals(1, subTasksIdInEpic.size(), "подзадача не удалилась");
        assertEquals(2, subTasksList.size(), "подзадача не удалилась");
    }

    @Test
    @DisplayName("Должен удалять эпик и все его подзадачи из HashMap по id")
    void shouldDeleteEpicAndAllItsSubTasksById() {
        assertNotNull(manager.getEpicById(3), "эпика нет");
        List<Integer> subTasksIdInEpic = epic1.getSubTasksId();
        assertEquals(2, subTasksIdInEpic.size(), "в эпике нет подзадач");
        assertNotNull(manager.getSubTaskById(5), "подзадачи нет");
        assertNotNull(manager.getSubTaskById(6), "подзадачи нет");

        manager.deleteEpicById(3);
        assertNull(manager.getEpicById(3), "эпик не удалился");
        assertNull(manager.getSubTaskById(5), "подзадача не удалилсь");
        assertNull(manager.getSubTaskById(6), "подзадача не удалилсь");
        List<Epic> epicList = manager.getEpicList();
        List<SubTask> subTaskList = manager.getSubTasksList();
        assertEquals(1, epicList.size(), "эпик не удалился");
        assertEquals(1, subTaskList.size(), "подзадача не удалилась");
    }

    @Test
    @DisplayName("Должен возвращать список подзадач эпика")
    void shouldGetSubTasksByEpic() {
        List<SubTask> subTasksByEpic = new ArrayList<>();
        subTasksByEpic.add(subtask1);
        subTasksByEpic.add(subtask2);

        List<SubTask> subTasksByEpicFromManager = manager.getSubTasksByEpic(epic1);
        assertEquals(subTasksByEpic.size(), subTasksByEpicFromManager.size(), "списки разного размера");

        for (SubTask subTask : subTasksByEpic) {
            assertEqualsTask(subTask, subTasksByEpicFromManager.get(subTasksByEpic.indexOf(subTask)),
                    "задачи не равны");
        }
    }

    @Test
    @DisplayName("Должен возвращать список задач")
    void shouldGetTasksList() {
        List<Task> taskList = new ArrayList<>();
        taskList.add(task1);
        taskList.add(task2);

        List<Task> taskListFromManager = manager.getTasksList();

        assertEquals(taskList.size(), taskListFromManager.size(), "списки разного размера");
        for (Task task : taskList) {
            assertEqualsTask(task, taskListFromManager.get(taskList.indexOf(task)),
                    "задачи не равны");
        }
    }

    @Test
    @DisplayName("Должен возвращать список подзадач")
    void shouldGetSubTasksList() {
        List<SubTask> subTaskList = new ArrayList<>();
        subTaskList.add(subtask1);
        subTaskList.add(subtask2);
        subTaskList.add(subtask3);

        List<SubTask> subTaskListFromManager = manager.getSubTasksList();

        assertEquals(subTaskList.size(), subTaskListFromManager.size(), "списки разного размера");
        for (SubTask subTask : subTaskList) {
            assertEqualsTask(subTask, subTaskListFromManager.get(subTaskList.indexOf(subTask)),
                    "задачи не равны");
        }
    }

    @Test
    @DisplayName("Должен возвращать список эпиков")
    void shouldGetEpicList() {
        List<Epic> epicList = new ArrayList<>();
        epicList.add(epic1);
        epicList.add(epic2);

        List<Epic> epicListFromManager = manager.getEpicList();

        assertEquals(epicList.size(), epicListFromManager.size(), "списки разного размера");
        for (Epic epic : epicList) {
            assertEqualsTask(epic, epicListFromManager.get(epicList.indexOf(epic)),
                    "задачи не равны");
        }
    }

    @Test
    @DisplayName("Должен удалять все задачи")
    void shouldDeleteAllTasks() {
        assertNotNull(manager.getTaskById(1), "Задачи нет");
        assertNotNull(manager.getTaskById(2), "Задачи нет");

        manager.deleteAllTasks();

        assertNull(manager.getTaskById(1), "задача не удалилась");
        assertNull(manager.getTaskById(2), "задача не удалилась");

        List<Task> tasksList = manager.getTasksList();
        assertEquals(0, tasksList.size(), "задачи не удалились");
    }

    @Test
    @DisplayName("Должен удалять все подзадачи")
    void shouldDeleteAllSubTasks() {
        assertNotNull(manager.getSubTaskById(5), "Задачи нет");
        assertNotNull(manager.getSubTaskById(6), "Задачи нет");
        assertNotNull(manager.getSubTaskById(7), "Задачи нет");

        manager.deleteAllSubTasks();

        assertNull(manager.getSubTaskById(5), "задача не удалилась");
        assertNull(manager.getSubTaskById(6), "задача не удалилась");
        assertNull(manager.getSubTaskById(7), "задача не удалилась");

        List<SubTask> subTasksList = manager.getSubTasksList();
        assertEquals(0, subTasksList.size(), "задачи не удалились");

        List<Integer> subTasksInEpic1 = epic1.getSubTasksId();
        List<Integer> subTasksInEpic2 = epic2.getSubTasksId();
        assertEquals(0, subTasksInEpic1.size(), "задачи не удалились");
        assertEquals(0, subTasksInEpic2.size(), "задачи не удалились");
    }

    @Test
    @DisplayName("Должен удалять все подзадачи и их подзадачи")
    void shouldDeleteAllEpics() {
        assertNotNull(manager.getEpicById(3), "Задачи нет");
        assertNotNull(manager.getEpicById(4), "Задачи нет");
        assertNotNull(manager.getSubTaskById(5), "Задачи нет");
        assertNotNull(manager.getSubTaskById(6), "Задачи нет");
        assertNotNull(manager.getSubTaskById(7), "Задачи нет");

        manager.deleteAllEpics();

        assertNull(manager.getEpicById(3), "задача не удалилась");
        assertNull(manager.getEpicById(4), "задача не удалилась");
        assertNull(manager.getSubTaskById(5), "задача не удалилась");
        assertNull(manager.getSubTaskById(6), "задача не удалилась");
        assertNull(manager.getSubTaskById(7), "задача не удалилась");


        List<Epic> epicList = manager.getEpicList();
        assertEquals(0, epicList.size(), "задачи не удалились");
        List<SubTask> subTasksList = manager.getSubTasksList();
        assertEquals(0, subTasksList.size(), "задачи не удалились");
    }

    @Test
    @DisplayName("Должен менять статус эпика в зависимости от статуса его подзадач")
    void shouldChangeStatusOfEpicDependingOnStatusOfItsSubtasks() {
        assertEquals(Status.IN_PROGRESS, epic1.getStatus(), "Статус эпика не IN_PROGRESS");
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.NEW);
        manager.updateSubTask(subtask1);
        manager.updateSubTask(subtask2);
        assertEquals(Status.NEW, epic1.getStatus(), "Статус эпика не изменился на NEW");

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        manager.updateSubTask(subtask1);
        manager.updateSubTask(subtask2);
        assertEquals(Status.DONE, epic1.getStatus(), "Статус эпика не изменился на DONE");

        subtask1.setStatus(Status.NEW);
        manager.updateSubTask(subtask1);
        assertEquals(Status.IN_PROGRESS, epic1.getStatus(), "Статус эпика не изменился на IN_PROGRESS");

        manager.deleteAllSubTasks();
        assertEquals(Status.NEW, epic1.getStatus(), "Статус эпика не изменился на NEW");
    }
}