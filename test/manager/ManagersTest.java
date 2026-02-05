package manager;

import manager.task.TaskManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Managers")
class ManagersTest {

    @Test
    @DisplayName("Should return initialized and ready-to-use TaskManager instances")
    void getDefaults_shouldCreateTaskManagerInstance() {

        TaskManager manager = Managers.getDefaults();

        assertNotNull(manager, "Object was not created");
    }
}