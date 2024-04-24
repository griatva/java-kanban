package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Менеджеры")
class ManagersTest {

    @Test
    @DisplayName("Должен возвращать проинициализированные и готовые к работе экземпляры TaskManager")
    void shouldReturnInitializedAndReadyToUseTaskManagerInstances() {

        TaskManager manager = Managers.getDefaults();

        Task task1 = manager.createTask(new Task("имя задачи-1", "описание-1", Status.NEW));
        Epic epic1 = manager.createEpic(new Epic("имя эпика-1", "описание эпика-1"));
        SubTask subtask1 = manager.createSubTask(new SubTask("имя подзадачи-1", "описание-1",
                Status.NEW, epic1.getId()));

        Task task = manager.getTaskById(1);
        Epic epic = manager.getEpicById(2);
        SubTask subTask = manager.getSubTaskById(3);

        assertEquals(task1, task, "задача не добавлена, экземпляр TaskManager не готов к работе");
        assertEquals(epic1, epic, "эпик не добавлен, экземпляр TaskManager не готов к работе");
        assertEquals(subtask1, subTask, "задача не добавлена, экземпляр TaskManager не готов к работе");
    }
}