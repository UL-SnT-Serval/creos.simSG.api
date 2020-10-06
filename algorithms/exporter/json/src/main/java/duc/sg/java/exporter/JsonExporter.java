package duc.sg.java.exporter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import duc.sg.java.extracter.CableExtracter;
import duc.sg.java.extracter.EntityExtracter;
import duc.sg.java.extracter.FuseExtracter;
import duc.sg.java.loadapproximator.LoadApproximator;
import duc.sg.java.model.*;

import java.util.HashMap;

public class JsonExporter<L> implements Exporter<JSONObject>{
    @Override
    public JSONObject export(SmartGrid grid) {
        return exportWithLoads(grid, null);
    }

    @Override
    public JSONObject exportWithLoads(SmartGrid grid, LoadApproximator<Double> approximator) {
        final var res = new JSONObject();

        final var fuses = new JSONArray();
        final var mapFuses = new HashMap<Fuse, Integer>();
        var index = new int[]{0};
        grid.getSubstations().forEach((Substation substation) -> {
            final var fuseLoads = new HashMap<Fuse, Double>();
            if(approximator != null) {
                fuseLoads.putAll(approximator.getFuseLoads(substation, true));
            }

            FuseExtracter.INSTANCE
                    .getExtracted(substation)
                    .forEach((Fuse fuse) -> {
                        mapFuses.put(fuse, index[0]);

                        final var fuseMap = new HashMap<String, Object>();
                        fuseMap.put("id", index[0] + "");
                        fuseMap.put("name", fuse.getName());

                        final var stateMap = new HashMap<String, Object>();
                        stateMap.put("status", fuse.getStatus().getState().getName());
                        stateMap.put("confidence", fuse.getStatus().confIsClosed());
                        fuseMap.put("state", new JSONObject(stateMap));

                        if(fuseLoads.size() != 0) {
                            final var loads = new JSONArray();

                            final var loadMap = new HashMap<String, Object>();
                            loadMap.put("value", fuseLoads.get(fuse));
                            loadMap.put("confidence", 1);
                            loads.add(new JSONObject(loadMap));

                            fuseMap.put("load", loads);
                        }

                        fuses.add(new JSONObject(fuseMap));

                        index[0]++;
                    });
        });
        res.put("fuses", fuses);

        final var cables = new JSONArray();
        index[0] = 0;
        grid.getSubstations().forEach((Substation substation) -> {
            final var cableLoads = new HashMap<Cable, Double>();
            if(approximator != null) {
                cableLoads.putAll(approximator.getCableLoads(substation));
            }

            CableExtracter.INSTANCE
                    .getExtracted(substation)
                    .forEach((Cable cable) -> {
                        final var cableMap = new HashMap<String, Object>();
                        cableMap.put("id", index[0] + "");

                        final var cableFuses = new JSONArray(2);
                        cableFuses.add(mapFuses.get(cable.getFirstFuse()) + "");
                        cableFuses.add(mapFuses.get(cable.getSecondFuse()) + "");
                        cableMap.put("fuses", cableFuses);

                        final var meters = new JSONArray();
                        cable.getMeters().forEach((Meter meter) -> {
                            final var meterMap = new HashMap<String, Object>();
                            meterMap.put("name", meter.getName());
                            meterMap.put("consumption", meter.getConsumption());
                            meters.add(new JSONObject(meterMap));
                        });
                        cableMap.put("meters", meters);


                        cables.add(new JSONObject(cableMap));

                        index[0]++;
                    });
        });
        res.put("cables", cables);



        final var entities = new JSONArray();
        grid.getSubstations().forEach((Substation substation) -> {
            EntityExtracter.INSTANCE
                    .getExtracted(substation)
                    .forEach((Entity entity) -> {
                        final var entityJson = new HashMap<String, Object>();
                        entityJson.put("name", entity.getName());
                        entityJson.put("type", entity.getClass().getName());

                        final var fusesJson = new JSONArray();
                        entity.getFuses().forEach((Fuse fuse) -> {
                            fusesJson.add(mapFuses.get(fuse) + "");
                        });
                        entityJson.put("fuses", fusesJson);

                        entities.add(new JSONObject(entityJson));
                    });
        });
        res.put("entities", entities);

        return res;
    }

}
