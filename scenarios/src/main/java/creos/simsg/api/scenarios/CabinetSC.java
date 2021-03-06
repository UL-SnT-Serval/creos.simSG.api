package creos.simsg.api.scenarios;

import creos.simsg.api.model.Cable;
import creos.simsg.api.model.Fuse;
import creos.simsg.api.model.SmartGrid;

/*
                                  |-[f3]----(cbl2)----[f5]-cab2
subs-[f1]----(cbl1)----[f2]-cab1-<
                                  |-[f4]----(cbl3)----[f6]-cab3
*/
public final class CabinetSC extends Scenario {
    public static final String F1_NAME = "i1";
    public static final String F2_NAME = "i2";
    public static final String F3_NAME = "i3";
    public static final String F4_NAME = "i4";
    public static final String F5_NAME = "i5";
    public static final String F6_NAME = "i6";


    public static final String SUBSTATION_NAME = "Substation";
    public static final String CABINET1_NAME = "Cabinet 1";
    public static final String CABINET2_NAME = "Cabinet 2";
    public static final String CABINET3_NAME = "Cabinet 3";

    CabinetSC(SmartGrid grid) {
        super(grid);
    }

    @Override
    public String substationName() {
        return SUBSTATION_NAME;
    }

    @Override
    public Fuse[] extractFuses() {
        var substation = grid.getSubstation(SUBSTATION_NAME).get();

        var res = new Fuse[6];
        res[0] = substation.getFuses().get(0);
        res[1] = res[0].getOpposite();

        var cabinet = res[1].getOwner();
        res[2] = cabinet.getFuses().get(1);
        res[3] = cabinet.getFuses().get(2);

        res[4] = res[2].getOpposite();
        res[5] = res[3].getOpposite();

        return res;
    }

    @Override
    public Cable[] extractCables() {
        var cables = new Cable[3];

        var fuses = extractFuses();
        cables[0] = fuses[0].getCable();
        cables[1] = fuses[2].getCable();
        cables[2] = fuses[3].getCable();

        return cables;
    }
}
