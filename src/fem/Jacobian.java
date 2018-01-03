package fem;

import localStorage.LocalElement;

import java.util.Vector;

public class Jacobian {

    private static final LocalElement LOCAL_ELEMENT = LocalElement.getInstance();
    private final double J[][];
    private final double invertedJ[][];
    private final double detJ;
    private final int integrationPoint; //0 || 1 || 2 || 3

    Jacobian(int integrationPoint, Vector<Double> x, Vector<Double> y) {
        this.integrationPoint = integrationPoint;

        J = new double[2][2];
        J[0][0] = LOCAL_ELEMENT.getdNdXi()[integrationPoint][0] * x.get(0) + LOCAL_ELEMENT.getdNdXi()[integrationPoint][1] * x.get(1) + LOCAL_ELEMENT.getdNdXi()[integrationPoint][2] * x.get(2) + LOCAL_ELEMENT.getdNdXi()[integrationPoint][3] * x.get(3);
        J[0][1] = LOCAL_ELEMENT.getdNdXi()[integrationPoint][0] * y.get(0) + LOCAL_ELEMENT.getdNdXi()[integrationPoint][1] * y.get(1) + LOCAL_ELEMENT.getdNdXi()[integrationPoint][2] * y.get(2) + LOCAL_ELEMENT.getdNdXi()[integrationPoint][3] * y.get(3);
        J[1][0] = LOCAL_ELEMENT.getdNdEta()[integrationPoint][0] * x.get(0) + LOCAL_ELEMENT.getdNdEta()[integrationPoint][1] * x.get(1) + LOCAL_ELEMENT.getdNdEta()[integrationPoint][2] * x.get(2) + LOCAL_ELEMENT.getdNdEta()[integrationPoint][3] * x.get(3);
        J[1][1] = LOCAL_ELEMENT.getdNdEta()[integrationPoint][0] * y.get(0) + LOCAL_ELEMENT.getdNdEta()[integrationPoint][1] * y.get(1) + LOCAL_ELEMENT.getdNdEta()[integrationPoint][2] * y.get(2) + LOCAL_ELEMENT.getdNdEta()[integrationPoint][3] * y.get(3);

        detJ = J[0][0] * J[1][1] - J[0][1] * J[1][0];

        invertedJ = new double[2][2];

        invertedJ[0][0] = J[1][1];
        invertedJ[0][1] = -J[0][1];
        invertedJ[1][0] = -J[1][0];
        invertedJ[1][1] = J[0][0];
    }

    public Jacobian(int integrationPoint, Element element) {
        this.integrationPoint = integrationPoint;

        J = new double[2][2];
        J[0][0] = LOCAL_ELEMENT.getdNdXi()[integrationPoint][0] * element.nodeVector.get(0).getX() + LOCAL_ELEMENT.getdNdXi()[integrationPoint][1] * element.nodeVector.get(1).getX() + LOCAL_ELEMENT.getdNdXi()[integrationPoint][2] * element.nodeVector.get(2).getX() + LOCAL_ELEMENT.getdNdXi()[integrationPoint][3] * element.nodeVector.get(3).getX();
        J[0][1] = LOCAL_ELEMENT.getdNdXi()[integrationPoint][0] * element.nodeVector.get(0).getY() + LOCAL_ELEMENT.getdNdXi()[integrationPoint][1] * element.nodeVector.get(1).getY() + LOCAL_ELEMENT.getdNdXi()[integrationPoint][2] * element.nodeVector.get(2).getY() + LOCAL_ELEMENT.getdNdXi()[integrationPoint][3] * element.nodeVector.get(3).getY();
        J[1][0] = LOCAL_ELEMENT.getdNdEta()[integrationPoint][0] * element.nodeVector.get(0).getX() + LOCAL_ELEMENT.getdNdEta()[integrationPoint][1] * element.nodeVector.get(1).getX() + LOCAL_ELEMENT.getdNdEta()[integrationPoint][2] * element.nodeVector.get(2).getX() + LOCAL_ELEMENT.getdNdEta()[integrationPoint][3] * element.nodeVector.get(3).getX();
        J[1][1] = LOCAL_ELEMENT.getdNdEta()[integrationPoint][0] * element.nodeVector.get(0).getY() + LOCAL_ELEMENT.getdNdEta()[integrationPoint][1] * element.nodeVector.get(1).getY() + LOCAL_ELEMENT.getdNdEta()[integrationPoint][2] * element.nodeVector.get(2).getY() + LOCAL_ELEMENT.getdNdEta()[integrationPoint][3] * element.nodeVector.get(3).getY();

        detJ = J[0][0] * J[1][1] - J[0][1] * J[1][0];

        invertedJ = new double[2][2];

        invertedJ[0][0] = J[1][1];
        invertedJ[0][1] = -J[0][1];
        invertedJ[1][0] = -J[1][0];
        invertedJ[1][1] = J[0][0];
    }

    public void showJacobian() {
        System.out.println("Jakobian punktu calkowania id:" + integrationPoint);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                System.out.print(+J[i][j] + "\t");
            }
            System.out.println("");
        }
        System.out.println("Det: " + detJ + "\n");
    }

    double[][] getInvertedJ() {
        return invertedJ;
    }

    double getDetJ() {
        return detJ;
    }
}
