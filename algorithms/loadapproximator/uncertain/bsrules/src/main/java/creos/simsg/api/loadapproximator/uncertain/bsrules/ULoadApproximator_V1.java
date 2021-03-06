package creos.simsg.api.loadapproximator.uncertain.bsrules;

import creos.simsg.api.extractor.FuseExtractor;
import creos.simsg.api.loadapproximator.LoadApproximator;
import creos.simsg.api.matrix.certain.EquationMatrixImp;
import creos.simsg.api.matrix.certain.CertainMatrixBuilder;
import creos.simsg.api.matrix.uncertain.UEquationMatrix;
import creos.simsg.api.model.Fuse;
import creos.simsg.api.model.State;
import creos.simsg.api.model.Substation;
import creos.simsg.api.uncertainty.PossibilityDouble;
import creos.simgsg.api.utils.BaseTransform;
import creos.simsg.api.validator.GridValidator;
import creos.simsg.api.validator.IValidator;
import org.ejml.alg.dense.linsol.svd.SolvePseudoInverseSvd;
import org.ejml.data.DenseMatrix64F;

import java.util.*;

/**
 * First version of the uncertain load approximation with business rules.
 *
 * In this version, the all set of rules is applied to filter out invalid configurations. But, the confidence of the
 * removed configuration is badly re-balanced to the others. This algorithm works only in topologies that contain
 * only one circle.
 *
 *
 * TODO: this class should implement {@link LoadApproximator}. A new class should be
 *  defined to type uncertain load
 *
 * TODO 2: Extract the matrix creation into another maven module (to put in the
 *  <em>algorithms/matrixbuilder</em> folder).
 */
public class ULoadApproximator_V1 {
    private ULoadApproximator_V1() {}

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
        Collection<Fuse> allFuses = FuseExtractor.INSTANCE.getExtracted(substation);
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

    public static UEquationMatrix[] build(Substation substation) {
        List<Fuse> uFuses = getUncertainFuses(substation);
        int nbPossibilities = (int) Math.pow(2, uFuses.size());

        var res = new ArrayList<UEquationMatrix>(nbPossibilities);
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
                EquationMatrixImp matrix = (EquationMatrixImp) new CertainMatrixBuilder().build(substation)[0];
                res.add(new UEquationMatrix(matrix, confidence));
                if (nbFusesClosed > maxClosedFuses) {
                    maxClosedFuses = nbFusesClosed;
                    idxMaxClosedFuses = res.size() - 1;
                }
            } else {
                confToAdd += confidence;
            }

        }

        if(maxClosedFuses != -1) {
            UEquationMatrix ufsm = res.get(idxMaxClosedFuses);
            ufsm.setConfidence(ufsm.getConfidence() + confToAdd);
        }

        return res.toArray(new UEquationMatrix[0]);
    }






    public static void approximate(final Substation substation) {
        UEquationMatrix[] matrices = build(substation);
        var visited = new HashSet<Fuse>();

        for (UEquationMatrix usfm : matrices) {
            var fuseStates = new DenseMatrix64F(usfm.getNbColumns(), usfm.getNbColumns(), true, usfm.getValues());

            final var matConsumptions = new DenseMatrix64F(usfm.getNbColumns(), 1, true, usfm.getEqResults());

            DenseMatrix64F solution = new DenseMatrix64F(matConsumptions.numRows, matConsumptions.numCols);
            SolvePseudoInverseSvd solver = new SolvePseudoInverseSvd();
            solver.setA(fuseStates);

            solver.solve(matConsumptions, solution);

            var solData = solution.data;
            var fuses = new HashSet<Fuse>(FuseExtractor.INSTANCE.getExtracted(substation));
            for (int i = 0; i < solData.length; i++) {
                Fuse current = usfm.getColumn(i);
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
