package duc.aintea.benchmark.loadapproximator;

import duc.aintea.benchmark.loadapproximator.cabinet.*;
import duc.aintea.benchmark.loadapproximator.singleCable.AllClosed;
import duc.aintea.benchmark.loadapproximator.singleCable.AllOpen;
import duc.aintea.benchmark.loadapproximator.singleCable.FCabOpen;
import duc.aintea.benchmark.loadapproximator.singleCable.FSubsOpen;
import duc.aintea.loadapproximation.LoadApproximator;
import duc.aintea.loadapproximation.UncertainLoadApproximator;
import duc.aintea.sg.Cable;
import duc.aintea.sg.Fuse;
import duc.aintea.sg.Meter;
import duc.aintea.sg.Substation;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public abstract class GenericBench {
    protected Substation substation;
    private Random random = new Random(12345);
    protected Fuse[] fuses;

    protected abstract void openFuses();
    protected abstract Substation initSubs();
    protected abstract Fuse[] initFuses();
    protected abstract Cable[] getCables();


    @Setup
    public void setup() {
        substation = initSubs();
        fuses = initFuses();

        var cables = getCables();
        for (int i = 0; i < cables.length; i++) {
            Cable c = cables[i];
            var meter = new Meter("m_" + i);
            meter.setConsumption(random.nextDouble() * 100);
            c.addMeters(meter);
        }
    }

    @Benchmark
    public void benchCertainApprox() {
        LoadApproximator.approximate(substation);
    }

    @Benchmark
    public void benchUncertainApprox() {
        UncertainLoadApproximator.approximate(substation);
    }

    public static void main(String[] args) throws RunnerException {
        var options = new OptionsBuilder()
                //Single Cable
                .include(AllClosed.class.getSimpleName())
                .include(AllOpen.class.getSimpleName())
                .include(FSubsOpen.class.getSimpleName())
                .include(FCabOpen.class.getSimpleName())
                // Cabinet
                .include(Sc1AllClosed.class.getSimpleName())
                .include(Sc2F4Open.class.getSimpleName())
                .include(Sc3F3Open.class.getSimpleName())
                .include(Sc4F3F4Open.class.getSimpleName())
                .include(Sc5F2Open.class.getSimpleName())
                .include(Sc6AllOpen.class.getSimpleName())
                // Para transformer
                .include(duc.aintea.benchmark.loadapproximator.paraTransformer.Sc1AllClosed.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraTransformer.Sc2F4F5Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraTransformer.Sc3F4Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraTransformer.Sc4F3Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraTransformer.Sc5F3F5Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraTransformer.Sc6F3F4F5Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraTransformer.Sc7F2Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraTransformer.Sc8F2F5Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraTransformer.Sc9F2F4Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraTransformer.Sc10F2F3Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraTransformer.Sc11F1Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraTransformer.Sc12F1F5Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraTransformer.Sc13F1F4Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraTransformer.Sc14F1F3Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraTransformer.Sc15F1F2Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraTransformer.Sc16F1F2F5Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraTransformer.Sc17F5Open.class.getSimpleName())
                // Para Cabinet
                .include(duc.aintea.benchmark.loadapproximator.paraCabinet.Sc1AllClosed.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraCabinet.Sc2F1Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraCabinet.Sc3F2Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraCabinet.Sc4F3F5Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraCabinet.Sc5F4F6Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraCabinet.Sc6F7Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraCabinet.Sc7F6Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraCabinet.Sc8F6F7Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraCabinet.Sc9F5Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraCabinet.Sc10F5F7Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraCabinet.Sc11F5F6Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraCabinet.Sc12F5F6F7Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraCabinet.Sc13F4Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraCabinet.Sc14F4F5Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraCabinet.Sc15F3F6Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraCabinet.Sc16F3F7Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraCabinet.Sc17F3Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraCabinet.Sc18F3F4Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.paraCabinet.Sc19F3F4F7Open.class.getSimpleName())
                // Indirect Cables
                .include(duc.aintea.benchmark.loadapproximator.indirectPara.Sc1AllClosed.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.indirectPara.Sc2F2Open.class.getSimpleName())
                .include(duc.aintea.benchmark.loadapproximator.indirectPara.Sc3F7Open.class.getSimpleName())
                .shouldDoGC(true)
                .threads(Threads.MAX)
                .shouldFailOnError(true)
                .resultFormat(ResultFormatType.CSV)
                .result("sc1-certain.csv")
                .build();
        new Runner(options).run();
    }




}
