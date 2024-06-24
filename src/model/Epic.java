package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasksId = new ArrayList<>();

    private LocalDateTime endDateTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(Integer id, String name, Status status, String description, LocalDateTime startDateTime, Duration duration) {
        super(id, name, status, description, startDateTime, duration);
    }

    @Override
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public List<Integer> getSubTasksId() {
        return subTasksId;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                ", id=" + this.getId() +
                ", subTasks=" + subTasksId.size() +
                ", startDateTime=" + this.getStartDateTime() +
                ", duration=" + this.getDuration() +
                '}';

    }
}