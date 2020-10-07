package duc.sg.java.server.message;

public class ULoadApproximationAnswer extends Message {
    private final ElmtULoad[] fuseLoads;
    private final ElmtULoad[] cableLoads;

    ULoadApproximationAnswer(ElmtULoad[] fuseLoads, ElmtULoad[] cableLoads) {
        super(MessageType.ULOAD_APPROX_ANSWER);
        this.fuseLoads = fuseLoads;
        this.cableLoads = cableLoads;
    }

    public ElmtULoad[] getFuseLoads() {
        return fuseLoads;
    }

    public ElmtULoad[] getCableLoads() {
        return cableLoads;
    }

    public static class ULoad {
        final double value;
        final double confidence;

        public ULoad(double value, double confidence) {
            this.confidence = confidence;
            this.value = value;
        }

        public double getConfidence() {
            return confidence;
        }

        public double getValue() {
            return value;
        }
    }

    public static class ElmtULoad {
        final String id;
        final ULoad[] uloads;

        public ElmtULoad(String id, ULoad[] uloads) {
            this.id = id;
            this.uloads = uloads;
        }

        public String getId() {
            return id;
        }

        public ULoad[] getUloads() {
            return uloads;
        }
    }

}
