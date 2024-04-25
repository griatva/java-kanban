package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.taskManagers.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Менеджеры")
class ManagersTest {

    @Test
    @DisplayName("Должен возвращать проинициализированные и готовые к работе экземпляры TaskManager")
    void getDefaults_shouldCreateTaskManagerInstance() {

        TaskManager manager = Managers.getDefaults();

        assertNotNull(manager, "объект не создан");
    }
}