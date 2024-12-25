package io.datadynamics.prometheus.micrometer;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.apache.commons.lang3.RandomUtils;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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

    public static void main(String[] args) throws Exception {
//        simpleRegistry();
//        compositeRegistry();
//        globalRegistry();
//        prometheusMeterRegistry();
//        counter();
//        gauge();
//        timer();
//        distributionSummary();
        longTaskTimer();
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
        SimpleMeterRegistry registry = new SimpleMeterRegistry();

        // Type 1 Counter
        Counter counter1 = counter(registry); // with tags
        counter1.increment();
        counter1.increment();
        counter1.increment();
        counter1.increment();
        System.out.println("Counter 1 : " + counter1.count());

        // Type 2 Counter
        Counter counter2 = registry.counter("counter"); // without tags
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

        System.out.println("Registry : " + registry.getMetersAsString());
    }

    public static void compositeRegistry() {
        CompositeMeterRegistry compositeRegistry = new CompositeMeterRegistry();
        Counter compositeCounter = compositeRegistry.counter("compositeCounter");
        compositeCounter.increment();
        compositeCounter.increment();

        SimpleMeterRegistry simpleRegistry = new SimpleMeterRegistry();
        Counter simpleCounter = simpleRegistry.counter("simpleCounter");
        simpleCounter.increment();
        System.out.println("Counter : " + simpleCounter.count()); // = 1.0

        compositeRegistry.add(simpleRegistry);

        compositeCounter.increment();
        compositeCounter.increment();
        System.out.println("Counter : " + compositeCounter.count()); // = 2.0

        Set<MeterRegistry> registries = compositeRegistry.getRegistries();
        registries.forEach(registry -> {
            registry.getMeters().forEach(meter -> {
                String registryClassName = registry.getClass().getName();
                String meterClassName = meter.getClass().getName();
                System.out.println(registryClassName + " : " + meterClassName + " : " + meter.getId() + " : " + meter.measure());
            });
        });
    }

    public static void globalRegistry() {
        Counter counter = Metrics.globalRegistry.counter("globalCounter");
        counter.increment();

        System.out.println("Counter : " + counter.count()); // = 0.0

        SimpleMeterRegistry simpleRegistry = new SimpleMeterRegistry();
        Counter simpleCounter = simpleRegistry.counter("simpleCounter");
        simpleCounter.increment();
        System.out.println("Counter : " + simpleCounter.count()); // = 1.0

        Metrics.addRegistry(simpleRegistry);
    }

    public static void prometheusMeterRegistry() {
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        Counter counter = Counter
                .builder("http.rest.api")
                .baseUnit("requests")
                .description("HTTP Request Total Count")
                .tags("instance", "api.datalake.net", "uri", "/api/v1/user")
                .register(registry);

        counter.increment();
        counter.increment();

        System.out.println(registry.scrape());
    }

    public static void counter() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();

        Counter counter = Counter.builder("http.rest.api")
                .baseUnit("requests")
                .description("HTTP Request Total Count")
                .tags("instance", "api.datalake.net", "uri", "/api/v1/user")
                .register(registry);

        System.out.println(counter.getClass().getName()); // CumulativeCounter

        counter.increment(1.1);
        counter.increment(0.5);
        counter.increment(-1.0); // 사용은 가능하나 결과값은 오류 발생

        System.out.println("Counter : " + counter.count()); // = 0.6000000000000001
    }

    public static void gauge() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        MyClass myObj = new MyClass();

        Gauge gauge = Gauge.builder("http.rest.api", myObj, myObj::getGaugeValue)
                .baseUnit("active")
                .description("Active Thread Count for HTTP REST API")
                .tags("instance", "api.datalake.net")
                .register(registry);

        System.out.println("Gauge : " + gauge.value()); // 81.0
        System.out.println("Gauge : " + gauge.value()); // 81.0
        System.out.println("Gauge : " + gauge.value()); // 49.0
    }

    public static void timer() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();

        Timer timer = Timer.builder("http.rest.api.request.time")
                .description("HTTP REST API Request Time")
                .maximumExpectedValue(Duration.ofMinutes(10))
                .minimumExpectedValue(Duration.ofSeconds(1))
                .sla(Duration.ofMinutes(10))
                .tags("instance", "api.datalake.net")
                .register(registry);

        for (int i = 0; i < 3; i++) {
            Timer.Sample sample = Timer.start(registry);
            sleep();
            sample.stop(timer);
        }

        System.out.println(registry.getMetersAsString());
    }

    public static void distributionSummary() {
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        DistributionSummary summary = DistributionSummary
                .builder("response.size")
                .description("a description of what this summary does")
                .baseUnit("bytes")
                .tags("instance", "api.datalake.net")
                .serviceLevelObjectives(80) // 80 보다 작은 것의 개수가 Gauge로 제공
                .maximumExpectedValue(100d)
                .register(registry);

        summary.record(20); // 1
        summary.record(77); // 2
        summary.record(32); // 3
        summary.record(55); // 4
        summary.record(88);
        summary.record(91);
        summary.record(81);
        summary.record(43); // 5
        summary.record(100);

        System.out.println(summary.takeSnapshot().toString());
        System.out.println(registry.scrape());
    }

    public static void longTaskTimer() throws InterruptedException {
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        LongTaskTimer timer = LongTaskTimer
                .builder("long.task.timer")
                .description("a description of what this timer does")
                .serviceLevelObjectives(Duration.ofSeconds(5))
                .publishPercentileHistogram()
                .publishPercentiles(0.8, 0.95)
                .tags("instance", "api.datalake.net")
                .register(registry);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(16, 20, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1000));
        for (int i = 0; i < 20; i++) {
            try {
                Thread.sleep(RandomUtils.insecure().nextInt(100, 500));
            } catch (InterruptedException e) {
            }

            final int idx = i;
            executor.execute(() -> {
                LongTaskTimer.Sample sample = timer.start();
                sleep();
                String index = String.format("[%s] ", idx);
                System.out.println(index + "Duration : " + sample.duration(TimeUnit.MILLISECONDS) + "ms");
                System.out.println(index + "Max time: " + timer.max(TimeUnit.MILLISECONDS) + " ms");
                System.out.println(index + "Active Tasks : " + timer.activeTasks());
                System.out.println(index + "\n" + registry.scrape());
                sample.stop();
            });
        }

        // 더 이상 새로운 작업을 받지 않고, 작업 큐에 대기 중인 모든 작업을 처리한 후 종료한다.
        executor.shutdown();
        try {
            // 10초 동안 모든 작업이 완료되길 기다린다.
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                // 10초 동안 모든 작업이 끝나지 않은 경우, 모든 작업을 취소하고 Thread Pool을 강제 종료한다.
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            // InterruptedException 발생 시, 작업 취소하고 Thread Pool을 강제 종료한다.
            executor.shutdownNow();
        }

    }

    public static void sleep() {
        try {
            int millis = RandomUtils.insecure().nextInt(500, 2000);
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

    static class MyClass {
        public double getGaugeValue(MyClass myClass) {
            return RandomUtils.insecure().randomInt(1, 100);
        }
    }

}
