package manager.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler {

    protected final Gson gson = HttpTaskServer.getGson();

    protected String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    protected abstract void handleGetEntities(HttpExchange exchange) throws IOException;

    protected abstract void handleGetEntityById(HttpExchange exchange) throws IOException;

    protected abstract void handlePostEntityCreate(HttpExchange exchange, String requestBody) throws IOException;

    protected abstract void handlePostEntityUpdate(HttpExchange exchange, String requestBody) throws IOException;

    protected abstract void handleDeleteEntityById(HttpExchange exchange) throws IOException;

    protected abstract Endpoint getEndpoint(String requestPath, String requestMethod, String requestBody);
}