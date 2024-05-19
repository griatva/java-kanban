package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Задачи")
class TaskTest {

    @Test
    @DisplayName("Две задачи с одинаковым ID должны быть равны")
    void equals_returnTrue_objectHaveSameId() {

        //given
        Task taskExpected = new Task("Имя-1", "Описание-1", Status.NEW);
        taskExpected.setId(1);

        Task taskActual = new Task("Имя-2", "Описание-2", Status.DONE);
        taskActual.setId(1);

        //when,then
        assertEquals(taskExpected, taskActual, "Задачи не равны");
    }
}