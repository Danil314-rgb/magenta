package db;

import model.City;
import model.Distance;
import model.Haversine;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Класс описывает работу с Базой Данных
 */
public class ConnectionToDb {

    private static Connection connection;
    private Properties properties;

    public ConnectionToDb(Properties properties) throws Exception {
        this.properties = properties;
        connection = initConnection();
    }

    /**
     * Метод подключения к БД
     */
    private Connection initConnection() throws Exception {
        Class.forName(properties.getProperty("driver"));
        String url = properties.getProperty("url");
        String login = properties.getProperty("login");
        String password = properties.getProperty("password");
        return DriverManager.getConnection(url, login, password);
    }

    /**
     * Метод для поиска всех городов из БД
     *
     * @return Возвращает список всех городов
     */
    public List<City> findAllCity() {
        List<City> cities = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("select * from city")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    cities.add(new City(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getDouble("latitude"),
                            resultSet.getDouble("longitude")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cities;
    }

    /**
     * Метод для расчёта расстояния между городами
     *
     * @param type     тип поиска
     * @param fromCity первый город
     * @param toCity   второй город
     * @return Возвращает расстояние между городом один и городом два
     */
    public double findDistance(String type, int fromCity, int toCity) {
        double distance = -1;
        List<Double> addressCities = new ArrayList<>();
        Haversine hev = new Haversine();
        if ("CrowFlight".equals(type)) {
            try (PreparedStatement statement = connection.prepareStatement("select latitude, longitude from city where id = ? or id = ?")) {
                statement.setInt(1, fromCity);
                statement.setInt(2, toCity);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        addressCities.add(resultSet.getDouble("latitude"));
                        addressCities.add(resultSet.getDouble("longitude"));
                    }
                    distance = hev.haversine(addressCities);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("DistanceMatrix".equals(type)) {
            try (PreparedStatement statement = connection.prepareStatement("select distance from distance where fromCity = ? and toCity = ?")) {
                statement.setInt(1, fromCity);
                statement.setInt(2, toCity);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        distance = resultSet.getDouble("distance");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return distance;
    }

    /**
     * Метод для парсинга XML документа  и добавления городом и расстояний в БД
     *
     * @param cities    список городов
     * @param distances список расстояний между городами
     */
    public static void addFromXml(ArrayList<City> cities, ArrayList<Distance> distances) throws ParserConfigurationException, SAXException, IOException {
        String sqlC = "insert into city (id, name, latitude, longitude) values (?, ?, ?, ?)";
        List<Integer> ids = checkId();
        try (PreparedStatement statement = connection.prepareStatement(sqlC)) {
            for (City city : cities) {
                statement.setInt(1, city.getId());
                statement.setString(2, city.getName());
                statement.setDouble(3, city.getLatitude());
                statement.setDouble(4, city.getLongitude());
                if (!ids.contains(city.getId())) {
                    statement.execute();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String sqlD = "insert into distance (distance, fromCity, toCity) values (?, ?, ?)";
        List<Integer> newIds = checkId();

        try (PreparedStatement statement = connection.prepareStatement(sqlD)) {
            for (Distance distance : distances) {
                statement.setDouble(1, distance.getDistance());
                statement.setInt(2, distance.getFromCity());
                statement.setInt(3, distance.getToCity());
                if (distance.getFromCity() != distance.getToCity() & newIds.contains(distance.getFromCity()) & newIds.contains(distance.getToCity())) {
                    statement.execute();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Успешно в Базе. HTTP 200");
    }

    /**
     * Метод для поиска всех id городов в БД
     *
     * @return Возвращает список id всех городов из БД
     */
    public static List<Integer> checkId() {
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("select id from city")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ids.add(resultSet.getInt("id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ids;
    }
}
