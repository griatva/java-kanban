package manager.server;

public enum Endpoint {

    // Task endpoints
    GET_TASKS,
    GET_TASK_BY_ID,
    POST_TASK_CREATE,
    POST_TASK_UPDATE,
    DELETE_TASK_BY_ID,

    // SubTask endpoints
    GET_SUBTASKS,
    GET_SUBTASK_BY_ID,
    POST_SUBTASK_CREATE,
    POST_SUBTASK_UPDATE,
    DELETE_SUBTASK_BY_ID,

    // Epic endpoints
    GET_EPICS,
    GET_EPIC_BY_ID,
    GET_EPICS_SUBTASKS,
    POST_EPIC_CREATE,
    POST_EPIC_UPDATE,
    DELETE_EPIC_BY_ID,


    UNKNOWN

}
