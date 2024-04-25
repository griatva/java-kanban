package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Подзадача")
class SubTaskTest {

    @Test
    @DisplayName("Две подзадачи с одинаковым ID должны быть равны")
    void equals_returnTrue_objectHaveSameId() {

        //given

        SubTask subTaskExpected = new SubTask("Имя-1", "Описание-1", Status.NEW, 2);
        subTaskExpected.setId(1);

        SubTask subTaskActual = new SubTask("Имя-2", "Описание-2", Status.DONE, 3);
        subTaskActual.setId(1);

        //that,than
        assertEquals(subTaskExpected, subTaskActual, "Задачи не равны");
    }
}