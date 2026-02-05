import manager.Managers;
import manager.task.FileBackedTaskManager;
import manager.task.TaskManager;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefaults();
        taskManager.deleteAllEpics();
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubTasks();


        Task task1 = taskManager.createTask(new Task("task name-1", "description-1", Status.DONE,
                LocalDateTime.of(2024, 6, 20, 12, 25), Duration.ofMinutes(60)));
        System.out.println("Creating task-1:" + task1);

        Task task2 = taskManager.createTask(new Task("task name-2", "description-2", Status.DONE,
                LocalDateTime.of(2024, 6, 20, 14, 25), Duration.ofMinutes(60)));
        System.out.println("Creating task-2:" + task2);

        Task task3 = taskManager.createTask(new Task("task name-3", "description-3", Status.DONE,
                null, null));
        System.out.println("Creating task-3:" + task3);

        Epic epic1 = taskManager.createEpic(new Epic("epic-1", "epic description-1"));
        System.out.println("Creating epic-1:" + epic1);


        Epic epic2 = taskManager.createEpic(new Epic("epic-2", "epic description-2"));
        System.out.println("Creating epic-2:" + epic2);


        SubTask subtask1 = taskManager.createSubTask(new SubTask("subtask name-1", Status.NEW, "description-1",
                epic1.getId(), null, null));
        System.out.println("Creating subtask-1: " + subtask1);

        SubTask subtask2 = taskManager.createSubTask(new SubTask("subtask name-2", Status.IN_PROGRESS, "description-2",
                epic1.getId(), LocalDateTime.of(2024, 6, 19, 18, 17), Duration.ofMinutes(60)));
        System.out.println("Creating subtask-2: " + subtask2);

        SubTask subtask3 = taskManager.createSubTask(new SubTask("subtask name-3", Status.IN_PROGRESS, "description-3",
                epic2.getId(), LocalDateTime.of(2024, 6, 19, 20, 17), Duration.ofMinutes(60)));
        System.out.println("Creating subtask-3: " + subtask3);

        SubTask subtask4 = taskManager.createSubTask(new SubTask("subtask name-4", Status.IN_PROGRESS, "description-4",
                epic2.getId(), LocalDateTime.of(2024, 6, 18, 14, 17), Duration.ofMinutes(30)));
        System.out.println("Creating subtask-4: " + subtask4);

        SubTask subtask5 = taskManager.createSubTask(new SubTask("subtask name-5", Status.IN_PROGRESS, "description-5",
                epic2.getId(), LocalDateTime.of(2024, 6, 19, 14, 17), Duration.ofMinutes(60)));
        System.out.println("Creating subtask-5: " + subtask5);


        boolean option = false;

        if (option) {
            printAllTasks(taskManager);
        } else {
            manipulations(taskManager, epic1, epic2, task1, task2, task3, subtask1, subtask2, subtask3, subtask4, subtask5);
        }


        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(new File("resources/task.csv"));

        System.out.println(manager.getTasksList());
        System.out.println(manager.getEpicList());
        System.out.println(manager.getSubTasksList());

    }


    private static void manipulations(TaskManager taskManager,
                                      Epic epic1,
                                      Epic epic2,
                                      Task task1,
                                      Task task2,
                                      Task task3,
                                      SubTask subtask1,
                                      SubTask subtask2,
                                      SubTask subtask3,
                                      SubTask subtask4,
                                      SubTask subtask5) {
        System.out.println("Print via methods" + "\n");
            System.out.println(taskManager.getSubTasksByEpic(epic1) + "\n");
            System.out.println(taskManager.getTasksList() + "\n");
            System.out.println(taskManager.getSubTasksList() + "\n");
            System.out.println(taskManager.getEpicList() + "\n");
        System.out.println(taskManager.getPrioritizedTasks() + "\n");


        System.out.println("Update task statuses" + "\n");
            task1.setStatus(Status.IN_PROGRESS);
            System.out.println(task1 + "\n");
            taskManager.updateTask(task1);
            System.out.println(taskManager.getTasksList() + "\n");

            task2.setStatus(Status.NEW);
            System.out.println(task2 + "\n");
            taskManager.updateTask(task2);
            System.out.println(taskManager.getTasksList() + "\n");

        System.out.println("Update subtask statuses and update epic status" + "\n");
            subtask1.setStatus(Status.DONE);
            subtask2.setStatus(Status.DONE);
            subtask3.setStatus(Status.NEW);
            taskManager.updateSubTask(subtask1);
            taskManager.updateSubTask(subtask2);
            taskManager.updateSubTask(subtask3);
            System.out.println(taskManager.getSubTasksList() + "\n");
            System.out.println(taskManager.getEpicList() + "\n");


        System.out.println("Set subtask startDateTime to null" + "\n");
        System.out.println(taskManager.getPrioritizedTasks().size());
        SubTask subtask6 = new SubTask("subtask name-2", Status.IN_PROGRESS, "description-2",
                epic1.getId(), null, null);
        subtask6.setId(7);
        taskManager.updateSubTask(subtask6);
        System.out.println(taskManager.getPrioritizedTasks().size() + "\n");


        System.out.println("Delete a single task" + "\n");
        taskManager.deleteTaskById(3);
            System.out.println(taskManager.getTasksList() + "\n");
        System.out.println(taskManager.getPrioritizedTasks() + "\n");

        System.out.println("Delete a single subtask" + "\n");
        taskManager.deleteSubTaskById(6);
            System.out.println(taskManager.getSubTasksList() + "\n");
            System.out.println(taskManager.getEpicList() + "\n");

        System.out.println("Delete a single epic" + "\n");
            System.out.println(taskManager.getEpicList() + "\n");
            System.out.println(taskManager.getSubTasksList() + "\n");
        taskManager.deleteEpicById(4);
            System.out.println(taskManager.getEpicList() + "\n");
            System.out.println(taskManager.getSubTasksList() + "\n");

        }


    private static void printAllTasks(TaskManager manager) {
        System.out.println("Tasks:");
        for (Task task : manager.getTasksList()) {
            System.out.println(task);
        }
        System.out.println("Epics:");
        for (Epic epic : manager.getEpicList()) {
            System.out.println(epic);

            for (Task task : manager.getSubTasksByEpic(epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Subtasks:");
        for (Task subtask : manager.getSubTasksList()) {
            System.out.println(subtask);
        }

        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getEpicById(3);
        manager.getEpicById(4);
        manager.getSubTaskById(5);
        manager.getSubTaskById(6);
        manager.getSubTaskById(7);

        System.out.println("History:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getEpicById(3);
        manager.getEpicById(4);

        System.out.println("History after adding duplicates");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

        manager.deleteTaskById(1);
        manager.deleteEpicById(3);
        manager.deleteSubTaskById(5);

        System.out.println("History after deleting task-1, subtask-1, epic-1");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

    }

}
