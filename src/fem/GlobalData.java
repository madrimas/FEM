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
    private double height; //wysokość siatki
    private double width; //szerokosc siatki
    private int heightNodesNumber;//liczba węzłów w siatce w wysokości
    private int widthNodesNumber;//liczba węzłów w siatce w szerokości
    private int nodesNumber;//liczba węzłów w siatce
    private int elementsNumber;//liczba elementów w siatce
    private double tempStart;//temperatura przy starcie procesu
    private double tau;//czas trwania całego procesu
    private double tempEnvironment;//stała temperatura otoczenia
    private double alpha;//współczynnik wymiany ciepła
    private double c;//ciepło właściwe
    private double lambda;//współczynnik przewodzenia ciepła
    private double rho;//gęstość materiału

    private LocalElement localElement;//element lokalny
    private double[][] localH;//macierz sztywności obecny  współczynników układu równań H
    private Vector<Double> localP;//wektor obciążeń obecny(dla konkrentego elementu) prawej części układu równań P
    private double[][] globalH;//macierz globala współczynników układu równań H
    private Vector<Double> globalP;//wektor globalny prawej części układu równań P
    private double deltaTau;//zmiana czasu

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
        localH = new double[4][4];
        localP = new Vector<>();
        globalH = new double[nodesNumber][nodesNumber];
        globalP = new Vector<>();
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
        Vector<Double> dNdX = new Vector<>();//przechowywanie to czego chcemy obliczyc
        dNdX.setSize(4);
        Vector<Double> dNdY = new Vector<>();//przechowywyanie tego co chcemy obliczyc
        dNdY.setSize(4);
        Vector<Double> x = new Vector<>();//wspolrzedne wezla z elementu (globalne wartosci)
        x.setSize(4);
        Vector<Double> y = new Vector<>();//wspolrzedne wezla z elementu (globalne wartosci)
        y.setSize(4);
        Vector<Double> temp0 = new Vector<>();//temperatura poczatkowa ktora sie zmienia przy iternacji
        temp0.setSize(4);
        double t0p, cij;//t0p - temeperatura zinterpolowana do punktu calkowania; cip - macierz C w konkretnej komórce i j
        int id;//id elementu globalne np 0 5 6 1
        double detJ = 0;

        localP.setSize(4);

        for (int elementNumber = 0; elementNumber < elementsNumber; elementNumber++) {

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    localH[i][j] = 0;
                }
                localP.set(i, 0.0);
            }

            for (int i = 0; i < 4; i++) {//wyciagamy dane elementu z elementow w siatce
                id = grid.elements.get(elementNumber).globalNodeID.get(i);
                x.set(i, grid.nodes.get(id).getX());
                y.set(i, grid.nodes.get(id).getY());
                temp0.set(i, grid.nodes.get(id).getTemp());
            }

            for (int integrationPoints = 0; integrationPoints < 4; integrationPoints++) {//lpc po powierzchni w jednym elemencie (sposób 2-punktowy)
                jacobian = new Jacobian(integrationPoints, x, y);
                t0p = 0;

                for (int i = 0; i < 4; i++) {//nodesNumber w jednym elemencie skonczonym
                    dNdX.set(i, (1.0 / jacobian.getDetJ() * (jacobian.getInvertedMatrixJ()[0][0] * localElement.getdNdXi()[integrationPoints][i] + jacobian.getInvertedMatrixJ()[0][1] * localElement.getdNdEta()[integrationPoints][i])));
                    dNdY.set(i, (1.0 / jacobian.getDetJ() * (jacobian.getInvertedMatrixJ()[1][0] * localElement.getdNdXi()[integrationPoints][i] + jacobian.getInvertedMatrixJ()[1][1] * localElement.getdNdEta()[integrationPoints][i])));

                    t0p += temp0.get(i) * localElement.getMatrixN()[integrationPoints][i];
                }
                detJ = Math.abs(jacobian.getDetJ());
                for (int i = 0; i < 4; i++) {//bo 4 funkcje kształtu a mnozenie jest ransponowane [N]*[N]^T
                    for (int j = 0; j < 4; j++) {
                        cij = c * rho * localElement.getMatrixN()[integrationPoints][i] * localElement.getMatrixN()[integrationPoints][j] * detJ;
                        localH[i][j] += lambda * (dNdX.get(i) * dNdX.get(j) + dNdY.get(i) * dNdY.get(j)) * detJ + cij / deltaTau;
                        localP.set(i, localP.get(i) + cij / deltaTau * t0p);
                    }
                }
            }

            //warunki brzegowe
            for (int acn = 0; acn < grid.elements.get(elementNumber).getAreaContactNumber(); acn++) {
                id = grid.elements.get(elementNumber).getLocalPointsNumbers().get(acn);//id powierzchni lokalnej 0 1 2 3 (numerowanie od lewej)
                switch (id) {//wyliczanie jaconianu zaleznie od powierzchni
                    case 0:
                        detJ = Math.sqrt(Math.pow(grid.elements.get(elementNumber).nodeVector.get(3).getX() - grid.elements.get(elementNumber).nodeVector.get(0).getX(), 2)//wyliczenie dlugosci krawedzi
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
                //nakladanie warunku brzegowego
                for (int i = 0; i < 2; i++) {//2 pc na powierzchni
                    for (int j = 0; j < 4; j++) {//4 bo transponowane
                        for (int k = 0; k < 4; k++) {
                            localH[j][i] += alpha * localElement.getGaussIntegrationAreaPoints()[id].node[i][j] * localElement.getGaussIntegrationAreaPoints()[i].node[i][k] * detJ;//detJ z powierzchni
                        }
                        localP.set(j, localP.get(j) + alpha * tempEnvironment * localElement.getGaussIntegrationAreaPoints()[id].node[i][j] * detJ);
                    }
                }
            }
            //agregacja (wpisanie do macierzy globalncyh)
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
