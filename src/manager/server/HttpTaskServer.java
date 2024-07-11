package manager.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import converter.DurationAdapter;
import converter.LocalDateTimeAdapter;
import manager.Managers;
import manager.task.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private static final int PORT = 8080;

    private final TaskManager manager;
    private final Gson gson;
    private final HttpServer server;

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
        this.gson = getGson();
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.createContext("/tasks", new TaskHandler(manager));
        server.createContext("/subtasks", new SubTaskHandler(manager));
        server.createContext("/epics", new EpicHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedHandler(manager));
    }

    public static void main(String[] args) {
        HttpTaskServer taskServer = new HttpTaskServer(Managers.getDefaults());
        taskServer.start();
    }

    protected void start() {
        System.out.println("Starting TaskServer " + PORT);
        server.start();
    }

    protected void stop() {
        server.stop(0);
        System.out.println("TaskServer is stopped on the port: " + PORT);
    }

    static Gson getGson() {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        return gson;
    }
}
