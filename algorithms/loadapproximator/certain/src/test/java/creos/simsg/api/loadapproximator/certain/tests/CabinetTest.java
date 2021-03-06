package creos.simsg.api.loadapproximator.certain.tests;

import creos.simsg.api.model.Substation;
import creos.simsg.api.scenarios.CabinetSC;
import creos.simsg.api.scenarios.ScenarioBuilder;
import creos.simsg.api.scenarios.ScenarioName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static creos.simsg.api.scenarios.CabinetSC.*;


public class CabinetTest extends LoadApproximatorTest {

    @Override
    protected Substation createSubstation() {
        return new ScenarioBuilder()
                .chooseScenario(ScenarioName.CABINET)
                .build()
                .getGrid()
                .getSubstation(CabinetSC.SUBSTATION_NAME)
                .get();
    }

    @Override
    protected String[] getFuses() {
        return new String[] {F1_NAME, F2_NAME, F3_NAME, F4_NAME, F5_NAME, F6_NAME};
    }

    @Override
    protected String[] getFuseCables() {
        return new String[]{F1_NAME, F3_NAME, F4_NAME};
    }

    private static Arguments[] openCloseF5F6() {
       return TestHelper.generateAllPossibilitiesWithValues(3, F5_NAME, F6_NAME);
    }

    private static Arguments[] openCloseF3F4F5F6() {
        return TestHelper.generateAllPossibilitiesWithValues(3, F3_NAME, F4_NAME, F5_NAME, F6_NAME);
    }

    private static Arguments[] openCloseF2F3F4F5F6() {
        return TestHelper.generateAllPossibilitiesWithValues(3, F2_NAME, F3_NAME, F4_NAME, F5_NAME, F6_NAME);
    }

    @ParameterizedTest
    @MethodSource("openCloseF5F6")
    public void sc1_testAllClosed(String[] toOpen, Double[] consumptions) {
        var loadCbl = consumptions[0] + consumptions[1] + consumptions[2];
        var expectedCables = new double[] {
                loadCbl,
                consumptions[1],
                consumptions[2]
        };
        var expectedFuses = new double[] {
                loadCbl,
                consumptions[0]-loadCbl,
                consumptions[1],
                consumptions[2],
                0,
                0
        };
        genericTest(toOpen, consumptions, expectedCables, expectedFuses);
    }


    @ParameterizedTest
    @MethodSource("openCloseF5F6")
    public void sc2_testF4Open(String[] toOpen, Double[] consumptions) {
        var expectedCables = new double[] {
                consumptions[0] + consumptions[1],
                consumptions[1],
                0
        };
        var expectedFuses = new double[] {
                consumptions[0] + consumptions[1],
                -consumptions[1],
                consumptions[1],
                0,
                0,
                0
        };
        consumptions[2] = 0.;
        genericTest(TestHelper.merge(toOpen, F4_NAME), consumptions, expectedCables, expectedFuses);
    }

    @ParameterizedTest
    @MethodSource("openCloseF5F6")
    public void sc3_testF3Open(String[] toOpen, Double[] consumptions) {
        var expectedCables = new double[] {
                consumptions[0] + consumptions[2],
                0,
                consumptions[2]
        };
        var expectedFuses = new double[] {
                consumptions[0] + consumptions[2],
                -consumptions[2],
                0,
                consumptions[2],
                0,
                0
        };
        consumptions[1] = 0.;
        genericTest(TestHelper.merge(toOpen, F3_NAME), consumptions, expectedCables, expectedFuses);
    }

    @ParameterizedTest
    @MethodSource("openCloseF5F6")
    public void sc4_testF3F4Open(String[] toOpen, Double[] consumptions) {
        var expectedCables = new double[] {consumptions[0], 0, 0};
        var expectedFuses = new double[] {consumptions[0], 0, 0, 0, 0, 0};
        consumptions[1] = 0.;
        consumptions[2] = 0.;
        genericTest(TestHelper.merge(toOpen, F3_NAME, F4_NAME), consumptions, expectedCables, expectedFuses);
    }

    @Disabled("See https://github.com/UL-SnT-Serval/creos.simSG.api/issues/14")
    @ParameterizedTest
    @MethodSource("openCloseF3F4F5F6")
    public void sc5_testF2Open(String[] toOpen, Double[] consumptions) {
        var expectedCables = new double[] {consumptions[0],0,0};
        var expectedFuses = new double[] {consumptions[0],0,0,0,0,0,0};
        consumptions[1] = 0.;
        consumptions[2] = 0.;
        genericTest(TestHelper.merge(toOpen, F2_NAME), consumptions, expectedCables, expectedFuses);
    }

    @ParameterizedTest
    @MethodSource("openCloseF2F3F4F5F6")
    public void sc6_testF1Open(String[] toOpen) {
        var expectedCables = new double[] {0,0,0};
        var expectedFuses = new double[] {0,0,0,0,0,0};
        var consumptions = new Double[]{0., 0., 0.};
        genericTest(TestHelper.merge(toOpen, F1_NAME), consumptions, expectedCables, expectedFuses);
    }


}
