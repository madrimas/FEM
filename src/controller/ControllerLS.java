package controller;

import linearSystem.GaussElimination;

import java.util.Scanner;
import java.util.Vector;

public class ControllerLS {

    public static void main(String[] args) {
        int number;
        int i, j;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Rozwiazywanie ukladu n-rownan z n-niewiadomymi Ax=b metoda Gaussa");
        System.out.println("Podaj n");
        number = scanner.nextInt();
        if (number < 1) {
            System.out.println("Nieprawidlowa wartosc parametru n");
            return;
        }

        double[][] a = new double[number][number];
        Vector<Double> b = new Vector<>();
        b.setSize(number);

        for (i = 0; i < number; i++) {
            for (j = 0; j < number; j++) {
                System.out.println("A[" + (i + 1) + "][" + (j + 1) + "] = ");
                a[i][j] = scanner.nextDouble();
                if ((i == j) && (a[i][j] == 0)) {
                    System.out.println("Wartosci na przekatnej musza byc rozne od 0");
                    return;
                }
            }
        }

        for (i = 0; i < number; i++) {
            System.out.println("b[" + (i + 1) + "] = ");
            b.set(i, scanner.nextDouble());
        }

        GaussElimination gauss = new GaussElimination();
        Vector<Double> x = gauss.gaussElimination(number, a, b);
        System.out.println("Wyniki metoda gaussa");
        for (i = 0; i < number; i++) {
            System.out.println("x[" + i + "] = " + x.get(i));
        }
    }
}
