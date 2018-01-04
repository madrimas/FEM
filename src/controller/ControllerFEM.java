package controller;

import fem.GlobalData;
import fem.Grid;
import linearSystem.GaussElimination;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

public class ControllerFEM {

    public static void main(String[] args) throws IOException {
        GlobalData globalData = GlobalData.getInstance();
        Grid grid = Grid.getInstance();
        Vector<Double> tempVector;
        GaussElimination gaussElimination = new GaussElimination();
        PrintWriter printWriter = new PrintWriter("data/out.txt");
        int stepNumber = 1;

        for (double tau = 0; tau < globalData.getTau(); tau += globalData.getDeltaTau()) {
            globalData.dataCompute();
            tempVector = gaussElimination.gaussElimination(globalData.getNodesNumber(), globalData.getGlobalH(), globalData.getGlobalP());
            for (int i = 0; i < globalData.getNodesNumber(); i++) {
                grid.nodes.get(i).setTemp(tempVector.get(i));
            }

            int counter = 0;
            //System.out.println("Krok nr " + stepNumber);
            printWriter.println("Krok nr " + stepNumber++);
            for (int i = 0; i < globalData.getWidthNodesNumber(); i++) {
                for (int j = 0; j < globalData.getHeightNodesNumber(); j++) {
                    //System.out.printf("%.15f\t", grid.nodes.get(counter).getTemp());
                    printWriter.printf("%.15f\t", grid.nodes.get(counter).getTemp());
                    if(((globalData.getTau()-tau)==globalData.getDeltaTau()) && (counter<globalData.getElementsNumber())) grid.showElement(counter);
                    counter++;
                }
                //System.out.println("");
                printWriter.println("");
            }
            //System.out.println("\n\n");
            printWriter.println("");
        }
        printWriter.close();
        grid.showNode();
        System.out.println("Dane wyjściowe w pliku out.txt");
    }
}
