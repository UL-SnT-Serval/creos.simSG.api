package creos.simsg.api.matrix.certain;

import creos.simsg.api.scenarios.ScenarioBuilder;
import creos.simsg.api.scenarios.ScenarioName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static creos.simsg.api.scenarios.ParaCabinetSC.*;


public class ParaCabinetTest extends MatrixBuilderTest {

    @Override
    protected void createSubstation() {
        substation = new ScenarioBuilder()
                .chooseScenario(ScenarioName.PARA_CABINET)
                .build()
                .getSubstation();
    }

    private static Arguments[] openCloseF8() {
        return TestHelper.generateAllPossibilities(F8_NAME);
    }

    private static Arguments[] openCloseF2F3F4F5F6F7F8() {
        return TestHelper.generateAllPossibilities(F2_NAME, F3_NAME, F4_NAME, F5_NAME, F6_NAME, F7_NAME, F8_NAME);
    }

    private static Arguments[] openCloseF3F4F5F6F7F8() {
        return TestHelper.generateAllPossibilities(F3_NAME, F4_NAME, F5_NAME, F6_NAME, F7_NAME, F8_NAME);
    }

    private static Arguments[] openCloseF4F7F8() {
        return TestHelper.generateAllPossibilities(F4_NAME, F7_NAME, F8_NAME);
    }

    private static Arguments[] openCloseF4F6F7F8() {
        return TestHelper.generateAllPossibilities(F4_NAME, F6_NAME, F7_NAME, F8_NAME);
    }

    private static Arguments[] openCloseF6F7F8() {
        return TestHelper.generateAllPossibilities(F6_NAME, F7_NAME, F8_NAME);
    }

    private static Arguments[] openCloseF7F8() {
        return TestHelper.generateAllPossibilities(F7_NAME, F8_NAME);
    }

    private static Arguments[] openCloseF4F8() {
        return TestHelper.generateAllPossibilities(F4_NAME, F8_NAME);
    }


    @ParameterizedTest
    @MethodSource("openCloseF8")
    public void sc1_allClosed(String[] toOpen) {
        double[] expected = new double[] {
                1,1,0,0,0,0,0,
                0,1,1,0,1,0,0,
                0,0,1,1,0,0,0,
                0,0,1,0,-1,0,0,
                0,0,0,1,0,1,1,
                0,0,0,0,1,1,0,
                0,0,0,0,0,0,1,
        };
        genericTest(expected, toOpen);
    }

    @ParameterizedTest
    @MethodSource("openCloseF2F3F4F5F6F7F8")
    public void sc2_f1Open(String[] toOpen) {
        genericTest(new double[]{0}, TestHelper.merge(toOpen, F1_NAME));
    }

    @ParameterizedTest
    @MethodSource("openCloseF3F4F5F6F7F8")
    public void sc3_f2Open(String[] toOpen) {
        genericTest(new double[]{1}, TestHelper.merge(toOpen, F2_NAME));
    }

    @ParameterizedTest
    @MethodSource("openCloseF4F6F7F8")
    public void sc4_f3f5Open(String[] toOpen) {
        genericTest(new double[]{1}, TestHelper.merge(toOpen, F3_NAME, F5_NAME));
    }

    @ParameterizedTest
    @MethodSource("openCloseF7F8")
    public void sc5_f4f6Open(String[] toOpen) {
        double[] expected = new double[] {
                1,1,0,0,
                0,1,1,1,
                0,0,1,0,
                0,0,0,1,
        };
        genericTest(expected, TestHelper.merge(toOpen, F4_NAME, F6_NAME));
    }

    @ParameterizedTest
    @MethodSource("openCloseF8")
    public void sc6_f7Open(String[] toOpen) {
        double[] expected = new double[] {
                1,1,0,0,0,0,
                0,1,1,0,1,0,
                0,0,1,1,0,0,
                0,0,1,0,-1,0,
                0,0,0,1,0,1,
                0,0,0,0,1,1,
        };
        genericTest(expected, TestHelper.merge(toOpen, F7_NAME));
    }

    @ParameterizedTest
    @MethodSource("openCloseF8")
    public void sc7_f6Open(String[] toOpen) {
        double[] expected = new double[] {
                1,1,0,0,0,0,
                0,1,1,0,1,0,
                0,0,1,1,0,0,
                0,0,0,0,1,0,
                0,0,0,1,0,1,
                0,0,0,0,0,1,
        };
        genericTest(expected, TestHelper.merge(toOpen, F6_NAME));
    }

