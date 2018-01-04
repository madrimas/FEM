package fem;

import java.io.IOException;
import java.util.Vector;

public class Grid {

    private static Grid grid = null;
    public Vector<Node> nodes;//wektor węzłów w siatce
    Vector<Element> elements;//wektor elementów w siatce
    private GlobalData globalData;

    private Grid() throws IOException {
        globalData = GlobalData.getInstance();
        nodes = new Vector<>();
        elements = new Vector<>();

        //wyliczenie zmiany wysokości i szerekości w zależności od ilości węzłów
        double dWidth = globalData.getWidth() / (globalData.getWidthNodesNumber() - 1);
        double dHeight = globalData.getHeight() / (globalData.getHeightNodesNumber() - 1);

        for (int i = 0; i < globalData.getWidthNodesNumber(); i++) {
            for (int j = 0; j < globalData.getHeightNodesNumber(); j++) {
                nodes.add(new Node(i * dWidth, j * dHeight));
            }
        }

        Vector<Node> tempNode;
        for (int i = 0; i < globalData.getWidthNodesNumber() - 1; i++) {
            for (int j = 0; j < globalData.getHeightNodesNumber() - 1; j++) {
                tempNode = new Vector<>();
                tempNode.add(nodes.get(globalData.getHeightNodesNumber() * i + j));
                tempNode.add(nodes.get(globalData.getHeightNodesNumber() * (i + 1) + j));
                tempNode.add(nodes.get(globalData.getHeightNodesNumber() * (i + 1) + (j + 1)));
                tempNode.add(nodes.get(globalData.getHeightNodesNumber() * i + (j + 1)));
                elements.add(new Element(i, j, tempNode));
            }
        }
    }

    public static Grid getInstance() throws IOException {
        if (grid == null) {
            grid = new Grid();
        }
        return grid;
    }

    public void showNode() {
        for (int i = 0; i < globalData.getNodesNumber(); i++) {
            int status = nodes.get(i).getStatus() ? 1 : 0;
            System.out.println("i:" + i + "\t\tStatus:" + status + "\t(" + nodes.get(i).getX() + ";" + nodes.get(i).getY() + ")");
        }
    }

    public void showElement(int elementNumber) {
        System.out.println("ELEMENT:" + elementNumber);
        for (int j = 0; j < 4; j++) {
            int status = nodes.get(elementNumber).getStatus() ? 1 : 0;
            System.out.println("ID" + (j) + "\tglobal ID:" + elements.get(elementNumber).globalNodeID.get(j) + "\tStatus:" + status + "\t(" + elements.get(elementNumber).nodeVector.get(j).getX() + ";" + elements.get(elementNumber).nodeVector.get(j).getY() + ")");
        }
    }
}
