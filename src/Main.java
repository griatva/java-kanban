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


        Task task1 = taskManager.createTask(new Task("название задачи-1", "описание-1", Status.DONE,
                LocalDateTime.of(2024, 6, 20, 12, 25), Duration.ofMinutes(60)));
        System.out.println("Создание задачи-1:" + task1);

        Task task2 = taskManager.createTask(new Task("название задачи-2", "описание-2", Status.DONE,
                LocalDateTime.of(2024, 6, 20, 14, 25), Duration.ofMinutes(60)));

        Task task3 = taskManager.createTask(new Task("название задачи-3", "описание-3", Status.DONE,
                null, null));
        System.out.println("Создание задачи-3:" + task3);

        Epic epic1 = taskManager.createEpic(new Epic("Эпик-1", "описание эпика-1"));
        System.out.println("Создание эпика-1:" + epic1);


        Epic epic2 = taskManager.createEpic(new Epic("Эпик-2", "описание эпика-2"));
        System.out.println("Создание эпика-2:" + epic2);


        SubTask subtask1 = taskManager.createSubTask(new SubTask("Название подзадачи-1", Status.NEW, "описание-1",
                epic1.getId(), null, null));
        System.out.println("Создание подзадачи-1: " + subtask1);

        SubTask subtask2 = taskManager.createSubTask(new SubTask("Название подзадачи-2", Status.IN_PROGRESS, "описание-2",
                epic1.getId(), LocalDateTime.of(2024, 6, 19, 18, 17), Duration.ofMinutes(60)));
        System.out.println("Создание подзадачи-2: " + subtask2);

        SubTask subtask3 = taskManager.createSubTask(new SubTask("Название подзадачи-3", Status.IN_PROGRESS, "описание-3",
                epic2.getId(), LocalDateTime.of(2024, 6, 19, 20, 17), Duration.ofMinutes(60)));
        System.out.println("Создание подзадачи-3: " + subtask3);

        SubTask subtask4 = taskManager.createSubTask(new SubTask("Название подзадачи-4", Status.IN_PROGRESS, "описание-4",
                epic2.getId(), LocalDateTime.of(2024, 06, 18, 14, 17), Duration.ofMinutes(30)));
        System.out.println("Создание подзадачи-4: " + subtask4);

        SubTask subtask5 = taskManager.createSubTask(new SubTask("Название подзадачи-5", Status.IN_PROGRESS, "описание-5",
                epic2.getId(), LocalDateTime.of(2024, 06, 19, 14, 17), Duration.ofMinutes(60)));
        System.out.println("Создание подзадачи-5: " + subtask5);


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
            System.out.println("печатаем через методы" + "\n");
            System.out.println(taskManager.getSubTasksByEpic(epic1) + "\n");
            System.out.println(taskManager.getTasksList() + "\n");
            System.out.println(taskManager.getSubTasksList() + "\n");
            System.out.println(taskManager.getEpicList() + "\n");
        System.out.println(taskManager.getPrioritizedTasks() + "\n");


            System.out.println("меняем статусы задач" + "\n");
            task1.setStatus(Status.IN_PROGRESS);
            System.out.println(task1 + "\n");
            taskManager.updateTask(task1);
            System.out.println(taskManager.getTasksList() + "\n");

            task2.setStatus(Status.NEW);
            System.out.println(task2 + "\n");
            taskManager.updateTask(task2);
            System.out.println(taskManager.getTasksList() + "\n");

            System.out.println("меняем статусы подзадач, обновляем статус эпика" + "\n");
            subtask1.setStatus(Status.DONE);
            subtask2.setStatus(Status.DONE);
            subtask3.setStatus(Status.NEW);
            taskManager.updateSubTask(subtask1);
            taskManager.updateSubTask(subtask2);
            taskManager.updateSubTask(subtask3);
            System.out.println(taskManager.getSubTasksList() + "\n");
            System.out.println(taskManager.getEpicList() + "\n");


        System.out.println("меняем startDateTime у сабтаска на null" + "\n");
        System.out.println(taskManager.getPrioritizedTasks().size());
        SubTask subtask6 = new SubTask("Название подзадачи-2", Status.IN_PROGRESS, "описание-2",
                epic1.getId(), null, null);
        subtask6.setId(7);
        taskManager.updateSubTask(subtask6);
        System.out.println(taskManager.getPrioritizedTasks().size() + "\n");


            System.out.println("удаляем одну задачу" + "\n");
        taskManager.deleteTaskById(3);
            System.out.println(taskManager.getTasksList() + "\n");
        System.out.println(taskManager.getPrioritizedTasks() + "\n");

            System.out.println("удаляем одну подзадачу" + "\n");
        taskManager.deleteSubTaskById(6);
            System.out.println(taskManager.getSubTasksList() + "\n");
            System.out.println(taskManager.getEpicList() + "\n");

            System.out.println("удаляем один эпик" + "\n");
            System.out.println(taskManager.getEpicList() + "\n");
            System.out.println(taskManager.getSubTasksList() + "\n");
        taskManager.deleteEpicById(4);
            System.out.println(taskManager.getEpicList() + "\n");
            System.out.println(taskManager.getSubTasksList() + "\n");

        }


    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasksList()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getEpicList()) {
            System.out.println(epic);

            for (Task task : manager.getSubTasksByEpic(epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
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

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getEpicById(3);
        manager.getEpicById(4);

        System.out.println("История после добавления повторов");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

        manager.deleteTaskById(1);
        manager.deleteEpicById(3);
        manager.deleteSubTaskById(5);

        System.out.println("История после удаления task-1, subtask-1, epic-1");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

    }

}
