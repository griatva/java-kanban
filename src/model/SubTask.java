package model;

public class SubTask extends Task {

    private final Integer epicId;


    public SubTask(Integer id, String name, Status status, String description, Integer epicId) {
        super(id, name, status, description, epicId);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, Status status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    @Override
    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                ", id=" + this.getId() +
                ", epicId=" + epicId +
                '}';
    }
}
