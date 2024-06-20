package manager.task;

import exception.NotFoundException;
import exception.ValidationException;
import manager.history.HistoryManager;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, SubTask> subTasks;
    protected final HashMap<Integer, Epic> epics;
    private final HistoryManager historyManager;
    protected int counterId = 0;
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartDateTime));

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.historyManager = historyManager;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean isTimeConflict(Task task1, Task task2) {
        return ((task1.getStartDateTime().isBefore(task2.getEndDateTime()) &&
                task2.getStartDateTime().isBefore(task1.getEndDateTime())));
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
        } else {
            throw new NotFoundException("Задача не найдена, id: " + id);
        }
        return task;
    }

    @Override
    public SubTask getSubTaskById(Integer id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            historyManager.addTaskInHistory(subTask);
        } else {
            throw new NotFoundException("Подзадача не найдена, id: " + id);
        }
        return subTask;
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.addTaskInHistory(epic);
        } else {
            throw new NotFoundException("Эпик не найден, id: " + id);
        }
        return epic;
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);

        if (task.getStartDateTime() != null) {
            prioritizedTasks.stream()
                    .filter(pt -> !Objects.equals(pt.getId(), task.getId()))
                    .filter(pt -> isTimeConflict(pt, task))
                    .findFirst()
                    .ifPresentOrElse(
                            pt -> {
                                throw new ValidationException("Пересечение с задачей id: " + pt.getId());
                            },
                            () -> prioritizedTasks.add(task)
                    );
        }
        return task;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        if (epic == null) {
            throw new NotFoundException("Не найден эпик, id: " + subTask.getEpicId());
        }
        epic.getSubTasksId().add(subTask.getId());

        if (subTask.getStartDateTime() != null) {
            prioritizedTasks.stream()
                    .filter(pt -> !Objects.equals(pt.getId(), subTask.getId()))
                    .filter(pt -> isTimeConflict(pt, subTask))
                    .findFirst()
                    .ifPresentOrElse(
                            pt -> {
                                throw new ValidationException("Пересечение с задачей id: " + pt.getId());
                            },
                            () -> prioritizedTasks.add(subTask)
                    );
        }

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
        Task original = tasks.get(taskId);
        if (tasks.containsKey(taskId)) {
            tasks.put(taskId, task);

            if (task.getStartDateTime() != null) {
                prioritizedTasks.stream()
                        .filter(pt -> !Objects.equals(pt.getId(), task.getId()))
                        .filter(pt -> isTimeConflict(pt, task))
                        .findFirst()
                        .ifPresentOrElse(
                                pt -> {
                                    throw new ValidationException("Пересечение с задачей id: " + pt.getId());
                                },
                                () -> {
                                    prioritizedTasks.remove(original);
                                    prioritizedTasks.add(task);
                                }
                        );
            } else {
                prioritizedTasks.remove(original);
            }

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

        if (subTask.getStartDateTime() != null) {
            prioritizedTasks.stream()
                    .filter(pt -> !Objects.equals(pt.getId(), subTask.getId()))
                    .filter(pt -> isTimeConflict(pt, subTask))
                    .findFirst()
                    .ifPresentOrElse(
                            pt -> {
                                throw new ValidationException("Пересечение с задачей id: " + pt.getId());
                            },
                            () -> {
                                if (changingSubTask.getStartDateTime() != null) {
                                    prioritizedTasks.remove(changingSubTask);
                                }
                                prioritizedTasks.add(subTask);
                            }
                    );
        } else {
            if (changingSubTask.getStartDateTime() != null) {
                prioritizedTasks.remove(changingSubTask);
            }
        }

        changingSubTask.setName(subTask.getName());
        changingSubTask.setDescription(subTask.getDescription());
        changingSubTask.setStatus(subTask.getStatus());
        changingSubTask.setStartDateTime(subTask.getStartDateTime());
        changingSubTask.setDuration(subTask.getDuration());
        changingSubTask.setEndDateTime(subTask.getEndDateTime());

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
        Task taskToRemove = tasks.remove(id);
        if (taskToRemove == null) {
            throw new NotFoundException("Не найдена задача, id: " + id);
        }
        historyManager.remove(id);
        if (taskToRemove.getStartDateTime() != null) {
            prioritizedTasks.remove(taskToRemove);
        }
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        SubTask subTaskToRemove = subTasks.remove(id);
        Epic epic = epics.get(subTaskToRemove.getEpicId());
        if (epic == null) {
            throw new NotFoundException("Эпик не найден, id: " + subTaskToRemove.getEpicId());
        }
        epic.getSubTasksId().remove(id);
        historyManager.remove(id);
        if (subTaskToRemove.getStartDateTime() != null) {
            prioritizedTasks.remove(subTaskToRemove);
        }
        calculateEpicStatus(epic);
    }

    @Override
    public void deleteEpicById(Integer id) {
        Epic epicToRemove = epics.get(id);
        if (epicToRemove == null) {
            throw new NotFoundException("Эпик не найден, id: " + id);
        }
        List<Integer> arrayList = epicToRemove.getSubTasksId();
        for (Integer subTaskId : arrayList) {
            try {
                subTasks.remove(subTaskId);
                historyManager.remove(subTaskId);
            } catch (RuntimeException e) {
                throw new NotFoundException("Подзадача не найдена, id: " + subTaskId);
            }
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<SubTask> getSubTasksByEpic(Epic epic) {

        return epic.getSubTasksId()
                .stream()
                .map(subTasks::get)
                .collect(Collectors.toList());
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
        prioritizedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        prioritizedTasks.removeAll(subTasks.values());
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
        prioritizedTasks.removeAll(subTasks.values());
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

    protected void calculateEpicStatus(Epic epic) {

        if (epic.getSubTasksId().isEmpty() || isEpicNEW(epic)) {
            epic.setStatus(Status.NEW);
        } else if (isEpicDONE(epic)) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

        LocalDateTime start = LocalDateTime.MAX;
        LocalDateTime end = LocalDateTime.MIN;
        Duration duration = Duration.ofMinutes(0);


        for (Integer subTaskId : epic.getSubTasksId()) {
            SubTask subTask = subTasks.get(subTaskId);
            LocalDateTime subTaskStartTime = subTask.getStartDateTime();
            if (subTaskStartTime != null) {
                if (subTaskStartTime.isBefore(start)) {
                    start = subTaskStartTime;
                }
                if (subTask.getEndDateTime().isAfter(end)) {
                    end = subTask.getEndDateTime();
                }
                duration = duration.plus(subTask.getDuration());
            }
        }

        if (start == LocalDateTime.MAX) {
            start = null;
            end = null;
            duration = null;
        }

        epic.setStartDateTime(start);
        epic.setEndDateTime(end);
        epic.setDuration(duration);
    }
}
