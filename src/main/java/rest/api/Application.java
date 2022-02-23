package rest.api;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import db.ConnectionToDb;

import static rest.api.ApiUtils.splitQuery;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Application {

    public static void app() throws IOException {
        try (InputStream in = ConnectionToDb.class.getClassLoader().getResourceAsStream("app.properties")) {
            Properties properties = new Properties();
            properties.load(in);
            ConnectionToDb connectionToDb = new ConnectionToDb(properties);

            int serverPort = 8000;
            HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);

            HttpContext context = server.createContext("/api/distance", (exchange -> {

                if ("GET".equals(exchange.getRequestMethod())) {
                    Map<String, List<String>> params = splitQuery(exchange.getRequestURI().getRawQuery());

                    String type = params.get("type").get(0);
                    int cityF = Integer.parseInt(params.get("cityF").get(0));
                    int cityC = Integer.parseInt(params.get("cityC").get(0));

                    double distance = connectionToDb.findDistance(type, cityF, cityC);

                    String respText = String.format("Город 1: %x, Город 2: %x, Расстояние: %f км", cityF, cityC, distance);
                    exchange.sendResponseHeaders(200, respText.getBytes().length);
                    OutputStream output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
                exchange.close();
            }));

            server.setExecutor(null);
            server.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
