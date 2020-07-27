package duc.sg.java.loadapproximator.uncertain.bsrules.old;

import duc.sg.java.extracter.FuseExtracter;
import duc.sg.java.matrix.certain.CertainFuseStateMatrix;
import duc.sg.java.matrix.certain.CertainMatrixBuilder;
import duc.sg.java.matrix.uncertain.UncertainFuseStatesMatrix;
import duc.sg.java.model.Fuse;
import duc.sg.java.model.State;
import duc.sg.java.model.Substation;
import duc.sg.java.uncertainty.PossibilityDouble;
import duc.sg.java.utils.BaseTransform;
import duc.sg.java.validator.umatrix.GridValidator;
import duc.sg.java.validator.umatrix.IValidator;
import org.ejml.alg.dense.linsol.svd.SolvePseudoInverseSvd;
import org.ejml.data.DenseMatrix64F;

import java.util.*;

public class UncertainLoadApproximator {
    private UncertainLoadApproximator() {}

    private static List<Fuse> getUncertainFuses(Substation substation) {
        var uFuses = new ArrayList<Fuse>();
        var waiting = new Stack<Fuse>();

        var visited = new HashSet<Fuse>();
        var added = new HashSet<Fuse>();

        waiting.add(substation.getFuses().get(0));

        while (!waiting.isEmpty()) {
            var current = waiting.pop();
            if(!visited.contains(current) && !current.getOwner().isDeadEnd() && current.getStatus().isUncertain()) {
                uFuses.add(current);
            }
            visited.add(current);

            var ownerOpp = current.getOpposite().getOwner();
            for(var f: ownerOpp.getFuses()) {
                if(!visited.contains(f) && !added.contains(f)) {
                    waiting.add(f);
                    added.add(f);
                }
            }
        }

        return uFuses;
    }



    private static Map<Fuse, State> boolarr2MapFuse(boolean[] fuseStates, List<Fuse> uFuses, Substation substation) {
        Collection<Fuse> allFuses = FuseExtracter.INSTANCE.getExtracted(substation);
        var res = new HashMap<Fuse, State>(allFuses.size());

        for(Fuse f: allFuses) {
            State state;
            if(uFuses.contains(f)) {
                int idx = uFuses.indexOf(f);
                state = fuseStates[idx]? State.CLOSED : State.OPEN;
            } else {
                state = f.getStatus().isClosed()? State.CLOSED : State.OPEN;
            }
            res.put(f, state);
        }


        return res;

    }

    public static UncertainFuseStatesMatrix[] build(Substation substation) {
        List<Fuse> uFuses = getUncertainFuses(substation);
        int nbPossibilities = (int) Math.pow(2, uFuses.size());

        var res = new ArrayList<UncertainFuseStatesMatrix>(nbPossibilities);
        IValidator validator = new GridValidator();

        int idxMaxClosedFuses = -1;
        int maxClosedFuses = -1;
        double confToAdd = 0;

        for (int idxCase = 0; idxCase < nbPossibilities; idxCase++) {
            boolean[] fuseStates = BaseTransform.toBinary(idxCase, uFuses.size());
            Map<Fuse, State> fuseStateMap = boolarr2MapFuse(fuseStates, uFuses, substation);

            double confidence = 1;
            int nbFusesClosed = 0;

            for (Fuse uf : uFuses) {
                if(fuseStateMap.get(uf) == State.CLOSED) {
                    uf.closeFuse();
                    confidence *= uf.getStatus().confIsClosed();
                    nbFusesClosed++;
                } else {
                    uf.openFuse();
                    confidence *= uf.getStatus().confIsOpen();
                }
            }

            if(validator.isValid(substation, fuseStateMap)) {
                CertainFuseStateMatrix matrix = (CertainFuseStateMatrix) CertainMatrixBuilder.INSTANCE.build(substation)[0];
                res.add(new UncertainFuseStatesMatrix(matrix, confidence));
                if (nbFusesClosed > maxClosedFuses) {
                    maxClosedFuses = nbFusesClosed;
                    idxMaxClosedFuses = res.size() - 1;
                }
            } else {
                confToAdd += confidence;
            }

        }

        if(maxClosedFuses != -1) {
            UncertainFuseStatesMatrix ufsm = res.get(idxMaxClosedFuses);
            ufsm.setConfidence(ufsm.getConfidence() + confToAdd);
        }

        return res.toArray(new UncertainFuseStatesMatrix[0]);
    }






    public static void approximate(final Substation substation) {
        UncertainFuseStatesMatrix[] matrices = build(substation);
        var visited = new HashSet<Fuse>();

        for (UncertainFuseStatesMatrix usfm : matrices) {
            var fuseStates = new DenseMatrix64F(usfm.getNbColumns(), usfm.getNbColumns(), true, usfm.getData());

            final var matConsumptions = new DenseMatrix64F(usfm.getNbColumns(), 1);
            var cblesOrder = usfm.getCables();
            for (int i = 0; i < cblesOrder.length; i++) {
                matConsumptions.set(i, 0, cblesOrder[i].getConsumption());
            }


            DenseMatrix64F solution = new DenseMatrix64F(matConsumptions.numRows, matConsumptions.numCols);
            SolvePseudoInverseSvd solver = new SolvePseudoInverseSvd();
            solver.setA(fuseStates);

            solver.solve(matConsumptions, solution);

            var solData = solution.data;
            var fuses = new HashSet<Fuse>(FuseExtracter.INSTANCE.getExtracted(substation));
            for (int i = 0; i < solData.length; i++) {
                Fuse current = usfm.getFuse(i);
                if (!visited.contains(current)) {
                    current.resetULoad();
                    visited.add(current);
                }

                var newPoss = new PossibilityDouble(solData[i], usfm.getConfidence());
                current.getUncertainLoad().addPossibility(newPoss);
                fuses.remove(current);
            }

            for(var f: fuses) {
                if (!visited.contains(f)) {
                    f.resetULoad();
                    visited.add(f);
                }
                var newPoss = new PossibilityDouble(0, usfm.getConfidence());
                f.getUncertainLoad().addPossibility(newPoss);
            }
        }

    }

}
