public class VisualizerEdge {
    int hash1;
    int hash2;
    String context;

    public VisualizerEdge(int hash1, int hash2, String context) {
        this.hash1 = hash1;
        this.hash2 = hash2;
        this.context = context;
    }

    public VisualizerEdge(Visualizer visualizer1, Visualizer visualizer2, String context) {
        this.hash1 = visualizer1.hashCode();
        this.hash2 = visualizer2.hashCode();
        this.context = context;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VisualizerEdge that = (VisualizerEdge) o;

        if (hash1 != that.hash1) return false;
        if (hash2 != that.hash2) return false;
        return context != null ? context.equals(that.context) : that.context == null;
    }

    @Override
    public int hashCode() {
        int result = hash1;
        result = 31 * result + hash2;
        result = 31 * result + (context != null ? context.hashCode() : 0);
        return result;
    }
}
