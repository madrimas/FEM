package fem;

import java.io.FileNotFoundException;

public class Node {

    private final double x, y;
    private final int status;
    private final GlobalData globalData;
    private double temp;

    Node(double x, double y) throws FileNotFoundException {
        globalData = GlobalData.getInstance();

        this.x = x;
        this.y = y;
        this.temp = globalData.getTempStart();

        if (this.x == 0.0 || this.y == 0.0 || this.x == globalData.getWidth() || this.y == globalData.getHeight()) {
            this.status = 1;
        } else {
            this.status = 0;
        }
    }

    public double getX() {

        return x;
    }

    public double getY() {
        return y;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public int getStatus() {
        return status;
    }
}
