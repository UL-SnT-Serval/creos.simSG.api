package creos.simsg.api.server.ws.message;

import creos.simsg.api.extractor.CableExtractor;
import creos.simsg.api.extractor.FuseExtractor;
import creos.simsg.api.loadapproximator.uncertain.bsrules.ULoadApproximator_V2;
import creos.simsg.api.loadapproximator.uncertain.naive.UncertainLoadApproximator;
import creos.simsg.api.model.Cable;
import creos.simsg.api.model.Fuse;
import creos.simsg.api.model.SmartGrid;
import creos.simsg.api.model.Substation;
import creos.simsg.api.uncertainty.Category;
import creos.simgsg.api.utils.Pair;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * This message is sent by client to request an uncertain load approximation
 */
public class ULoadApproxMsg extends Message implements RequestMessage {
    final SmartGrid grid;
    final ULoadType type;

    ULoadApproxMsg(SmartGrid grid, ULoadType type) {
        super(MessageType.ULOAD_APPROX);
        this.type = type;
        this.grid = grid;
    }

    @Override
    public Message process() {
        final var fuseLoads = new ArrayList<ULoadApproximationAnswer.ElmtULoad>();
        final var cableLoads = new ArrayList<ULoadApproximationAnswer.ElmtULoad>();

        switch (type) {
            case NAIVE ->
                grid.getSubstations().forEach(
                        UncertainLoadApproximator::approximate
                );
            case BS_RULE ->
                grid.getSubstations().forEach(
                        ULoadApproximator_V2::approximate
                );
        }

        final var consumer = new ULoadConsumer(fuseLoads, cableLoads);
        grid.getSubstations().forEach(consumer);

        return new ULoadApproximationAnswer(
                fuseLoads.toArray(new ULoadApproximationAnswer.ElmtULoad[0]),
                cableLoads.toArray(new ULoadApproximationAnswer.ElmtULoad[0])
        );
    }

    private static class ULoadConsumer implements Consumer<Substation> {
        final ArrayList<ULoadApproximationAnswer.ElmtULoad> fuseLoads;
        final ArrayList<ULoadApproximationAnswer.ElmtULoad> cableLoads;

        private ULoadConsumer(ArrayList<ULoadApproximationAnswer.ElmtULoad> fuseLoads, ArrayList<ULoadApproximationAnswer.ElmtULoad> cableLoads) {
            this.fuseLoads = fuseLoads;
            this.cableLoads = cableLoads;
        }

        @Override
        public void accept(Substation substation) {
            FuseExtractor.INSTANCE
                    .getExtracted(substation)
                    .forEach((Fuse fuse) -> {
                        var jsonULoads = new ArrayList<ULoadApproximationAnswer.ULoad>();
                        fuse.getUncertainLoad()
                                .formatWithCategory()
                                .forEach((Pair<Double, Category> uLoad) ->
                                    jsonULoads.add(new ULoadApproximationAnswer.ULoad(uLoad.getFirst(), uLoad.getSecond()))
                                );
//                                .forEach((PossibilityDouble poss) -> {
//                                    jsonULoads.add(new ULoadApproximationAnswer.ULoad(poss.getValue(), Category.probToCategory(poss.getConfidence().getProbability())));
//                                });

                        fuseLoads.add(new ULoadApproximationAnswer.ElmtULoad(
                                fuse.getId(),
                                jsonULoads.toArray(new ULoadApproximationAnswer.ULoad[0])
                        ));
                    });

            CableExtractor.INSTANCE
                    .getExtracted(substation)
                    .forEach((Cable cable) -> {
                        var cableULoads = new ArrayList<ULoadApproximationAnswer.ULoad>();
                        cable.getUncertainLoad()
                                .formatWithCategory()
                                .forEach((Pair<Double, Category> uLoad) ->
                                    cableULoads.add(new ULoadApproximationAnswer.ULoad(uLoad.getFirst(), uLoad.getSecond()))
                                );

                        cableLoads.add(new ULoadApproximationAnswer.ElmtULoad(
                                cable.getId(),
                                cableULoads.toArray(new ULoadApproximationAnswer.ULoad[0])
                        ));
                    });
        }
    }


    public enum ULoadType {
        NAIVE, BS_RULE
    }
}
