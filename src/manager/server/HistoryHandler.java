package manager.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.task.TaskManager;

import java.io.IOException;

public class HistoryHandler implements HttpHandler, ResponseWriter {

    private final TaskManager manager;
    private final Gson gson = HttpTaskServer.getGson();

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            writeResponse(exchange, gson.toJson(manager.getHistory()), 200);
        }
    }
}
