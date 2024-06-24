package model;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    private String name;
    private String description;
    private Status status;
    private Integer id;
    private LocalDateTime startDateTime;
    private Duration duration;


    public Task(String name, String description, Status status, LocalDateTime startDateTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startDateTime = startDateTime;
        this.duration = duration;
    }

    public Task(Integer id, String name, Status status, String description, LocalDateTime startDateTime,
                Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.startDateTime = startDateTime;
        this.duration = duration;
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        LocalDateTime endDateTime;
        if (startDateTime == null) {
            endDateTime = null;
        } else {
            endDateTime = startDateTime.plus(duration);
        }
        return endDateTime;
    }


    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return (Objects.equals(id, task.id));
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (id != null) {
            hash += id.hashCode();
        }
        hash = hash * 31;

        return hash;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", startDateTime=" + startDateTime +
                ", duration=" + duration +
                '}';
    }
}
