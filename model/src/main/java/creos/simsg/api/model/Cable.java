package creos.simsg.api.model;

import creos.simsg.api.uncertainty.MultiplePossibilities;
import creos.simsg.api.uncertainty.PossibilityDouble;
import creos.simsg.api.uncertainty.math.UMath;

import java.util.*;

public class Cable {
    /**
     * Unique identifier of the cable.
     * WARNING: If you change the type of the identified, please modify also the WebUI :)
     */
    private final String id;

    /**
     * Cables end by exactly 2 fuses, so the length of this array should always equal 2.
     * It can be replaced by two fields
     */
    private final Fuse[] fuses;
    private final List<Meter> meters;

    public Cable(String id) {
        this.id = id;
        fuses = new Fuse[2];
        meters = new ArrayList<>();
    }

    public Cable() {
        this(UUID.randomUUID().toString());
    }

    public String getId() {
        return id;
    }

    private void setFuse(int idx, Fuse f) {
        fuses[idx] = f;
        f.setCable(this);
    }

    private Fuse getFuse(int idx) {
        return fuses[idx];
    }


    public void setFirstFuse(Fuse f) {
        setFuse(0, f);
    }

    public void setSecondFuse(Fuse f) {
        setFuse(1, f);
    }

    public void setFuses(Fuse first, Fuse second) {
        setFuse(0, first);
        setFuse(1, second);
    }

    public Fuse getFirstFuse() {
        return getFuse(0);
    }

    public Fuse getSecondFuse() {
        return getFuse(1);
    }

    public void addMeters(Meter... ms) {
        Collections.addAll(meters, ms);
    }

    public List<Meter> getMeters() {
        return Collections.unmodifiableList(meters);
    }

    public double getConsumption() {
        return meters.stream()
                .mapToDouble(Meter::getConsumption)
                .sum();
    }

    /**
     * Return the uncertain load, computed by the `algorithms/loadapproximator/uncertain` modules.
     * The cable uLoad equal the maximum of its fuses loads, for each configuration
     *
     * TODO: remove this function and store the result in the {@link SharedMemory}, or in the algorithm
     *  itself @see <a href="https://github.com/UL-SnT-Serval/creos.simSG.api/issues/3">Issue 3</a>
     *
     * @return uncertain loads
     */
    public MultiplePossibilities getUncertainLoad() {
        if(fuses[0] == null) {
            return fuses[1].getUncertainLoad();
        }

        if(fuses[1] == null) {
            return fuses[0].getUncertainLoad();
        }


        MultiplePossibilities res = new MultiplePossibilities();
        Iterator<PossibilityDouble> itPossF1 = fuses[0].getUncertainLoad().iterator();
        Iterator<PossibilityDouble> itPossF2 = fuses[1].getUncertainLoad().iterator();

        while (itPossF1.hasNext()) {
            var possF1 = itPossF1.next();
            var possF2 = itPossF2.next();

            res.addPossibility(UMath.max(possF1, possF2));
        }

        return res;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Cable)) {
            return false;
        }
        var casted = (Cable) obj;
        return id.equals(casted.id);
    }

    @Override
    public String toString() {
        return "Cable(" +
                "fuses=" + Arrays.toString(Arrays.stream(fuses).map(Fuse::getName).toArray()) +
                ", meters=" + Arrays.toString(meters.stream().map(Meter::getName).toArray()) +
                 ", consumption=" + getConsumption() +
                 ", load=" + getUncertainLoad() +
                ')';
    }
}
