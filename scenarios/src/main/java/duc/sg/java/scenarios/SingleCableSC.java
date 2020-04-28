package duc.sg.java.scenarios;

import duc.sg.java.model.*;

/*
    subs-[f1]----(cbl1)----[f2]-cab
 */
public final class SingleCableSC extends Scenario {
    public static final String F1_NAME = "i1";
    public static final String F2_NAME = "i2";

    public static final String SUBSTATION_NAME = "Substation";
    public static final String CABINET_NAME = "Cabinet";

    SingleCableSC(SmartGrid grid) {
        super(grid);
    }

    @Override
    public String substationName() {
        return SUBSTATION_NAME;
    }

    @Override
    public Fuse[] extractFuses() {
        Substation substation = grid.getSubstation(SUBSTATION_NAME).get();

        var res = new Fuse[2];
        res[0] = substation.getFuses().get(0);
        res[1] = res[0].getOpposite();
        return res;
    }

    @Override
    public Cable[] extractCables() {
        return new Cable[]{extractFuses()[0].getCable()};
    }
}
