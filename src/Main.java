import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task task1 = taskManager.createTask(new Task("название задачи-1", "описание-1", Status.NEW));
        System.out.println("Создание задачи-1:" + task1);

        Task task2 = taskManager.createTask(new Task("название задачи-2", "описание-2", Status.DONE));
        System.out.println("Создание задачи-2:" + task2);


        Epic epic1 = taskManager.createEpic(new Epic("Эпик-1", "описание эпика-1"));
        System.out.println("Создание эпика-1:" + epic1);


        Epic epic2 = taskManager.createEpic(new Epic("Эпик-2", "описание эпика-2"));
        System.out.println("Создание эпика-2:" + epic2);


        SubTask subtask1 = taskManager.createSubTask(new SubTask("Название подзадачи-1", "описание-1", Status.NEW, epic1));
        System.out.println("Создание подзадачи-1: " + subtask1);


        SubTask subtask2 = taskManager.createSubTask(new SubTask("Название подзадачи-2", "описание-2", Status.IN_PROGRESS, epic1));
        System.out.println("Создание подзадачи-2: " + subtask2);

        SubTask subtask3 = taskManager.createSubTask(new SubTask("Название подзадачи-3", "описание-3", Status.IN_PROGRESS, epic2));
        System.out.println("Создание подзадачи-3: " + subtask3);

        System.out.println("печатаем напрямую из таблицы" + "\n");
        System.out.println(taskManager.getTasks() + "\n");
        System.out.println(taskManager.getSubtasks() + "\n");
        System.out.println(taskManager.getEpics() + "\n");

        System.out.println("печатаем через методы" + "\n");
        System.out.println(taskManager.printSubTasksOneEpic(epic1) + "\n");
        System.out.println(taskManager.printTasksList() + "\n");
        System.out.println(taskManager.printSubTasksList() + "\n");
        System.out.println(taskManager.printEpicList() + "\n");


        System.out.println("меняем статусы задач" + "\n");
        task1.setStatus(Status.IN_PROGRESS);
        System.out.println(task1 + "\n");
        taskManager.updateTask(task1);
        System.out.println(taskManager.printTasksList() + "\n");

        task2.setStatus(Status.NEW);
        System.out.println(task2 + "\n");
        taskManager.updateTask(task2);
        System.out.println(taskManager.printTasksList() + "\n");

        System.out.println("меняем статусы подзадач, обновляем статус эпика" + "\n");
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.NEW);
        taskManager.updateSubTask(subtask1);
        taskManager.updateSubTask(subtask2);
        taskManager.updateSubTask(subtask3);
        System.out.println(taskManager.printSubTasksList() + "\n");
        System.out.println(taskManager.printEpicList() + "\n");

        System.out.println("удаляем одну задачу" + "\n");
        taskManager.deleteTaskById(1);
        System.out.println(taskManager.printTasksList() + "\n");

        System.out.println("удаляем одну подзадачу" + "\n");
        taskManager.deleteSubTaskById(5);
        System.out.println(taskManager.printSubTasksList() + "\n");
        System.out.println(taskManager.printEpicList() + "\n");

        System.out.println("удаляем один эпик" + "\n");
        taskManager.deleteEpicById(3);
        System.out.println(taskManager.printSubTasksList() + "\n");
        System.out.println(taskManager.printEpicList() + "\n");

    }
}
