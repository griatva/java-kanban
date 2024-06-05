package manager;

import manager.history.HistoryManager;
import manager.history.InMemoryHistoryManager;
import manager.task.FileBackedTaskManager;
import manager.task.TaskManager;

import java.io.File;

public class Managers {

    private static final File defaultFile = new File("resources/", "task.csv");

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaults() {
        return FileBackedTaskManager.loadFromFile(defaultFile);
    }

}
