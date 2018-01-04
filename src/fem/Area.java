package fem;

import java.util.Vector;

class Area {
    private Vector<Node> nodes;

    Area(Node firstNode, Node secondNode) {
        nodes = new Vector<>();
        this.nodes.add(firstNode);
        this.nodes.add(secondNode);
    }

    Vector<Node> getNodes() {
        return nodes;
    }
}
