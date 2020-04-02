package duc.sg.java.loadapproximator.loadapproximation;

import duc.sg.java.loadapproximator.loadapproximation.matrix.UncertainFuseStatesMatrix;
import duc.sg.java.loadapproximator.loadapproximation.matrix.UncertainMatrixBuilder;
import duc.sg.java.model.Fuse;
import duc.sg.java.model.Substation;
import duc.sg.java.uncertainty.PossibilityDouble;
import org.ejml.alg.dense.linsol.svd.SolvePseudoInverseSvd;
import org.ejml.data.DenseMatrix64F;

public class UncertainLoadApproximator {
    private UncertainLoadApproximator() {}

    public static void approximate(final Substation substation) {
//        var fuseLoads = new HashMap<Fuse, Map<Double, Double>>();
//        var fuseLoads = new HashMap<Fuse, MultDblePossibilities>();
        UncertainFuseStatesMatrix[] matrices = UncertainMatrixBuilder.build(substation);

        for(var usfm: matrices) {
            var fuseStates = new DenseMatrix64F(usfm.getNbColumns(), usfm.getNbColumns(), true, usfm.getData());

            final var matConsumptions = new DenseMatrix64F(usfm.getNbColumns(), 1);
            var cblesOrder = usfm.getCables();
            for (int i = 0; i < cblesOrder.length; i++) {
                matConsumptions.set(i,0, cblesOrder[i].getConsumption());
            }


            DenseMatrix64F solution = new DenseMatrix64F(matConsumptions.numRows, matConsumptions.numCols);
            SolvePseudoInverseSvd solver = new SolvePseudoInverseSvd();
            solver.setA(fuseStates);

            solver.solve(matConsumptions, solution);

            var solData = solution.data;
            for (int i = 0; i < solData.length; i++) {

//                fuseLoads.computeIfAbsent(usfm.getFuse(i), (fuseKey) -> {
////                    var val = new HashMap<Double, Double>();
////                    val.put(0., 1.);
////                    return val;
//                    var val = new MultDblePossibilities();
//                    val.add(new PossibilityDouble(0., Confidence.MAX_PROBABILITY));
//                    return val;
//                });


//                Map<Double, Double> loadConfPair = fuseLoads.get(usfm.getFuse(i));
//                loadConfPair.compute(solData[i], (loadKey, confVal) -> {
//                   if(confVal == null) {
//                       return usfm.getConfidence();
//                   } else {
//                       return confVal + usfm.getConfidence();
//                   }
//                });
//                loadConfPair.compute(0., new BiFunction<Double, Double, Double>() {
//                    @Override
//                    public Double apply(Double key, Double currentVal) {
//                        return currentVal - usfm.getConfidence();
//                    }
//                });
//                MultDblePossibilities loadConfPair = fuseLoads.get(usfm.getFuse(i));
//                loadConfPair.compute(new PossibilityDouble());
//                loadConfPair.add(new PossibilityDouble(solData[i], usfm.getConfidence()));

                Fuse current = usfm.getFuse(i);
                current.getUncertainLoad().add(new PossibilityDouble(solData[i], usfm.getConfidence()));

            }
        }


//        for(Map.Entry<Fuse, Map<Double, Double>> fuseLoad: fuseLoads.entrySet()) {
//            Fuse fuse = fuseLoad.getKey();
//            Map<Double, Double> loadConfPair = fuseLoad.getValue();
//            loadConfPair.remove(0., 0.);
//            fuse.setLoad(loadConfPair);
//        }
    }
}