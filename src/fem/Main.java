package fem;

import linearSystem.GaussElimination;

import java.io.FileNotFoundException;
import java.util.Vector;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        GlobalData globalData = GlobalData.getInstance();
        Grid grid = Grid.getInstance();
        Vector<Double> tempVector;
        GaussElimination gaussElimination = new GaussElimination();

        for (int tau = 0; tau < globalData.getTau(); tau++) {
            globalData.dataCompute();
            tempVector = gaussElimination.gaussElimination(globalData.getNodesNumber(), globalData.gethGlobal(), globalData.getpGlobal());
            for (int i = 0; i < globalData.getNodesNumber(); i++) {
                grid.nodes.get(i).setTemp(tempVector.get(i));
            }
        }

        int count = 0;
        for (int i = 0; i < globalData.getWidthNodesNumber(); i++) {
            for (int j = 0; j < globalData.getHeightNodesNumber(); j++) {
                System.out.printf("%.15f\t", grid.nodes.get(count++).getTemp());
            }
            System.out.println("");
        }
        System.out.println("\n\n");
    }
}
