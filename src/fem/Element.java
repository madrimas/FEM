package fem;

import java.io.IOException;
import java.util.Vector;

class Element {

    Vector<Node> nodeVector;//węzły w elemencie (4)
    Vector<Integer> globalNodeID;//globalne ID węzłów
    private int areaContactNumber;//liczba powierzchni stykowych z otoczeniem (0-4) na nie naklada sie warunki brzegowe
    private Vector<Integer> localAreaNumbers;//lokalne numery powierzchni kontatowych elementu (od lewej)

    //dh - wysokość, dw - szerokość -- jednego elementu
    Element(int i, int j, Vector<Node> nodes) throws IOException {

        nodeVector = new Vector<>();
        Vector<Area> areaVector = new Vector<>();//powierzchnia elementu (4 powierdzchnie w elemencie)
        globalNodeID = new Vector<>();
        GlobalData globalData = GlobalData.getInstance();

        //przypisanie węzłów do elemencie
        nodeVector.add(nodes.get(0));
        nodeVector.add(nodes.get(1));
        nodeVector.add(nodes.get(2));
        nodeVector.add(nodes.get(3));

        //wyznaczenie i przypisanie globalnych ID wezłów
        globalNodeID.add(globalData.getHeightNodesNumber() * i + j);
        globalNodeID.add(globalData.getHeightNodesNumber() * (i + 1) + j);
        globalNodeID.add(globalData.getHeightNodesNumber() * (i + 1) + (j + 1));
        globalNodeID.add(globalData.getHeightNodesNumber() * i + (j + 1));

        //przypisanie powierzchni do powierzchniowych
        areaVector.add(new Area(nodeVector.get(3), nodeVector.get(0)));
        areaVector.add(new Area(nodeVector.get(0), nodeVector.get(1)));
        areaVector.add(new Area(nodeVector.get(1), nodeVector.get(2)));
        areaVector.add(new Area(nodeVector.get(2), nodeVector.get(3)));

        areaContactNumber = 0;
        localAreaNumbers = new Vector<>();
        for (int k = 0; k < 4; k++) {
            if (areaVector.get(k).getNodes().get(0).getStatus() && areaVector.get(k).getNodes().get(1).getStatus()) {
                areaContactNumber++;
                localAreaNumbers.add(k);
            }
        }
    }

    int getAreaContactNumber() {
        return areaContactNumber;
    }

    Vector<Integer> getLocalAreaNumbers() {
        return localAreaNumbers;
    }
}
