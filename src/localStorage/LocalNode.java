package localStorage;

class LocalNode {
    private final double xi;
    private final double eta;

    LocalNode(double xi, double eta) {
        this.xi = xi;
        this.eta = eta;
    }

    double getXi() {
        return xi;
    }

    double getEta() {
        return eta;
    }
}
