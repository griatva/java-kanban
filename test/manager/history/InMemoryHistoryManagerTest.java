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
    @DisplayName("Adding a task should remove its duplicate from the history")
    void addTaskInHistory_deleteTheSameTaskFromHistory_addTaskInHistory() {

        //given
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

        Task task = new Task("name", "description", Status.NEW);
        task.setId(1);
        Epic epic = new Epic("name", "description");
        epic.setId(2);
        SubTask subtask = new SubTask("name", "description", Status.NEW, epic.getId());
        subtask.setId(3);
        inMemoryHistoryManager.addTaskInHistory(task);
        inMemoryHistoryManager.addTaskInHistory(epic);
        inMemoryHistoryManager.addTaskInHistory(subtask);

        //when
        inMemoryHistoryManager.addTaskInHistory(task);
        List<Task> history = inMemoryHistoryManager.getHistory();

        //then
        assertEquals(3, history.size(), "There are more tasks in the list than expected");
        assertNotEquals(task, history.getFirst(), "Failed to remove duplicate");

    }

    @Test
    @DisplayName("Should add the task to the end of the list")
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
        assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "IDs do not match");
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "Names do not match");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(), "Descriptions do not match");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "Statuses do not match");
        assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "Epic IDs do not match");
    }

    @Test
    @DisplayName("Should return the task viewing history in the order they were added")
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
        assertEquals(taskExpected.getId(), taskActual.getId(), "IDs do not match");
        assertEquals(taskExpected.getName(), taskActual.getName(), "Names do not match");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "Descriptions do not match");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "Statuses do not match");

        assertEquals(epicExpected.getId(), epicActual.getId(), "IDs do not match");
        assertEquals(epicExpected.getName(), epicActual.getName(), "Names do not match");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(), "Descriptions do not match");
        assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "Statuses do not match");

        assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "IDs do not match");
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "Names do not match");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(), "Descriptions do not match");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "Statuses do not match");
        assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "Epic IDs do not match");

    }

    @Test
    @DisplayName("Should remove a task from the history by ID while preserving the list order")
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
        assertEquals(3, history.size(), "Task was not removed");

        assertEquals(taskExpected.getId(), taskActual.getId(), "IDs do not match");
        assertEquals(taskExpected.getName(), taskActual.getName(), "Names do not match");
        assertEquals(taskExpected.getDescription(), taskActual.getDescription(), "Descriptions do not match");
        assertEquals(taskExpected.getStatus(), taskActual.getStatus(), "Statuses do not match");

        assertEquals(epicExpected.getId(), epicActual.getId(), "IDs do not match");
        assertEquals(epicExpected.getName(), epicActual.getName(), "Names do not match");
        assertEquals(epicExpected.getDescription(), epicActual.getDescription(), "Descriptions do not match");
        assertEquals(epicExpected.getStatus(), epicActual.getStatus(), "Statuses do not match");

        assertEquals(subTaskExpected.getId(), subTaskActual.getId(), "IDs do not match");
        assertEquals(subTaskExpected.getName(), subTaskActual.getName(), "Names do not match");
        assertEquals(subTaskExpected.getDescription(), subTaskActual.getDescription(), "Descriptions do not match");
        assertEquals(subTaskExpected.getStatus(), subTaskActual.getStatus(), "Statuses do not match");
        assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), "Epic IDs do not match");

    }
}