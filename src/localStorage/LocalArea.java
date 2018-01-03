package localStorage;

import java.util.Vector;

public class LocalArea {
    public final double node[][];
    final Vector<LocalNode> localNode;

    LocalArea(LocalNode firstNode, LocalNode secondNode) {
        localNode = new Vector<>();
        localNode.add(firstNode);
        localNode.add(secondNode);

        node = new double[2][4];
    }
}
