package duc.sg.java.preprocessor.powerflow;

import duc.sg.java.circlefinder.Circle;
import duc.sg.java.circlefinder.CircleFinder;
import duc.sg.java.extractor.FuseExtractor;
import duc.sg.java.model.Fuse;
import duc.sg.java.model.Substation;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class PowerFlow2 implements IPowerFlow {
    @Override
    public Fuse[] getFuseOnMandatoryPF(Substation substation) {
        List<Circle> circles = CircleFinder.getDefault().getCircles(substation);
        List<Fuse> allFuses = FuseExtractor.INSTANCE.getExtracted(substation);

        var fuseOnCircles = new HashSet<Fuse>();
        circles.forEach((Circle c) -> Collections.addAll(fuseOnCircles, c.getFuses()));

        return allFuses.stream()
                .filter((Fuse f) -> !fuseOnCircles.contains(f) && !f.getOwner().isAlwaysDeadEnd())
                .toArray(Fuse[]::new);

    }
}
