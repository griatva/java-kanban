package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, SubTask> subTasks;
    private HashMap<Integer, Epic> epics;
    private final HistoryManager historyManager;
    private int counterId = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks  = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.historyManager = historyManager;
    }


    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int generateId() {
        return ++counterId;
    }

    @Override
    public Task getTaskById(Integer id) {
        historyManager.addTaskInHistory(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public SubTask getSubTaskById(Integer id) {
        historyManager.addTaskInHistory(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public Epic getEpicById(Integer id) {
        historyManager.addTaskInHistory(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        epic.getSubTasksId().add(subTask.getId());
        calculateEpicStatus(epic);
        return subTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        calculateEpicStatus(epic);
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
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

    @Override
    public void updateEpic(Epic epic) {
        Epic changingEpic = epics.get(epic.getId());
        if (changingEpic == null) {
            return;
        }
        changingEpic.setName(epic.getName());
        changingEpic.setDescription(epic.getDescription());
    }

    @Override
    public void deleteTaskById(Integer id) {
        tasks.remove(id);
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        SubTask subTaskToRemove = subTasks.remove(id);
        Epic epic = epics.get(subTaskToRemove.getEpicId());
        epic.getSubTasksId().remove(id);

        calculateEpicStatus(epic);
    }

    @Override
    public void deleteEpicById(Integer id) {
        List<Integer> arrayList = epics.get(id).getSubTasksId();
        for (Integer subTaskId : arrayList) {
            subTasks.remove(subTaskId);
        }
        epics.remove(id);
    }

    @Override
    public List<SubTask> getSubTasksByEpic(Epic epic) {
        List<SubTask> subTasksByEpic = new ArrayList<>();
        for (Integer subTasksId : epic.getSubTasksId()) {
            subTasksByEpic.add(subTasks.get(subTasksId));
        }
        return subTasksByEpic;
    }

    @Override
    public List<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<SubTask> getSubTasksList() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Epic> getEpicList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTasksId().clear();
            calculateEpicStatus(epic);
        }
    }

    @Override
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
