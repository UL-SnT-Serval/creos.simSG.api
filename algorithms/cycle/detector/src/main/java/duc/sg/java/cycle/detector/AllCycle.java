package duc.sg.java.cycle.detector;

import duc.sg.java.model.Entity;
import duc.sg.java.model.Fuse;

import java.util.*;

public class AllCycle {
    private int lastIndex = 0;
    private Deque<Fuse> circle;
    private Map<Fuse, Integer> idxMap;
    private Map<Fuse, Integer> lowLinkMap;
    private Entity beginningEntity;


    public AllCycle() {
        circle = new ArrayDeque<>();
        idxMap = new HashMap<>();
        lowLinkMap = new HashMap<>();
    }

    public Fuse[] getEndCircle(Fuse start) {
        Fuse oppStart = start.getOpposite();
        if(beginningEntity == null) {
            beginningEntity = start.getOwner();
        }

        idxMap.put(start, lastIndex);
        lowLinkMap.put(start, lastIndex);
        lastIndex++;
        circle.push(start);

        idxMap.put(oppStart, lastIndex);
        lowLinkMap.put(oppStart, lastIndex);
        lastIndex++;
        circle.push(oppStart);

        if(oppStart.isClosed()) {
            var owner = oppStart.getOwner();
            if(!owner.isDeadEnd()) {
                for (var f : owner.getFuses()) {
                    if(f.isClosed()) {
                        if (!idxMap.containsKey(f) && !owner.equals(beginningEntity)) {
                            getEndCircle(f);
                            lowLinkMap.put(start, Math.min(lowLinkMap.get(start), lowLinkMap.get(f)));
                        } else if (circle.contains(f)) {
                            lowLinkMap.put(start, Math.min(lowLinkMap.get(start), lowLinkMap.get(f)));
                        }
                    }
                }
            }
        }

        if(lowLinkMap.get(start).equals(idxMap.get(start))) {
            Fuse f;
            var res = new ArrayList<Fuse>();
            do {
                f = circle.pop();
                if(!f.equals(start) && start.getOwner().equals(f.getOwner())) {
                    res.add(0, f);
                } else {
                    res.add(f);
                }
            }while(!f.equals(start));
            if(res.size() > 2 && start.getOwner().getFuses().size() >= 2 ) {
                return res.toArray(new Fuse[0]);
            }
        }
        return new Fuse[0];
    }
}