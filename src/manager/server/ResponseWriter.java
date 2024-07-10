package manager.server;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public interface ResponseWriter {

    default void writeResponse(HttpExchange h, String responseBody, int responseCode) throws IOException {
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
