package db;

import rest.api.Application;
import xml.ParseXML;

import java.io.InputStream;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        try (InputStream in = ConnectionToDb.class.getClassLoader().getResourceAsStream("app.properties")) {
            Properties properties = new Properties();
            properties.load(in);
            ConnectionToDb connectionToDb = new ConnectionToDb(properties);

            System.out.println("Спсисок всех городов:");
            for (var item : connectionToDb.findAllCity()) {
                System.out.println(item.getId() + " " + item.getName() + " " + item.getLatitude() + " " + item.getLongitude());
            }

            System.out.println(connectionToDb.findDistance("DistanceMatrix", 3, 2) + " km");
            System.out.println(connectionToDb.findDistance("CrowFlight", 1, 2) + " km");

            ParseXML.parserXml();

            Application.app();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
