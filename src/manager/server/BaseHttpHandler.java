package manager.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.task.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {

    protected final TaskManager manager;
    protected final Gson gson = HttpTaskServer.getGson();

    protected BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
    }

    protected String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    protected void writeResponse(HttpExchange h, String responseBody, int responseCode) throws IOException {
        try (h) {
            if (responseCode == 204) {
                h.sendResponseHeaders(responseCode, -1);
            } else {
                byte[] resp = responseBody.getBytes(StandardCharsets.UTF_8);
                h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
                h.sendResponseHeaders(responseCode, resp.length);
                h.getResponseBody().write(resp);
            }
        }
    }
}