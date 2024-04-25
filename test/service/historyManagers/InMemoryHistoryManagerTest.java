package service.historyManagers;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    @DisplayName("Должен удалять первый элемент, если добавляется больше 10 задач")
    void addTaskInHistory_deleteFirstTask_addMoreThan10Elements() {

        //given
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

        Task task = new Task("name", "description", Status.NEW);
        task.setId(1);
        Epic epic = new Epic("name", "description");
        epic.setId(2);
        SubTask subtask = new SubTask("name", "description", Status.NEW, epic.getId());
        subtask.setId(3);

        //that
        inMemoryHistoryManager.addTaskInHistory(task);
        inMemoryHistoryManager.addTaskInHistory(epic);
        inMemoryHistoryManager.addTaskInHistory(epic);
        inMemoryHistoryManager.addTaskInHistory(epic);
        inMemoryHistoryManager.addTaskInHistory(epic);
        inMemoryHistoryManager.addTaskInHistory(epic);
        inMemoryHistoryManager.addTaskInHistory(epic);
        inMemoryHistoryManager.addTaskInHistory(epic);
        inMemoryHistoryManager.addTaskInHistory(epic);
        inMemoryHistoryManager.addTaskInHistory(epic);
        inMemoryHistoryManager.addTaskInHistory(subtask);

        //than
        assertEquals(2, inMemoryHistoryManager.getHistory().getFirst().getId(),
                "Первый элемент не удалился");
        assertFalse(inMemoryHistoryManager.getHistory().contains(task), "Первый элемент не удалился");
        assertEquals(3, inMemoryHistoryManager.getHistory().getLast().getId(),
                "11-ый элемент не добавился в конец списка");
        assertEquals(10, inMemoryHistoryManager.getHistory().size(), "Список неправильного размера");
    }
}