package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    private int counterId = 0;


    public int generateId() {
        return ++counterId;
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    public SubTask createSubTask(SubTask subTask) {
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        Epic epic = subTask.getEpic();
        epic.getSubTasks().add(subTask);
        calculateEpicStatus(subTask.getEpic());
        return subTask;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        calculateEpicStatus(epic);
        return epic;
    }

    public void updateTask(Task task) {
        Task changingTask = tasks.get(task.getId());
        if (changingTask == null) {
            return;
        }
        changingTask.setName(task.getName());
        changingTask.setDescription(task.getDescription());
        changingTask.setStatus(task.getStatus());
    }

    public void updateSubTask(SubTask subTask) {
        SubTask changingSubTask = subTasks.get(subTask.getId());
        if (changingSubTask == null) {
            return;
        }
        changingSubTask.setName(subTask.getName());
        changingSubTask.setDescription(subTask.getDescription());
        changingSubTask.setStatus(subTask.getStatus());

        calculateEpicStatus(subTask.getEpic());
    }

    public void updateEpic(Epic epic) {
        Epic changingEpic = epics.get(epic.getId());
        if (changingEpic == null) {
            return;
        }
        changingEpic.setName(epic.getName());
        changingEpic.setDescription(epic.getDescription());
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteSubTaskById(int id) {
        SubTask subTaskToRemove = subTasks.remove(id);
        Epic epic = subTaskToRemove.getEpic();
        epic.getSubTasks().remove(subTaskToRemove);

        calculateEpicStatus(subTaskToRemove.getEpic());
    }

    public void deleteEpicById(int id) {
        List<SubTask> arrayList = epics.get(id).getSubTasks();
        for (SubTask subTask : arrayList) {
            subTasks.remove(subTask.getId());
        }
        epics.remove(id);
    }

    private void calculateEpicStatus(Epic epic) {

        if (epic.getSubTasks().isEmpty() || epic.isAllSubTasksNEW()) {
            epic.setStatus(Status.NEW);
        } else if (epic.isAllSubTasksDONE()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

    }

    public List<SubTask> printSubTasksOneEpic(Epic epic) {
        return epic.getSubTasks();
    }

    public List<Task> printTasksList() {
        return new ArrayList<>(tasks.values());
    }

    public List<SubTask> printSubTasksList() {
        return new ArrayList<>(subTasks.values());
    }

    public List<Epic>  printEpicList() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
            calculateEpicStatus(epic);
        }
    }

    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }


    @Override
    public String toString() {
        return "TaskManager{" +
                "tasks=" + tasks.size() +
                ", subTasks=" + subTasks.size() +
                ", epics=" + epics.size() +
                ", counterId=" + counterId +
                '}';
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, SubTask> getSubtasks() {
        return subTasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }
}
