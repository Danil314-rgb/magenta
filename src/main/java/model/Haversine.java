package model;

import java.util.List;

/**
 * Класс описывает расчёт рассояния по формуле
 */
public class Haversine {

    /**
     * Метод для расчёта расстояния
     *
     * @param addressCities входные данные для расчёта
     * @return возращает расчитанную дистанцию между городами
     */
    public double haversine(List<Double> addressCities) {

        double lat1 = addressCities.get(0);
        double lon1 = addressCities.get(1);
        double lat2 = addressCities.get(2);
        double lon2 = addressCities.get(3);

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return rad * c;
    }
}
