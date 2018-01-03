package localStorage;

public class LocalElement {

    private static LocalElement localElement = null;
    private final LocalNode[] gaussIntegrationPoints = {
            new LocalNode(-1.0 / Math.sqrt(3.0), -1.0 / Math.sqrt(3.0)),
            new LocalNode(1.0 / Math.sqrt(3.0), -1.0 / Math.sqrt(3.0)),
            new LocalNode(1.0 / Math.sqrt(3.0), 1.0 / Math.sqrt(3.0)),
            new LocalNode(-1.0 / Math.sqrt(3.0), 1.0 / Math.sqrt(3.0))
    };
    private final LocalArea[] gaussIntegrationAreaPoints = {
            new LocalArea(new LocalNode(-1.0, 1.0 / Math.sqrt(3.0)),
                    new LocalNode(-1.0, -1.0 / Math.sqrt(3.0))),
            new LocalArea(new LocalNode(-1.0 / Math.sqrt(3.0), -1.0),
                    new LocalNode(1.0 / Math.sqrt(3.0), -1.0)),
            new LocalArea(new LocalNode(1.0, -1.0 / Math.sqrt(3.0)),
                    new LocalNode(1.0, 1.0 / Math.sqrt(3.0))),
            new LocalArea(new LocalNode(1.0 / Math.sqrt(3.0), 1.0),
                    new LocalNode(-1.0 / Math.sqrt(3.0), 1.0))
    };
    private final double dNdXi[][] = new double[4][4];
    private final double dNdEta[][] = new double[4][4];
    private final double matrixN[][] = new double[4][4];//macierz funkcji kształtu

    private LocalElement() {

        //uzupełnienie macierzy funkcjami kształtu
        for (int i = 0; i < 4; i++) {
            matrixN[i][0] = getN1(gaussIntegrationPoints[i].getXi(), gaussIntegrationPoints[i].getEta());
            matrixN[i][1] = getN2(gaussIntegrationPoints[i].getXi(), gaussIntegrationPoints[i].getEta());
            matrixN[i][2] = getN3(gaussIntegrationPoints[i].getXi(), gaussIntegrationPoints[i].getEta());
            matrixN[i][3] = getN4(gaussIntegrationPoints[i].getXi(), gaussIntegrationPoints[i].getEta());

            dNdXi[i][0] = getdN1dXi(gaussIntegrationPoints[i].getEta());
            dNdXi[i][1] = getdN2dXi(gaussIntegrationPoints[i].getEta());
            dNdXi[i][2] = getdN3dXi(gaussIntegrationPoints[i].getEta());
            dNdXi[i][3] = getdN4dXi(gaussIntegrationPoints[i].getEta());

            dNdEta[i][0] = getdN1dEta(gaussIntegrationPoints[i].getXi());
            dNdEta[i][1] = getdN2dEta(gaussIntegrationPoints[i].getXi());
            dNdEta[i][2] = getdN3dEta(gaussIntegrationPoints[i].getXi());
            dNdEta[i][3] = getdN4dEta(gaussIntegrationPoints[i].getXi());
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                gaussIntegrationAreaPoints[i].node[j][0] = getN1(gaussIntegrationAreaPoints[i].localNode.get(j).getXi(), gaussIntegrationAreaPoints[i].localNode.get(j).getEta());
                gaussIntegrationAreaPoints[i].node[j][1] = getN2(gaussIntegrationAreaPoints[i].localNode.get(j).getXi(), gaussIntegrationAreaPoints[i].localNode.get(j).getEta());
                gaussIntegrationAreaPoints[i].node[j][2] = getN3(gaussIntegrationAreaPoints[i].localNode.get(j).getXi(), gaussIntegrationAreaPoints[i].localNode.get(j).getEta());
                gaussIntegrationAreaPoints[i].node[j][3] = getN4(gaussIntegrationAreaPoints[i].localNode.get(j).getXi(), gaussIntegrationAreaPoints[i].localNode.get(j).getEta());
            }
        }
    }

    public static LocalElement getInstance() {
        if (localElement == null)
            localElement = new LocalElement();

        return localElement;
    }

    private double getN1(double xi, double eta) {
        return 0.25 * (1 - xi) * (1 - eta);
    }

    private double getN2(double xi, double eta) {
        return 0.25 * (1 + xi) * (1 - eta);
    }

    private double getN3(double xi, double eta) {
        return 0.25 * (1 + xi) * (1 + eta);
    }

    private double getN4(double xi, double eta) {
        return 0.25 * (1 - xi) * (1 + eta);
    }

    private double getdN1dXi(double eta) {
        return -0.25 * (1 - eta);
    }

    private double getdN2dXi(double eta) {
        return 0.25 * (1 - eta);
    }

    private double getdN3dXi(double eta) {
        return 0.25 * (1 + eta);
    }

    private double getdN4dXi(double eta) {
        return -0.25 * (1 + eta);
    }

    private double getdN1dEta(double xi) {
        return -0.25 * (1 - xi);
    }

    private double getdN2dEta(double xi) {
        return -0.25 * (1 + xi);
    }

    private double getdN3dEta(double xi) {
        return 0.25 * (1 + xi);
    }

    private double getdN4dEta(double xi) {
        return 0.25 * (1 - xi);
    }

    public LocalArea[] getGaussIntegrationAreaPoints() {
        return gaussIntegrationAreaPoints;
    }

    public double[][] getdNdXi() {
        return dNdXi;
    }

    public double[][] getdNdEta() {
        return dNdEta;
    }

    public double[][] getMatrixN() {
        return matrixN;
    }
}
