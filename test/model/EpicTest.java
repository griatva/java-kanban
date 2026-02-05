package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Epic")
class EpicTest {

    @Test
    @DisplayName("Two epics with the same ID should be equal")
    void equals_returnTrue_objectHaveSameId() {

        //given
        Epic epicExpected = new Epic("Name-1", "description-1");
        epicExpected.setStatus(Status.NEW);
        epicExpected.setId(1);

        Epic epicActual = new Epic("Name-2", "description-2");
        epicActual.setStatus(Status.DONE);
        epicActual.setId(1);

        //when,then
        assertEquals(epicExpected, epicActual, "Epics are not equal");
    }
}