package manager.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import exception.ValidationException;
import manager.task.TaskManager;
import model.SubTask;

import java.io.IOException;

public class SubTaskHandler extends TaskHandler implements HttpHandler {

    public SubTaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            try {
                String requestBody = readRequestBody(exchange);
                Endpoint endpoint = getEndpoint(
                        exchange.getRequestURI().getPath(),
                        exchange.getRequestMethod(),
                        requestBody);

                switch (endpoint) {
                    case GET_SUBTASKS: {
                        handleGetTasks(exchange);
                        break;
                    }
                    case GET_SUBTASK_BY_ID: {
                        handleGetTaskById(exchange);
                        break;
                    }
                    case POST_SUBTASK_CREATE: {
                        handlePostTaskCreate(exchange, requestBody);
                        break;
                    }
                    case POST_SUBTASK_UPDATE: {
                        handlePostTaskUpdate(exchange, requestBody);
                        break;
                    }
                    case DELETE_SUBTASK_BY_ID: {
                        handleDeleteTaskById(exchange);
                        break;
                    }
                    case UNKNOWN:
                        String message = "Несуществующий запрос";
                        writeResponse(exchange, gson.toJson(message), 400);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                String message = "Непредвиденная ошибка";
                writeResponse(exchange, gson.toJson(message), 500);
            }


        }
    }

    @Override
    protected void handleGetTasks(HttpExchange exchange) throws IOException {
        try (exchange) {
            writeResponse(exchange, gson.toJson(manager.getSubTasksList()), 200);
        }
    }

    @Override
    protected void handleGetTaskById(HttpExchange exchange) throws IOException {
        try (exchange) {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            Integer id = Integer.parseInt(pathParts[2]);
            try {
                writeResponse(exchange, gson.toJson(manager.getSubTaskById(id)), 200);
            } catch (NotFoundException e) {
                writeResponse(exchange, gson.toJson(e.getMessage()), 404);
            }
        }
    }

    @Override
    protected void handlePostTaskCreate(HttpExchange exchange, String requestBody) throws IOException {
        try (exchange) {
            SubTask subtask = gson.fromJson(requestBody, SubTask.class);
            try {
                writeResponse(exchange, gson.toJson(manager.createSubTask(subtask)), 201);
            } catch (ValidationException e) {
                writeResponse(exchange, gson.toJson(e.getMessage()), 406);
            }
        }
    }

    @Override
    protected void handlePostTaskUpdate(HttpExchange exchange, String requestBody) throws IOException {
        try (exchange) {
            SubTask subtask = gson.fromJson(requestBody, SubTask.class);
            try {
                manager.updateSubTask(subtask);
                String message = "Подзадача успешно обновлена";
                writeResponse(exchange, gson.toJson(message), 201);
            } catch (ValidationException e) {
                writeResponse(exchange, gson.toJson(e.getMessage()), 406);
            }
        }
    }

    @Override
    protected void handleDeleteTaskById(HttpExchange exchange) throws IOException {
        try (exchange) {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            Integer id = Integer.parseInt(pathParts[2]);
            try {
                manager.deleteSubTaskById(id);
                writeResponse(exchange, null, 204);
            } catch (NotFoundException e) {
                writeResponse(exchange, gson.toJson(e.getMessage()), 404);
            }
        }
    }

    @Override
    protected Endpoint getEndpoint(String requestPath, String requestMethod, String requestBody) {
        String[] pathParts = requestPath.split("/");

        if (requestMethod.equals("GET")) {
            if (pathParts.length == 2) {
                return Endpoint.GET_SUBTASKS;
            }
            if (pathParts.length == 3) {
                return Endpoint.GET_SUBTASK_BY_ID;
            }
        }

        if (requestMethod.equals("POST")) {

            SubTask subTask = gson.fromJson(requestBody, SubTask.class);
            Integer subTaskId = subTask.getId();
            if (subTaskId == null) {
                return Endpoint.POST_SUBTASK_CREATE;
            } else {
                return Endpoint.POST_SUBTASK_UPDATE;
            }
        }

        if (requestMethod.equals("DELETE")) {
            return Endpoint.DELETE_SUBTASK_BY_ID;
        }
        return Endpoint.UNKNOWN;
    }


}
