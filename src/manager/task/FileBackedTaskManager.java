package manager.task;

import converter.TaskConverter;
import exception.ManagerSaveException;
import manager.Managers;
import manager.history.HistoryManager;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    private FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    private void save() {

        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(writer)) {

            bw.write("id,type,name,status,description,epicId");
            bw.newLine();

            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                bw.write(TaskConverter.toString(entry.getValue()));
                bw.newLine();
            }
            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                bw.write(TaskConverter.toString(entry.getValue()));
                bw.newLine();
            }
            for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
                bw.write(TaskConverter.toString(entry.getValue()));
                bw.newLine();
            }
        } catch (IOException exp) {
            throw new ManagerSaveException("Ошибка записи в файл: " + file.getName(), exp);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {

        FileBackedTaskManager manager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);

        int maxId = 0;

        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader)) {

            String firstLine = br.readLine();
            if (firstLine == null) return manager;

            while (true) {
                String line = br.readLine();

                if (line == null)
                    break;

                Task task = TaskConverter.fromString(line);
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

                        break;
                }
                if (maxId < id) {
                    maxId = id;
                }
            }
            for (SubTask subTask : manager.subTasks.values()) {
                Epic tiedEpic = manager.epics.get(subTask.getEpicId());
                tiedEpic.getSubTasksId().add(subTask.getId());
            }
        } catch (IOException exp) {
            throw new RuntimeException("Ошибка чтения файла: " + file.getName(), exp);
        }
        manager.counterId = maxId;
        return manager;
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
