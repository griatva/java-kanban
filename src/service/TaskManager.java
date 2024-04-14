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


    private int generateId() {
        return ++counterId;
    }

    public Task getTaskById(Integer id) {
        return tasks.get(id);
    }

    public SubTask getSubTaskById(Integer id) {
        return subTasks.get(id);
    }

    public Epic getEpicById(Integer id) {
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
        Epic epic = epics.get(subTask.getEpicId());
        epic.getSubTasksId().add(subTask.getId());
        calculateEpicStatus(epic);
        return subTask;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        calculateEpicStatus(epic);
        return epic;
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateSubTask(SubTask subTask) {
        SubTask changingSubTask = subTasks.get(subTask.getId());
        if (changingSubTask == null) {
            return;
        }
        changingSubTask.setName(subTask.getName());
        changingSubTask.setDescription(subTask.getDescription());
        changingSubTask.setStatus(subTask.getStatus());

        calculateEpicStatus(epics.get(subTask.getEpicId()));
    }

    public void updateEpic(Epic epic) {
        Epic changingEpic = epics.get(epic.getId());
        if (changingEpic == null) {
            return;
        }
        changingEpic.setName(epic.getName());
        changingEpic.setDescription(epic.getDescription());
    }

    public void deleteTaskById(Integer id) {
        tasks.remove(id);
    }

    public void deleteSubTaskById(Integer id) {
        SubTask subTaskToRemove = subTasks.remove(id);
        Epic epic = epics.get(subTaskToRemove.getEpicId());
        epic.getSubTasksId().remove(id);

        calculateEpicStatus(epic);
    }

    public void deleteEpicById(Integer id) {
        List<Integer> arrayList = epics.get(id).getSubTasksId();
        for (Integer subTaskId : arrayList) {
            subTasks.remove(subTaskId);
        }
        epics.remove(id);
    }

    public List<SubTask> getSubTasksByEpic(Epic epic) {
        List<SubTask> subTasksByEpic = new ArrayList<>();
        for (Integer subTasksId : epic.getSubTasksId()) {
            subTasksByEpic.add(subTasks.get(subTasksId));
        }
        return subTasksByEpic;
    }

    public List<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    public List<SubTask> getSubTasksList() {
        return new ArrayList<>(subTasks.values());
    }

    public List<Epic> getEpicList() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTasksId().clear();
            calculateEpicStatus(epic);
        }
    }

    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    public boolean isEpicNEW(Epic epic) {
        int counter = 0;
        List<Integer> subTasksIdInEpic = epic.getSubTasksId();

        for (Integer subTaskId : subTasksIdInEpic) {
            if (subTasks.get(subTaskId).getStatus() == Status.NEW) {
                counter++;
            }
        }
        return counter == subTasksIdInEpic.size();
    }

    public boolean isEpicDONE(Epic epic) {
        int counter = 0;
        List<Integer> subTasksIdInEpic = epic.getSubTasksId();
        if (subTasksIdInEpic.isEmpty()) {
            return false;
        } else {
            for (Integer subTaskId : subTasksIdInEpic) {
                if (subTasks.get(subTaskId).getStatus() == Status.DONE) {
                    counter++;
                }
            }
            return counter == subTasksIdInEpic.size();
        }
    }

    private void calculateEpicStatus(Epic epic) {

        if (epic.getSubTasksId().isEmpty() || isEpicNEW(epic)) {
            epic.setStatus(Status.NEW);
        } else if (isEpicDONE(epic)) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }


}