    @ParameterizedTest
    @MethodSource("openCloseF8")
    public void sc8_f6f7Open(String[] toOpen) {
        double[] expected = new double[] {
                1,1,0,0,
                0,1,1,1,
                0,0,1,0,
                0,0,0,1,
        };
        genericTest(expected, TestHelper.merge(toOpen, F7_NAME, F6_NAME));
    }

    @ParameterizedTest
    @MethodSource("openCloseF8")
    public void sc9_f5Open(String[] toOpen){
        double[] expected = new double[] {
                1,1,0,0,0,0,
                0,1,1,0,0,0,
                0,0,1,1,0,0,
                0,0,0,1,1,1,
                0,0,0,0,1,0,
                0,0,0,0,0,1,
        };
        genericTest(expected, TestHelper.merge(toOpen, F5_NAME));
    }

    @ParameterizedTest
    @MethodSource("openCloseF8")
    public void sc10_f5f7Open(String[] toOpen){
        double[] expected = new double[] {
                1,1,0,0,0,
                0,1,1,0,0,
                0,0,1,1,0,
                0,0,0,1,1,
                0,0,0,0,1,
        };
        genericTest(expected, TestHelper.merge(toOpen, F5_NAME, F7_NAME));
    }

    @ParameterizedTest
    @MethodSource("openCloseF8")
    public void sc11_f5f6Open(String[] toOpen) {
        var expected = new double[] {
                1,1,0,0,0,
                0,1,1,0,0,
                0,0,1,1,0,
                0,0,0,1,1,
                0,0,0,0,1,
        };
        genericTest(expected, TestHelper.merge(toOpen, F5_NAME, F6_NAME));
    }

    @ParameterizedTest
    @MethodSource("openCloseF4F8")
    public void sc12_f5f6f7Open(String[] toOpen) {
        var expected = new double[] {
                1,1,0,
                0,1,1,
                0,0,1,
        };
        genericTest(expected, TestHelper.merge(toOpen, F5_NAME, F6_NAME, F7_NAME));
    }

    @ParameterizedTest
    @MethodSource("openCloseF8")
    public void sc13_f4Open(String[] toOpen) {
        var expected = new double[] {
                1,1,0,0,0,0,
                0,1,1,1,0,0,
                0,0,1,0,0,0,
                0,0,0,1,1,0,
                0,0,0,0,1,1,
                0,0,0,0,0,1,
        };
        genericTest(expected, TestHelper.merge(toOpen, F4_NAME));
    }

    @ParameterizedTest
    @MethodSource("openCloseF6F7F8")
    public void sc14_f4f5Open(String[] toOpen) {
        var expected = new double[] {
                1,1,0,
                0,1,1,
                0,0,1,
        };
        genericTest(expected, TestHelper.merge(toOpen, F4_NAME, F5_NAME));
    }

    @ParameterizedTest
    @MethodSource("openCloseF4F7F8")
    public void sc15_f3f6Open(String[] toOpen) {
        var expected = new double[] {
                1,1,0,
                0,1,1,
                0,0,1,
        };
        genericTest(expected, TestHelper.merge(toOpen, F3_NAME, F6_NAME));
    }

    @ParameterizedTest
    @MethodSource("openCloseF8")
    public void sc16_f3f7Open(String[] toOpen) {
        var expected = new double[] {
                1,1,0,0,0,
                0,1,1,0,0,
                0,0,1,1,0,
                0,0,0,1,1,
                0,0,0,0,1,
        };
        genericTest(expected, TestHelper.merge(toOpen, F3_NAME, F7_NAME));
    }

    @ParameterizedTest
    @MethodSource("openCloseF8")
    public void sc17_f3Open(String[] toOpen) {
        var expected = new double[] {
                1,1,0,0,0,0,
                0,1,1,0,0,0,
                0,0,1,1,0,0,
                0,0,0,1,1,1,
                0,0,0,0,1,0,
                0,0,0,0,0,1,
        };
        genericTest(expected, TestHelper.merge(toOpen, F3_NAME));
    }

    @ParameterizedTest
    @MethodSource("openCloseF8")
    public void sc18_f3f4Open(String[] toOpen) {
        var expected = new double[] {
                1,1,0,0,0,
                0,1,1,0,0,
                0,0,1,1,0,
                0,0,0,1,1,
                0,0,0,0,1,
        };
        genericTest(expected, TestHelper.merge(toOpen, F3_NAME, F4_NAME));
    }

    @ParameterizedTest
    @MethodSource("openCloseF8")
    public void sc19_f3f4f7Open(String[] toOpen) {
        var expected = new double[] {
                1,1,0,
                0,1,1,
                0,0,1,
        };
        genericTest(expected, TestHelper.merge(toOpen, F3_NAME, F4_NAME, F7_NAME));
    }

}
