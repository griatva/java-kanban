package converter;

import model.*;

public class TaskConverter {

    public static String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," +
                task.getDescription() + "," + null;
    }

    public static String toString(SubTask subTask) {

        return subTask.getId() + "," + subTask.getType() + "," + subTask.getName() + "," +
                subTask.getStatus() + "," + subTask.getDescription() + "," + subTask.getEpicId();
    }


    public static Task fromString(String line) {

        Task task = null;

        String[] fields = line.split(",");

        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case TASK:
                task = new Task(id, name, status, description);
                break;

            case EPIC:
                task = new Epic(id, name, status, description);
                break;

            case SUBTASK:
                Integer epicId = Integer.parseInt(fields[5]);
                task = new SubTask(id, name, status, description, epicId);
                break;
        }
        return task;
    }
}
