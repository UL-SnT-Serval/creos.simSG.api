package duc.sg.java.validator.umatrix;

import duc.sg.java.model.Fuse;
import duc.sg.java.model.State;
import duc.sg.java.scenarios.*;

import java.util.HashMap;
import java.util.Map;

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
//                .chooseScenario(ScenarioName.SINGLE_CABLE)
//                .chooseScenario(ScenarioName.CABINET)
//                .chooseScenario(ScenarioName.PARA_TRANSFORMER)
//                .chooseScenario(ScenarioName.PARA_CABINET)
//                .chooseScenario(ScenarioName.INDIRECT_PARALLEL)
                .chooseScenario(ScenarioName.PARA_W_DEADEND)
                .build();
//
//        var possMatrix = PossibilityMatrixBuilder1.build(sc.getSubstation());
//        System.out.println(possMatrix);


        Map<Fuse, State> fuseStateMap = new HashMap<>();
        Fuse[] fuses = sc.extractFuses();

        // Single cable
//        fuseStateMap.put(get(fuses, SingleCableSC.F1_NAME), State.OPEN);
//        fuseStateMap.put(get(fuses, SingleCableSC.F2_NAME), State.OPEN);

        // Cabinet
//        fuseStateMap.put(get(fuses, CabinetSC.F1_NAME), State.CLOSED);
//        fuseStateMap.put(get(fuses, CabinetSC.F2_NAME), State.CLOSED);
//        fuseStateMap.put(get(fuses, CabinetSC.F3_NAME), State.CLOSED);
//        fuseStateMap.put(get(fuses, CabinetSC.F4_NAME), State.OPEN);
//        fuseStateMap.put(get(fuses, CabinetSC.F5_NAME), State.CLOSED);
//        fuseStateMap.put(get(fuses, CabinetSC.F6_NAME), State.CLOSED);

        // Para W Dead
//        fuseStateMap.put(get(fuses, ParaWithDeadendSC.F1_NAME), State.CLOSED);
//        fuseStateMap.put(get(fuses, ParaWithDeadendSC.F2_NAME), State.CLOSED);
//        fuseStateMap.put(get(fuses, ParaWithDeadendSC.F3_NAME), State.CLOSED);
//        fuseStateMap.put(get(fuses, ParaWithDeadendSC.F4_NAME), State.OPEN);



        IValidator validator = new Validator();
        System.out.println(validator.isValid(sc.getSubstation(), fuseStateMap));


    }
}