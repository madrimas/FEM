package localStorage;

public class LocalNode {
    private final double xi;
    private final double eta;

    LocalNode(double xi, double eta) {
        this.xi = xi;
        this.eta = eta;
    }

    public double getXi() {
        return xi;
    }

    public double getEta() {
        return eta;
    }
}
