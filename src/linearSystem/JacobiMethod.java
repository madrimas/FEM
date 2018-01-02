package linearSystem;

import java.util.Vector;

public class JacobiMethod {
    Vector<Double> jacobiMethod(int size, double[][] matrix, Vector<Double> vector) {
        Vector<Double> resultVector = new Vector<>();
        resultVector.setSize(size);

        double[][] arrayM = new double[size][size];
        Vector<Double> vectorN = new Vector<>();
        vectorN.setSize(size);
        Vector<Double> tempResult = new Vector<>();
        tempResult.setSize(size);

        for (int i = 0; i < size; i++) {
            vectorN.set(i, 1 / matrix[i][i]);
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j)
                    arrayM[i][j] = 0;
                else
                    arrayM[i][j] = -(matrix[i][j] * vectorN.get(i));
            }
        }

        for (int i = 0; i < size; i++) {
            resultVector.set(i, 0.0);
        }

        int iterationsNumber = 50;
        for (int i = 0; i < iterationsNumber; i++) {
            for (int j = 0; j < size; j++) {
                tempResult.set(j, vectorN.get(j) * vector.get(j));
                for (int k = 0; k < size; k++) {
                    tempResult.set(j, tempResult.get(j) + arrayM[j][k] * resultVector.get(k));
                }
            }
            for (int j = 0; j < size; j++) {
                resultVector.set(j, tempResult.get(j));
            }
        }

        return resultVector;
    }
}
