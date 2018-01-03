package integral;

import java.util.ArrayList;
import java.util.List;

public class Integral {

    public double integral2P() {
        List<Double> weightList = new ArrayList<>();
        weightList.add(1.0);
        weightList.add(1.0);

        List<Double> coordinatesList = new ArrayList<>();
        coordinatesList.add(-0.577);
        coordinatesList.add(0.577);

        int coordinatesNumber = coordinatesList.size();

        double result = 0;
        for (int i = 0; i < coordinatesNumber; i++) {
            for (int j = 0; j < coordinatesNumber; j++) {
                result += function(coordinatesList.get(i), coordinatesList.get(j)) * weightList.get(i) * weightList.get(j);
            }
        }
        return result;
    }

    public double integral3P() {
        List<Double> weightList = new ArrayList<>();
        weightList.add(5.0 / 9.0);
        weightList.add(8.0 / 9.0);
        weightList.add(5.0 / 9.0);

        List<Double> coordinatesList = new ArrayList<>();
        coordinatesList.add(-0.7745);
        coordinatesList.add(0.0);
        coordinatesList.add(0.7745);

        int coordinatesNumber = coordinatesList.size();

        double result = 0;

        for (int i = 0; i < coordinatesNumber; i++) {
            for (int j = 0; j < coordinatesNumber; j++) {
                result += function(coordinatesList.get(i), coordinatesList.get(j)) * weightList.get(i) * weightList.get(j);
            }
        }

        return result;
    }

    private double function(double x, double y) {
        return (2 * x * x * y * y + 6 * x + 5);
    }
}