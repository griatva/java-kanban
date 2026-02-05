package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Tasks")
class TaskTest {

    @Test
    @DisplayName("Two tasks with the same ID should be equal")
    void equals_returnTrue_objectHaveSameId() {

        //given
        Task taskExpected = new Task("Name-1", "description-1", Status.NEW);
        taskExpected.setId(1);

        Task taskActual = new Task("Name-2", "description-2", Status.DONE);
        taskActual.setId(1);

        //when,then
        assertEquals(taskExpected, taskActual, "Tasks are not equal");
    }
}