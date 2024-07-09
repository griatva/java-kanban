package manager.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import manager.task.TaskManager;
import model.Epic;

import java.io.IOException;

public class EpicHandler extends TaskHandler implements HttpHandler {

    public EpicHandler(TaskManager manager) {
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
                    case GET_EPICS: {
                        handleGetTasks(exchange);
                        break;
                    }
                    case GET_EPIC_BY_ID: {
                        handleGetTaskById(exchange);
                        break;
                    }
                    case POST_EPIC_CREATE: {
                        handlePostTaskCreate(exchange, requestBody);
                        break;
                    }
                    case POST_EPIC_UPDATE: {
                        handlePostTaskUpdate(exchange, requestBody);
                        break;
                    }
                    case DELETE_EPIC_BY_ID: {
                        handleDeleteTaskById(exchange);
                        break;
                    }
                    case GET_EPICS_SUBTASKS: {
                        handleGetEpicsSubtasks(exchange);
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
            writeResponse(exchange, gson.toJson(manager.getEpicList()), 200);
        }
    }

    @Override
    protected void handleGetTaskById(HttpExchange exchange) throws IOException {
        try (exchange) {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            Integer id = Integer.parseInt(pathParts[2]);
            try {
                writeResponse(exchange, gson.toJson(manager.getEpicById(id)), 200);
            } catch (NotFoundException e) {
                writeResponse(exchange, gson.toJson(e.getMessage()), 404);
            }
        }
    }

    @Override
    protected void handlePostTaskCreate(HttpExchange exchange, String requestBody) throws IOException {
        try (exchange) {
            Epic epic = gson.fromJson(requestBody, Epic.class);

            writeResponse(exchange, gson.toJson(manager.createEpic(epic)), 201);

        }
    }

    @Override
    protected void handlePostTaskUpdate(HttpExchange exchange, String requestBody) throws IOException {
        try (exchange) {
            Epic epic = gson.fromJson(requestBody, Epic.class);

            manager.updateEpic(epic);
            String message = "Эпик успешно обновлен";
            writeResponse(exchange, gson.toJson(message), 201);

        }
    }

    @Override
    protected void handleDeleteTaskById(HttpExchange exchange) throws IOException {
        try (exchange) {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            Integer id = Integer.parseInt(pathParts[2]);
            try {
                manager.deleteEpicById(id);
                writeResponse(exchange, null, 204);
            } catch (NotFoundException e) {
                writeResponse(exchange, gson.toJson(e.getMessage()), 404);
            }
        }
    }

    protected void handleGetEpicsSubtasks(HttpExchange exchange) throws IOException {
        try (exchange) {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            Integer id = Integer.parseInt(pathParts[2]);
            try {
                Epic epic = manager.getEpicById(id);
                writeResponse(exchange, gson.toJson(manager.getSubTasksByEpic(epic)), 200);
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
                return Endpoint.GET_EPICS;
            }
            if (pathParts.length == 3) {
                return Endpoint.GET_EPIC_BY_ID;
            }
            if (pathParts.length == 4 && pathParts[3].equals("subtasks")) {
                return Endpoint.GET_EPICS_SUBTASKS;
            }
        }

        if (requestMethod.equals("POST")) {

            Epic epic = gson.fromJson(requestBody, Epic.class);
            Integer epicId = epic.getId();
            if (epicId == null) {
                return Endpoint.POST_EPIC_CREATE;
            } else {
                return Endpoint.POST_EPIC_UPDATE;
            }
        }

        if (requestMethod.equals("DELETE")) {
            return Endpoint.DELETE_EPIC_BY_ID;
        }
        return Endpoint.UNKNOWN;
    }


}
