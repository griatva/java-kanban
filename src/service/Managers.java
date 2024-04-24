package service;

public class Managers {

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaults() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

}
