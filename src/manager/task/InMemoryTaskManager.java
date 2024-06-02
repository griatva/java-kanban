package manager.task;

import exception.NotFoundException;
import manager.history.HistoryManager;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, SubTask> subTasks;
    protected final HashMap<Integer, Epic> epics;
    private final HistoryManager historyManager;
    protected int counterId = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks  = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.historyManager = historyManager;
    }

    public int getCounterId() {
        return counterId;
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
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.addTaskInHistory(task);
        }
        return task;
    }

    @Override
    public SubTask getSubTaskById(Integer id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            historyManager.addTaskInHistory(subTask);
        }
        return subTask;
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.addTaskInHistory(epic);
        }
        return epic;
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
        Integer taskId = task.getId();
        if (tasks.containsKey(taskId)) {
            tasks.put(task.getId(), task);
        } else {
            throw new NotFoundException("Не найдена задача, id: " + taskId);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        Integer subTaskId = subTask.getId();
        SubTask changingSubTask = subTasks.get(subTaskId);
        if (changingSubTask == null) {
            throw new NotFoundException("Не найдена подзадача, id: " + subTaskId);
        }
        changingSubTask.setName(subTask.getName());
        changingSubTask.setDescription(subTask.getDescription());
        changingSubTask.setStatus(subTask.getStatus());

        Integer epicId = subTask.getEpicId();
        Epic savedEpic = epics.get(epicId);
        if (savedEpic == null) {
            throw new NotFoundException("Не найден эпик, id: " + epicId);
        }

        calculateEpicStatus(savedEpic);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic changingEpic = epics.get(epic.getId());
        if (changingEpic == null) {
            throw new NotFoundException("Не найден эпик, id: " + epic.getId());
        }
        changingEpic.setName(epic.getName());
        changingEpic.setDescription(epic.getDescription());
    }

    @Override
    public void deleteTaskById(Integer id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        SubTask subTaskToRemove = subTasks.remove(id);
        Epic epic = epics.get(subTaskToRemove.getEpicId());
        epic.getSubTasksId().remove(id);
        historyManager.remove(id);

        calculateEpicStatus(epic);
    }

    @Override
    public void deleteEpicById(Integer id) {
        List<Integer> arrayList = epics.get(id).getSubTasksId();
        for (Integer subTaskId : arrayList) {
            subTasks.remove(subTaskId);
            historyManager.remove(subTaskId);
        }
        epics.remove(id);
        historyManager.remove(id);
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
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTasksId().clear();
            calculateEpicStatus(epic);
        }
    }

    @Override
    public void deleteAllEpics() {
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        subTasks.clear();
    }

    private boolean isEpicNEW(Epic epic) {
        int counter = 0;
        List<Integer> subTasksIdInEpic = epic.getSubTasksId();

        for (Integer subTaskId : subTasksIdInEpic) {
            if (subTasks.get(subTaskId).getStatus() == Status.NEW) {
                counter++;
            }
        }
        return counter == subTasksIdInEpic.size();
    }

    private boolean isEpicDONE(Epic epic) {
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
