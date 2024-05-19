package manager.history;

import model.Task;

import java.util.List;

public interface HistoryManager {

    List<Task> getHistory();

    void addTaskInHistory(Task task);

    void remove(int id);

}
