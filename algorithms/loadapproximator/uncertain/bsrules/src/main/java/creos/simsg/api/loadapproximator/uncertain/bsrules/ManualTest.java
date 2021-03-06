package creos.simsg.api.loadapproximator.uncertain.bsrules;

import creos.simsg.api.scenarios.Scenario;
import creos.simsg.api.scenarios.ScenarioBuilder;
import creos.simsg.api.scenarios.ScenarioName;
import creos.simsg.api.scenarios.SingleCableSC;
import creos.simsg.api.model.Fuse;
import creos.simsg.api.model.Meter;
import creos.simsg.api.scenarios.*;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Manual comparison of the first ({@link ULoadApproximator_V1}) and second ({@link ULoadApproximator_V2}) version of
 * the algorithm.
 */
public class ManualTest {

    public static Fuse get(Fuse[] array, String toSearch) {
        for(Fuse f: array) {
            if(f.getName().equals(toSearch)) {
                return f;
            }
        }

        return null;
    }

    public static void main(String[] args) {
        Scenario sc = new ScenarioBuilder()
                .chooseScenario(ScenarioName.SINGLE_CABLE)
//                .chooseScenario(ScenarioName.PARA_TRANSFORMER)
//                .chooseScenario(ScenarioName.PARA_CABINET)
//                .chooseScenario(ScenarioName.INDIRECT_PARALLEL)
//                .chooseScenario(ScenarioName.PARA_W_DEADEND)
                .build();

        Fuse[] fuses = sc.extractFuses();

        // Single cable
        get(fuses, SingleCableSC.F1_NAME).setConfIsClosed(0.8);
        var meter1 = new Meter("m1");
        meter1.setConsumption(20);
        get(fuses, SingleCableSC.F1_NAME).getCable().addMeters(meter1);

        // Para Deadend
//        get(fuses, ParaWithDeadendSC.F1_NAME).openFuse();
//        get(fuses, ParaWithDeadendSC.F1_NAME).getStatus().makeCertain();
//        get(fuses, ParaWithDeadendSC.F3_NAME).getStatus().setConfIsClosed(0.8);

        // Para transformer
//        get(fuses, ParaTransformerSC.F1_NAME).setConfIsClosed(0.6);
//        get(fuses, ParaTransformerSC.F2_NAME).setConfIsClosed(0.6);
//        var meter1 = new Meter("m1");
//        var meter2 = new Meter("m2");
//        var meter3 = new Meter("m3");
//        meter1.setConsumption(20);
//        meter2.setConsumption(30);
//        meter3.setConsumption(40);
//        get(fuses, ParaTransformerSC.F1_NAME).getCable().addMeters(meter1);
//        get(fuses, ParaTransformerSC.F3_NAME).getCable().addMeters(meter2);
//        get(fuses, ParaTransformerSC.F5_NAME).getCable().addMeters(meter3);

//        get(fuses, ParaTransformerSC.F1_NAME).getStatus().setConfIsClosed(0.69);
//        get(fuses, ParaTransformerSC.F2_NAME).getStatus().setConfIsClosed(0.52);

//        get(fuses, ParaTransformerSC.F1_NAME).openFuse();
//        get(fuses, ParaTransformerSC.F1_NAME).getStatus().makeCertain();
//        get(fuses, ParaTransformerSC.F2_NAME).openFuse();
//        get(fuses, ParaTransformerSC.F2_NAME).getStatus().makeCertain();



//        UncertainFuseStatesMatrix[] test = UncertainLoadApproximator.build(sc.getSubstation());
//        for (var a: test) {
//            System.out.println(Arrays.toString(a.getData()) + " -> " + a.getConfidence());
//        }
//
//        System.out.println();
//        System.out.println();
//        System.out.println();
//
//        UncertainFuseStatesMatrix[] test2 = UncertainMatrixBuilder.build(sc.getSubstation());
//        for (var a: test2) {
//            System.out.println(Arrays.toString(a.getData()) + " -> " + a.getConfidence());
//        }


        ULoadApproximator_V1.approximate(sc.getSubstation());
        Stream.of(fuses)
                .map(new Function<Fuse, String>() {
                    @Override
                    public String apply(Fuse fuse) {
                        return fuse.getName() + " -> " + fuse.getUncertainLoad();
                    }
                })
                .forEach(System.out::println);

        System.out.println();

        ULoadApproximator_V2.approximate(sc.getSubstation());
        Stream.of(fuses)
                .map(new Function<Fuse, String>() {
                    @Override
                    public String apply(Fuse fuse) {
                        return fuse.getName() + " -> " + fuse.getUncertainLoad();
                    }
                })
                .forEach(System.out::println);

        System.out.println();

    }

}
