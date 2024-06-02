import manager.Managers;
import manager.task.FileBackedTaskManager;
import manager.task.TaskManager;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.io.File;


public class Main {

    public static void main(String[] args) {


        TaskManager taskManager = Managers.getDefaults();

        Task task1 = taskManager.createTask(new Task("название задачи-1", "описание-1", Status.NEW));
        System.out.println("Создание задачи-1:" + task1);

        Task task2 = taskManager.createTask(new Task("название задачи-2", "описание-2", Status.DONE));
        System.out.println("Создание задачи-2:" + task2);


        Epic epic1 = taskManager.createEpic(new Epic("Эпик-1", "описание эпика-1"));
        System.out.println("Создание эпика-1:" + epic1);


        Epic epic2 = taskManager.createEpic(new Epic("Эпик-2", "описание эпика-2"));
        System.out.println("Создание эпика-2:" + epic2);


        SubTask subtask1 = taskManager.createSubTask(new SubTask("Название подзадачи-1", "описание-1", Status.NEW, epic1.getId()));
        System.out.println("Создание подзадачи-1: " + subtask1);


        SubTask subtask2 = taskManager.createSubTask(new SubTask("Название подзадачи-2", "описание-2", Status.IN_PROGRESS, epic1.getId()));
        System.out.println("Создание подзадачи-2: " + subtask2);

        SubTask subtask3 = taskManager.createSubTask(new SubTask("Название подзадачи-3", "описание-3", Status.IN_PROGRESS, epic2.getId()));
        System.out.println("Создание подзадачи-3: " + subtask3);


        boolean option = false;

        if (option) {
            printAllTasks(taskManager);
        } else {
            manipulations(taskManager, epic1, task1, task2, subtask1, subtask2, subtask3);
        }

        File fileTest = new File("resources/", "task.csv");
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(fileTest);
        System.out.println(manager.equals(taskManager));
        System.out.println(manager.getTasksList());
        System.out.println(manager.getEpicList());
        System.out.println(manager.getSubTasksList());
        System.out.println(manager.getCounterId());
    }


    private static void manipulations(TaskManager taskManager,
                                      Epic epic1,
                                      Task task1,
                                      Task task2,
                                      SubTask subtask1,
                                      SubTask subtask2,
                                      SubTask subtask3) {
            System.out.println("печатаем через методы" + "\n");
            System.out.println(taskManager.getSubTasksByEpic(epic1) + "\n");
            System.out.println(taskManager.getTasksList() + "\n");
            System.out.println(taskManager.getSubTasksList() + "\n");
            System.out.println(taskManager.getEpicList() + "\n");


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

            System.out.println("удаляем одну задачу" + "\n");
            taskManager.deleteTaskById(1);
            System.out.println(taskManager.getTasksList() + "\n");

            System.out.println("удаляем одну подзадачу" + "\n");
            taskManager.deleteSubTaskById(5);
            System.out.println(taskManager.getSubTasksList() + "\n");
            System.out.println(taskManager.getEpicList() + "\n");

            System.out.println("удаляем один эпик" + "\n");
            System.out.println(taskManager.getEpicList() + "\n");
            System.out.println(taskManager.getSubTasksList() + "\n");
            taskManager.deleteEpicById(3);
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
