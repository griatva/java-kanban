package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Эпик")
class EpicTest {

    @Test
    @DisplayName("Два эпика с одинаковым ID должны быть равны")
    void equals_returnTrue_objectHaveSameId() {

        //given
        Epic epicExpected = new Epic("Имя-1", "Описание-1");
        epicExpected.setStatus(Status.NEW);
        epicExpected.setId(1);

        Epic epicActual = new Epic("Имя-2", "Описание-2");
        epicActual.setStatus(Status.DONE);
        epicActual.setId(1);

        //that,than
        assertEquals(epicExpected, epicActual, "Эпики не равны");
    }
}