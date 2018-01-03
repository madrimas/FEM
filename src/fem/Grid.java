package fem;

import java.io.IOException;
import java.util.Vector;

public class Grid {

    private static Grid grid = null;
    Vector<Node> nodes;
    Vector<Element> elements;
    GlobalData globalData;

    private Grid() throws IOException {
        globalData = GlobalData.getInstance();
        nodes = new Vector<>();
        elements = new Vector<>();

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
                int size = elements.size();
            }
        }
    }

    static Grid getInstance() throws IOException {
        if (grid == null) {
            grid = new Grid();
        }
        return grid;
    }

    public void wypiszND() {
        for (int i = 0; i < globalData.getNodesNumber(); i++) {
            System.out.println("i:" + i + "\tStatus:" + nodes.get(i).getStatus() + "\t(" + nodes.get(i).getX() + ";" + nodes.get(i).getY() + ")");
        }
    }

    public void wypiszEL(int i) {
        System.out.println("ELEMENT:" + i);
        for (int j = 0; j < 4; j++) {
            System.out.println("ID" + (j) + "\tglobal ID:" + elements.get(i).globalNodeID.get(j) + "\tStatus:" + elements.get(i).nodeVector.get(j).getStatus() + "\t(" + elements.get(i).nodeVector.get(j).getX() + ";" + elements.get(i).nodeVector.get(j).getY() + ")");
        }
    }
}
