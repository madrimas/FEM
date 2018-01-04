package fem;

import localStorage.LocalElement;

import java.util.Vector;

class Jacobian {

    private static LocalElement LOCAL_ELEMENT = LocalElement.getInstance();
    private double invertedMatrixJ[][];
    private double detJ;

    Jacobian(int integrationPoint, Vector<Double> x, Vector<Double> y) {

        double[][] matrixJ = new double[2][2];
        matrixJ[0][0] = LOCAL_ELEMENT.getdNdXi()[integrationPoint][0] * x.get(0) + LOCAL_ELEMENT.getdNdXi()[integrationPoint][1] * x.get(1) + LOCAL_ELEMENT.getdNdXi()[integrationPoint][2] * x.get(2) + LOCAL_ELEMENT.getdNdXi()[integrationPoint][3] * x.get(3);
        matrixJ[0][1] = LOCAL_ELEMENT.getdNdXi()[integrationPoint][0] * y.get(0) + LOCAL_ELEMENT.getdNdXi()[integrationPoint][1] * y.get(1) + LOCAL_ELEMENT.getdNdXi()[integrationPoint][2] * y.get(2) + LOCAL_ELEMENT.getdNdXi()[integrationPoint][3] * y.get(3);
        matrixJ[1][0] = LOCAL_ELEMENT.getdNdEta()[integrationPoint][0] * x.get(0) + LOCAL_ELEMENT.getdNdEta()[integrationPoint][1] * x.get(1) + LOCAL_ELEMENT.getdNdEta()[integrationPoint][2] * x.get(2) + LOCAL_ELEMENT.getdNdEta()[integrationPoint][3] * x.get(3);
        matrixJ[1][1] = LOCAL_ELEMENT.getdNdEta()[integrationPoint][0] * y.get(0) + LOCAL_ELEMENT.getdNdEta()[integrationPoint][1] * y.get(1) + LOCAL_ELEMENT.getdNdEta()[integrationPoint][2] * y.get(2) + LOCAL_ELEMENT.getdNdEta()[integrationPoint][3] * y.get(3);

        detJ = matrixJ[0][0] * matrixJ[1][1] - matrixJ[0][1] * matrixJ[1][0];

        invertedMatrixJ = new double[2][2];

        invertedMatrixJ[0][0] = matrixJ[1][1];
        invertedMatrixJ[0][1] = -matrixJ[0][1];
        invertedMatrixJ[1][0] = -matrixJ[1][0];
        invertedMatrixJ[1][1] = matrixJ[0][0];
    }

    double[][] getInvertedMatrixJ() {
        return invertedMatrixJ;
    }

    double getDetJ() {
        return detJ;
    }
}
