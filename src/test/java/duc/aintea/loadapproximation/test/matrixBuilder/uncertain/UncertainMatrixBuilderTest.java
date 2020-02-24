package duc.aintea.loadapproximation.test.matrixBuilder.uncertain;

import duc.aintea.loadapproximation.matrix.FuseStatesMatrix;
import duc.aintea.loadapproximation.matrix.UncertainMatrixBuilder;
import duc.aintea.sg.Fuse;
import duc.aintea.sg.Substation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class UncertainMatrixBuilderTest {
    protected Map<String, Fuse> fusesMap;
    protected Substation substation;

    protected abstract Substation createSubstation();

    protected void initFuseMap() {
        fusesMap = new HashMap<>();

        var waiting = new ArrayDeque<Fuse>();
        waiting.add(substation.getFuses().get(0));

        while (!waiting.isEmpty()) {
            var current = waiting.pop();
            current.setConfidence(100);
            fusesMap.put(current.getName(), current);

            var opp = current.getOpposite();
            opp.setConfidence(100);
            fusesMap.put(opp.getName(), opp);

            var ownerOpp = opp.getOwner();
            for (var f : ownerOpp.getFuses()) {
                if (!fusesMap.containsKey(f.getName())) {
                    waiting.add(f);
                }
            }
        }
    }

    @BeforeEach
    public void setup() {
        substation = createSubstation();
        initFuseMap();
    }

    @AfterEach
    public void tearDown() {
        for(var key: fusesMap.keySet()) {
            fusesMap.get(key).setConfidence(100);
            fusesMap.get(key).closeFuse();
        }
    }

    protected void genericTest(String[] uFusesName, List<double[]> possibilities, int[] expectedCounter, String... toOpen) {
        for (var fName: uFusesName) {
            fusesMap.get(fName).setConfidence(0.5);
        }

        for(var to: toOpen) {
            fusesMap.get(to).openFuse();
        }

        FuseStatesMatrix[] matrices = UncertainMatrixBuilder.build(substation);


        var expectedSize = 0;
        for(var eC: expectedCounter) {
            expectedSize += eC;
        }
        assertEquals(expectedSize, matrices.length);

        var actualCounter = new int[possibilities.size()];

        for (var m : matrices) {
            var actual = m.getData();

            for (int i = 0; i < possibilities.size(); i++) {
                if (Arrays.equals(possibilities.get(i), actual)) {
                    actualCounter[i]++;
                }
            }
        }

        assertArrayEquals(expectedCounter, actualCounter);
    }

}