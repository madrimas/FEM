package fem;

import java.io.IOException;

public class Node {

    private double x, y;
    private boolean status;//1-na krawędzi siatki, 0-wewnątrz siatki
    private double temp;

    Node(double x, double y) throws IOException {
        GlobalData globalData = GlobalData.getInstance();

        this.x = x;
        this.y = y;
        this.temp = globalData.getTempStart();

        if (this.x == 0.0 || this.y == 0.0 || this.x == globalData.getWidth() || this.y == globalData.getHeight()) {
            this.status = true;
        } else {
            this.status = false;
        }
    }

    double getX() {
        return x;
    }

    double getY() {
        return y;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    boolean getStatus() {
        return status;
    }
}
