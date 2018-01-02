package linearSystem;

import java.util.Vector;

public class GaussElimination {

    Vector<Double> gaussElimination(int size, double[][] matrix, Vector<Double> vector) {
        Vector<Double> resultVector = new Vector<>();
        resultVector.setSize(size);

        double[][] arrayAB = new double[size][size + 1];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                arrayAB[j][i] = matrix[j][i];
            }
        }

        for (int i = 0; i < size; i++) {
            arrayAB[i][size] = vector.get(i);
        }

        double temp;
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                if (Math.abs(arrayAB[i][i]) < Math.pow(10, -12)) {
                    System.err.println("Błąd! Dzielnik równy 0");
                    break;
                }

                temp = -arrayAB[j][i] / arrayAB[i][i];
                for (int k = 0; k < size + 1; k++) {
                    arrayAB[j][k] += temp * arrayAB[i][k];
                }
            }
        }

        for (int i = 0; i < size; i++) {
            resultVector.set(i, 0.0);
        }

        for (int i = size - 1; i >= 0; i--) {
            temp = arrayAB[i][size];
            for (int j = size - 1; j >= 0; j--) {
                temp -= arrayAB[i][j] * resultVector.get(j);
            }
            if (Math.abs(arrayAB[i][i]) < Math.pow(10, -12)) {
                System.err.println("Błąd! Dzielnik równy 0");
                break;
            }
            resultVector.set(i, temp / arrayAB[i][i]);
        }

        return resultVector;
    }
}
