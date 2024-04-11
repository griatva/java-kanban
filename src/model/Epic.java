package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<SubTask> subTasks = new ArrayList<>();


    public Epic(String name, String description) {
        super(name, description);
    }


    public List<SubTask> getSubTasks() {
        return subTasks;
    }


    @Override
    public String toString() {
        return "Epic{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                ", id=" + this.getId() +
                ", subTasks=" + subTasks.size() +
                '}';

    }

    public boolean isAllSubTasksNEW() {
        int counter = 0;
        boolean result;
        for (SubTask subTask : subTasks) {
            if (subTask.getStatus() == Status.NEW) {
                counter++;
            }
        }
        if (counter == subTasks.size()) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public boolean isAllSubTasksDONE() {
        int counter = 0;
        boolean result;
        for (SubTask subTask : subTasks) {
            if (subTask.getStatus() == Status.DONE) {
                counter++;
            }
        }
        if (counter == subTasks.size()) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }
}