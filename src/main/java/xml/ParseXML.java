package xml;

import db.ConnectionToDb;
import model.City;
import model.Distance;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ParseXML {

    private static ArrayList<City> cities = new ArrayList<>();
    private static ArrayList<Distance> distances = new ArrayList<>();

    /**
     * Метод для парсинга городов и расстояний из XML файла
     */
    public static void parserXml() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File("src/main/resources/CitiesAndDistance.xml"));

        NodeList citiesElements = document.getDocumentElement().getElementsByTagName("city");
        NodeList distanceElements = document.getDocumentElement().getElementsByTagName("distance");

        for (int i = 0; i < citiesElements.getLength(); i++) {
            Node city = citiesElements.item(i);
            NamedNodeMap attributes = city.getAttributes();
            cities.add(new City(
                    Integer.parseInt(attributes.getNamedItem("id").getNodeValue()),
                    attributes.getNamedItem("name").getNodeValue(),
                    Double.parseDouble(attributes.getNamedItem("latitude").getNodeValue()),
                    Double.parseDouble(attributes.getNamedItem("longitude").getNodeValue()))
            );
        }

        for (int i = 0; i < distanceElements.getLength(); i++) {
            Node distance = distanceElements.item(i);
            NamedNodeMap attributes = distance.getAttributes();
            distances.add(new Distance(
                    Integer.parseInt(attributes.getNamedItem("fromCity").getNodeValue()),
                    Integer.parseInt(attributes.getNamedItem("toCity").getNodeValue()),
                    Double.parseDouble(attributes.getNamedItem("distance").getNodeValue())
            ));
        }
        ConnectionToDb.addFromXml(cities, distances);
    }
}
