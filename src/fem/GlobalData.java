package fem;

import localStorage.LocalElement;

import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GlobalData {

    private static GlobalData globalData;
    private final double height, width; //wysokosc, szerokosc
    private final int heightNodesNumber, widthNodesNumber; //liczba wezlow po wysokosci i po szerokosci
    private final int nodesNumber; //liczba wezlow
    private final int elementsNumber; //liczba elementow
    private final double tempStart; //temperatura poczatkowa
    private final double tau; //czas procesu
    private final double tempEnvironment; //temperatura otoczenia
    private final double alpha; //wspolczynnik wymiany ciepla
    private final double c; //cieplo wlasciwe
    private final double lambda; //wspolczynnik przewodzenia ciepla
    private final double rho; //gestosc

    private final LocalElement localElement;
    private final double[][] localH;
    private final Vector<Double> localP;
    private final double[][] globalH;
    private final Vector<Double> globalP;
    private double deltaTau;

    private GlobalData() throws IOException {

        FileReader dataFile = new FileReader("data/data.txt");
        StreamTokenizer reader = new StreamTokenizer(dataFile);
        List<Double> fileList = new ArrayList<>();

        int streamValue;
        while ((streamValue = reader.nextToken()) != StreamTokenizer.TT_EOF) {
            if (streamValue == StreamTokenizer.TT_NUMBER)
                fileList.add(reader.nval);
        }

        this.height = fileList.get(0);
        this.width = fileList.get(1);
        this.heightNodesNumber = fileList.get(2).intValue();
        this.widthNodesNumber = fileList.get(3).intValue();
        this.tempStart = fileList.get(4);
        this.tau = fileList.get(5);
        this.deltaTau = fileList.get(6);
        this.tempEnvironment = fileList.get(7);
        this.alpha = fileList.get(8);
        this.c = fileList.get(9);
        this.lambda = fileList.get(10);
        this.rho = fileList.get(11);

        nodesNumber = heightNodesNumber * widthNodesNumber;
        elementsNumber = (heightNodesNumber - 1) * (widthNodesNumber - 1);

        localElement = LocalElement.getInstance();
        localH = new double[4][4]; //lokalna macierz współczynników układów równań
        localP = new Vector<>();//lokalny wektor prawej części układu równań
        globalH = new double[nodesNumber][nodesNumber];//globalna macierz współczynników układów równań
        globalP = new Vector<>();//globalny wektor prawej części układu równań
        globalP.setSize(nodesNumber);
    }

    public static GlobalData getInstance() throws IOException {
        if (globalData == null) {
            globalData = new GlobalData();
        }
        return globalData;
    }

    public void dataCompute() throws IOException {

        for (int i = 0; i < nodesNumber; i++) {
            for (int j = 0; j < nodesNumber; j++) {
                globalH[i][j] = 0.0;
            }
            globalP.set(i, 0.0);
        }

        Grid grid = Grid.getInstance();
        Jacobian jacobian;
        Vector<Double> dNdX = new Vector<>();
        dNdX.setSize(4);
        Vector<Double> dNdY = new Vector<>();
        dNdY.setSize(4);
        Vector<Double> x = new Vector<>();
        x.setSize(4);
        Vector<Double> y = new Vector<>();
        y.setSize(4);
        Vector<Double> temp0 = new Vector<>();
        temp0.setSize(4);
        double t0p, cij;
        int id;
        double detJ = 0;

        localP.setSize(4);

        for (int elementNumber = 0; elementNumber < elementsNumber; elementNumber++) {

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    localH[i][j] = 0;
                }
                localP.set(i, 0.0);
            }

            for (int i = 0; i < 4; i++) {
                id = grid.elements.get(elementNumber).globalNodeID.get(i);
                x.set(i, grid.nodes.get(id).getX());
                y.set(i, grid.nodes.get(id).getY());
                temp0.set(i, grid.nodes.get(id).getTemp());
            }

            for (int integrationPoints = 0; integrationPoints < 4; integrationPoints++) {//lpc po powierzchni w jednym elemencie
                jacobian = new Jacobian(integrationPoints, x, y);
                t0p = 0;

                for (int i = 0; i < 4; i++) {//nodesNumber w jednym elemencie skonczonym
                    dNdX.set(i, (1.0 / jacobian.getDetJ() * (jacobian.getInvertedJ()[0][0] * localElement.getdNdXi()[integrationPoints][i] + jacobian.getInvertedJ()[0][1] * localElement.getdNdEta()[integrationPoints][i])));
                    dNdY.set(i, (1.0 / jacobian.getDetJ() * (jacobian.getInvertedJ()[1][0] * localElement.getdNdXi()[integrationPoints][i] + jacobian.getInvertedJ()[1][1] * localElement.getdNdEta()[integrationPoints][i])));

                    t0p += temp0.get(i) * localElement.getMatrixN()[integrationPoints][i];
                }
                detJ = Math.abs(jacobian.getDetJ());
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        cij = c * rho * localElement.getMatrixN()[integrationPoints][i] * localElement.getMatrixN()[integrationPoints][j] * detJ;
                        localH[i][j] += lambda * (dNdX.get(i) * dNdX.get(j) + dNdY.get(i) * dNdY.get(j)) * detJ + cij / deltaTau;
                        localP.set(i, localP.get(i) + cij / deltaTau * t0p);
                    }
                }
            }

            //warunki brzegowe
            for (int acn = 0; acn < grid.elements.get(elementNumber).getAreaContactNumber(); acn++) {
                id = grid.elements.get(elementNumber).getLocalPointsNumbers().get(acn);
                switch (id) {
                    case 0:
                        detJ = Math.sqrt(Math.pow(grid.elements.get(elementNumber).nodeVector.get(3).getX() - grid.elements.get(elementNumber).nodeVector.get(0).getX(), 2)
                                + Math.pow(grid.elements.get(elementNumber).nodeVector.get(3).getY() - grid.elements.get(elementNumber).nodeVector.get(0).getY(), 2)) / 2.0;
                        break;
                    case 1:
                        detJ = Math.sqrt(Math.pow(grid.elements.get(elementNumber).nodeVector.get(0).getX() - grid.elements.get(elementNumber).nodeVector.get(1).getX(), 2)
                                + Math.pow(grid.elements.get(elementNumber).nodeVector.get(0).getY() - grid.elements.get(elementNumber).nodeVector.get(1).getY(), 2)) / 2.0;
                        break;
                    case 2:
                        detJ = Math.sqrt(Math.pow(grid.elements.get(elementNumber).nodeVector.get(1).getX() - grid.elements.get(elementNumber).nodeVector.get(2).getX(), 2)
                                + Math.pow(grid.elements.get(elementNumber).nodeVector.get(1).getY() - grid.elements.get(elementNumber).nodeVector.get(2).getY(), 2)) / 2.0;
                        break;
                    case 3:
                        detJ = Math.sqrt(Math.pow(grid.elements.get(elementNumber).nodeVector.get(2).getX() - grid.elements.get(elementNumber).nodeVector.get(3).getX(), 2)
                                + Math.pow(grid.elements.get(elementNumber).nodeVector.get(2).getY() - grid.elements.get(elementNumber).nodeVector.get(3).getY(), 2)) / 2.0;
                        break;
                }

                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 4; j++) {
                        for (int k = 0; k < 4; k++) {
                            localH[j][i] += alpha * localElement.getGaussIntegrationAreaPoints()[id].node[i][j] * localElement.getGaussIntegrationAreaPoints()[i].node[i][k] * detJ;
                        }
                        localP.set(j, localP.get(j) + alpha * tempEnvironment * localElement.getGaussIntegrationAreaPoints()[id].node[i][j] * detJ);
                    }
                }
            }
            //agregacja
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    globalH[grid.elements.get(elementNumber).globalNodeID.get(i)][grid.elements.get(elementNumber).globalNodeID.get(j)] += localH[i][j];
                }
                globalP.set(grid.elements.get(elementNumber).globalNodeID.get(i), globalP.get(grid.elements.get(elementNumber).globalNodeID.get(i)) + localP.get(i));
            }
        }
    }

    double getHeight() {
        return height;
    }

    double getWidth() {
        return width;
    }

    public int getHeightNodesNumber() {
        return heightNodesNumber;
    }

    public int getWidthNodesNumber() {
        return widthNodesNumber;
    }

    public int getNodesNumber() {
        return nodesNumber;
    }

    double getTempStart() {
        return tempStart;
    }

    public double getDeltaTau() {
        return deltaTau;
    }

    public double getTau() {
        return tau;
    }

    public double[][] getGlobalH() {
        return globalH;
    }

    public Vector<Double> getGlobalP() {
        return globalP;
    }

    public int getElementsNumber() {
        return elementsNumber;
    }
}
