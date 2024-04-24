package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Задачи")
class TaskTest {

    @Test
    @DisplayName("Две задачи с одинаковым ID должны быть равны")
    void shouldBeEqualToCopy() {

        TaskManager manager = Managers.getDefaults();
        Task taskExpected = new Task("Имя", "Описание", Status.NEW);
        taskExpected.setStatus(Status.NEW);
        taskExpected.setId(1);

        Task task = new Task("Имя", "Описание", Status.NEW);
        manager.createTask(task);
        Task taskSaved = manager.getTaskById(task.getId());

        assertEqualsTask(taskExpected, taskSaved, "Эпики не равны");

        Task task1 = manager.getTaskById(1);
        Task task2 = manager.getTaskById(1);
        assertEqualsTask(task1, task2, "Эпики не равны");
    }

    private static void assertEqualsTask(Task expected, Task actual, String massage) {
        assertEquals(expected.getId(), actual.getId(), massage + ", id");
        assertEquals(expected.getName(), actual.getName(), massage + ", name");
        assertEquals(expected.getDescription(), actual.getDescription(), massage + ", description");
        assertEquals(expected.getStatus(), actual.getStatus(), massage + ", status");

    }
}