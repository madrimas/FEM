package fem;

import localStorage.LocalElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
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
    private final double alfa; //wspolczynnik wymiany ciepla
    private final double c; //cieplo wlasciwe
    private final double lambda; //wspolczynnik przewodzenia ciepla
    private final double rho; //gestosc

    private final LocalElement localElement;
    private final double[][] hLocal;
    private final Vector<Double> pLocal;
    private final double[][] hGlobal;
    private final Vector<Double> pGlobal;
    private double dTau; //poczatkowa wartosc przyrostu czasu

    private GlobalData() throws FileNotFoundException {

        Scanner input = new Scanner(new File("data.txt"));
        input.hasNextDouble();
        this.height = input.nextDouble();
        input.findInLine(";");
        this.width = input.nextDouble();
        input.findInLine(";");
        this.heightNodesNumber = input.nextInt();
        input.findInLine(";");
        this.widthNodesNumber = input.nextInt();
        input.findInLine(";");
        this.tempStart = input.nextDouble();
        input.findInLine(";");
        this.tau = input.nextDouble();
        input.findInLine(";");
        this.dTau = input.nextDouble();
        input.findInLine(";");
        this.tempEnvironment = input.nextDouble();
        input.findInLine(";");
        this.alfa = input.nextDouble();
        input.findInLine(";");
        this.c = input.nextDouble();
        input.findInLine(";");
        this.lambda = input.nextDouble();
        input.findInLine(";");
        this.rho = input.nextDouble();
        input.close();

        nodesNumber = heightNodesNumber * widthNodesNumber;
        elementsNumber = (heightNodesNumber - 1) * (widthNodesNumber - 1);

        localElement = LocalElement.getInstance();
        hLocal = new double[4][4];
        pLocal = new Vector<>();
        hGlobal = new double[nodesNumber][nodesNumber];
        pGlobal = new Vector<>();
        pGlobal.setSize(nodesNumber);
    }

    public static GlobalData getInstance() throws FileNotFoundException {
        if (globalData == null) {
            globalData = new GlobalData();
        }
        return globalData;
    }

    public static GlobalData getGlobalData() {
        return globalData;
    }

    void dataCompute() throws FileNotFoundException {

        for (int i = 0; i < nodesNumber; i++) {
            for (int j = 0; j < nodesNumber; j++) {
                hGlobal[i][j] = 0.0;
            }
            pGlobal.set(i, 0.0);
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

        pLocal.setSize(4);

        for (int elementNumber = 0; elementNumber < elementsNumber; elementNumber++) {

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    hLocal[i][j] = 0;
                }
                pLocal.set(i, 0.0);
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
                        hLocal[i][j] += lambda * (dNdX.get(i) * dNdX.get(j) + dNdY.get(i) * dNdY.get(j)) * detJ + cij / dTau;
                        pLocal.set(i, pLocal.get(i) + cij / dTau * t0p);
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
                            hLocal[j][i] += alfa * localElement.getGaussIntegrationAreaPoints()[id].node[i][j] * localElement.getGaussIntegrationAreaPoints()[i].node[i][k] * detJ;
                        }
                        pLocal.set(j, pLocal.get(j) + alfa * tempEnvironment * localElement.getGaussIntegrationAreaPoints()[id].node[i][j] * detJ);
                    }
                }
            }
            //agregacja
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    hGlobal[grid.elements.get(elementNumber).globalNodeID.get(i)][grid.elements.get(elementNumber).globalNodeID.get(j)] += hLocal[i][j];
                }
                pGlobal.set(grid.elements.get(elementNumber).globalNodeID.get(i), pGlobal.get(grid.elements.get(elementNumber).globalNodeID.get(i)) + pLocal.get(i));
            }
        }
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
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

    public int getElementsNumber() {
        return elementsNumber;
    }

    public double getTempStart() {
        return tempStart;
    }

    public double getTau() {
        return tau;
    }

    public double getdTau() {
        return dTau;
    }

    public double getTempEnvironment() {
        return tempEnvironment;
    }

    public double getAlfa() {
        return alfa;
    }

    public double getC() {
        return c;
    }

    public double getLambda() {
        return lambda;
    }

    public double getRho() {
        return rho;
    }

    public LocalElement getLocalElement() {
        return localElement;
    }

    public double[][] gethLocal() {
        return hLocal;
    }

    public Vector<Double> getpLocal() {
        return pLocal;
    }

    public double[][] gethGlobal() {
        return hGlobal;
    }

    public Vector<Double> getpGlobal() {
        return pGlobal;
    }
}
