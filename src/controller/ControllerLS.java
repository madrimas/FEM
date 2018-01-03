package controller;

import linearSystem.GaussElimination;
import linearSystem.JacobiMethod;

import java.util.Scanner;
import java.util.Vector;

public class ControllerLS {
    private static double A[][];
    private static Vector<Double> b;

    public static void main(String[] args) {
        int number;
        int i, j;

        Scanner dane = new Scanner(System.in);
        System.out.println("Rozwiazywanie ukladu n-rownan z n-niewiadomymi Ax=b");
        System.out.println("Podaj n");
        number = dane.nextInt();
        if (number < 1) {
            System.out.println("Nieprawidlowa warosc parametru n");
            return;
        }

        A = new double[number][number];
        b = new Vector<>();
        b.setSize(number);

        for (i = 0; i < number; i++) {
            for (j = 0; j < number; j++) {
                System.out.println("A[" + (i + 1) + "][" + (j + 1) + "] = ");
                A[i][j] = dane.nextDouble();
                if ((i == j) && (A[i][j] == 0)) {
                    System.out.println("Wartosci na przekatnej musza byc rozne od 0");
                    return;
                }
            }
        }

        for (i = 0; i < number; i++) {
            System.out.println("b[" + (i + 1) + "] = ");
            b.set(i, dane.nextDouble());
        }

        GaussElimination gauss = new GaussElimination();
        Vector<Double> x = gauss.gaussElimination(number, A, b);
        System.out.println("Wyniki metoda gaussa");
        for (i = 0; i < number; i++) {
            System.out.println("x[" + i + "] = " + x.get(i));
        }
        JacobiMethod jacobi = new JacobiMethod();
        x = jacobi.jacobiMethod(number, A, b);
        System.out.println("Wyniki metoda jacobiego");
        for (i = 0; i < number; i++) {
            System.out.println("x[" + i + "] = " + x.get(i));
        }
    }
}
