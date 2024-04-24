package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Подзадача")
class SubTaskTest {

    @Test
    @DisplayName("Две подзадачи с одинаковым ID должны быть равны")
    void shouldbeBeEqualToCopy() {
        TaskManager manager = Managers.getDefaults();

        Epic epic = new Epic("Эпик", "Описание эпика");
        Epic epicForTest = manager.createEpic(epic);

        SubTask subTaskExpected = new SubTask("Имя подзадачи", "Описание подзадачи",
                Status.NEW, epicForTest.getId());
        subTaskExpected.setId(2);

        SubTask subTask = new SubTask("Имя подзадачи", "Описание подзадачи",
                Status.NEW, epicForTest.getId());
        manager.createSubTask(subTask);
        SubTask subTaskSaved = manager.getSubTaskById(subTask.getId());

        assertEqualsTask(subTaskExpected, subTaskSaved, "Подзадачи не равны");

        SubTask subTask1 = manager.getSubTaskById(2);
        SubTask subTask2 = manager.getSubTaskById(2);

        assertEqualsTask(subTask1, subTask2, "Подзадачи не равны");

    }

    private static void assertEqualsTask(Task expected, Task actual, String massage) {
        assertEquals(expected.getId(), actual.getId(), massage + ", id");
        assertEquals(expected.getName(), actual.getName(), massage + ", name");
        assertEquals(expected.getDescription(), actual.getDescription(), massage + ", description");
        assertEquals(expected.getStatus(), actual.getStatus(), massage + ", status");

    }
}