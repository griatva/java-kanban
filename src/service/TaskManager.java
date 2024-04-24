package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {


    Task getTaskById(Integer id);

    SubTask getSubTaskById(Integer id);

    Epic getEpicById(Integer id);

    Task createTask(Task task);

    SubTask createSubTask(SubTask subTask);

    Epic createEpic(Epic epic);

    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    void deleteTaskById(Integer id);

    void deleteSubTaskById(Integer id);

    void deleteEpicById(Integer id);

    List<SubTask> getSubTasksByEpic(Epic epic);

    List<Task> getTasksList();

    List<SubTask> getSubTasksList();

    List<Epic> getEpicList();

    void deleteAllTasks();

    void deleteAllSubTasks();

    void deleteAllEpics();

    List<Task> getHistory();


}