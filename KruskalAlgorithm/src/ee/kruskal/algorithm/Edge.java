package ee.kruskal.algorithm;

public class Edge implements Comparable<Edge> {
    private final int source;
    private final int destination;
    private final int weight;
    private boolean inMST;

    public Edge(int source, int destination, int weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
        this.inMST = false;
    }

    public int getSource() {
        return source;
    }

    public int getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }

    public boolean isInMST() {
        return inMST;
    }

    public void setInMST(boolean inMST) {
        this.inMST = inMST;
    }

    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight);
    }

    @Override
    public String toString() {
        return "(" + source + " - " + destination + " : " + weight + ")";
    }
}
