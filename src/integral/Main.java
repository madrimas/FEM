package integral;

public class Main {

    public static void main(String[] args) {
        Integral integral = new Integral();

        System.out.println("Rezultat sposobem 2-punktowym: " + integral.integral2P());

        System.out.println("Rezultat sposobem 3-punktowym: " + integral.integral3P());
    }
}
