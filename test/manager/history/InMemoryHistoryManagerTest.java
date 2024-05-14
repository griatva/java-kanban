package manager.history;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class InMemoryHistoryManagerTest {

    @Test
    @DisplayName("При добавлении задачи должен удалять повтор данной задачи из истории")
    void addTaskInHistory_deleteTheSameTaskFromHistory_addTaskInHistory() {

        //given
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

        Task task = new Task("name", "description", Status.NEW);
        task.setId(1);
        Epic epic = new Epic("name", "description");
        epic.setId(2);
        SubTask subtask = new SubTask("name", "description", Status.NEW, epic.getId());
        subtask.setId(3);

        //when
        inMemoryHistoryManager.addTaskInHistory(task);
        inMemoryHistoryManager.addTaskInHistory(epic);
        inMemoryHistoryManager.addTaskInHistory(subtask);

        inMemoryHistoryManager.addTaskInHistory(task);

        List<Task> history = inMemoryHistoryManager.getHistory();

        //then
        assertEquals(3, history.size(), "задач в листе больше, чем должно быть");
        assertNotEquals(task, history.getFirst(), "повтор не удалился");

    }

    @Test
    @DisplayName("Должен добавлять задачу в конец списка")
    void addTaskInHistory_addTaskToEndOfList() {
        //given
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

        Task task = new Task("name", "description", Status.NEW);
        task.setId(1);
        Epic epic = new Epic("name", "description");
        epic.setId(2);

        SubTask subTaskExpected = new SubTask("name", "description", Status.NEW, epic.getId());
        subTaskExpected.setId(3);

        //when
        inMemoryHistoryManager.addTaskInHistory(task);
        inMemoryHistoryManager.addTaskInHistory(epic);
        inMemoryHistoryManager.addTaskInHistory(subTaskExpected);

        List<Task> history = inMemoryHistoryManager.getHistory();

        SubTask subTaskActual = (SubTask) history.getLast();

        //then
        assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "не совпадают id");
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "не совпадает name");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(), "не совпадают description");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "не совпадают status");
        assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "не совпадают epicId");
    }

    @Test
    @DisplayName("Должен возвращать список просмотра задач в порядке их добавления")
    void getHistory_returnHistoryListInOrderTheyWereAdded() {
        //given
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

        Task taskExpected = new Task("name", "description", Status.NEW);
        taskExpected.setId(1);
        Epic epicExpected = new Epic("name", "description");
        epicExpected.setId(2);
        SubTask subTaskExpected = new SubTask("name", "description", Status.NEW, epicExpected.getId());
        subTaskExpected.setId(3);
        inMemoryHistoryManager.addTaskInHistory(taskExpected);
        inMemoryHistoryManager.addTaskInHistory(epicExpected);
        inMemoryHistoryManager.addTaskInHistory(subTaskExpected);

        //when
        List<Task> history = inMemoryHistoryManager.getHistory();

        Task taskActual = history.getFirst();
        Epic epicActual = (Epic) history.get(1);
        SubTask subTaskActual = (SubTask) history.getLast();

        //then
        assertEquals(taskExpected.getId(), taskActual.getId(), "не совпадают id");
        assertEquals(taskExpected.getName(), taskActual.getName(), "не совпадает name");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "не совпадают description");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "не совпадают status");

        assertEquals(epicExpected.getId(), epicActual.getId(), "не совпадают id");
        assertEquals(epicExpected.getName(), epicActual.getName(), "не совпадает name");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(), "не совпадают description");
        assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "не совпадают status");

        assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "не совпадают id");
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "не совпадает name");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(), "не совпадают description");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "не совпадают status");
        assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "не совпадают epicId");
    }

    @Test
    @DisplayName("Должен удалить задачу из истории по Id, сохранив упорядоченность списка")
    void remove_deleteTaskFromHistoryByIdAndSaveOrderOfTasksInList() {

        //given
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

        Task taskExpected = new Task("name", "description", Status.NEW);
        taskExpected.setId(1);
        Epic epicExpected = new Epic("name", "description");
        epicExpected.setId(2);
        Epic epicForRemove = new Epic("name", "description");
        epicForRemove.setId(3);
        SubTask subTaskExpected = new SubTask("name", "description", Status.NEW, epicExpected.getId());
        subTaskExpected.setId(4);

        inMemoryHistoryManager.addTaskInHistory(taskExpected);
        inMemoryHistoryManager.addTaskInHistory(epicExpected);
        inMemoryHistoryManager.addTaskInHistory(epicForRemove);
        inMemoryHistoryManager.addTaskInHistory(subTaskExpected);

        //when
        inMemoryHistoryManager.remove(epicForRemove.getId());

        List<Task> history = inMemoryHistoryManager.getHistory();
        Task taskActual = history.getFirst();
        Epic epicActual = (Epic) history.get(1);
        SubTask subTaskActual = (SubTask) history.getLast();

        //then
        assertEquals(3, history.size(), "задача не удалилась");

        assertEquals(taskExpected.getId(), taskActual.getId(), "не совпадают id");
        assertEquals(taskExpected.getName(), taskActual.getName(), "не совпадает name");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "не совпадают description");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "не совпадают status");

        assertEquals(epicExpected.getId(), epicActual.getId(), "не совпадают id");
        assertEquals(epicExpected.getName(), epicActual.getName(), "не совпадает name");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(), "не совпадают description");
        assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "не совпадают status");

        assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "не совпадают id");
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "не совпадает name");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(), "не совпадают description");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "не совпадают status");
        assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "не совпадают epicId");
    }
}