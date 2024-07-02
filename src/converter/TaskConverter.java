package converter;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskConverter {

    public static String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," +
                task.getDescription() + "," + null + "," + task.getStartDateTime() + "," + task.getDuration();
    }

    public static String toString(SubTask subTask) {

        return subTask.getId() + "," + subTask.getType() + "," + subTask.getName() + "," +
                subTask.getStatus() + "," + subTask.getDescription() + "," + subTask.getEpicId() +
                "," + subTask.getStartDateTime() + "," + subTask.getDuration();
    }


    public static Task fromString(String line) {

        Task task = null;

        String[] fields = line.split(",");

        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];


        String startDateTime = fields[6];
        LocalDateTime start;
        if (startDateTime.equals("null")) {
            start = null;
        } else {
            start = LocalDateTime.parse(startDateTime);
        }

        String durationString = fields[7];
        Duration duration;
        if (durationString.equals("null")) {
            duration = null;
        } else {
            duration = Duration.parse(fields[7]);
        }


        switch (type) {
            case TASK:
                task = new Task(id, name, status, description, start, duration);
                break;

            case EPIC:
                task = new Epic(id, name, status, description, start, duration);
                break;

            case SUBTASK:
                Integer epicId = Integer.parseInt(fields[5]);
                task = new SubTask(id, name, status, description, epicId, start, duration);
                break;
        }
        return task;
    }
}
