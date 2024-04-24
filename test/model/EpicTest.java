package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Эпик")
class EpicTest {

    @Test
    @DisplayName("Два эпика с одинаковым ID должны быть равны")
    void shouldBeEqualToCopy() {

        TaskManager manager = Managers.getDefaults();
        Epic epicExpected = new Epic("Имя", "Описание");
        epicExpected.setStatus(Status.NEW);
        epicExpected.setId(1);

        Epic epic = new Epic("Имя", "Описание");
        manager.createEpic(epic);
        Epic epicSaved = manager.getEpicById(epic.getId());

        assertEqualsTask(epicExpected, epicSaved, "Эпики не равны");

        Epic epic1 = manager.getEpicById(1);
        Epic epic2 = manager.getEpicById(1);
        assertEqualsTask(epic1, epic2, "Эпики не равны");
    }

    private static void assertEqualsTask(Task expected, Task actual, String massage) {
        assertEquals(expected.getId(), actual.getId(), massage + ", id");
        assertEquals(expected.getName(), actual.getName(), massage + ", name");
        assertEquals(expected.getDescription(), actual.getDescription(), massage + ", description");
        assertEquals(expected.getStatus(), actual.getStatus(), massage + ", status");
    }
}