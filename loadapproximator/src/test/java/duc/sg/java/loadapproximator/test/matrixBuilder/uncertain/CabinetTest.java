package duc.sg.java.loadapproximator.test.matrixBuilder.uncertain;

import duc.sg.java.loadapproximator.test.TestHelper;
import duc.sg.java.utils.BaseTransform;
import duc.sg.java.model.Substation;
import duc.sg.java.scenarios.ScenarioBuilder;
import duc.sg.java.scenarios.ScenarioName;
import org.junit.jupiter.params.provider.Arguments;

import java.util.*;

import static duc.sg.java.scenarios.CabinetSC.*;


public class CabinetTest extends UncertainMatrixBuilderTest {
    protected static Map<String, Integer> fuseIdx;

    static {
        fuseIdx = new HashMap<>(6);
        fuseIdx.put(F1_NAME, 0);
        fuseIdx.put(F2_NAME, 1);
        fuseIdx.put(F3_NAME, 2);
        fuseIdx.put(F4_NAME, 3);
        fuseIdx.put(F5_NAME, 4);
        fuseIdx.put(F6_NAME, 5);
    }

    @Override
    protected Substation createSubstation() {
        return new ScenarioBuilder()
                .chooseScenario(ScenarioName.CABINET)
                .build()
                .getGrid()
                .getSubstation(SUBSTATION_NAME)
                .get();
    }

    private static Arguments[] uncertain() {
        return TestHelper.allUncertainAndOpenPossibilities(F1_NAME, F2_NAME, F3_NAME, F4_NAME, F5_NAME, F6_NAME);
    }

    Possibility getScenario(boolean[] closedFused) {
        if (!closedFused[0]) {
            return new Possibility(new double[]{0});
        }

        if (!closedFused[1]) {
            return new Possibility(new double[]{1});
        }

        if (!closedFused[2]) {
            if (closedFused[3]) {
                return new Possibility(new double[]{
                        1, 1, 0,
                        0, 0, 1,
                        0, 1, 1
                });
            }
            return new Possibility(new double[]{1});
        }

        if (closedFused[3]) {
            return new Possibility(new double[]{
                    1, 1, 0, 0,
                    0, 0, 1, 0,
                    0, 0, 0, 1,
                    0, 1, 1, 1
            });
        }

        return new Possibility(new double[]{
                1, 1, 0,
                0, 0, 1,
                0, 1, 1
        });
    }

    public boolean contains(String[] source, String... toSearch) {
        int counter = 0;
        for(var s: source) {
           for(var ts: toSearch) {
               if(s.equals(ts)) {
                   counter++;
                   break;
               }
           }
        }
        return counter == toSearch.length;
    }


//    @ParameterizedTest
//    @MethodSource("uncertain")
    public void test(String[] uFusesName, String[] toOpen) {
        //filter only u_fuses that will impact the results - i.e. not DE
        List<String> final_uFusesNames = new ArrayList<>(uFusesName.length);
        if(!contains(toOpen, F1_NAME)) {
            for (var ufn : uFusesName) {
                if (ufn.equals(F1_NAME)) {
                    final_uFusesNames.add(ufn);
                }

                if (ufn.equals(F2_NAME) && contains(toOpen, F2_NAME)) {
                    break;
                }

                if (ufn.equals(F2_NAME) && !contains(toOpen, F3_NAME, F4_NAME)) {
                    final_uFusesNames.add(F2_NAME);
                }

                if (ufn.equals(F3_NAME) && !contains(toOpen, F2_NAME, F4_NAME)) {
                    final_uFusesNames.add(F3_NAME);
                }

                if (ufn.equals(F4_NAME) && !contains(toOpen, F2_NAME, F3_NAME)) {
                    final_uFusesNames.add(F4_NAME);
                }
            }
        }

        var nbPossibilities = (int) Math.pow(2, final_uFusesNames.size());
        var possibilities = new HashMap<Possibility, Integer>();
        for (int idxPoss = 0; idxPoss < nbPossibilities; idxPoss++) {
            // Generate a scenario (close/open uncertain fuses)
            boolean[] uFuseStates = BaseTransform.toBinary(idxPoss, final_uFusesNames.size());

            // state of all fuses
            // true = CLOSED
            boolean[] allFuseStates = new boolean[6];
            Arrays.fill(allFuseStates, true);
            for (var to: toOpen) {
                allFuseStates[fuseIdx.get(to)] = false;
            }

            // Open uncertain fuses that should be open according to values in uFuseStates
            for (int idxUFuseName = 0; idxUFuseName < final_uFusesNames.size(); idxUFuseName++) {
                var currName = final_uFusesNames.get(idxUFuseName);
                allFuseStates[fuseIdx.get(currName)] = uFuseStates[idxUFuseName];
            }

            possibilities.compute(getScenario(allFuseStates), (key, currentVal) -> currentVal == null ? 1 : currentVal + 1);
        }

        // generate the list of possibilities and the expected counter
        var listPossibilities = new ArrayList<double[]>(possibilities.size());
        var expectedCounter = new int[possibilities.size()];

        var idx = 0;
        for(var poss: possibilities.keySet()) {
            listPossibilities.add(poss.data);
            expectedCounter[idx] = possibilities.get(poss);
            idx++;
        }


        genericTest(uFusesName, listPossibilities, expectedCounter, toOpen);

    }

}
