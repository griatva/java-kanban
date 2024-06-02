package manager.task;

import exception.ManagerSaveException;
import manager.Managers;
import manager.history.HistoryManager;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
        this.file = new File("resources/", "task.csv");
    }

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    private void save() {

        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(writer)) {

            bw.write("id,type,name,status,description,epicId");
            bw.newLine();

            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                bw.write(toString(entry.getValue()));
                bw.newLine();
            }
            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                bw.write(toString(entry.getValue()));
                bw.newLine();
            }
            for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
                bw.write(toString(entry.getValue()));
                bw.newLine();
            }
        } catch (IOException exp) {
            throw new ManagerSaveException("Ошибка записи в файл: " + file.getName(), exp);
        }
    }

    private String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," +
                task.getDescription() + "," + task.getEpicId();
    }

    public static FileBackedTaskManager loadFromFile(File file) {

        FileBackedTaskManager manager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);

        int maxId = 0;

        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader)) {

            br.readLine();

            while (true) {
                String line = br.readLine();

                if (line == null)
                    break;

                Task task = fromString(line);
                int id = task.getId();
                switch (task.getType()) {
                    case TASK:
                        manager.tasks.put(id, task);
                        break;
                    case EPIC:
                        Epic epic = (Epic) task;
                        manager.epics.put(id, epic);
                        break;
                    case SUBTASK:
                        SubTask subTask = (SubTask) task;
                        manager.subTasks.put(id, subTask);
                        Epic tiedEpic = manager.epics.get(subTask.getEpicId());
                        tiedEpic.getSubTasksId().add(subTask.getId());
                        break;
                }
                if (maxId < id) {
                    maxId = id;
                }
            }
        } catch (IOException exp) {
            throw new RuntimeException("Ошибка чтения файла: " + file.getName(), exp);
        }
        manager.counterId = maxId;
        return manager;
    }

    private static Task fromString(String line) {

        Task task = null;

        String[] fields = line.split(",");

        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case TASK:
                task = new Task(id, name, status, description, null);
                break;

            case EPIC:
                task = new Epic(id, name, status, description, null);
                break;

            case SUBTASK:
                Integer epicId = Integer.parseInt(fields[5]);
                task = new SubTask(id, name, status, description, epicId);
                break;
        }
        return task;
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        SubTask newSubTask = super.createSubTask(subTask);
        save();
        return newSubTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(Integer id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }
}
