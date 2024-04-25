package service;

import service.historyManagers.HistoryManager;
import service.historyManagers.InMemoryHistoryManager;
import service.taskManagers.InMemoryTaskManager;
import service.taskManagers.TaskManager;

public class Managers {

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaults() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

}
