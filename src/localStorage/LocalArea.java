package localStorage;

import java.util.Vector;

public class LocalArea {
    final Vector<LocalNode> localNode;
    final double node[][];

    LocalArea(LocalNode firstNode, LocalNode secondNode) {
        localNode = new Vector<>();
        localNode.add(firstNode);
        localNode.add(secondNode);

        node = new double[2][4];
    }
}
