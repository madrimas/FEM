package fem;

import java.util.Vector;

public class Area {
    private final Vector<Node> nodes;

    Area(Node firstNode, Node secondNode) {
        nodes = new Vector<>();
        this.nodes.add(firstNode);
        this.nodes.add(secondNode);
    }

    public Vector<Node> getNodes() {
        return nodes;
    }
}
