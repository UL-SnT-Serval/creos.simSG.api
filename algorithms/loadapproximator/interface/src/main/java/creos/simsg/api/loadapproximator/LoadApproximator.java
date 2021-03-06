package creos.simsg.api.loadapproximator;

import creos.simsg.api.extractor.EffectiveConfigurationExtractor;
import creos.simsg.api.model.Cable;
import creos.simsg.api.model.Configuration;
import creos.simsg.api.model.Fuse;
import creos.simsg.api.model.Substation;

import java.util.Map;

/**
 * Interface to define algorithms for the load approximation.
 *
 * @param <T> Type of the result of the load approximation.
 */
public interface LoadApproximator<T> {
    Map<Fuse, T> approximate(Substation substation, Configuration configuration);
    void approximateAndSave(Substation substation, Configuration configuration);
    default void approximateAndSave(Substation substation) {
        Configuration configuration = EffectiveConfigurationExtractor.INSTANCE
                .getExtracted(substation)
                .get(0);
        approximateAndSave(substation, configuration);
    }

    Map<Fuse, T> getFuseLoads(Substation substation, Configuration configuration, boolean forceRecompute);
    default Map<Fuse, T> getFuseLoads(Substation substation) {
        return getFuseLoads(substation, false);
    }
    default Map<Fuse, T> getFuseLoads(Substation substation, boolean forRecompute) {
        Configuration configuration = EffectiveConfigurationExtractor.INSTANCE
                .getExtracted(substation)
                .get(0);
        return getFuseLoads(substation, configuration, forRecompute);
    }
    default Map<Fuse, T> getFuseLoads(Substation substation, Configuration configuration) {
        return getFuseLoads(substation, configuration, false);
    }

    Map<Cable, T> getCableLoads(Substation substation, Configuration configuration, boolean forceRecompute);
    default Map<Cable, T> getCableLoads(Substation substation, Configuration configuration) {
        return getCableLoads(substation, configuration, false);
    }
    default Map<Cable, T> getCableLoads(Substation substation) {
        return getCableLoads(substation, false);
    }
    default Map<Cable, T> getCableLoads(Substation substation, boolean forceRecompute) {
        Configuration configuration = EffectiveConfigurationExtractor.INSTANCE
                .getExtracted(substation)
                .get(0);
        return getCableLoads(substation, configuration, forceRecompute);
    }
}
