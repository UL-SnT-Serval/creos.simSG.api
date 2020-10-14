package duc.sg.java.circlefinder;

import duc.sg.java.extracter.FuseExtracter;
import duc.sg.java.model.Entity;
import duc.sg.java.model.Fuse;
import duc.sg.java.model.Substation;
import duc.sg.java.utils.OArrays;

import java.util.*;

class CircleFinderImpl implements CircleFinder {
    static final CircleFinder INSTANCE = new CircleFinderImpl();

    private CircleFinderImpl() {}

    @Override
    public void findAndSave(Substation substation) {
        List<Circle> circles = new ArrayList<>();
        var fuseInCircle = new HashSet<Fuse>();

        List<Fuse> fuses = FuseExtracter.INSTANCE.getExtracted(substation);

        int i=0;
        while(i< fuses.size()) {
            Fuse currentFuse = fuses.get(i);
            Entity currentEnt = currentFuse.getOwner();


            long nbUnprocFuses = currentEnt.getFuses()
                    .stream()
                    .filter((Fuse f) -> !fuseInCircle.contains(f))
                    .count();


            if(currentEnt.getFuses().size() >= 2 && nbUnprocFuses != 0) {
                List<Fuse> entFuses = currentEnt.getFuses();
                for (int idx1st = 0; idx1st < entFuses.size(); idx1st++) {
                    if(currentEnt instanceof Substation || !entFuses.get(idx1st).equals(currentFuse)) {
                        for (int idx2nd = idx1st + 1; idx2nd < entFuses.size(); idx2nd++) {
                            if(currentEnt instanceof Substation || !entFuses.get(idx2nd).equals(currentFuse)) {
                                Fuse first = entFuses.get(idx1st);
                                Fuse second = entFuses.get(idx2nd);

                                Optional<Circle> optCycle = DetectCircle.findCircle(first, second);
                                if(optCycle.isPresent()) {
                                    Circle circle = optCycle.get();
                                    circles.add(circle);
                                    Collections.addAll(fuseInCircle, circle.getFuses());
                                }

                            }
                        }
                    }
                }
            }

            i += currentEnt.getFuses().size();
        }

        handleInnerCircles(circles);
        substation.getGrid().save(CircleUtils.getKey(substation), circles);
    }

    private Circle addFusesToCircle(Circle circle, List<Fuse> toAdd) {
        Fuse[] current = circle.getFuses();
        Fuse[] newCycle = new Fuse[current.length + toAdd.size()];

        System.arraycopy(current, 0, newCycle, 0, current.length);
        int i = current.length;
        for(Fuse f: toAdd) {
            newCycle[i] = f;
            i++;
        }

        return new CircleImp(newCycle);

    }


    private void handleInnerCircles(List<Circle> circles) {
        for (int i = 0; i < circles.size(); i++) {
            for (int j = i + 1; j < circles.size(); j++) {
                Circle circle1 = circles.get(i);
                Circle circle2 = circles.get(j);

                var fusesInCommon = new HashSet<Fuse>();
                var fusesDifferentC1 = new ArrayList<Fuse>();
                var fusesDifferentC2 = new ArrayList<Fuse>();

                for (Fuse fC1: circle1.getFuses()) {
                    if(OArrays.contains(circle2.getFuses(), fC1)) {
                        fusesInCommon.add(fC1);
                    } else {
                        fusesDifferentC1.add(fC1);
                    }
                }

                for(Fuse fC2: circle2.getFuses()) {
                    if(OArrays.contains(circle1.getFuses(), fC2)) {
                        fusesInCommon.add(fC2);
                    } else {
                        fusesDifferentC2.add(fC2);
                    }
                }

                if(!fusesInCommon.isEmpty()) {
                    boolean c1Longest = circle1.getFuses().length > circle2.getFuses().length;

                    if(c1Longest) {
                        Circle newCircle = addFusesToCircle(circle1, fusesDifferentC2);
                        circles.set(i, newCircle);
                    } else {
                        Circle newCircle = addFusesToCircle(circle2, fusesDifferentC1);
                        circles.set(j, newCircle);
                    }

                }



            }
        }
    }




}