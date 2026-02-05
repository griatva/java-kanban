package manager.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import exception.ValidationException;
import manager.task.TaskManager;
import model.Task;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager manager) {
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
                    case GET_ENTITIES: {
                        handleGetEntities(exchange);
                        break;
                    }
                    case GET_ENTITY_BY_ID: {
                        handleGetEntityById(exchange);
                        break;
                    }
                    case POST_ENTITY_CREATE: {
                        handlePostEntityCreate(exchange, requestBody);
                        break;
                    }
                    case POST_ENTITY_UPDATE: {
                        handlePostEntityUpdate(exchange, requestBody);
                        break;
                    }
                    case DELETE_ENTITY_BY_ID: {
                        handleDeleteEntityById(exchange);
                        break;
                    }
                    case UNKNOWN:
                        String message = "Non-existent request";
                        writeResponse(exchange, gson.toJson(message), 400);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                String message = "Unexpected error";
                writeResponse(exchange, gson.toJson(message), 500);
            }


        }
    }


    protected void handleGetEntities(HttpExchange exchange) throws IOException {
        try (exchange) {
            writeResponse(exchange, gson.toJson(manager.getTasksList()), 200);
        }
    }

    protected void handleGetEntityById(HttpExchange exchange) throws IOException {
        try (exchange) {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            Integer id = Integer.parseInt(pathParts[2]);
            try {
                writeResponse(exchange, gson.toJson(manager.getTaskById(id)), 200);
            } catch (NotFoundException e) {
                writeResponse(exchange, gson.toJson(e.getMessage()), 404);
            }
        }
    }

    protected void handlePostEntityCreate(HttpExchange exchange, String requestBody) throws IOException {
        try (exchange) {
            try {
                Task task = gson.fromJson(requestBody, Task.class);

                try {
                    writeResponse(exchange, gson.toJson(manager.createTask(task)), 201);
                } catch (ValidationException e) {
                    writeResponse(exchange, gson.toJson(e.getMessage()), 406);
                }
            } catch (IOException e) {
                e.printStackTrace();
                writeResponse(exchange, "Error reading request body", 500);
            }
        }
    }

    protected void handlePostEntityUpdate(HttpExchange exchange, String requestBody) throws IOException {
        try (exchange) {
            try {
                Task task = gson.fromJson(requestBody, Task.class);
                try {
                    manager.updateTask(task);
                    String message = "Task successfully updated";
                    writeResponse(exchange, gson.toJson(message), 201);
                } catch (ValidationException e) {
                    writeResponse(exchange, gson.toJson(e.getMessage()), 406);
                }
            } catch (IOException e) {
                e.printStackTrace();
                writeResponse(exchange, "Error reading request body", 500);
            }
        }
    }

    protected void handleDeleteEntityById(HttpExchange exchange) throws IOException {
        try (exchange) {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            Integer id = Integer.parseInt(pathParts[2]);
            try {
                manager.deleteTaskById(id);

                writeResponse(exchange, null, 204);
            } catch (NotFoundException e) {
                writeResponse(exchange, gson.toJson(e.getMessage()), 404);
            }
        }
    }


    protected Endpoint getEndpoint(String requestPath, String requestMethod, String requestBody) {
        String[] pathParts = requestPath.split("/");

        if (requestMethod.equals("GET")) {
            if (pathParts.length == 2) {
                return Endpoint.GET_ENTITIES;
            }
            if (pathParts.length == 3) {
                return Endpoint.GET_ENTITY_BY_ID;
            }
        }

        if (requestMethod.equals("POST")) {

            Task task = gson.fromJson(requestBody, Task.class);
            Integer taskId = task.getId();
            if (taskId == null) {
                return Endpoint.POST_ENTITY_CREATE;
            } else {
                return Endpoint.POST_ENTITY_UPDATE;
            }
        }

        if (requestMethod.equals("DELETE")) {
            return Endpoint.DELETE_ENTITY_BY_ID;
        }
        return Endpoint.UNKNOWN;
    }
}
