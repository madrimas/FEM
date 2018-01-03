package fem;

import java.io.IOException;
import java.util.Vector;

class Element {

    Vector<Node> nodeVector;//węzły w elemencie
    Vector<Area> areaVector;//powierzchnia elementu
    Vector<Integer> globalNodeID;
    GlobalData globalData;
    private int areaContactNumber;//liczba powierzchni stykowych z otoczeniem
    private Vector<Integer> localPointsNumbers;//lokalne numery powierzchni kontatowych elementu

    //dh - wysokość, dw - szerokość -- jednego elementu
    Element(int x, int y, Vector<Node> nodes) throws IOException {

        nodeVector = new Vector<>();
        areaVector = new Vector<>();
        globalNodeID = new Vector<>();
        globalData = GlobalData.getInstance();

        //przypisanie współrzędnych węzłów w elemencie
        nodeVector.add(nodes.get(0));
        nodeVector.add(nodes.get(1));
        nodeVector.add(nodes.get(2));
        nodeVector.add(nodes.get(3));

        //wyznaczenie i przypisanie globalnych ID wezłów
        globalNodeID.add(globalData.getHeightNodesNumber() * x + y);
        globalNodeID.add(globalData.getHeightNodesNumber() * (x + 1) + y);
        globalNodeID.add(globalData.getHeightNodesNumber() * (x + 1) + (y + 1));
        globalNodeID.add(globalData.getHeightNodesNumber() * x + (y + 1));

        //przypisanie powierzchni do wezlow powierzchniowych
        areaVector.add(new Area(nodeVector.get(3), nodeVector.get(0)));
        areaVector.add(new Area(nodeVector.get(0), nodeVector.get(1)));
        areaVector.add(new Area(nodeVector.get(1), nodeVector.get(2)));
        areaVector.add(new Area(nodeVector.get(2), nodeVector.get(3)));

        areaContactNumber = 0;
        for (int i = 0; i < 4; i++) {
            if (areaVector.get(i).getNodes().get(0).getStatus() == 1 && areaVector.get(i).getNodes().get(1).getStatus() == 1)
                areaContactNumber++;
        }
        localPointsNumbers = new Vector<>();
        for (int i = 0; i < 4; i++) {
            if (areaVector.get(i).getNodes().get(0).getStatus() == 1 && areaVector.get(i).getNodes().get(1).getStatus() == 1)
                localPointsNumbers.add(i);
        }
    }

    int getAreaContactNumber() {
        return areaContactNumber;
    }

    Vector<Integer> getLocalPointsNumbers() {
        return localPointsNumbers;
    }
}
