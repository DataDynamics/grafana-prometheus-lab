package io.datadynamics.prometheus.micrometer;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.apache.commons.lang3.RandomUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class MicrometerTests {

    ///////////////////////////////////////
    // Micrometer의 Meter 종류
    /// ////////////////////////////////////

    Timer timer;
    Counter counter;
    Gauge gauge;
    DistributionSummary distributionSummary;
    LongTaskTimer longTaskTimer;
    FunctionCounter functionCounter;
    FunctionTimer functionTimer;
    TimeGauge timeGauge;

    public static void main(String[] args) {
        simpleRegistry();
        compositeRegistry();
    }

    /////////////////////////////////////////////////
    // Meter

    /// //////////////////////////////////////////////

    public static Counter counter(MeterRegistry registry) {
        Counter counter = Counter
                .builder("counter")
                .baseUnit("beans") // optional
                .description("a description of what this counter does") // optional
                .tags("region", "test") // optional
                .register(registry);
        return counter;
    }


    public static Gauge gauge(MeterRegistry registry, MyClass myObj) {
        Gauge gauge = Gauge
                .builder("gauge", myObj, myObj::getGaugeValue)
                .baseUnit("beans") // optional
                .description("a description of what this counter does") // optional
                .tags("region", "test") // optional
                .register(registry);
        return gauge;
    }

    /////////////////////////////////////////////////
    // Registry

    /// //////////////////////////////////////////////

    public static void simpleRegistry() {
        // In-Memory Simple Meter Registry
        MeterRegistry registry = new SimpleMeterRegistry();

        // Type 1 Counter
        Counter counter1 = counter(registry);
        counter1.increment();
        counter1.increment();
        counter1.increment();
        counter1.increment();
        System.out.println("Counter 1 : " + counter1.count());

        // Type 2 Counter
        Counter counter2 = registry.counter("counter");
        counter2.increment();
        counter2.increment();
        counter2.increment();
        counter2.increment();
        counter2.increment();
        System.out.println("Counter 2 : " + counter2.count());

        // Type 1 Gauge
        AtomicInteger gauge1 = registry.gauge("numberGauge", new AtomicInteger(0));
        gauge1.set(10);
        gauge1.set(20);
        gauge1.set(30);
        gauge1.set(40);
        gauge1.set(50);
        System.out.println("Gauge 1 : " + gauge1.get());

        // Type 2 Gauge (무조건 Double)
        MyClass myObj = new MyClass();
        Gauge gauge2 = gauge(registry, myObj);
        System.out.println("Gauge 2 : " + gauge2.value());
        System.out.println("Gauge 2 : " + gauge2.value());
        System.out.println("Gauge 2 : " + gauge2.value());
        System.out.println("Gauge 2 : " + gauge2.value());

        // registry.summary("summary").record(10);
    }

    public static void compositeRegistry() {
        CompositeMeterRegistry composite = new CompositeMeterRegistry();

        Counter compositeCounter = composite.counter("counter");
        compositeCounter.increment();

        SimpleMeterRegistry simple = new SimpleMeterRegistry();
        composite.add(simple);

        compositeCounter.increment();
    }

    public static void globalRegistry() {

    }

    static class MyClass {
        public double getGaugeValue(MyClass myClass) {
            return RandomUtils.insecure().randomDouble(1, 100);
        }
    }

}
