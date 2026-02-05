package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Subtask")
class SubTaskTest {

    @Test
    @DisplayName("Two subtasks with the same ID should be equal")
    void equals_returnTrue_objectHaveSameId() {

        //given

        SubTask subTaskExpected = new SubTask("Name-1", "description-1", Status.NEW, 2);
        subTaskExpected.setId(1);

        SubTask subTaskActual = new SubTask("Name-2", "description-2", Status.DONE, 3);
        subTaskActual.setId(1);

        //when,then
        assertEquals(subTaskExpected, subTaskActual, "Subtasks are not equal");
    }
}